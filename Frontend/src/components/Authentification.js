import React, { useState } from "react";
import { useNavigate} from "react-router-dom";
import  "../styles/Authentification.css";
import logo from "../assets/logo.jpeg";
import {login} from "../API/api";


export default function Authentification() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const submit = async (e) => {
        e.preventDefault();
        setError("");
        try {
            const user = await login(email, password);localStorage.setItem("user", JSON.stringify(user));
            navigate("/Home");
        } catch (err) {
             if (err.response?.status === 401) setError("Email ou mot de passe incorrect.");
            else setError("Erreur serveur.");
        }
    };

    return (
        <div className="authentification">
            <div className="auth-box">

                    <img src={logo} className="episen" width={170} height={100} alt="logo"/>
                <h2>Se connecter </h2>
        <form onSubmit={submit} className="form">
            <input
                type="text"
                placeholder="Entrez votre email"
                className="input-group"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
            />
            <input
                type="password"
                className="input-group"
                placeholder="Entrez votre mot de passe"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <button type="submit">Login</button>
            {error && <div style={{ color: "black" }}>{error}</div>}
        </form>
        </div>
        </div>
    );
}
