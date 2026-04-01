
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


------------------------------"USE Case  2: Recommander des profils----------------------------------------------------
WI1: Script de peuplement PostgreSQL pour      
utilisateurs,profil, amis, publications 
et interactions.
WI:2 Section recommandés Index	
WI:3 profils recommandés
WI4: Algo recommandation
WI5: Ajouter en ami
WI6: Accepter amitié
-----------------------------------------------------------------------------------------------------------------------

------------------------------USE Case  2: Envoyer/Recevoir un message----------------------------------------------------

WI1: Tri automatique par dernier message
WI2: Recherche / filtrage par nomAffichage notification
WI3: Ouverture conversation au clic
WI4: Navigation vers boîte réception
-----------------------------------------------------------------------------------------------------------------------














||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||-



      Wi                                         |  Categorie | Statut | Description
WI1 Script de peuplement PostgreSQL pour 
utilisateurs ,profil, amis, publications     
et interactions.          |  GUI       | OK     |Depuis la fiche d’un utilisateur ou la liste d’amis, je clique sur Nouveau message et une nouvelle conversation vide s’ouvre avec ce contact
WI2  Création conversation backend               |  API+GUI   | OK     |Au clic sur « Nouveau message », une entrée est créée dans la table Conversation avec les deux participants, visible ensuite dans la base
WI3  Affichage dans la liste de conversations    |  GUI       | OK     | Quand la conversation est créée, elle apparaît en haut de ma boîte de réception avec le nom (photo facultatif) de l’autre utilisateur
WI4  Envoyer un message texte                    |  GUI       | OK     |Dans une conversation ouverte, je tape un message et je clique sur « Envoyer » , le message apparaît dans le fil
WI5  Réception du message                        |  GUI       | OK     |Quand l’autre utilisateur répond, son message apparaît en temps réel (websocket) ou après rafraîchissement de la page 




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

R2 - Recommandations basées sur profilage

US1 - Exploiter les informations du profil
On recupere la liste d'amis de l'utilisateur ainsi que ses centres d'intêrets

US2 - Ajouter au 1er systeme de recommandations une influence des tags des centres d'intêret profil

US3 - Exposition du fil V2

On repartit en 80/20 le % des publications affichées dans le fil par blocs de 10 avec 80% grâce au score des tags des publications avec lesquelles on a precedemment interagi et ceux du profil, en partageant selon les tops tags et 20% selon les publications avec lesquelles nos amis ont interagi.
