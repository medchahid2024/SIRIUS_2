import React from "react";
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import Navbar from "./Navbar";
import Profil from "./Profil";
import Authentification from "./Authentification";
import Home from "./Home";
import Statistiques from "./Statistiques";
import Publication from "./Publication";

export default function Router() {
  return (
    <BrowserRouter>
      <div>
        <Routes>
          <Route path="/login" element={<Authentification />} />
          <Route
            path="/*"
            element={
              <>
                <Navbar />
                <Routes>
                  <Route path="/" element={<Navigate to="/login" replace />} />
                  <Route path="/Home" element={<Home />} />
                  <Route path="/Profil" element={<Profil />} />
                  <Route path="/Publication" element={<Publication />} />
                  <Route path="/Statistiques/:id" element={<Statistiques />} />
                </Routes>
              </>
            }
          />
        </Routes>
      </div>
    </BrowserRouter>
  );
}
