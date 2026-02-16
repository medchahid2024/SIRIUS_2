import React, { useEffect, useMemo, useState } from "react";
import SexePieChart from "../Graphes/SexePieChart";
import NationalitePieChart from "../Graphes/NationalitePieChart";
import MeilleursAmisBarChart from "../Graphes/MeilleursAmisBarChart";
import {getStatsSexe, getStatsNationalite, getMeilleursAmis,} from "../API/api";
import "../styles/StyleGraph.css";


export default function Statistiques() {
    const [user, setUser] = useState(null);
    const [sexeData, setSexeData] = useState([]);
    const [natData, setNatData] = useState([]);
    const [scoreData, setScoreData] = useState([]);
    const userId = useMemo(() => user?.idUtilisateur ?? null, [user]);

    useEffect(() => {
        const u = localStorage.getItem("user");
        if (u) setUser(JSON.parse(u));
        else setUser(null);
    }, []);
    useEffect(() => {if (!userId) return;
        Promise.all([getStatsSexe(userId), getStatsNationalite(userId), getMeilleursAmis(userId),])
            .then(([sexeRes, natRes, scoreRes]) => {setSexeData([
                { name: "Masculin", value: sexeRes.pctMasculin },
                { name: "Féminin", value: sexeRes.pctFeminin },
                { name: "pas rempli", value: sexeRes?.pctInconnu  },
            ]);
                setNatData((natRes ?? []).map((x) => ({ name: x.nationalite, value: x.nb })));
                setScoreData(
                    (scoreRes ?? []).map((x) => ({ name: `Ami ${x.id}`,
                        value: x.score ,
                    }))
                );
            });
    }, [userId]);


    return (<div  className="row" style={{ padding: 20, display: "grid", gap: 28 }}>
            <h2>Statistiques</h2>
            <SexePieChart data={sexeData} />
            <NationalitePieChart data={natData} />
            <MeilleursAmisBarChart data={scoreData} />
        </div>
    );
}
