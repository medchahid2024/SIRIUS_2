import React, { useEffect, useMemo, useRef, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import logo from "../assets/logo.jpeg";
import "../styles/Home.css";
import { getTotalUnread } from "../API/api";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import apiClient from "../API/apiClient";

export default function Navbar() {
    const [user, setUser] = useState(null);
    const [unreadTotal, setUnreadTotal] = useState(0);
    const navigate = useNavigate();
    const stompRef = useRef(null);

    const wsUrl = useMemo(() => {
        const base = (apiClient.defaults.baseURL || "").replace(/\/$/, "");
        return `${base}/ws`;
    }, []);

    useEffect(() => {
        const u = localStorage.getItem("user");
        if (u) setUser(JSON.parse(u));
    }, []);

    useEffect(() => {
        if (!user?.idUtilisateur) return;

        const fetchUnread = async () => {
            try {
                const n = await getTotalUnread(user.idUtilisateur);
                setUnreadTotal(Number(n) || 0);
            } catch {}
        };

        fetchUnread();

        if (stompRef.current) return;

        const client = new Client({
            webSocketFactory: () => new SockJS(wsUrl),
            reconnectDelay: 3000,
            onConnect: () => {
                client.subscribe(`/topic/unread/${user.idUtilisateur}`, () => {
                    fetchUnread();
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


    const goMessagerieInbox = () => {
        navigate(`/Messagerie?inbox=1&t=${Date.now()}`);
    };

    const logout = () => {
        localStorage.removeItem("user");
        window.location.href = "/";
    };

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-danger">
            <div className="container-fluid">
                <Link to="/Home" className="navbar-brand">
                    <img src={logo} width={100} height={60} alt="logo" />
                </Link>

                <button
                    className="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"></span>
                </button>

                <div className="collapse navbar-collapse" id="navbarSupportedContent">
                    <form className="d-flex ms-auto" role="search" onSubmit={(e) => e.preventDefault()}>
                        <input className="form-control me-2" type="search" placeholder="Rechercher..." />
                        <button className="btn btn-outline-light" type="submit">
                            Rechercher
                        </button>
                    </form>
                </div>

                <div className="navbar-right d-flex align-items-center gap-3">
                    {}
                    <button
                        type="button"
                        onClick={goMessagerieInbox}
                        className="navbar-brand position-relative"
                        style={{ background: "transparent", border: 0, padding: 0, cursor: "pointer" }}
                    >
                        messagerie
                        {unreadTotal > 0 && (
                            <span
                                style={{
                                    position: "absolute",
                                    top: -6,
                                    right: -12,
                                    background: "#fff",
                                    color: "#dc3545",
                                    borderRadius: 999,
                                    padding: "2px 6px",
                                    fontSize: 12,
                                    lineHeight: "12px",
                                    fontWeight: 700,
                                }}
                            >
                {unreadTotal}
              </span>
                        )}
                    </button>

                    <Link to="/Publication" className="navbar-brand">publication</Link>
                    <Link to="/Home" className="navbar-brand">notification</Link>
                    <Link to="/Home" className="navbar-brand">about</Link>

                    <Link to="/Profil" className="user-name">
                        {user ? `${user.nom} ${user.prenom}` : "Profil"}
                    </Link>

                    <button className="btn btn-sm btn-outline-light" onClick={logout}>
                        Déconnexion
                    </button>
                </div>
            </div>
        </nav>
    );
}
