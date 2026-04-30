import React from "react";
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import Navbar from "./Navbar";
import Profil from "./Profil";
import Authentification from "./Authentification";
import Home from "./Home";
import Statistiques from "./Statistiques";
import Publication from "./Publication";
import Messagerie from "./Messagerie";
import ProfilAmi from "./ProfilAmi";
import HistoriqueActivite from "./HistoriqueActivite";

function DefaultRoute() {
  const user = localStorage.getItem("user");
  return user ? <Navigate to="/Publication" replace /> : <Navigate to="/login" replace />;
}

function AppLayout() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<DefaultRoute />} />
        <Route path="/Home" element={<Home />} />
        <Route path="/Profil" element={<Profil />} />
        <Route path="/Publication" element={<Publication />} />
        <Route path="/Statistiques/:id" element={<Statistiques />} />
        <Route path="/Messagerie" element={<Messagerie />} />
        <Route path="/ProfilAmi" element={<ProfilAmi />} />
      </Routes>
    </>
  );
}

export default function Router() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Authentification />} />
        <Route path="/*" element={<AppLayout />} />
      </Routes>
    </BrowserRouter>
  );
}
