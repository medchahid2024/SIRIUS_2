
|||||||   ||||||||
||   ||         ||
|||||||   ||||||||
||  ||          ||
||   ||         ||
||    ||  ||||||||

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
