import React from 'react';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import App from "./App";
import Navbar from "./Navbar";

import Profil from "./Profil";
export default function Router () {
    return (
        <BrowserRouter>
            <div>
                <Navbar />
                <Routes>
                    <Route path="/" element={<App />}/>
                    <Route path="/Profil" element={<Profil />} />
                </Routes>
            </div>
        </BrowserRouter>
    );
};