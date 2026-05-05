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
    getMesAmis,
    createGroupe,
    ajouterMembre,
    supprimerMembre,
    quitterGroupe,
    sendFichier,
} from "../API/api";

import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export default function Messagerie() {
    const [searchParams, setSearchParams] = useSearchParams();
    const toParam = searchParams.get("to");
    const inboxParam = searchParams.get("inbox");
    const tParam = searchParams.get("t");

    const [user, setUser] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");


    const [conversations, setConversations] = useState([]);
    const [activeConv, setActiveConv] = useState(null);
    const [activeOther, setActiveOther] = useState(null);

    const [messages, setMessages] = useState([]);
    const [text, setText] = useState("");
    const [error, setError] = useState("");


    const [showGroupModal, setShowGroupModal] = useState(false);
    const [groupName, setGroupName] = useState('');
    const [friendsList, setFriendsList] = useState([]);
    const [selectedFriends, setSelectedFriends] = useState([]);

    const [showMembresPanel, setShowMembresPanel] = useState(false);
    const [amisDisponibles, setAmisDisponibles] = useState([]);

    const [onlineSet, setOnlineSet] = useState(new Set());
    const [otherTyping, setOtherTyping] = useState(false);
    const typingTimerRef = useRef(null);


    const stompRef = useRef(null);
    const subMsgRef = useRef(null);
    const subTypingRef = useRef(null);

    const knownMessageIdsRef = useRef(new Set());
    const endRef = useRef(null);
    const fileInputRef = useRef(null);

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
            setShowMembresPanel(false);

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
                client.subscribe("/topic/presence", (frame) => {
                    const evt = JSON.parse(frame.body);
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

        client.publish({
            destination: `/app/conversations/${activeConv}/active`,
            body: JSON.stringify({ userId: user.idUtilisateur, conversationId: activeConv, active: true }),
        });

        subMsgRef.current = client.subscribe(
            `/topic/conversations/${activeConv}`,
            (frame) => {
                const msg = JSON.parse(frame.body);
                if (!msg?.idMessage) return;
                if (knownMessageIdsRef.current.has(msg.idMessage)) return;

                knownMessageIdsRef.current.add(msg.idMessage);
                setMessages((prev) => [...prev, msg]);

                markConversationRead(activeConv, user.idUtilisateur).catch(() => {});
                refreshInbox(user.idUtilisateur).catch(() => {});
            }
        );

        subTypingRef.current = client.subscribe(
            `/topic/conversations/${activeConv}/typing`,
            (frame) => {
                const evt = JSON.parse(frame.body);
                if (evt.userId === user.idUtilisateur) return;
                setOtherTyping(!!evt.typing);
            }
        );

        return () => {
            subMsgRef.current?.unsubscribe();
            subTypingRef.current?.unsubscribe();
            subMsgRef.current = null;
            subTypingRef.current = null;
            if (client?.connected) {
                client.publish({
                    destination: `/app/conversations/${activeConv}/active`,
                    body: JSON.stringify({ userId: user.idUtilisateur, conversationId: activeConv, active: false }),
                });
            }
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

    const openGroupModal = async () => {
        if (!user?.idUtilisateur) return;
        try {
            const amis = await getMesAmis(user.idUtilisateur);
            setFriendsList(Array.isArray(amis) ? amis : []);
        } catch {
            setFriendsList([]);
        }
        setSelectedFriends([]);
        setGroupName('');
        setShowGroupModal(true);
    };

    const toggleFriend = (id) => {
        setSelectedFriends(prev =>
            prev.includes(id) ? prev.filter(f => f !== id) : [...prev, id]
        );
    };

    const openMembresPanel = async () => {
        if (!user?.idUtilisateur || !activeConv) return;
        const conv = conversations.find(c => c.idConversation === activeConv);
        const membresIds = new Set((conv?.membres || []).map(m => m.idUtilisateur));
        try {
            const amis = await getMesAmis(user.idUtilisateur);
            setAmisDisponibles((amis || []).filter(a => !membresIds.has(a.idUtilisateur)));
        } catch {
            setAmisDisponibles([]);
        }
        setShowMembresPanel(true);
    };

    const handleAjouterMembre = async (amiId) => {
        try {
            await ajouterMembre(activeConv, user.idUtilisateur, amiId);
            await refreshInbox(user.idUtilisateur);
            await openMembresPanel();
        } catch {
            setError("Impossible d'ajouter ce membre.");
        }
    };

    const handleSupprimerMembre = async (membreId) => {
        try {
            await supprimerMembre(activeConv, user.idUtilisateur, membreId);
            await refreshInbox(user.idUtilisateur);
            await openMembresPanel();
        } catch {
            setError("Impossible de supprimer ce membre.");
        }
    };

    const handleQuitterGroupe = async () => {
        if (!activeConv || !user?.idUtilisateur) return;
        try {
            await quitterGroupe(activeConv, user.idUtilisateur);
            setActiveConv(null);
            setActiveOther(null);
            setMessages([]);
            setShowMembresPanel(false);
            await refreshInbox(user.idUtilisateur);
        } catch {
            setError("Impossible de quitter le groupe.");
        }
    };

    const handleSendFile = async (e) => {
        const fichier = e.target.files[0];
        if (!fichier || !activeConv || !user?.idUtilisateur) return;
        e.target.value = "";
        try {
            const msg = await sendFichier(activeConv, user.idUtilisateur, fichier);
            if (msg?.idMessage && !knownMessageIdsRef.current.has(msg.idMessage)) {
                knownMessageIdsRef.current.add(msg.idMessage);
                setMessages((prev) => [...prev, msg]);
            }
            markConversationRead(activeConv, user.idUtilisateur).catch(() => {});
            await refreshInbox(user.idUtilisateur);
        } catch {
            setError("Impossible d'envoyer le fichier.");
        }
    };

    const handleCreateGroup = async () => {
        if (!groupName.trim() || selectedFriends.length < 2) return;
        try {
            const conv = await createGroupe(groupName, user.idUtilisateur, selectedFriends);
            await refreshInbox(user.idUtilisateur);
            setShowGroupModal(false);
            await openConversation(conv.idConversation, null);
        } catch {
            setError('Impossible de créer le groupe.');
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

    const displayed = searchTerm.trim()
        ? conversations.filter(conv => {
            const term = searchTerm.toLowerCase().trim();
            if (conv.isGroupe) return conv.nom?.toLowerCase().includes(term);
            return (
                conv.other?.nom?.toLowerCase().includes(term) ||
                conv.other?.prenom?.toLowerCase().includes(term)
            );
        })
        : conversations;

    return (
        <div className="container-fluid messagerie-page" style={{ paddingTop: 12 }}>
            <div className="row h-100 messagerie-row">
                <div className="col-12 col-md-4 col-lg-3 messagerie-left">
                    <div className="d-flex justify-content-between align-items-center p-2">
                        <h5 className="m-0">Messagerie</h5>

                        <div className="d-flex gap-1">
                            <input
                                type="text"
                                placeholder="Rechercher un profil"
                                className="form-control form-control-sm"
                                style={{ width: "200px" }}
                                value={searchTerm || ''}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />

                            <button
                                type="button"
                                className="refreshIconBtn btn btn-outline-secondary btn-sm p-1"
                                title="Actualiser la liste"
                                onClick={manualRefresh}
                            >
                                ⟳
                            </button>
                        </div>
                    </div>

                    {showGroupModal && (
                        <div className="modal d-block" style={{ background: 'rgba(0,0,0,0.4)', position: 'fixed', inset: 0, zIndex: 1050 }}>
                            <div className="modal-dialog">
                                <div className="modal-content">
                                    <div className="modal-header">
                                        <h5 className="modal-title">Créer un groupe</h5>
                                        <button type="button" className="btn-close" onClick={() => setShowGroupModal(false)} />
                                    </div>
                                    <div className="modal-body">
                                        <input
                                            className="form-control mb-3"
                                            placeholder="Nom du groupe"
                                            value={groupName}
                                            onChange={e => setGroupName(e.target.value)}
                                        />
                                        {friendsList.length === 0
                                            ? <div className="text-muted">Aucun ami trouvé</div>
                                            : friendsList.map(ami => (
                                                <div key={ami.idUtilisateur} className="form-check mb-1">
                                                    <input
                                                        className="form-check-input"
                                                        type="checkbox"
                                                        id={`ami-${ami.idUtilisateur}`}
                                                        checked={selectedFriends.includes(ami.idUtilisateur)}
                                                        onChange={() => toggleFriend(ami.idUtilisateur)}
                                                    />
                                                    <label className="form-check-label" htmlFor={`ami-${ami.idUtilisateur}`}>
                                                        {ami.nom} {ami.prenom}
                                                    </label>
                                                </div>
                                            ))
                                        }
                                    </div>
                                    <div className="modal-footer">
                                        <button className="btn btn-secondary" onClick={() => setShowGroupModal(false)}>Annuler</button>
                                        <button
                                            className="btn btn-danger"
                                            onClick={handleCreateGroup}
                                            disabled={!groupName.trim() || selectedFriends.length < 2}
                                        >
                                            Créer
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}


                    <div className="list-group list-group-flush">
                        {displayed.length === 0 ? (
                            <div className="p-3 text-muted">Pas encore de conversations</div>
                        ) : (
                            displayed.map((conv) => {
                                const autreUser = conv.other;
                                const online = !conv.isGroupe && autreUser?.idUtilisateur && onlineSet.has(autreUser.idUtilisateur);
                                const nonLus = Number(conv.unreadCount || 0);
                                const titre = conv.isGroupe
                                    ? conv.nom
                                    : `${autreUser?.nom ?? ''} ${autreUser?.prenom ?? ''}`;
                                const prefixe = conv.lastSenderId === user?.idUtilisateur
                                    ? 'Vous : '
                                    : conv.lastSenderPrenom ? `${conv.lastSenderPrenom} : ` : '';

                                return (
                                    <button
                                        key={conv.idConversation}
                                        className={`list-group-item list-group-item-action ${activeConv === conv.idConversation ? 'active' : ''}`}
                                        onClick={() => openConversation(conv.idConversation, conv.isGroupe ? null : conv.other)}
                                    >
                                        <div className="d-flex align-items-center">
                                            <div className="fw-bold me-auto">{titre}</div>

                                            {online && (
                                                <span
                                                    title="En ligne"
                                                    className="badge bg-success rounded-pill me-2"
                                                    style={{width: 10, height: 10}}
                                                />
                                            )}

                                            {nonLus > 0 && (
                                                <span className="badge bg-danger">{nonLus}</span>
                                            )}
                                        </div>

                                        <div className="small text-muted mt-1">
                                            {conv.lastMessage ? prefixe + conv.lastMessage : 'Aucun message'}
                                        </div>
                                    </button>
                                );
                            })
                        )}
                    </div>

                    <div className="p-2 border-top">
                        <button
                            type="button"
                            className="btn btn-danger w-100"
                            onClick={openGroupModal}
                        >
                            Créer un groupe
                        </button>
                    </div>

                </div>

                <div className="col-12 col-md-8 col-lg-9 messagerie-right">
                    {!activeConv ? (
                        <div className="h-100 d-flex align-items-center justify-content-center text-muted">
                            Sélectionne une conversation ou démarre-en une depuis ta liste
                            d'amis.
                        </div>
                    ) : (
                        <>
                            <div className="p-2 border-bottom bg-white">
                                {(() => {
                                    const conv = conversations.find(c => c.idConversation === activeConv);
                                    const estCreateur = conv?.creatorId === user?.idUtilisateur;
                                    return conv?.isGroupe ? (
                                        <>
                                            <div className="d-flex align-items-center justify-content-between">
                                                <div className="fw-semibold">{conv.nom}</div>
                                                <div className="d-flex gap-2">
                                                    {estCreateur && (
                                                        <button
                                                            className="btn btn-sm btn-outline-secondary"
                                                            onClick={() => showMembresPanel ? setShowMembresPanel(false) : openMembresPanel()}
                                                        >
                                                            Gérer
                                                        </button>
                                                    )}
                                                    <button
                                                        className="btn btn-sm btn-outline-danger"
                                                        onClick={handleQuitterGroupe}
                                                    >
                                                        Quitter
                                                    </button>
                                                </div>
                                            </div>
                                            {estCreateur && showMembresPanel && (
                                                <div className="mt-2 p-2 border rounded bg-light">
                                                    <div className="fw-semibold mb-1 small">Membres actuels</div>
                                                    {(conv.membres || []).filter(m => m.idUtilisateur !== user.idUtilisateur).map(m => (
                                                        <div key={m.idUtilisateur} className="d-flex align-items-center justify-content-between mb-1">
                                                            <span className="small">{m.prenom} {m.nom}</span>
                                                            <button
                                                                className="btn btn-sm btn-outline-danger py-0"
                                                                onClick={() => handleSupprimerMembre(m.idUtilisateur)}
                                                            >
                                                                Retirer
                                                            </button>
                                                        </div>
                                                    ))}
                                                    {amisDisponibles.length > 0 && (
                                                        <>
                                                            <div className="fw-semibold mb-1 small mt-2">Ajouter un ami</div>
                                                            {amisDisponibles.map(a => (
                                                                <div key={a.idUtilisateur} className="d-flex align-items-center justify-content-between mb-1">
                                                                    <span className="small">{a.nom} {a.prenom}</span>
                                                                    <button
                                                                        className="btn btn-sm btn-outline-success py-0"
                                                                        onClick={() => handleAjouterMembre(a.idUtilisateur)}
                                                                    >
                                                                        Ajouter
                                                                    </button>
                                                                </div>
                                                            ))}
                                                        </>
                                                    )}
                                                </div>
                                            )}
                                        </>
                                    ) : (
                                        <div className="fw-semibold">
                                            {activeOther ? `${activeOther.nom} ${activeOther.prenom}` : "Conversation"}
                                        </div>
                                    );
                                })()}
                                {otherTyping && (
                                    <div className="small text-muted">En train d’écrire…</div>
                                )}
                            </div>

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
                                                    {m.fichierUrl ? (
                                                        (m.fichierNom?.endsWith('.png') || m.fichierNom?.endsWith('.jpg') || m.fichierNom?.endsWith('.jpeg')) ? (
                                                            <img
                                                                src={`${apiClient.defaults.baseURL}${m.fichierUrl}`}
                                                                alt={m.fichierNom}
                                                                style={{ maxWidth: 200, borderRadius: 8, display: "block" }}
                                                            />
                                                        ) : (
                                                            <a
                                                                href={`${apiClient.defaults.baseURL}${m.fichierUrl}`}
                                                                target="_blank"
                                                                rel="noreferrer"
                                                                style={{ color: mine ? "#fff" : "#dc3545" }}
                                                            >
                                                                {m.fichierNom}
                                                            </a>
                                                        )
                                                    ) : m.contenu}
                                                </div>
                                            </div>
                                        );
                                    })
                                )}

                                <div ref={endRef}/>
                            </div>

                            <div className="messagerie-composer">
                                <div className="d-flex gap-2">
                                    <input
                                        className="form-control"
                                        placeholder="Écrire un message"
                                        value={text}
                                        onChange={(e) => onChangeText(e.target.value)}
                                        onKeyDown={(e) => e.key === "Enter" && onSend()}
                                    />
                                    <input
                                        type="file"
                                        ref={fileInputRef}
                                        style={{ display: "none" }}
                                        accept=".png,.jpg,.jpeg,.pdf,.docx,.pptx"
                                        onChange={handleSendFile}
                                    />
                                    <button
                                        className="btn btn-outline-secondary"
                                        onClick={() => fileInputRef.current?.click()}
                                        title="Joindre un fichier"
                                    >
                                        Joindre
                                    </button>
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
