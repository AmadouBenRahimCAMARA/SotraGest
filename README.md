# SotraGest - Logiciel de Gestion de Société de Transport

SotraGest est une application de bureau développée en Java par Amadou Ben Rahim CAMARA pour la gestion complète des opérations d'une compagnie de transport par bus. Elle permet de gérer les bus, les chauffeurs, les trajets, les voyageurs, et la vente de tickets.

## Fonctionnalités

L'application inclut les modules de gestion suivants :
- **Tableau de bord** : Vue d'ensemble de l'activité.
- **Gestion des Ventes** : Vente de tickets et suivi.
- **Gestion des Trajets** : Création et administration des itinéraires.
- **Gestion des Bus** : Suivi de la flotte de véhicules.
- **Gestion des Chauffeurs** : Administration du personnel de conduite.
- **Gestion des Agents** : Gestion des utilisateurs de type "agent".
- **Gestion des Voyageurs** : Enregistrement et suivi des clients.
- **Historique et Statistiques** : Consultation de l'historique des ventes et statistiques de performance.
- **Génération de Tickets** : Création de tickets de transport au format PDF.

## Technologies Utilisées

*   **Langage** : Java 17
*   **Interface Graphique** : JavaFX
*   **Gestion de projet et dépendances** : Apache Maven
*   **Base de données** : PostgreSQL
*   **Génération PDF** : iText7
*   **Hashage de mots de passe** : jBCrypt

## Prérequis

Avant de commencer, assurez-vous d'avoir installé les logiciels suivants sur votre machine :
*   [JDK (Java Development Kit)](https://www.oracle.com/java/technologies/downloads/) - Version 17 ou supérieure.
*   [Apache Maven](https://maven.apache.org/download.cgi) - Pour compiler et exécuter le projet.
*   [PostgreSQL](https://www.postgresql.org/download/) - Le système de gestion de base de données.

## Installation et Configuration

1.  **Cloner le dépôt** (si applicable) ou décompresser l'archive du projet.

2.  **Configurer la base de données** :
    *   Lancez PostgreSQL.
    *   Créez une nouvelle base de données nommée `sotragest`.
    *   Assurez-vous d'avoir un utilisateur (par exemple `postgres`) ayant les droits sur cette base.
    *   Modifiez le fichier `src/main/java/com/sotragest/configuration/ConfigurationBaseDonnees.java` pour y mettre à jour le nom d'utilisateur et le mot de passe de votre base de données si ceux-ci diffèrent des valeurs par défaut.

## Compilation et Exécution

Ouvrez un terminal ou une invite de commande et naviguez jusqu'à la racine du projet.

1.  **Compiler le projet** :
    Cette commande va nettoyer le projet, télécharger les dépendances et compiler les sources.
    ```bash
    mvn clean compile
    ```

2.  **Exécuter l'application** :
    Une fois la compilation réussie, lancez l'application avec la commande suivante.
    ```bash
    mvn exec:java -Dexec.mainClass=com.sotragest.SotraGestApplication
    ```

L'application devrait se lancer.
