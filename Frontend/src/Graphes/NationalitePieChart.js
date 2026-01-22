import React from "react";
import { ResponsiveContainer, PieChart, Pie, Tooltip, Cell } from "recharts";

const randomCouleur = () => `#${Math.random().toString(16).slice(2, 8).padEnd(6, "0")}`;
export default function NationalitePieChart({ data }) {
    return (
        <div className="nationalite" style={{ height: 320 }}>
            <h3> nationalités</h3>
            <ResponsiveContainer width="100%" height="100%"><PieChart>
                    <Tooltip /><Pie data={data} dataKey="value" nameKey="name" outerRadius={110} label>
                        {(data || []).map((e, i) => (
                            <Cell key={e.name} fill={randomCouleur()} />
                        ))}
                    </Pie>
                </PieChart>
            </ResponsiveContainer>
        </div>
    );
}
