import React, { useEffect, useState } from "react";

export default function Publication() {
  const [publications, setPublications] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {

    const u = localStorage.getItem("user");
    if (!u) {
      setError("Aucun utilisateur connecté (localStorage user vide).");
      return;
    }

    const user = JSON.parse(u);


    const userId = user.idUtilisateur ?? user.idutilisateur;

    if (!userId) {
      setError("Impossible de trouver l'id utilisateur dans localStorage (user.idUtilisateur / user.idutilisateur).");
      return;
    }

    fetch(`http://localhost:8080/MyUpec/publication/interacted/${userId}`)
        .then((res) => {
          if (!res.ok) throw new Error("Erreur API: " + res.status);
          return res.json();
        })
        .then((data) => setPublications(data))
        .catch((e) => setError(e.message));
  }, []);

  return (
      <div className="container mt-4">
        <h1>Mes publications (interactions)</h1>

        {error && <div className="alert alert-danger">{error}</div>}

        {!error && publications.length === 0 && (
            <div className="alert alert-info">Aucune publication avec interaction.</div>
        )}

        {publications.map((pub) => (
            <div key={pub.idPublication} className="card mb-3">
              <div className="card-body">
                <h5 className="card-title">{pub.contenuTexte}</h5>

                <h6 className="card-subtitle mb-2 text-muted">
                  {pub.auteur?.prenom} {pub.auteur?.nom} · {pub.typePublication}
                </h6>

                <small className="text-muted">
                  {pub.dateCreation ? new Date(pub.dateCreation).toLocaleString() : ""}
                </small>
              </div>
            </div>
        ))}
      </div>
  );
}
