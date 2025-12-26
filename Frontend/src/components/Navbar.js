import React from "react";
import { Link } from "react-router-dom";
import logo from "../assets/logo.jpeg";

export default function Navbar() {
    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-danger">
            <div className="container-fluid">
                <Link to="/" className="navbar-brand">
                    <img src={logo} width={100} height={60} alt="logo" />
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
                        <input className="form-control me-2" type="search" placeholder="Rechercher..." />
                        <button className="btn btn-outline-light" type="submit">Rechercher</button>
                    </form>
                         <Link to="/Profil" className="nav-link">
                    <span className="navbar-text text-white ms-3" >Chahid Mohammed</span></Link>
                </div>
            </div>
        </nav>
    );
}
