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
export function getConversations(userId) {
    return api.get(`/MyUpec/messagerie/conversations/${userId}`).then((r) => r.data);
}

export function createConversation(fromUserId, toUserId) {
    return api.post(`/MyUpec/messagerie/conversations`, { fromUserId, toUserId }).then((r) => r.data);
}

export function getMessages(conversationId, userId) {
    return api.get(`/MyUpec/messagerie/conversations/${conversationId}/messages`, { params: { userId } })
        .then((r) => r.data);
}

export function sendMessage(conversationId, senderId, contenu) {
    return api.post(`/MyUpec/messagerie/conversations/${conversationId}/messages`, { senderId, contenu })
        .then((r) => r.data);
}
export function markConversationRead(conversationId, userId) {
    return api.post(`/MyUpec/messagerie/conversations/${conversationId}/read`, { userId });
}

export function getTotalUnread(userId) {
    return api.get(`/MyUpec/messagerie/unread/${userId}`).then((r) => r.data);
}

export function getOnlineUsers() {
    return api.get(`/MyUpec/messagerie/presence/online`).then((r) => r.data);
}

export function createGroupe(nom, creatorId, participantIds) {
    return api.post(`/MyUpec/messagerie/groupes`, { nom, creatorId, participantIds }).then((r) => r.data);
}

export function ajouterMembre(convId, requesterId, userId) {
    return api.post(`/MyUpec/messagerie/groupes/${convId}/membres`, { requesterId, userId }).then((r) => r.data);
}

export function supprimerMembre(convId, requesterId, userId) {
    return api.delete(`/MyUpec/messagerie/groupes/${convId}/membres/${userId}`, { params: { requesterId } }).then((r) => r.data);
}

export function quitterGroupe(convId, userId) {
    return api.delete(`/MyUpec/messagerie/groupes/${convId}/quitter`, { params: { userId } }).then((r) => r.data);
}

export function sendFichier(convId, senderId, fichier) {
    const formData = new FormData();
    formData.append("senderId", senderId);
    formData.append("fichier", fichier);
    return api.post(`/MyUpec/messagerie/conversations/${convId}/fichiers`, formData, {
        headers: { "Content-Type": undefined }
    }).then((r) => r.data);
}
export function getProfil(userId) {
    return api.get(`/MyUpec/profil/monProfil/${userId}`).then((r) => r.data);

}
export function getSuggestionAmi(myId, amiId) {
    return api.get(`/MyUpec/ami/suggestion/${amiId}`, {params: { myId },}).then((r) => r.data);
}
export function envoyerDemandeAmi(myId, amiId) {
    return api.post("/MyUpec/ami/envoyer", null, {params: { myId, amiId },}).then((r) => r.data);
}
export function getStatutRelation(myId, amiId) {
    return api.get("/MyUpec/ami/statut", {params: { myId, amiId },}).then((r) => r.data); }

export function accepterDemandeAmi(myId, amiId) {
    return api.post("/MyUpec/ami/accepter", null, { params: { myId, amiId } }).then((r) => r.data);
}

export function getFeed(userId, offset = 0, limit = 10) {
    return api
        .get(`/api/feed/${userId}`, { params: { offset, limit } })
        .then((r) => r.data);
}
export function getStatsActivite(userId, annee, mois) {
    return api.get(`/api/stats/activite/${userId}`, { params: { annee, mois } }).then((r) => r.data);

}