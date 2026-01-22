import api from "./apiClient";

export function getStatsSexe(userId) {
    return api.get(`/MyUpec/ami/statistiques/pourcentage/${userId}`).then((r) => r.data);
}

export function getStatsNationalite(userId) {
    return api.get(`/MyUpec/ami/statistiques/nationalite/${userId}`).then((r) => r.data);
}
export function getMesAmis(userId) {
    return api.get(`/MyUpec/ami/mesAmis/${userId}`).then((r) => r.data);
}
export function getMeilleursAmis(userId) {
    return api.get(`mesAmis/Meilleures/${userId}`).then((r) => r.data);
}
export function login(email, password) {
    const body = new URLSearchParams();
    body.append("email", email);
    body.append("password", password);

    return api.post("/MyUpec/utilisateur/login", body, {
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
        })
        .then((r) => r.data);

}