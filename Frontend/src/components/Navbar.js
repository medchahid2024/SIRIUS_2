import React, { useEffect, useState } from "react";
import { NavLink, Link, useNavigate } from "react-router-dom";
import logo from "../assets/logo.jpeg";
import "../styles/Navbar.css";
import { getTotalUnread } from "../API/api";

export default function Navbar() {
  const [user, setUser] = useState(null);
  const [unreadTotal, setUnreadTotal] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const u = localStorage.getItem("user");
    if (u) {
      setUser(JSON.parse(u));
    }
  }, []);

  useEffect(() => {
    if (!user?.idUtilisateur && !user?.idutilisateur) return;

    const userId = user.idUtilisateur ?? user.idutilisateur;

    const tick = async () => {
      try {
        const n = await getTotalUnread(userId);
        setUnreadTotal(Number(n) || 0);
      } catch {
        setUnreadTotal(0);
      }
    };

    tick();
    const id = setInterval(tick, 5000);

    return () => clearInterval(id);
  }, [user]);

  const goMessagerieInbox = () => {
    navigate(`/Messagerie?inbox=1&t=${Date.now()}`);
  };

  const logout = () => {
    localStorage.removeItem("user");
    navigate("/login");
  };

  return (
    <nav className="sirius-navbar">
      <div className="sirius-navbar-inner">
        <Link to="/Publication" className="sirius-logo-link">
          <img src={logo} alt="UPEC" className="sirius-logo" />
        </Link>


        <div className="sirius-links">
          <button type="button" onClick={goMessagerieInbox} className="sirius-nav-button">
            Messagerie
            {unreadTotal > 0 && <span className="sirius-badge">{unreadTotal}</span>}
          </button>

          <NavLink to="/Publication" className="sirius-nav-link">
            Publications
          </NavLink>

          <NavLink to="/Home" className="sirius-nav-link">
            Notifications
          </NavLink>

          <NavLink to="/Home" className="sirius-nav-link">
            About
          </NavLink>

          <NavLink to="/Profil" className="sirius-user-link">
            {user ? `${user.nom} ${user.prenom}` : "Profil"}
          </NavLink>

          <button className="sirius-logout" onClick={logout}>
            Déconnexion
          </button>
        </div>
      </div>
    </nav>
  );
}