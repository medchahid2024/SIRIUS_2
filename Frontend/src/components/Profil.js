import "../styles/Profil.css";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {getMesAmis ,getProfil} from "../API/api";


export default function Profil() {
    const [user, setUser] = useState(null);
    const [friends, setFriends] = useState([]);
    const [afficheAmis, setafficheAmis] = useState(false);
    const [profil, setProfil] = useState(null);


    useEffect(() => {
        const u = localStorage.getItem("user");
        if (u) setUser(JSON.parse(u));
    }, []);

    useEffect(() => {
        if (!user?.idUtilisateur) return;

        getMesAmis(user.idUtilisateur)
            .then((data) => setFriends(Array.isArray(data) ? data : []))
            .catch(console.error);
        getProfil(user.idUtilisateur)
            .then((data) => setProfil(data))
            .catch(console.error);
    }, [user?.idUtilisateur]);

        return (
        <div className="profil-page">
            <div className="profil-banner"></div>

            <div className="profil-card">
                {profil?.photoProfil && (
                    <img
                        src={profil.photoProfil}
                        alt={`${user.prenom} ${user.nom}`}
                        style={{ width: 100, height: 100, borderRadius: "50%" }}
                    />
                )}
                <h2>{user ? `${user.nom} ${user.prenom}` : "Profil"} </h2>

                <Link to={`/Statistiques/${user?.idUtilisateur}`}>
                    <p className="profil-title">Voir les statistiques</p>
                </Link>

                <p
                    className="profil-title"
                    onClick={() => setafficheAmis(!afficheAmis)}
                    style={{cursor: "pointer"}}
                >
                    Mes amis ({friends.length})
                </p>

                {afficheAmis && (
                    <div className="friends-list">
                        {friends.map((f) => (
                            <div
                                className="friend-item"
                                key={f.idUtilisateur}
                                style={{
                                    display: "flex",
                                    justifyContent: "space-between",
                                    alignItems: "center",
                                    gap: 12,
                                }}
                            >
                                <Link
                                    to={`/ProfilAmi?to=${f.idUtilisateur}`}
                                >
                                    {f.nom} {f.prenom}
                                </Link>

                                <Link
                                    to={`/Messagerie?to=${f.idUtilisateur}`}
                                    className="btn btn-sm btn-outline-danger"
                                >
                                    Nouveau message
                                </Link>
                            </div>
                        ))}
                    </div>
                )}

                <p className="profil-location">France</p>
            </div>

            <div className="profil-layout">
                <aside className="profil-left">
                    <div className="card">
                        <h3>À propos</h3>
                        <p>{profil?.bio}</p>
                        <p><strong>Établissement:</strong> {profil?.etablissement}</p>
                        <p><strong>Nationalité:</strong> {profil?.nationalite}</p>
                        <p><strong>Centres d'intérêt:</strong> {profil?.centresInteret}</p>
                    </div>
                </aside>

                <main className="profil-center">
                    <div className="card">
                        <h3>Mes Publications</h3>
                        <div className="post">Publication 1</div>
                        <div className="post">Publication 2</div>
                    </div>
                </main>

                <aside className="profil-right">
                    <div className="card">
                        <ul></ul>
                    </div>
                </aside>
            </div>
        </div>
    );
}