import api from "./apiClient";

export function getStatsSexe(userId) {
    return api.get(`/MyUpec/ami/statistiques/pourcentage/${userId}`).then((r) => r.data);
}

export function getStatsNationalite(userId) {
    return api.get(`/MyUpec/ami/statistiques/nationalite/${userId}`).then((r) => r.data);
}

export function getMeilleursAmis(userId) {
    return api.get(`mesAmis/Meilleures/${userId}`).then((r) => r.data);
}
