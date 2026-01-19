import React from "react";
import { ResponsiveContainer, PieChart, Pie, Tooltip, Cell } from "recharts";

const randomCouleur = () => `#${Math.random().toString(16).slice(2, 8).padEnd(6, "0")}`;
export default function SexePieChart({ data }) {
    return (
        <div className="sexe" style={{ height: 320 }}>
            <h3>Répartition sexe</h3>
            <ResponsiveContainer width="100%" height="100%"><PieChart>
                    <Tooltip />
                <Pie data={data} dataKey="value" nameKey="name" outerRadius={110} label>{(data || []).map((e, i) => (
                    <Cell key={e.name} fill={randomCouleur()} />
                        ))}
                </Pie>
                </PieChart>
            </ResponsiveContainer>
        </div>
    );
}
