import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import Publication from "./Publication";
import { getFeed } from "../API/api";

jest.mock("../API/api", () => ({
  getFeed: jest.fn(),
}));

beforeEach(() => {
  localStorage.setItem(
    "user",
    JSON.stringify({
      idUtilisateur: 6,
      nom: "Zouak",
      prenom: "Abdessalam",
    })
  );

  getFeed.mockResolvedValue([
    {
      idPublication: 24138,
      contenuTexte: "Publication test avec image",
      typePublication: "PROGRAMMATION",
      dateCreation: "2026-03-05T08:07:53.393392Z",
      mediaURL: "https://picsum.photos/800/500",
      nbLikes: 5,
      nbCommentaires: 1,
      nbPartages: 0,
      commentaires: [],
    },
  ]);
});

afterEach(() => {
  localStorage.clear();
  jest.clearAllMocks();
});

test("affiche le titre du fil d'actualité", async () => {
  render(<Publication />);

  expect(await screen.findByText("Fil de recommandations")).toBeInTheDocument();
});

test("affiche une publication avec image et compteurs", async () => {
  render(<Publication />);

  const textes = await screen.findAllByText("Publication test avec image");
  expect(textes.length).toBeGreaterThan(0);

  expect(screen.getByText(/5\s*Likes/)).toBeInTheDocument();
  expect(screen.getByText(/1\s*Commentaires/)).toBeInTheDocument();
  expect(screen.getByText(/0\s*Partages/)).toBeInTheDocument();

  const image = screen.getByAltText("Publication test avec image");
  expect(image).toBeInTheDocument();
  expect(image).toHaveAttribute("src", "https://picsum.photos/800/500");
});

test("ouvre le bloc commentaires au clic", async () => {
  render(<Publication />);

  const boutonCommentaires = await screen.findByText(/1\s*Commentaires/);
  fireEvent.click(boutonCommentaires);

  expect(screen.getByText("Aucun commentaire pour le moment.")).toBeInTheDocument();
});