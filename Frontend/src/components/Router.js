import React from "react";
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import App from "./App";
import Navbar from "./Navbar";
import Profil from "./Profil";
import Authentification from "./Authentification";
import Home from "./Home";

export default function Router() {
    return (
        <BrowserRouter>
            <div>
                <Routes><Route path="/login" element={<Authentification />} />
      <Route path="/*" element={<><Navbar />
  <Routes>
 <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/Home" element={<Home />} />
   <Route path="/Profil" element={<Profil />} />
    <Route path="/app" element={<App />} />
  </Routes>
                            </>
                           }
                    />
                </Routes>
            </div>
        </BrowserRouter>
    );
}
