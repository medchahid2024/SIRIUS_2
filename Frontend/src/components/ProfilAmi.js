import { Link, useSearchParams } from "react-router-dom";
import React, { useEffect, useState } from "react";
import "../styles/ProfilAmi.css";
import "../styles/Profil.css";

import { envoyerDemandeAmi, accepterDemandeAmi, getProfil, getSuggestionAmi, getStatutRelation } from "../API/api";

export default function ProfilAmi() {
    const [searchParams] = useSearchParams();
    const [ami, setAmi] = useState(null);
    const [chargement, setChargement] = useState(true);
    const [suggestions, setSuggestions] = useState([]);
    const [chargementSuggestions, setChargementSuggestions] = useState(false);

    const user = localStorage.getItem("user");
    const myId = JSON.parse(user || "null")?.idUtilisateur;

    const [statut, setStatut] = useState("AUCUNE");

    const idAmi = searchParams.get("to");

    useEffect(() => {
        if (!idAmi || !myId) return;
        getStatutRelation(myId, idAmi).then((monStatut) => {
            if (monStatut === "EN_ATTENTE") {
                setStatut("ENVOYE");
            } else if (monStatut === "ACCEPTEE") {
                setStatut("AMIS");
            } else {
                getStatutRelation(idAmi, myId).then((sonStatut) => {
                    if (sonStatut === "EN_ATTENTE") {
                        setStatut("RECU");
                    } else {
                        setStatut("AUCUNE");
                    }
                });
            }
        });
    }, [idAmi, myId]);

    useEffect(() => {
        if (!idAmi) {
            setChargement(false);
            return;
        }
        getProfil(idAmi)
            .then((data) => {
                setAmi(data);
                setChargement(false);
            })
            .catch(() => setChargement(false));
    }, [idAmi]);

    useEffect(() => {
        if (!idAmi || !myId) return;
        setChargementSuggestions(true);
        getSuggestionAmi(myId, idAmi)
            .then((data) => setSuggestions(data))
            .finally(() => setChargementSuggestions(false));
    }, [idAmi, myId]);

    const envoyerDemande = () => {
        if (!myId || !idAmi || statut !== "AUCUNE") return;
        setStatut("ENVOI");
        envoyerDemandeAmi(myId, idAmi)
            .then(() => setStatut("ENVOYE"))
            .catch((err) => {
                alert(err.response?.data || "Erreur lors de l'envoi");
                setStatut("AUCUNE");
            });
    };

    const accepterDemande = () => {
        if (!myId || !idAmi || statut !== "RECU") return;
        setStatut("ACCEPTATION");
        accepterDemandeAmi(myId, idAmi)
            .then(() => setStatut("AMIS"))
            .catch((err) => {
                alert(err.response?.data || "Erreur lors de l'acceptation");
                setStatut("RECU");
            });
    };

    const getBouton = () => {
        switch (statut) {case "ENVOI":
            return { texte: "Envoi...", disabled: true, classe: "btn-outline-light" };
            case "ENVOYE":
                return { texte: "Demande envoyée", disabled: true, classe: "btn-secondary" };
            case "RECU":
                return { texte: "Accepter", disabled: false, classe: "btn-success", onClick: accepterDemande };
            case "ACCEPTATION":
                return { texte: "Acceptation...", disabled: true, classe: "btn-outline-light" };
            case "AMIS":
                return { texte: "Ami", disabled: true, classe: "btn-success" };
            case "REFUSEE":
                return { texte: "Demande refusée", disabled: true, classe: "btn-danger" };
            default:
                return { texte: "Ajouter", disabled: false, classe: "btn-outline-light", onClick: envoyerDemande };
        }
    };

    const bouton = getBouton();

    if (chargement) {
        return (
            <div className="profil-page">
                <p>Chargement...</p>
            </div>
        );
    }

    if (!ami) {
        return (
            <div className="profil-page">
                <p>Profil introuvable</p>
                <Link to="/Profil">Retour à mon profil</Link>
            </div>
        );
    }

    return (
        <div className="profil-page">
            <div className="profil-banner"></div>

            <div className="profil-card">
                <img
                    src={ami.photoProfil}
                    alt={`${ami.utilisateur.nom} ${ami.utilisateur.prenom}`}
                    style={{ width: 100, height: 100, borderRadius: "50%" }}
                />
                <h2>{ami.utilisateur.nom} {ami.utilisateur.prenom}</h2>
                <p className="profil-location">{ami.ville}</p>

                <div style={{ display: "flex", gap: "50px" }}>
                    <Link to={`/Messagerie?to=${idAmi}`} className="btn btn-outline-danger">
                        Envoyer un message
                    </Link>

                    <button
                        type="button"
                        className={bouton.classe}
                        onClick={bouton.onClick}
                        disabled={bouton.disabled}
                    >
                        <strong>{bouton.texte}</strong>
                    </button>
                </div>
            </div>

            <div className="profil-layout">
                <aside className="profil-left">
                    <div className="card">
                        <h3>À propos</h3>
                        <p>{ami.bio}</p>
                        <p><strong>Établissement:</strong> {ami.etablissement}</p>
                        <p><strong>Nationalité:</strong> {ami.nationalite}</p>
                        <p><strong>Centres d'intérêt:</strong> {ami.centresInteret}</p>
                        <p><strong>Email:</strong> {ami.utilisateur.email}</p>
                    </div>
                </aside>

                <main className="profil-center">
                    <div className="card">
                        <h3>Publications de {ami.utilisateur.prenom}</h3>
                        <div className="post">Publication 1</div>
                        <div className="post">Publication 2</div>
                    </div>
                </main>

                <aside className="profil-right">
                    <div className="card">
                        <h4>Suggestions</h4>
                        {!chargementSuggestions && suggestions.length === 0 && (
                            <div>Aucune suggestion pour le moment.</div>
                        )}

                        <ul>
                            {suggestions.map((s) => (
                                <li key={s.amiId} className="ProfilAmiLi">
                                    <div className="ProfilAmiDiv">
                                        <Link to={`/ProfilAmi?to=${s.amiId}`}>
                                            <img src={s.photo} alt={`${s.prenom} ${s.nom}`} />
                                            <span>{s.prenom} {s.nom}</span>
                                        </Link>
                                    </div>
                                    <button className="btn btn-sm btn-outline-danger">
                                        Ajouter
                                    </button>
                                </li>
                            ))}
                        </ul>
                    </div>
                </aside>
            </div>
        </div>
    );
}