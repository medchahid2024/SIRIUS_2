import React, { useEffect, useState } from "react";
import { getStatsActivite } from "../API/api";
import "../styles/HistoriqueActivite.css";

export default function HistoriqueActivite() {
    const [stats, setStats] = useState(null);
    const [annee, setAnnee] = useState(2026);
    const [mois, setMois] = useState(3);

    const user = JSON.parse(localStorage.getItem("user") || "{}");
    const userId = user?.idUtilisateur;

    useEffect(() => {
        if (!userId) return;
        getStatsActivite(userId, annee, mois)
            .then((data) => setStats(data))
            .catch((err) => console.error("Erreur:", err));
    }, [userId, annee, mois]);

    const nomsMois = [
        "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
    ];

    const afficherPourcentage = (valeur) => {
        if (valeur > 0) return <span className="pourcentage-positif">+{valeur}%</span>;
        if (valeur < 0) return <span className="pourcentage-negatif">{valeur}%</span>;
        return <span className="pourcentage-neutre">0%</span>;
    };

    if (!userId) return <div className="historique-container"><h4>Utilisateur non connecté</h4></div>;
    if (!stats) return <div className="historique-container"><h4>Chargement...</h4></div>;

    return (
        <div className="historique-container">
            <h2 className="historique-titre">Historique d'activité</h2>

            <div className="historique-filtres">
                <select value={mois} onChange={(e) => setMois(+e.target.value)}>
                    {nomsMois.map((nom, i) => (
                        <option key={i} value={i + 1}>{nom}</option>
                    ))}
                </select>

                <select value={annee} onChange={(e) => setAnnee(+e.target.value)}>
                    <option value={2024}>2024</option>
                    <option value={2025}>2025</option>
                    <option value={2026}>2026</option>
                </select>
            </div>

            <div className="stats-cards">
                <div className="stat-card">
                    <h4>Publications</h4>
                    <p className="stat-valeur">{stats.nbPublications}</p>
                    {afficherPourcentage(stats.pourcentagePublications)}
                </div>
                <div className="stat-card">
                    <h4>Interactions</h4>
                    <p className="stat-valeur">{stats.nbInteractions}</p>
                    {afficherPourcentage(stats.pourcentageInteractions)}
                </div>
                <div className="stat-card">
                    <h4>Nouveaux amis</h4>
                    <p className="stat-valeur">{stats.NombreAmiParMois}</p>
                    {afficherPourcentage(stats.pourcentageAmis)}
                </div>
                <div className="stat-card">
                    <h4>Total amis</h4>
                    <p className="stat-valeur">{stats.NombreAmiTotal}</p>
                </div>
            </div>
        </div>
    );
}