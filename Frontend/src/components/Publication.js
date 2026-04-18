import React, { useEffect, useState, useCallback } from "react";
import { getFeed } from "../API/api";
import "../styles/Publication.css";

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
          setItems(Array.isArray(data) ? data : []);
          setOffset(limit);
          setHasMore(true);
        } else {
          setItems((prev) => [...prev, ...(Array.isArray(data) ? data : [])]);
          setOffset((prev) => prev + limit);
        }

        if (!Array.isArray(data) || data.length < limit) {
          setHasMore(false);
        }
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

  const getMediaUrl = (url) => {
    if (!url || typeof url !== "string") return null;

    const trimmed = url.trim();
    if (!trimmed) return null;

    if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
      return trimmed;
    }

    return `http://localhost:8080${trimmed}`;
  };

  const formatDate = (dateString) => {
    if (!dateString) return "";

    const date = new Date(dateString);
    if (Number.isNaN(date.getTime())) return "";

    return date.toLocaleString("fr-FR", {
      day: "2-digit",
      month: "short",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const getPublicationTitle = (publication) => {
    const text = publication?.contenuTexte?.trim();

    if (!text) {
      return "Publication sans titre";
    }

    if (text.length <= 80) {
      return text;
    }

    return `${text.slice(0, 80)}...`;
  };

  return (
    <div className="publication-page">
      <div className="publication-feed-container">
        <div className="publication-feed-header">
          <h2 className="publication-feed-title">Fil de recommandations</h2>
          <p className="publication-feed-subtitle">
            Découvre les publications qui peuvent t’intéresser
          </p>
        </div>

        {error && <div className="alert alert-danger">{error}</div>}

        {items.map((p) => {
          const mediaUrl = getMediaUrl(p.mediaURL);

          return (
            <article className="social-post-card" key={p.idPublication}>
              <div className="social-post-header">
                <div className="social-post-user-block">
                  <div className="social-post-avatar">
                    {(p.typePublication || "P").charAt(0)}
                  </div>

                  <div className="social-post-meta">
                    <h5 className="social-post-title">{getPublicationTitle(p)}</h5>
                    <div className="social-post-date-row">
                      <span className="social-post-date">{formatDate(p.dateCreation)}</span>
                      <span className="social-post-dot">•</span>
                      <span className="social-post-tag">{p.typePublication}</span>
                    </div>
                  </div>
                </div>
              </div>

              {p.contenuTexte && (
                <div className="social-post-content">
                  <p className="social-post-text">{p.contenuTexte}</p>
                </div>
              )}

              {mediaUrl && (
                <div className="social-post-media-wrapper">
                  <img
                    src={mediaUrl}
                    alt={getPublicationTitle(p)}
                    className="social-post-media"
                    onError={(e) => {
                      e.target.style.display = "none";
                    }}
                  />
                </div>
              )}

              <div className="social-post-actions">
                <button type="button" className="social-action-btn">
                  <span className="social-action-icon">♡</span>
                  <span>Like</span>
                </button>

                <button type="button" className="social-action-btn">
                  <span className="social-action-icon">💬</span>
                  <span>Commentaire</span>
                </button>

                <button type="button" className="social-action-btn">
                  <span className="social-action-icon">↗</span>
                  <span>Partage</span>
                </button>
              </div>
            </article>
          );
        })}

        {!loading && items.length === 0 && !error && (
          <div className="alert alert-info">Aucune recommandation.</div>
        )}

        {hasMore && (
          <div className="publication-load-more-wrapper">
            <button
              className="btn btn-outline-primary publication-load-more-btn"
              onClick={() => load(false)}
              disabled={loading}
            >
              {loading ? "Chargement..." : "Voir plus"}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}