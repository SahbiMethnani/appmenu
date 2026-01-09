Feature: Gestion des commandes

  Scenario: Un client passe une commande
    Given le menu contient des plats
    When le client passe une commande pour la table 5
    Then la commande est créée avec le statut "en_attente"
