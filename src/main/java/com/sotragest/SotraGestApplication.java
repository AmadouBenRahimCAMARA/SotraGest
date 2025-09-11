package com.sotragest;

import com.sotragest.configuration.ConfigurationBaseDonnees;
import com.sotragest.vue.FenetreConnexion;
import javafx.application.Application;
import javafx.stage.Stage;


// Classe principale de l'application SotraGest

public class SotraGestApplication extends Application {
    @Override
    public void start(Stage stagePrimaire) {
        try {
            System.out.println("🚀 Démarrage de SotraGest...");
            
            // Vérifier la connexion à la base de données
            System.out.println("🔍 Vérification de la base de données...");
            ConfigurationBaseDonnees.verifierConnexion();
            
            // Initialisation de la base de données
            System.out.println("🔧 Initialisation de la base de données...");
            ConfigurationBaseDonnees.initialiserBaseDonnees();
            
            // Lancement de la fenêtre de connexion
            System.out.println("🖥️ Ouverture de la fenêtre de connexion...");
            FenetreConnexion fenetreConnexion = new FenetreConnexion();
            fenetreConnexion.afficher(stagePrimaire);
            
            System.out.println("✅ Application démarrée avec succès!");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du démarrage de l'application : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("🎯 SOTRAGEST - Système de Gestion de Transport");
        System.out.println("=".repeat(50));
        launch(args);
    }
}