package com.sotragest.vue;

import com.sotragest.dao.UtilisateurDAO;
import com.sotragest.modele.Agent;
import com.sotragest.modele.Gerant;
import com.sotragest.modele.Utilisateur;
import com.sotragest.vue.gestion.*;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


// Fenêtre principale de l'application SotraGest

public class FenetrePrincipale {
    private Stage stage;
    private Utilisateur utilisateurConnecte;
    private BorderPane conteneurPrincipal;
    private VBox menuGauche;
    private StackPane zoneCentrale;

    public FenetrePrincipale(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        initialiserInterface();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new BorderPane();
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Barre supérieure
        creerBarreSuperieure();

        // Menu de gauche
        creerMenuGauche();

        // Zone centrale
        zoneCentrale = new StackPane();
        zoneCentrale.setPadding(new Insets(20));
        afficherTableauDeBord();

        conteneurPrincipal.setCenter(zoneCentrale);
    }

    private void creerBarreSuperieure() {
        HBox barreSuperieure = new HBox();
        barreSuperieure.setAlignment(Pos.CENTER_LEFT);
        barreSuperieure.setPadding(new Insets(10, 20, 10, 20));
        barreSuperieure.setStyle("-fx-background-color: #2c3e50;");
        barreSuperieure.setSpacing(20);

        // Titre de l'application
        Label titreSotraGest = new Label("SotraGest");
        titreSotraGest.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titreSotraGest.setTextFill(Color.WHITE);

        // Espace vide pour pousser les infos utilisateur à droite
        Region espaceVide = new Region();
        HBox.setHgrow(espaceVide, Priority.ALWAYS);

        // Informations utilisateur
        VBox infoUtilisateur = new VBox(2);
        infoUtilisateur.setAlignment(Pos.CENTER_RIGHT);

        Label nomUtilisateur = new Label(utilisateurConnecte.getNomCompletUtilisateur());
        nomUtilisateur.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nomUtilisateur.setTextFill(Color.WHITE);

        String typeUtilisateur = utilisateurConnecte instanceof Gerant ? "Gérant" : "Agent";
        Label roleUtilisateur = new Label(typeUtilisateur);
        roleUtilisateur.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        roleUtilisateur.setTextFill(Color.web("#bdc3c7"));

        infoUtilisateur.getChildren().addAll(nomUtilisateur, roleUtilisateur);

        // Bouton de déconnexion
        Button boutonDeconnexion = new Button("Déconnexion");
        boutonDeconnexion.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        boutonDeconnexion.setOnAction(e -> deconnecter());

        barreSuperieure.getChildren().addAll(titreSotraGest, espaceVide, infoUtilisateur, boutonDeconnexion);
        conteneurPrincipal.setTop(barreSuperieure);
    }

    private void creerMenuGauche() {
        menuGauche = new VBox(5);
        menuGauche.setPadding(new Insets(20));
        menuGauche.setPrefWidth(250);
        menuGauche.setStyle("-fx-background-color: #34495e;");

        // Titre du menu
        Label titreMenu = new Label("MENU PRINCIPAL");
        titreMenu.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        titreMenu.setTextFill(Color.web("#bdc3c7"));
        titreMenu.setPadding(new Insets(0, 0, 15, 0));

        menuGauche.getChildren().add(titreMenu);

        // Tableau de bord (toujours accessible)
        ajouterBoutonMenu("📊 Tableau de bord", e -> afficherTableauDeBord());

        // Gestion des voyageurs (accessible à tous)
        ajouterBoutonMenu("👥 Gestion des voyageurs", e -> afficherGestionVoyageurs());

        // Vente de tickets (accessible à tous)
        ajouterBoutonMenu("🎫 Vente de tickets", e -> afficherVenteTickets());

        // Historique des ventes (accessible à tous)
        ajouterBoutonMenu("📈 Historique des ventes", e -> afficherHistoriqueVentes());

        // Recherche de trajets (accessible à tous)
        ajouterBoutonMenu("🔍 Recherche de trajets", e -> afficherRechercheTrajets());

        // Fonctionnalités réservées aux gérants
        if (utilisateurConnecte instanceof Gerant) {
            if (utilisateurConnecte.getNomCompagnie() == null || utilisateurConnecte.getNomCompagnie().isBlank()) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Initialisation de la compagnie");
                dialog.setHeaderText("Nom de la société de transport");
                dialog.setContentText("Entrez le nom de votre compagnie :");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(nom -> {
                    utilisateurConnecte.setNomCompagnie(nom);
                    new UtilisateurDAO().mettreAJourNomCompagnie(utilisateurConnecte.getIdUtilisateur(), nom);
                });
            }
            // Séparateur
            Separator separateur = new Separator();
            separateur.setStyle("-fx-background-color: #7f8c8d;");
            menuGauche.getChildren().add(separateur);

            Label titreGestion = new Label("GESTION ADMINISTRATIVE");
            titreGestion.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            titreGestion.setTextFill(Color.web("#95a5a6"));
            titreGestion.setPadding(new Insets(10, 0, 10, 0));
            menuGauche.getChildren().add(titreGestion);

            ajouterBoutonMenu("👤 Gestion des agents", e -> afficherGestionAgents());
            ajouterBoutonMenu("🚌 Gestion des bus", e -> afficherGestionBus());
            ajouterBoutonMenu("👨‍✈️ Gestion des chauffeurs", e -> afficherGestionChauffeurs());
            ajouterBoutonMenu("🛣️ Gestion des trajets", e -> afficherGestionTrajets());
            ajouterBoutonMenu("📊 Statistiques", e -> afficherStatistiques());
        }

        ScrollPane scrollMenu = new ScrollPane(menuGauche);
        scrollMenu.setFitToWidth(true);
        scrollMenu.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollMenu.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollMenu.setStyle("-fx-background: #34495e; -fx-background-color: #34495e;");

        conteneurPrincipal.setLeft(scrollMenu);
    }

    private void ajouterBoutonMenu(String texte, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button bouton = new Button(texte);
        bouton.setPrefWidth(210);
        bouton.setPrefHeight(40);
        bouton.setAlignment(Pos.CENTER_LEFT);
        bouton.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        bouton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );

        bouton.setOnMouseEntered(e -> bouton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        ));

        bouton.setOnMouseExited(e -> bouton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: white; " +
            "-fx-border-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        ));

        bouton.setOnAction(action);
        menuGauche.getChildren().add(bouton);
    }

    private void afficherTableauDeBord() {
        FenetreTableauDeBord tableauDeBord = new FenetreTableauDeBord(utilisateurConnecte);
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(tableauDeBord.obtenirContenu());
    }

    private void afficherGestionVoyageurs() {
        FenetreGestionVoyageurs gestionVoyageurs = new FenetreGestionVoyageurs();
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(gestionVoyageurs.obtenirContenu());
    }

    private void afficherVenteTickets() {
        FenetreVenteTickets venteTickets = new FenetreVenteTickets(utilisateurConnecte);
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(venteTickets.obtenirContenu());
    }

    private void afficherHistoriqueVentes() {
        FenetreHistoriqueVentes historiqueVentes = new FenetreHistoriqueVentes();
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(historiqueVentes.obtenirContenu());
    }

    private void afficherRechercheTrajets() {
        FenetreRechercheTrajets rechercheTrajets = new FenetreRechercheTrajets();
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(rechercheTrajets.obtenirContenu());
    }

    private void afficherGestionAgents() {
        FenetreGestionAgents gestionAgents = new FenetreGestionAgents(utilisateurConnecte);
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(gestionAgents.obtenirContenu());
    }

    private void afficherGestionBus() {
        FenetreGestionBus gestionBus = new FenetreGestionBus();
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(gestionBus.obtenirContenu());
    }

    private void afficherGestionChauffeurs() {
        FenetreGestionChauffeurs gestionChauffeurs = new FenetreGestionChauffeurs();
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(gestionChauffeurs.obtenirContenu());
    }

    private void afficherGestionTrajets() {
        FenetreGestionTrajets gestionTrajets = new FenetreGestionTrajets();
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(gestionTrajets.obtenirContenu());
    }

    private void afficherStatistiques() {
        FenetreStatistiques statistiques = new FenetreStatistiques();
        zoneCentrale.getChildren().clear();
        zoneCentrale.getChildren().add(statistiques.obtenirContenu());
    }

    private void deconnecter() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Déconnexion");
        confirmation.setHeaderText("Confirmer la déconnexion");
        confirmation.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                stage.close();
                // Relancer la fenêtre de connexion
                try {
                    FenetreConnexion fenetreConnexion = new FenetreConnexion();
                    Stage nouveauStage = new Stage();
                    fenetreConnexion.afficher(nouveauStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void afficher(Stage stage) {
        this.stage = stage;
        Scene scene = new Scene(conteneurPrincipal, 1200, 1200);
        stage.setTitle("SotraGest - " + utilisateurConnecte.getNomCompletUtilisateur());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}