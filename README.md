# Gestion de Bibliothèque Municipale - JavaFX

Ce projet est une application desktop de gestion pour une bibliothèque municipale, développée en JavaFX avec une architecture MVC, utilisant Hibernate pour la persistance des données et MySQL comme base de données.

## 1. Prérequis Système

Avant de commencer, assurez-vous d'avoir les éléments suivants installés sur votre machine :

- **Système d'exploitation** : Windows (pour l'installateur EXE), Linux ou macOS (pour le développement).
- **Java Development Kit (JDK)** : Version 19 ou supérieure (recommandé : JDK 19 avec `JAVA_HOME` configuré).
- **Maven** : Version 3.9.0 ou supérieure.
- **Base de données** : MySQL Server 8.x.
- **Outils de Packaging (Optionnel)** : Inno Setup 6+ (pour générer l'installateur Windows).

## 2. Configuration de la Base de Données

L'application utilise Hibernate (JPA) pour gérer la base de données. Par défaut, elle se connecte à un serveur MySQL local.

1.  **Démarrer MySQL** et créer la base de données :
    ```sql
    CREATE DATABASE IF NOT EXISTS biblio CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
    ```
2.  **Vérifier la configuration** dans le fichier `src/main/resources/META-INF/persistence.xml` :
    - **URL** : `jdbc:mysql://localhost:3306/biblio`
    - **User** : `root`
    - **Password** : (par défaut vide)

*Note : Hibernate est configuré en mode `update`, il créera automatiquement les tables nécessaires lors du premier lancement.*

## 3. Identifiants par Défaut

Lors du premier lancement de l'application, un compte administrateur est créé automatiquement si aucun utilisateur n'existe :

- **Login** : `admin`
- **Mot de passe** : `admin123`

*Il est fortement recommandé de changer ce mot de passe ou de créer un nouvel administrateur après la première connexion.*

## 4. Procédure d'Installation et Build

### Développement (Lancer depuis l'IDE)
1.  Compiler le projet : `mvn clean install`
2.  Lancer via Maven : `mvn javafx:run`

### Génération de l'Exécutable (Fat JAR)
Pour créer un JAR unique contenant toutes les dépendances (JavaFX, Hibernate, Drivers MySQL) :
```bash
mvn clean package
```
Le fichier généré sera : `target/biblio-municipale.jar`.

### Création de l'Installateur Windows (EXE)
Le projet est configuré pour fonctionner avec **Inno Setup**.
1.  Générer le Fat JAR avec la commande ci-dessus.
2.  Ouvrir votre script Inno Setup (`.iss`) et pointer vers `target/biblio-municipale.jar`.
3.  Compiler le script pour obtenir l'installateur final.

## 5. Guide d'Utilisation Rapide

L'application est divisée en plusieurs modules accessibles via le tableau de bord principal :

### 🔐 Authentification
- Connectez-vous avec vos identifiants. 
- Les administrateurs ont un accès complet, tandis que les bibliothécaires ont des permissions limitées.

### 📊 Tableau de Bord
- Visualisez les statistiques en temps réel : nombre total de livres, adhérents actifs, emprunts en cours et retards.

### 📚 Gestion des Livres
- **Livres** : Ajouter, modifier, supprimer ou rechercher des ouvrages par titre, auteur ou ISBN.
- **Catégories** : Organiser vos livres par thématiques (Roman, Science, Sport, etc.).

### 👥 Gestion des Adhérents
- Enregistrer de nouveaux membres de la bibliothèque.
- Consulter l'historique des emprunts pour chaque adhérent.

### 🔄 Emprunts et Retours
- **Emprunter** : Associer un livre disponible à un adhérent.
- **Retourner** : Enregistrer le retour d'un livre et calculer automatiquement les éventuelles pénalités de retard.
- **Historique** : Consulter tous les mouvements passés.

### ⚙️ Administration (Admin uniquement)
- **Utilisateurs** : Gérer les comptes des employés (création, activation/désactivation).
- **Paramètres** : Configuration globale de l'application.

---
*Développé dans le cadre d'un mini-projet JavaFX.*
