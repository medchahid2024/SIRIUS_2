import React from "react";
import "../styles/StyleGraph.css";
import {ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid,} from "recharts";
const randomCouleur = () => `#${Math.random().toString(16).slice(2, 8).padEnd(6, "0")}`;
export default function MeilleursAmisBarChart({ data }) {
    return (
        <div className="barchart" style={{ height: 350 }}>
            <h3>Meilleurs amis </h3>
            <ResponsiveContainer width="100%" height="100%">
                <BarChart data={data} layout="vertical" margin={{ left: 10, right: 30 }}>
                    <CartesianGrid strokeDasharray="3 3" /><Tooltip /><XAxis type="number" />
                    <YAxis type="category" dataKey="name" width={120} /> <Bar dataKey="value" fill={randomCouleur()} />
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
}
