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

-----------------USE Case  3: Afficher les statistiques d’historique d’activité---------------------
					
US1 Stats activité : mise à jour à la connexion|
US2 API stats mensuelles (mois courant)        |
US3 Dashboard “Historique d’activité”          |
US4 Filtre mois précédent                      |
---------------------------------------------------------------------------------------------------
            Description
---------------------------------------------------------------------------------------------------
À chaque login réussi, le backend enregistre un
 événement « LOGIN » et met à jour les compteurs 
 du mois en cours(nbConnexions). 
 L’endpoint GET renvoie ensuite un JSON agrégé pour l’utilisateur connecté. 
 Quand je clique sur l’onglet « Historique d’activité », je vois les compteurs du mois 
 (connexions, publications, interactions, etc.) sous forme de graphes.
  Si je change de mois via le sélecteur, le front rappelle GET /api/stats/activite?month=... 
  et l’affichage se met à jour sans recharger la page.
---------------------------------------------------------------------------------------