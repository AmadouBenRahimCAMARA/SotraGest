package com.sotragest.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import org.mindrot.jbcrypt.BCrypt;


// Configuration et initialisation de la base de données PostgreSQL

public class ConfigurationBaseDonnees {
    private static final String URL_BD = "jdbc:postgresql://localhost:5432/sotragest";
    private static final String UTILISATEUR_BD = "postgres";
    private static final String MOT_DE_PASSE_BD = "rahim";

    public static Connection obtenirConnexion() throws SQLException {
        return DriverManager.getConnection(URL_BD, UTILISATEUR_BD, MOT_DE_PASSE_BD);
    }

    public static void initialiserBaseDonnees() {
        try (Connection connexion = obtenirConnexion();
             Statement statement = connexion.createStatement()) {

            // Création de la table utilisateurs (base pour gérants et agents)
            statement.execute("""
                CREATE TABLE IF NOT EXISTS utilisateurs (
                    id_utilisateur SERIAL PRIMARY KEY,
                    identifiant VARCHAR(50) UNIQUE NOT NULL,
                    mot_de_passe VARCHAR(255) NOT NULL,
                    nom_utilisateur VARCHAR(100) NOT NULL,
                    prenom_utilisateur VARCHAR(100) NOT NULL,
                    telephone_utilisateur VARCHAR(20),
                    email_utilisateur VARCHAR(100),
                    type_utilisateur VARCHAR(20) NOT NULL CHECK (type_utilisateur IN ('GERANT', 'AGENT')),
                    niveau_acces VARCHAR(20) DEFAULT 'RESTREINT',
                    poste_travail VARCHAR(100),
                    date_creation_utilisateur TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    actif_utilisateur BOOLEAN DEFAULT TRUE,
                    nom_compagnie VARCHAR(100)
                )
                """);

            // Création de la table voyageurs
            statement.execute("""
                CREATE TABLE IF NOT EXISTS voyageurs (
                    id_voyageur SERIAL PRIMARY KEY,
                    nom_voyageur VARCHAR(100) NOT NULL,
                    prenom_voyageur VARCHAR(100) NOT NULL,
                    telephone_voyageur VARCHAR(20) NOT NULL,
                    email_voyageur VARCHAR(100),
                    date_naissance_voyageur DATE,
                    adresse_voyageur TEXT,
                    piece_identite VARCHAR(50),
                    numero_piece VARCHAR(50),
                    date_inscription TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

            // Création de la table chauffeurs
            statement.execute("""
                CREATE TABLE IF NOT EXISTS chauffeurs (
                    id_chauffeur SERIAL PRIMARY KEY,
                    nom_chauffeur VARCHAR(100) NOT NULL,
                    prenom_chauffeur VARCHAR(100) NOT NULL,
                    telephone_chauffeur VARCHAR(20) NOT NULL,
                    email_chauffeur VARCHAR(100),
                    date_naissance_chauffeur DATE,
                    adresse_chauffeur TEXT,
                    numero_permis VARCHAR(50) UNIQUE NOT NULL,
                    date_obtention_permis DATE,
                    date_expiration_permis DATE,
                    categorie_permis VARCHAR(10),
                    date_embauche TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    salaire DECIMAL(10,2),
                    actif_chauffeur BOOLEAN DEFAULT TRUE
                )
                """);

            // Création de la table bus
            statement.execute("""
                CREATE TABLE IF NOT EXISTS bus (
                    id_bus SERIAL PRIMARY KEY,
                    matricule VARCHAR(20) UNIQUE NOT NULL,
                    marque VARCHAR(50) NOT NULL,
                    modele VARCHAR(50) NOT NULL,
                    annee INTEGER,
                    capacite INTEGER NOT NULL,
                    etat VARCHAR(20) DEFAULT 'BON' CHECK (etat IN ('EXCELLENT', 'BON', 'MOYEN', 'MAUVAIS', 'HORS_SERVICE')),
                    kilometrage DECIMAL(10,2) DEFAULT 0,
                    date_achat TIMESTAMP,
                    dernier_entretien TIMESTAMP,
                    prochain_entretien TIMESTAMP,
                    couleur VARCHAR(30),
                    climatisation BOOLEAN DEFAULT FALSE,
                    actif_bus BOOLEAN DEFAULT TRUE
                )
                """);

            // Création de la table trajets
            statement.execute("""
                CREATE TABLE IF NOT EXISTS trajets (
                    id_trajet SERIAL PRIMARY KEY,
                    ville_depart VARCHAR(100) NOT NULL,
                    ville_arrivee VARCHAR(100) NOT NULL,
                    date_heure_depart TIMESTAMP NOT NULL,
                    date_heure_arrivee_estimee TIMESTAMP,
                    date_heure_arrivee_reelle TIMESTAMP,
                    bus_id INTEGER REFERENCES bus(id_bus),
                    chauffeur_id INTEGER REFERENCES chauffeurs(id_chauffeur),
                    prix DECIMAL(10,2) NOT NULL,
                    nombre_places_disponibles INTEGER NOT NULL,
                    statut_trajet VARCHAR(20) DEFAULT 'PROGRAMME' CHECK (statut_trajet IN ('PROGRAMME', 'EN_COURS', 'TERMINE', 'ANNULE', 'REPORTE')),
                    commentaires TEXT,
                    date_creation_trajet TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """);

            // Création de la table tickets
            statement.execute("""
                CREATE TABLE IF NOT EXISTS tickets (
                    id_ticket SERIAL PRIMARY KEY,
                    numero_ticket VARCHAR(50) UNIQUE NOT NULL,
                    voyageur_id INTEGER REFERENCES voyageurs(id_voyageur),
                    trajet_id INTEGER REFERENCES trajets(id_trajet),
                    numero_siege INTEGER,
                    prix DECIMAL(10,2) NOT NULL,
                    statut_ticket VARCHAR(20) DEFAULT 'VALIDE' CHECK (statut_ticket IN ('VALIDE', 'UTILISE', 'ANNULE', 'EXPIRE')),
                    date_vente TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    date_utilisation TIMESTAMP,
                    vendeur_id INTEGER REFERENCES utilisateurs(id_utilisateur),
                    code_qr VARCHAR(100),
                    observations_ticket TEXT
                )
                """);

            // Vérifier et corriger l'utilisateur admin
            corrigerUtilisateurAdmin(connexion);

            // Insertion de données de test
            statement.execute("""
                INSERT INTO voyageurs (nom_voyageur, prenom_voyageur, telephone_voyageur, email_voyageur, date_naissance_voyageur, adresse_voyageur, piece_identite, numero_piece)
                SELECT 'Kabore', 'Jean', '07234567', 'jean.kabore@email.com', '1985-03-15', 'Ouagadougou, Centre', 'CNIB', 'B123456789'
                WHERE NOT EXISTS (SELECT 1 FROM voyageurs WHERE telephone_voyageur = '07234567')
                """);

            statement.execute("""
                INSERT INTO voyageurs (nom_voyageur, prenom_voyageur, telephone_voyageur, email_voyageur, date_naissance_voyageur, adresse_voyageur, piece_identite, numero_piece)
                SELECT 'Kone', 'Marie', '07345678', 'marie.kone@email.com', '1990-07-22', 'Bobo-Dioulasso, Hauts-Bassins', 'CNIB', 'B987654321'
                WHERE NOT EXISTS (SELECT 1 FROM voyageurs WHERE telephone_voyageur = '07345678')
                """);

            statement.execute("""
                INSERT INTO chauffeurs (nom_chauffeur, prenom_chauffeur, telephone_chauffeur, email_chauffeur, date_naissance_chauffeur, adresse_chauffeur, numero_permis, date_obtention_permis, date_expiration_permis, categorie_permis, salaire)
                SELECT 'Traoré', 'Ibrahim', '07456789', 'ibrahim.traore@email.com', '1980-05-10', 'Banfora, Cascades', 'PERM001', '2010-01-15', '2025-10-15', 'D', 150000
                WHERE NOT EXISTS (SELECT 1 FROM chauffeurs WHERE numero_permis = 'PERM001')
                """);

            statement.execute("""
                INSERT INTO chauffeurs (nom_chauffeur, prenom_chauffeur, telephone_chauffeur, email_chauffeur, date_naissance_chauffeur, adresse_chauffeur, numero_permis, date_obtention_permis, date_expiration_permis, categorie_permis, salaire)
                SELECT 'Diallo', 'Mamadou', '07567890', 'mamadou.diallo@email.com', '1975-12-03', 'Dori, Sahel', 'PERM002', '2008-06-20', '2025-12-20', 'D', 160000
                WHERE NOT EXISTS (SELECT 1 FROM chauffeurs WHERE numero_permis = 'PERM002')
                """);

            statement.execute("""
                INSERT INTO bus (matricule, marque, modele, annee, capacite, couleur, climatisation, etat)
                SELECT 'AA-001-BF', 'Mercedes', 'Sprinter', 2020, 25, 'Blanc', true, 'EXCELLENT'
                WHERE NOT EXISTS (SELECT 1 FROM bus WHERE matricule = 'AA-001-BF')
                """);

            statement.execute("""
                INSERT INTO bus (matricule, marque, modele, annee, capacite, couleur, climatisation, etat)
                SELECT 'AA-002-BF', 'Iveco', 'Daily', 2019, 30, 'Bleu', true, 'BON'
                WHERE NOT EXISTS (SELECT 1 FROM bus WHERE matricule = 'AA-002-BF')
                """);

            System.out.println("✅ Base de données initialisée avec succès!");
            System.out.println("🔑 Identifiants par défaut : admin / admin");

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'initialisation de la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void corrigerUtilisateurAdmin(Connection connexion) throws SQLException {
        // Vérifier si l'utilisateur admin existe
        try (Statement stmt = connexion.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateurs WHERE identifiant = 'admin'");
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("🔄 Utilisateur admin trouvé - mise à jour du mot de passe...");
                
                // Générer un nouveau hash pour "admin"
                String nouveauHash = BCrypt.hashpw("admin", BCrypt.gensalt(12));
                System.out.println("🔐 Nouveau hash généré : " + nouveauHash.substring(0, 15) + "...");
                
                // Mettre à jour le mot de passe
                try (var updateStmt = connexion.prepareStatement(
                    "UPDATE utilisateurs SET mot_de_passe = ? WHERE identifiant = 'admin'")) {
                    updateStmt.setString(1, nouveauHash);
                    int updated = updateStmt.executeUpdate();
                    
                    if (updated > 0) {
                        System.out.println("✅ Mot de passe admin mis à jour avec succès!");
                        
                        // Test de vérification
                        boolean testHash = BCrypt.checkpw("admin", nouveauHash);
                        System.out.println("🧪 Test de vérification : " + (testHash ? "✅ RÉUSSI" : "❌ ÉCHEC"));
                    }
                }
            } else {
                System.out.println("➕ Création de l'utilisateur admin...");
                
                // Créer l'utilisateur admin avec un hash 
                String hashAdmin = BCrypt.hashpw("admin", BCrypt.gensalt(12));
                System.out.println("🔐 Hash admin généré : " + hashAdmin.substring(0, 15) + "...");
                
                try (var insertStmt = connexion.prepareStatement("""
                    INSERT INTO utilisateurs (identifiant, mot_de_passe, nom_utilisateur, prenom_utilisateur, telephone_utilisateur, email_utilisateur, type_utilisateur, niveau_acces)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                    """)) {
                    
                    insertStmt.setString(1, "admin");
                    insertStmt.setString(2, hashAdmin);
                    insertStmt.setString(3, "Admin");
                    insertStmt.setString(4, "Propriétaire");
                    insertStmt.setString(5, "0123456789");
                    insertStmt.setString(6, "admin@sotragest.com");
                    insertStmt.setString(7, "GERANT");
                    insertStmt.setString(8, "COMPLET");
                    
                    int inserted = insertStmt.executeUpdate();
                    if (inserted > 0) {
                        System.out.println("✅ Utilisateur admin créé avec succès!");
                        
                        // Test de vérification
                        boolean testHash = BCrypt.checkpw("admin", hashAdmin);
                        System.out.println("🧪 Test de vérification : " + (testHash ? "✅ RÉUSSI" : "❌ ÉCHEC"));
                    }
                }
            }
        }
    }

    public static void verifierConnexion() {
        try (Connection connexion = obtenirConnexion()) {
            System.out.println("✅ Connexion à la base de données réussie!");
            
            // Vérifier si l'utilisateur admin existe
            try (Statement stmt = connexion.createStatement()) {
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateurs WHERE identifiant = 'admin'");
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("✅ Utilisateur admin trouvé dans la base");
                    
                    // Vérifier le hash du mot de passe
                    var rsHash = stmt.executeQuery("SELECT mot_de_passe FROM utilisateurs WHERE identifiant = 'admin'");
                    if (rsHash.next()) {
                        String hashStocke = rsHash.getString("mot_de_passe");
                        System.out.println("🔍 Hash actuel : " + hashStocke.substring(0, 15) + "...");
                        
                        // Tester le hash
                        boolean testHash = BCrypt.checkpw("admin", hashStocke);
                        System.out.println("🧪 Test hash avec 'admin' : " + (testHash ? "✅ VALIDE" : "❌ INVALIDE"));
                        
                        if (!testHash) {
                            System.out.println("🔧 Hash invalide détecté - correction automatique...");
                            corrigerUtilisateurAdmin(connexion);
                        }
                    }
                } else {
                    System.out.println("⚠️ Utilisateur admin non trouvé - création...");
                    corrigerUtilisateurAdmin(connexion);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
            System.err.println("💡 Vérifiez que PostgreSQL est démarré et que la base 'sotragest' existe");
        }
    }

    
    // Méthode pour forcer la réinitialisation du mot de passe admin
     
    public static boolean forcerReinitialisationAdmin() {
        try (Connection connexion = obtenirConnexion()) {
            corrigerUtilisateurAdmin(connexion);
            return true;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la réinitialisation : " + e.getMessage());
            return false;
        }
    }
}