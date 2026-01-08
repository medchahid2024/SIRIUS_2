import React, {useEffect, useState} from "react";
import { Link } from "react-router-dom";
import logo from "../assets/logo.jpeg";
import  "../styles/Home.css";

export default function Navbar() {
    const [user, setUser] = useState(null);
    useEffect(() => {
        const u = localStorage.getItem("user");
        if (u) setUser(JSON.parse(u));
    }, []);

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-danger">
            <div className="container-fluid">
                <Link to="/Home" className="navbar-brand">
                    <img src={logo} width={100} height={60} alt="logo"/>
                </Link>

                <button
                    className="navbar-toggler"
                    type="button"
                    data-bs-toggle="collapse"
                    data-bs-target="#navbarSupportedContent"
                    aria-controls="navbarSupportedContent"
                    aria-expanded="false"
                    aria-label="Toggle navigation"
                >
                    <span className="navbar-toggler-icon"></span>
                </button>


                <div className="collapse navbar-collapse" id="navbarSupportedContent">
                    <form className="d-flex ms-auto" role="search">
                        <input className="form-control me-2" type="search" placeholder="Rechercher..."/>
                        <button className="btn btn-outline-light" type="submit">Rechercher</button>
                    </form>

                </div>
                <div className="navbar-right">
                    <a className="navbar-brand" href="/Home">messagerie</a>
                    <a className="navbar-brand" href="/Publication">publication</a>
                    <a className="navbar-brand" href="/Home">notification</a>
                    <a className="navbar-brand" href="/Home">about</a>

                    <Link to="/Profil" className="user-name">
                        {user ? `${user.nom} ${user.prenom}` : "Profil"}                  </Link>
                </div>
            </div>
        </nav>
    );
}
