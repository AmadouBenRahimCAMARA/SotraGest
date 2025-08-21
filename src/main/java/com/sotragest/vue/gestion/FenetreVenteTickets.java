package com.sotragest.vue.gestion;

import com.sotragest.dao.*;
import com.sotragest.modele.*;
import com.sotragest.utilitaires.GenerateurPDF;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;


// Fenêtre de vente de tickets

public class FenetreVenteTickets {
    private VBox conteneurPrincipal;
    private Utilisateur vendeur;
    private VoyageurDAO voyageurDAO;
    private TrajetDAO trajetDAO;
    private TicketDAO ticketDAO;
    
    private ComboBox<Voyageur> comboVoyageurs;
    private ComboBox<Trajet> comboTrajets;
    private TextField champNumeroSiege;
    private Label labelPrix;
    private TextArea champObservations;

    public FenetreVenteTickets(Utilisateur vendeur) {
        this.vendeur = vendeur;
        this.voyageurDAO = new VoyageurDAO();
        this.trajetDAO = new TrajetDAO();
        this.ticketDAO = new TicketDAO();
        initialiserInterface();
        chargerDonnees();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre
        Label titre = new Label("Vente de Tickets");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#2c3e50"));

        // Formulaire de vente
        VBox formulaire = creerFormulaireVente();

        conteneurPrincipal.getChildren().addAll(titre, formulaire);
    }

    private VBox creerFormulaireVente() {
        VBox formulaire = new VBox(15);
        formulaire.setPadding(new Insets(20));
        formulaire.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );
        formulaire.setMaxWidth(600);

        // Sélection du voyageur
        Label labelVoyageur = new Label("Voyageur:");
        labelVoyageur.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        HBox ligneVoyageur = new HBox(10);
        ligneVoyageur.setAlignment(Pos.CENTER_LEFT);

        comboVoyageurs = new ComboBox<>();
        comboVoyageurs.setPrefWidth(300);
        comboVoyageurs.setPromptText("Sélectionner un voyageur");

        Button boutonNouveauVoyageur = new Button("➕ Nouveau");
        boutonNouveauVoyageur.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
        boutonNouveauVoyageur.setOnAction(e -> ouvrirFormulaireNouveauVoyageur());

        ligneVoyageur.getChildren().addAll(comboVoyageurs, boutonNouveauVoyageur);

        // Sélection du trajet
        Label labelTrajet = new Label("Trajet:");
        labelTrajet.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        comboTrajets = new ComboBox<>();
        comboTrajets.setPrefWidth(400);
        comboTrajets.setPromptText("Sélectionner un trajet");
        comboTrajets.setOnAction(e -> mettreAJourPrix());

        // Numéro de siège
        Label labelSiege = new Label("Numéro de siège:");
        labelSiege.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        HBox ligneSiege = new HBox(10);
        ligneSiege.setAlignment(Pos.CENTER_LEFT);

        champNumeroSiege = new TextField();
        champNumeroSiege.setPromptText("Numéro de siège");
        champNumeroSiege.setPrefWidth(150);

        Button boutonSiegeAleatoire = new Button("🎲 Aléatoire");
        boutonSiegeAleatoire.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5;");
        boutonSiegeAleatoire.setOnAction(e -> genererSiegeAleatoire());

        ligneSiege.getChildren().addAll(champNumeroSiege, boutonSiegeAleatoire);

        // Prix
        Label labelPrixTitre = new Label("Prix:");
        labelPrixTitre.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        labelPrix = new Label("0 FCFA");
        labelPrix.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        labelPrix.setTextFill(Color.web("#27ae60"));

        // Observations
        Label labelObservations = new Label("Observations:");
        labelObservations.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        champObservations = new TextArea();
        champObservations.setPromptText("Observations ou remarques...");
        champObservations.setPrefRowCount(3);

        // Boutons
        HBox boutons = new HBox(15);
        boutons.setAlignment(Pos.CENTER);

        Button boutonVendre = new Button("💳 Vendre le ticket");
        Button boutonAnnuler = new Button("❌ Annuler");

        boutonVendre.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 16px; -fx-padding: 10px 20px;");
        boutonAnnuler.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-font-size: 16px; -fx-padding: 10px 20px;");

        boutonVendre.setOnAction(e -> vendreTicket());
        boutonAnnuler.setOnAction(e -> viderFormulaire());

        boutons.getChildren().addAll(boutonVendre, boutonAnnuler);

        formulaire.getChildren().addAll(
            labelVoyageur, ligneVoyageur,
            labelTrajet, comboTrajets,
            labelSiege, ligneSiege,
            labelPrixTitre, labelPrix,
            labelObservations, champObservations,
            boutons
        );

        return formulaire;
    }

    private void chargerDonnees() {
        // Charger les voyageurs
        List<Voyageur> voyageurs = voyageurDAO.obtenirTousLesVoyageurs();
        comboVoyageurs.setItems(FXCollections.observableArrayList(voyageurs));

        // Charger les trajets disponibles
        List<Trajet> trajets = trajetDAO.obtenirTrajetsDisponibles();
        comboTrajets.setItems(FXCollections.observableArrayList(trajets));
    }

    private void mettreAJourPrix() {
        Trajet trajetSelectionne = comboTrajets.getSelectionModel().getSelectedItem();
        if (trajetSelectionne != null) {
            labelPrix.setText(String.format("%.0f FCFA", trajetSelectionne.getPrix()));
        } else {
            labelPrix.setText("0 FCFA");
        }
    }

    private void genererSiegeAleatoire() {
        Trajet trajetSelectionne = comboTrajets.getSelectionModel().getSelectedItem();
        if (trajetSelectionne != null && trajetSelectionne.getBus() != null) {
            Random random = new Random();
            int numeroSiege = random.nextInt(trajetSelectionne.getBus().getCapacite()) + 1;
            champNumeroSiege.setText(String.valueOf(numeroSiege));
        } else {
            afficherAlerte("Erreur", "Veuillez d'abord sélectionner un trajet.");
        }
    }

    private void ouvrirFormulaireNouveauVoyageur() {
        Stage fenetreFormulaire = new Stage();
        fenetreFormulaire.initModality(Modality.APPLICATION_MODAL);
        fenetreFormulaire.setTitle("Nouveau voyageur");

        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(20));

        TextField champNom = new TextField();
        champNom.setPromptText("Nom");

        TextField champPrenom = new TextField();
        champPrenom.setPromptText("Prénom");

        TextField champTelephone = new TextField();
        champTelephone.setPromptText("Téléphone");

        TextField champEmail = new TextField();
        champEmail.setPromptText("Email");

        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER);

        Button boutonSauvegarder = new Button("💾 Sauvegarder");
        Button boutonAnnuler = new Button("❌ Annuler");

        boutonSauvegarder.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;");
        boutonAnnuler.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");

        boutons.getChildren().addAll(boutonSauvegarder, boutonAnnuler);

        boutonSauvegarder.setOnAction(e -> {
            if (!champNom.getText().trim().isEmpty() && 
                !champPrenom.getText().trim().isEmpty() && 
                !champTelephone.getText().trim().isEmpty()) {
                
                Voyageur nouveauVoyageur = new Voyageur();
                nouveauVoyageur.setNomVoyageur(champNom.getText().trim());
                nouveauVoyageur.setPrenomVoyageur(champPrenom.getText().trim());
                nouveauVoyageur.setTelephoneVoyageur(champTelephone.getText().trim());
                nouveauVoyageur.setEmailVoyageur(champEmail.getText().trim());

                if (voyageurDAO.creerVoyageur(nouveauVoyageur)) {
                    chargerDonnees();
                    comboVoyageurs.getSelectionModel().select(nouveauVoyageur);
                    afficherAlerte("Succès", "Voyageur créé avec succès.");
                    fenetreFormulaire.close();
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la création du voyageur.");
                }
            } else {
                afficherAlerte("Erreur", "Veuillez remplir tous les champs obligatoires.");
            }
        });

        boutonAnnuler.setOnAction(e -> fenetreFormulaire.close());

        conteneur.getChildren().addAll(
            new Label("Nom:"), champNom,
            new Label("Prénom:"), champPrenom,
            new Label("Téléphone:"), champTelephone,
            new Label("Email:"), champEmail,
            boutons
        );

        // Rendre le contenu scrollable
        ScrollPane scrollPane = new ScrollPane(conteneur);
        scrollPane.setFitToWidth(true); // S'assure que le contenu s'adapte à la largeur

        Scene scene = new Scene(scrollPane, 350, 400); // Augmenter un peu la hauteur par défaut
        fenetreFormulaire.setScene(scene);
        fenetreFormulaire.showAndWait();
    }

    private void vendreTicket() {
        // Validation
        Voyageur voyageurSelectionne = comboVoyageurs.getSelectionModel().getSelectedItem();
        Trajet trajetSelectionne = comboTrajets.getSelectionModel().getSelectedItem();
        String numeroSiege = champNumeroSiege.getText().trim();

        if (voyageurSelectionne == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un voyageur.");
            return;
        }

        if (trajetSelectionne == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un trajet.");
            return;
        }

        if (numeroSiege.isEmpty()) {
            afficherAlerte("Erreur", "Veuillez saisir un numéro de siège.");
            return;
        }

        try {
            int siege = Integer.parseInt(numeroSiege);
            if (siege < 1 || siege > trajetSelectionne.getBus().getCapacite()) {
                afficherAlerte("Erreur", "Le numéro de siège doit être entre 1 et " + trajetSelectionne.getBus().getCapacite());
                return;
            }

            // Créer le ticket
            Ticket nouveauTicket = new Ticket(voyageurSelectionne, trajetSelectionne, siege, vendeur);
            nouveauTicket.setObservationsTicket(champObservations.getText().trim());

            if (ticketDAO.creerTicket(nouveauTicket)) {
                // Générer et afficher le PDF
                try {
                    GenerateurPDF.genererTicketPDF(nouveauTicket,vendeur);
                    afficherAlerte("Succès", "Ticket vendu avec succès!\nNuméro: " + nouveauTicket.getNumeroTicket());
                    viderFormulaire();
                    chargerDonnees(); // Recharger pour mettre à jour les places disponibles
                } catch (Exception ex) {
                    afficherAlerte("Avertissement", "Ticket créé mais erreur lors de la génération du PDF: " + ex.getMessage());
                }
            } else {
                afficherAlerte("Erreur", "Erreur lors de la création du ticket.");
            }

        } catch (NumberFormatException e) {
            afficherAlerte("Erreur", "Le numéro de siège doit être un nombre valide.");
        }
    }

    private void viderFormulaire() {
        comboVoyageurs.getSelectionModel().clearSelection();
        comboTrajets.getSelectionModel().clearSelection();
        champNumeroSiege.clear();
        champObservations.clear();
        labelPrix.setText("0 FCFA");
    }

    private void afficherAlerte(String titre, String message) {
        Alert alerte = new Alert(Alert.AlertType.INFORMATION);
        alerte.setTitle(titre);
        alerte.setHeaderText(null);
        alerte.setContentText(message);
        alerte.showAndWait();
    }

    public VBox obtenirContenu() {
        return conteneurPrincipal;
    }
}