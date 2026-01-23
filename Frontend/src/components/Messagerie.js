import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";
import "../styles/Messagerie.css";
import apiClient from "../API/apiClient";
import {
    createConversation,
    getConversations,
    getMessages,
    sendMessage,
    markConversationRead,
    getOnlineUsers,
} from "../API/api";

import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default function Messagerie() {
    const [searchParams, setSearchParams] = useSearchParams();
    const toParam = searchParams.get("to");
    const inboxParam = searchParams.get("inbox");
    const tParam = searchParams.get("t"); // pour forcer un "refresh" à chaque click navbar

    const [user, setUser] = useState(null);

    const [conversations, setConversations] = useState([]);
    const [activeConv, setActiveConv] = useState(null);
    const [activeOther, setActiveOther] = useState(null);

    const [messages, setMessages] = useState([]);
    const [text, setText] = useState("");
    const [error, setError] = useState("");


    const [onlineSet, setOnlineSet] = useState(new Set());
    const [otherTyping, setOtherTyping] = useState(false);
    const typingTimerRef = useRef(null);


    const stompRef = useRef(null);
    const subMsgRef = useRef(null);
    const subTypingRef = useRef(null);

    const knownMessageIdsRef = useRef(new Set());
    const endRef = useRef(null);

    const wsUrl = useMemo(() => {
        const base = (apiClient.defaults.baseURL || "").replace(/\/$/, "");
        return `${base}/ws`;
    }, []);

    const refreshInbox = useCallback(async (userId) => {
        const list = await getConversations(userId);
        setConversations(Array.isArray(list) ? list : []);
    }, []);

    const loadMessages = useCallback(async (convId, userId) => {
        const list = await getMessages(convId, userId);
        const arr = Array.isArray(list) ? list : [];
        setMessages(arr);
        knownMessageIdsRef.current = new Set(arr.map((m) => m.idMessage));
    }, []);

    const openConversation = useCallback(
        async (convId, other) => {
            if (!user?.idUtilisateur) return;

            setError("");
            setActiveConv(convId);
            setActiveOther(other || null);
            setOtherTyping(false);

            try {
                await loadMessages(convId, user.idUtilisateur);


                markConversationRead(convId, user.idUtilisateur).catch(() => {});
                refreshInbox(user.idUtilisateur).catch(() => {});

                requestAnimationFrame(() => {
                    endRef.current?.scrollIntoView({ behavior: "auto" });
                });
            } catch {
                setError("Impossible de charger les messages.");
            }
        },
        [user?.idUtilisateur, loadMessages, refreshInbox]
    );


    useEffect(() => {
        const u = localStorage.getItem("user");
        if (u) setUser(JSON.parse(u));
    }, []);


    useEffect(() => {
        if (!user?.idUtilisateur) return;

        const tick = async () => {
            try {
                await refreshInbox(user.idUtilisateur);


                if (activeConv) {
                    await loadMessages(activeConv, user.idUtilisateur);
                    markConversationRead(activeConv, user.idUtilisateur).catch(() => {});
                }
            } catch {

            }
        };

        tick();
        const id = setInterval(tick, 5000);
        return () => clearInterval(id);
    }, [user?.idUtilisateur, refreshInbox, loadMessages, activeConv]);


    useEffect(() => {
        if (!user?.idUtilisateur || !toParam) return;

        (async () => {
            try {
                const toUserId = Number(toParam);
                const conv = await createConversation(user.idUtilisateur, toUserId);

                const updated = await getConversations(user.idUtilisateur);
                const found = (updated || []).find(
                    (c) => c.idConversation === conv.idConversation
                );

                await openConversation(conv.idConversation, found?.other);

                setSearchParams({}, { replace: true });
            } catch {
                setError("Impossible de démarrer la conversation.");
            }
        })();
    }, [toParam, user?.idUtilisateur, setSearchParams, openConversation]);



    useEffect(() => {
        if (!inboxParam) return;
        if (!user?.idUtilisateur) return;
        if (toParam) return;

        (async () => {
            try {
                await refreshInbox(user.idUtilisateur);
            } catch {

            }

            setActiveConv(null);
            setActiveOther(null);
            setMessages([]);
            setError("");
            setOtherTyping(false);

            setSearchParams({}, { replace: true });
        })();
    }, [
        inboxParam,
        tParam,
        toParam,
        user?.idUtilisateur,
        refreshInbox,
        setSearchParams,
    ]);

    useEffect(() => {
        endRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages.length]);

    useEffect(() => {
        if (!user?.idUtilisateur) return;

        getOnlineUsers()
            .then((ids) => setOnlineSet(new Set(ids || [])))
            .catch(() => {});

        if (stompRef.current) return;

        const client = new Client({
            webSocketFactory: () => new SockJS(wsUrl),
            reconnectDelay: 3000,
            onConnect: () => {
                // presence subscribe
                client.subscribe("/topic/presence", (frame) => {
                    const evt = JSON.parse(frame.body); // {userId, online}
                    setOnlineSet((prev) => {
                        const next = new Set(prev);
                        if (evt.online) next.add(evt.userId);
                        else next.delete(evt.userId);
                        return next;
                    });
                });


                client.publish({
                    destination: "/app/presence/register",
                    body: JSON.stringify({ userId: user.idUtilisateur }),
                });
            },
        });

        client.activate();
        stompRef.current = client;

        return () => {
            client.deactivate();
            stompRef.current = null;
        };
    }, [user?.idUtilisateur, wsUrl]);


    useEffect(() => {
        const client = stompRef.current;
        if (!client || !client.connected || !activeConv || !user?.idUtilisateur) return;


        subMsgRef.current?.unsubscribe();
        subTypingRef.current?.unsubscribe();
        subMsgRef.current = null;
        subTypingRef.current = null;

        subMsgRef.current = client.subscribe(
            `/topic/conversations/${activeConv}`,
            (frame) => {
                const msg = JSON.parse(frame.body);
                if (!msg?.idMessage) return;
                if (knownMessageIdsRef.current.has(msg.idMessage)) return;

                knownMessageIdsRef.current.add(msg.idMessage);
                setMessages((prev) => [...prev, msg]);

                // mark as read if active convo
                markConversationRead(activeConv, user.idUtilisateur).catch(() => {});
                refreshInbox(user.idUtilisateur).catch(() => {});
            }
        );

        subTypingRef.current = client.subscribe(
            `/topic/conversations/${activeConv}/typing`,
            (frame) => {
                const evt = JSON.parse(frame.body); // {userId, typing}
                if (evt.userId === user.idUtilisateur) return;
                setOtherTyping(!!evt.typing);
            }
        );

        return () => {
            subMsgRef.current?.unsubscribe();
            subTypingRef.current?.unsubscribe();
            subMsgRef.current = null;
            subTypingRef.current = null;
        };
    }, [activeConv, user?.idUtilisateur, refreshInbox]);

    const notifyTyping = useCallback(
        (typing) => {
            const client = stompRef.current;
            if (!client || !client.connected || !activeConv || !user?.idUtilisateur)
                return;

            client.publish({
                destination: `/app/conversations/${activeConv}/typing`,
                body: JSON.stringify({ userId: user.idUtilisateur, typing }),
            });
        },
        [activeConv, user?.idUtilisateur]
    );

    const onChangeText = (v) => {
        setText(v);

        notifyTyping(true);
        if (typingTimerRef.current) clearTimeout(typingTimerRef.current);
        typingTimerRef.current = setTimeout(() => notifyTyping(false), 1200);
    };

    const onSend = async () => {
        setError("");
        const content = text.trim();
        if (!content || !activeConv || !user?.idUtilisateur) return;

        try {
            const msg = await sendMessage(activeConv, user.idUtilisateur, content);
            setText("");
            notifyTyping(false);

            if (msg?.idMessage && !knownMessageIdsRef.current.has(msg.idMessage)) {
                knownMessageIdsRef.current.add(msg.idMessage);
                setMessages((prev) => [...prev, msg]);
            }

            markConversationRead(activeConv, user.idUtilisateur).catch(() => {});
            await refreshInbox(user.idUtilisateur);

            requestAnimationFrame(() => {
                endRef.current?.scrollIntoView({ behavior: "smooth" });
            });
        } catch {
            setError("Impossible d'envoyer le message.");
        }
    };

    const manualRefresh = async () => {
        if (!user?.idUtilisateur) return;
        try {
            await refreshInbox(user.idUtilisateur);
            if (activeConv) {
                await loadMessages(activeConv, user.idUtilisateur);
                markConversationRead(activeConv, user.idUtilisateur).catch(() => {});
            }
        } catch {

        }
    };

    return (
        <div className="container-fluid messagerie-page" style={{ paddingTop: 12 }}>
            <div className="row h-100 messagerie-row">
                {/* LEFT: inbox */}
                <div className="col-12 col-md-4 col-lg-3 messagerie-left">
                    <div className="d-flex justify-content-between align-items-center p-2">
                        <h5 className="m-0">Messagerie</h5>

                        {/* ✅ icône petite (CSS refreshIconBtn) */}
                        <button
                            type="button"
                            className="refreshIconBtn"
                            title="Rafraîchir"
                            aria-label="Rafraîchir"
                            onClick={manualRefresh}
                        >
                            ⟳
                        </button>
                    </div>

                    <div className="list-group list-group-flush">
                        {conversations.length === 0 ? (
                            <div className="p-3 text-muted">Aucune conversation.</div>
                        ) : (
                            conversations.map((c) => {
                                const other = c.other;
                                const isOnline = other?.idUtilisateur
                                    ? onlineSet.has(other.idUtilisateur)
                                    : false;
                                const unread = Number(c.unreadCount || 0);

                                return (
                                    <button
                                        key={c.idConversation}
                                        className={
                                            "list-group-item list-group-item-action " +
                                            (activeConv === c.idConversation ? "active" : "")
                                        }
                                        onClick={() => openConversation(c.idConversation, c.other)}
                                    >
                                        <div className="d-flex align-items-center gap-2">
                                            <div className="fw-semibold">
                                                {other?.nom} {other?.prenom}
                                            </div>

                                            {isOnline && (
                                                <span
                                                    title="En ligne"
                                                    style={{
                                                        width: 8,
                                                        height: 8,
                                                        borderRadius: 99,
                                                        background: "#28a745",
                                                        display: "inline-block",
                                                    }}
                                                />
                                            )}

                                            {unread > 0 && (
                                                <span
                                                    style={{
                                                        marginLeft: "auto",
                                                        background: "#dc3545",
                                                        color: "#fff",
                                                        borderRadius: 999,
                                                        padding: "2px 8px",
                                                        fontSize: 12,
                                                    }}
                                                >
                          {unread}
                        </span>
                                            )}
                                        </div>

                                        <div className="small" style={{opacity: 0.85}}>
                                            {c?.lastMessage ? c.lastMessage : "(conversation vide)"}
                                        </div>
                                    </button>
                                );
                            })
                        )}
                    </div>
                </div>

                {/* RIGHT: thread */}
                <div className="col-12 col-md-8 col-lg-9 messagerie-right">
                    {!activeConv ? (
                        <div className="h-100 d-flex align-items-center justify-content-center text-muted">
                            Sélectionne une conversation ou démarre-en une depuis ta liste
                            d'amis.
                        </div>
                    ) : (
                        <>
                            {/* header thread */}
                            <div className="p-2 border-bottom bg-white">
                                <div className="fw-semibold">
                                    {activeOther
                                        ? `${activeOther.nom} ${activeOther.prenom}`
                                        : "Conversation"}
                                </div>
                                {otherTyping && (
                                    <div className="small text-muted">En train d’écrire…</div>
                                )}
                            </div>

                            {/* messages */}
                            <div className="messagerie-thread">
                                {error && <div className="text-danger mb-2">{error}</div>}

                                {messages.length === 0 ? (
                                    <div className="text-muted">Aucun message pour l'instant.</div>
                                ) : (
                                    messages.map((m) => {
                                        const mine = m.senderId === user?.idUtilisateur;
                                        return (
                                            <div
                                                key={m.idMessage}
                                                className={
                                                    "d-flex mb-2 " +
                                                    (mine ? "justify-content-end" : "justify-content-start")
                                                }
                                            >
                                                <div
                                                    className={
                                                        "msg-bubble " + (mine ? "msg-mine" : "msg-other")
                                                    }
                                                >
                                                    {!mine && (
                                                        <div
                                                            className="small fw-semibold"
                                                            style={{opacity: 0.8, marginBottom: 4}}
                                                        >
                                                            {m.senderNom} {m.senderPrenom}
                                                        </div>
                                                    )}
                                                    {m.contenu}
                                                </div>
                                            </div>
                                        );
                                    })
                                )}

                                <div ref={endRef}/>
                            </div>

                            {/* composer */}
                            <div className="messagerie-composer">
                                <div className="d-flex gap-2">
                                    <input
                                        className="form-control"
                                        placeholder="Écrire un message…"
                                        value={text}
                                        onChange={(e) => onChangeText(e.target.value)}
                                        onKeyDown={(e) => e.key === "Enter" && onSend()}
                                    />
                                    <button className="btn btn-danger" onClick={onSend}>
                                        Envoyer
                                    </button>
                                </div>
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}
