===============================================Chahid Mohammed ==================================================================================================================
=============================================== Uc1 Profil===============================================================================================

      Wi                                         |  Categorie | Statut | Description
===================================================================================================================================================================================
WI1  Auth: login email/mdp                       |  GUI       | OK     |Je vais sur la page de connexion, je saisis mon email et mon mot de passe, puis je valide. Si tout est correct, j’arrive sur la page d’accueil (Index). Si je me trompe, un message d’erreur s’affiche.
WI2  Page Profil: données compte                 |  GUI       | OK     |Après la connexion, je vois la page Index avec le menu et les boutons pour accéder au Profil et aux Statistiques. Je peux naviguer sans recharger la page.
WI3  Profil: compteur amis + liste               |  GUI       | OK     |Sur la page Profil, je vois le nombre total d’amis. Je clique sur ce nombre et la liste de mes amis s’affiche avec leurs informations de bas
WI4  stats: sexe                                 |  Algorithme| OK     |L'endpoint /api/stats/sexe retourne la répartition H/F en JSON.
WI5  meilleurs amis                              |  Algorithme| OK     |L'endpoint /api/amis/meilleurs calcule et retourne le TOP 10 amis avec leurs scores (interactions + établissement + ancienneté)
WI6  Dashboard stats                             |  GUI       | OK     |Je clique sur "Statistiques" et je vois plusieurs tableaux de bord. Les données viennent des APIs /stats/sexe et /amis/meilleurs

ZOUAK HAMZAOUI Abdessalam - Scope fil d'actualité

R1 — Recommandation basée sur les interactions (US réalisées)

Les fonctionnalités suivantes ont été implémentées pour le module Fil d’actualité personnalisé [R1] :

US1 - Exploiter l’historique d’interactions utilisateur
Prise en compte des interactions passées de l’utilisateur (likes, commentaires, consultations) pour influencer la recommandation des publications.

US2 - Calcul du score de similarité d’un post
Calcul d’un score de recommandation pour chaque publication en fonction de la similarité avec les contenus déjà appréciés ou consultés par l’utilisateur.

US3 - Pondération des interactions
Les différents types d’interactions sont pondérés (ex. : un like a plus d’impact qu’une simple consultation).

US4 - Classement des publications
Les publications sont triées par ordre décroissant selon leur score de pertinence calculé.

US5 - Exposition du fil personnalisé
Le fil d’actualité affiche en priorité les publications les plus pertinentes pour l’utilisateur, avec chargement progressif (pagination).