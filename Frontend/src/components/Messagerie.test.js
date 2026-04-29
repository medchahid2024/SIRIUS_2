import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { MemoryRouter } from "react-router-dom";
import Messagerie from "./Messagerie";
import {
    getConversations,
    getOnlineUsers,
    markConversationRead,
} from "../API/api";

jest.mock("../API/api", () => ({
    getConversations: jest.fn(),
    getMessages: jest.fn(),
    getOnlineUsers: jest.fn(),
    markConversationRead: jest.fn(),
    sendMessage: jest.fn(),
    createConversation: jest.fn(),
    getMesAmis: jest.fn(),
    createGroupe: jest.fn(),
    ajouterMembre: jest.fn(),
    supprimerMembre: jest.fn(),
    quitterGroupe: jest.fn(),
    sendFichier: jest.fn(),
}));

jest.mock("../API/apiClient", () => ({
    defaults: { baseURL: "http://localhost:8080" },
}));

jest.mock("sockjs-client", () => function () { return {}; });
jest.mock("@stomp/stompjs", () => ({
    Client: class {
        activate() {}
        deactivate() {}
        publish() {}
        subscribe() { return { unsubscribe: () => {} }; }
        get connected() { return false; }
    },
}));

beforeEach(() => {
    localStorage.setItem("user", JSON.stringify({
        idUtilisateur: 1,
        nom: "Hossame",
        prenom: "Arsalane",
    }));
    getConversations.mockResolvedValue([]);
    getOnlineUsers.mockResolvedValue([]);
    markConversationRead.mockResolvedValue({});
});

afterEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
});

test("affiche le message quand aucune conversation n'est sélectionnée", async () => {
    render(
        <MemoryRouter>
            <Messagerie />
        </MemoryRouter>
    );
    expect(await screen.findByText(/Sélectionne une conversation/i)).toBeInTheDocument();
});

test("affiche les conversations dans la liste", async () => {
    getConversations.mockResolvedValue([
        {
            idConversation: 1,
            isGroupe: false,
            other: { idUtilisateur: 2, nom: "Dupont", prenom: "Marie" },
            lastMessage: "Bonjour",
            lastSenderId: 2,
            lastSenderPrenom: "Marie",
            unreadCount: 0,
        },
    ]);

    render(
        <MemoryRouter>
            <Messagerie />
        </MemoryRouter>
    );

    expect(await screen.findByText("Dupont Marie")).toBeInTheDocument();
});

test("filtre les conversations selon la recherche", async () => {
    getConversations.mockResolvedValue([
        {
            idConversation: 1,
            isGroupe: false,
            other: { idUtilisateur: 2, nom: "Dupont", prenom: "Marie" },
            lastMessage: "Bonjour",
            lastSenderId: 2,
            lastSenderPrenom: "Marie",
            unreadCount: 0,
        },
        {
            idConversation: 2,
            isGroupe: false,
            other: { idUtilisateur: 3, nom: "Martin", prenom: "Paul" },
            lastMessage: "Salut",
            lastSenderId: 3,
            lastSenderPrenom: "Paul",
            unreadCount: 1,
        },
    ]);

    render(
        <MemoryRouter>
            <Messagerie />
        </MemoryRouter>
    );

    await screen.findByText("Dupont Marie");

    const input = screen.getByPlaceholderText("Rechercher un profil");
    fireEvent.change(input, { target: { value: "Dupont" } });

    expect(screen.getByText("Dupont Marie")).toBeInTheDocument();
    expect(screen.queryByText("Martin Paul")).not.toBeInTheDocument();
});
