package com.sotragest.vue;

import com.sotragest.controleur.ControleurConnexion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


// Fenêtre de connexion de l'application

public class FenetreConnexion {
    private Stage stage;
    private TextField champIdentifiant;
    private PasswordField champMotDePasse;
    private Button boutonConnexion;
    private Label labelErreur;
    private ControleurConnexion controleur;

    public FenetreConnexion() {
        this.controleur = new ControleurConnexion(this);
    }

    public void afficher(Stage stage) {
        this.stage = stage;
        VBox conteneurPrincipal = initialiserInterface();
        Scene scene = new Scene(new ScrollPane(conteneurPrincipal), 440, 560);
        stage.setTitle("SotraGest - Connexion");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    private VBox initialiserInterface() {
        VBox conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setAlignment(Pos.CENTER);
        conteneurPrincipal.setPadding(new Insets(30));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre de l'application
        Label titreSotraGest = new Label("SotraGest");
        titreSotraGest.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titreSotraGest.setTextFill(Color.web("#2c3e50"));

        Label sousTitre = new Label("Système de Gestion de Transport");
        sousTitre.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        sousTitre.setTextFill(Color.web("#7f8c8d"));

        // Panneau de connexion
        VBox panneauConnexion = new VBox(15);
        panneauConnexion.setAlignment(Pos.CENTER);
        panneauConnexion.setPadding(new Insets(30));
        panneauConnexion.setStyle(
            "-fx-background-color: #d6c2a9; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );
        panneauConnexion.setMaxWidth(800);

        Label titreConnexion = new Label("Connexion");
        titreConnexion.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titreConnexion.setTextFill(Color.web("#2c3e50"));

        // Champs de saisie
        VBox champsContainer = new VBox(10);
        
        Label labelIdentifiant = new Label("Identifiant :");
        labelIdentifiant.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        champIdentifiant = new TextField();
        champIdentifiant.setPromptText("Saisissez votre identifiant");
        champIdentifiant.setPrefHeight(40);
        champIdentifiant.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );

        Label labelMotDePasse = new Label("Mot de passe :");
        labelMotDePasse.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        champMotDePasse = new PasswordField();
        champMotDePasse.setPromptText("Saisissez votre mot de passe");
        champMotDePasse.setPrefHeight(40);
        champMotDePasse.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-radius: 5; " +
            "-fx-background-radius: 5;"
        );

        champsContainer.getChildren().addAll(
            labelIdentifiant, champIdentifiant,
            labelMotDePasse, champMotDePasse
        );

        // Bouton de connexion
        boutonConnexion = new Button("Se connecter");
        boutonConnexion.setPrefWidth(200);
        boutonConnexion.setPrefHeight(45);
        boutonConnexion.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        boutonConnexion.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );

        // Label d'erreur
        labelErreur = new Label();
        labelErreur.setTextFill(Color.web("#e74c3c"));
        labelErreur.setFont(Font.font("Arial", FontWeight.MEDIUM, 12));

        // Informations de connexion par défaut
        Label infoConnexion = new Label("Connectez-vous avec votre identifiant et mot de passe ");
        infoConnexion.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        infoConnexion.setTextFill(Color.web("#2980b9"));

        panneauConnexion.getChildren().addAll(
            titreConnexion, champsContainer, boutonConnexion, labelErreur, infoConnexion
        );

        conteneurPrincipal.getChildren().addAll(titreSotraGest, sousTitre, panneauConnexion);

        // Événements
        boutonConnexion.setOnAction(e -> controleur.tenterConnexion());
        champMotDePasse.setOnAction(e -> controleur.tenterConnexion());

        // Effets de survol
        boutonConnexion.setOnMouseEntered(e -> boutonConnexion.setStyle(
            "-fx-background-color: #2ecc71; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        ));
        boutonConnexion.setOnMouseExited(e -> boutonConnexion.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        ));

        // Valeurs par défaut 
        champIdentifiant.setText("");
        champMotDePasse.setText("");

        return conteneurPrincipal;
    }

    public String getIdentifiant() {
        return champIdentifiant.getText();
    }

    public String getMotDePasse() {
        return champMotDePasse.getText();
    }

    public void afficherErreur(String message) {
        labelErreur.setText(message);
    }

    public void viderErreur() {
        labelErreur.setText("");
    }

    public Stage getStage() {
        return stage;
    }
}