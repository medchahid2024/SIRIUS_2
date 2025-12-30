import "../styles/Profil.css";
import React from "react";
import logo from "../assets/logo.jpeg";


export default function Profil() {
    return (

        <div className="profil-page">


            <div className="profil-banner"></div>


            <div className="profil-card">

                <h2>Chahid Mohammed</h2>
                <p className="profil-title">Voir les statistiques</p>
                <p className="profil-location">France</p>
            </div>

            <div className="profil-layout">

                <aside className="profil-left">
                    <div className="card">
                        <h3>À propos</h3>
                        <p>
                            Étudiant en informatique, passionné par le développement web
                            et les technologies modernes.
                        </p>
                    </div>
                </aside>

                <main className="profil-center">
                    <div className="card">
                        <h3>Activité</h3>
                        <div className="post">Publication 1</div>
                        <div className="post">Publication 2</div>
                    </div>
                </main>

                <aside className="profil-right">
                    <div className="card">
                        <h3>Compétences</h3>
                        <ul>
                            <li>React</li>
                            <li>CSS</li>
                            <li>JavaScript</li>
                        </ul>
                    </div>
                </aside>

            </div>
        </div>
    );
}
