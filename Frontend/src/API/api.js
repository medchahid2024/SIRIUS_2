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
export function getProfil(userId) {
    return api.get(`/MyUpec/profil/monProfil/${userId}`).then((r) => r.data);


}
export function getSuggestionAmi(myId, amiId) {
    return api.get(`/MyUpec/ami/suggestion/${amiId}`, {params: { myId },}).then((r) => r.data);
}