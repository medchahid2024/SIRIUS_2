import {Link} from "react-router-dom";
import React from "react";

return (
    <div className="profil-page">
        <div className="profil-banner"></div>

        <div className="profil-card">
            <h2>{user ? `${user.nom} ${user.prenom}` : "Profil"} </h2>





                        </div>




            <p className="profil-location">France</p>


        <div className="profil-layout">
            <aside className="profil-left">
                <div className="card">
                    <h3>À propos</h3>
                    <p></p>
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