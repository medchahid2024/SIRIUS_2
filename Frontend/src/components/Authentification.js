import React, { useState } from "react";
import { useNavigate} from "react-router-dom";
import  "../styles/Authentification.css";
import logo from "../assets/logo.jpeg";

export default function Authentification() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");
    const navigate = useNavigate();

    const submit = async (e) => {
        e.preventDefault();
        setError("");

        const body = new URLSearchParams();
        body.append("email", email);
        body.append("password", password);

        const res = await fetch("http://localhost:8080/MyUpec/utilisateur/login", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body,
        });

        if (res.status === 200) {
            const user = await res.json();
            localStorage.setItem("user", JSON.stringify(user));
            navigate("/Home");
        } else if (res.status === 401) {
            setError("Email ou mot de passe incorrect.");
        } else {
            setError("Erreur serveur.");
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
