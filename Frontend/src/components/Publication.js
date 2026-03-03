import React, { useEffect, useState, useCallback } from "react";
import { getFeed } from "../API/api";

export default function Publication() {
  const [items, setItems] = useState([]);
  const [offset, setOffset] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const limit = 10;

  const load = useCallback(
    async (reset = false) => {
      try {
        setLoading(true);
        setError(null);

        const u = localStorage.getItem("user");
        if (!u) throw new Error("Aucun utilisateur connecté (localStorage user vide).");

        const user = JSON.parse(u);
        const userId = user.idUtilisateur ?? user.idutilisateur;
        if (!userId) throw new Error("id utilisateur introuvable dans localStorage.");

        const currentOffset = reset ? 0 : offset;

        const data = await getFeed(userId, currentOffset, limit);

        if (reset) {
          setItems(data);
          setOffset(limit);
          setHasMore(true);
        } else {
          setItems((prev) => [...prev, ...data]);
          setOffset((prev) => prev + limit);
        }

        if (!Array.isArray(data) || data.length < limit) setHasMore(false);
      } catch (e) {
        setError(e?.message || "Erreur inconnue");
      } finally {
        setLoading(false);
      }
    },
    [offset]
  );

  useEffect(() => {
    load(true);
  }, [load]);

  return (
    <div className="container mt-4">
      <h2>Recommandations</h2>

      {error && <div className="alert alert-danger">{error}</div>}

      {items.map((p) => (
        <div className="card mb-3" key={p.idPublication}>
          <div className="card-body">
            <div className="d-flex justify-content-between">
              <div>
                <h5 className="card-title mb-1">{p.contenuTexte}</h5>
                <small className="text-muted">Tag: {p.typePublication}</small>
              </div>
              <div className="text-end">
                <small className="text-muted">Score</small>
                <div style={{ fontWeight: "bold" }}>
                  {Number(p.score).toFixed(4)}
                </div>
              </div>
            </div>
          </div>
        </div>
      ))}

      {!loading && items.length === 0 && !error && (
        <div className="alert alert-info">Aucune recommandation.</div>
      )}

      {hasMore && (
        <button
          className="btn btn-outline-primary"
          onClick={() => load(false)}
          disabled={loading}
        >
          {loading ? "Chargement..." : "Charger plus"}
        </button>
      )}
    </div>
  );
}