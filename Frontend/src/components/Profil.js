import "../styles/Profil.css";
import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";


export default function Profil() {
    const [user, setUser] = useState(null);
    const [friends, setFriends] = useState([]);
    const [afficheAmis, setafficheAmis] = useState(false);




    useEffect(() => {const u = localStorage.getItem("user");
        if (u) setUser(JSON.parse(u));
    }, []);



    useEffect(() => {
        if (!user?.idUtilisateur) return;

        fetch(`http://localhost:8080/MyUpec/ami/mesAmis/${user.idUtilisateur}`)
            .then((r) => r.json())
            .then((data) => setFriends(Array.isArray(data) ? data : []))
            .catch(console.error);
    }, [user?.idUtilisateur]);


    return (

        <div className="profil-page">


            <div className="profil-banner"></div>


            <div className="profil-card">

                <h2>{user ? `${user.nom} ${user.prenom}` : "Profil"} </h2>
                <Link to={`/Statistiques/${user?.idUtilisateur }`}>
                    <p className="profil-title">Voir les statistiques</p>
                </Link>



                <p
                    className="profil-title"
                    onClick={() => setafficheAmis(!afficheAmis)}
                    style={{ cursor: "pointer" }}
                >
                    Mes amis ({friends.length})
                </p>


                {afficheAmis && (
                    <div className="friends-list">
                        {friends.map((f) => (
                            <div className="friend-item" key={f.idUtilisateur}>
                                <div>{f.nom} {f.prenom}</div>
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
                        <p>

                        </p>
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
                        <h3></h3>
                        <ul>

                        </ul>
                    </div>
                </aside>

            </div>
        </div>
    );
}
