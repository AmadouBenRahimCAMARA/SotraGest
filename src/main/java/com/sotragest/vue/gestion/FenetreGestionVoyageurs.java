package com.sotragest.vue.gestion;

import com.sotragest.dao.VoyageurDAO;
import com.sotragest.modele.Voyageur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


// Fenêtre de gestion des voyageurs

public class FenetreGestionVoyageurs {
    private VBox conteneurPrincipal;
    private TableView<Voyageur> tableauVoyageurs;
    private ObservableList<Voyageur> listeVoyageurs;
    private VoyageurDAO voyageurDAO;
    private TextField champRecherche;

    public FenetreGestionVoyageurs() {
        this.voyageurDAO = new VoyageurDAO();
        this.listeVoyageurs = FXCollections.observableArrayList();
        initialiserInterface();
        chargerVoyageurs();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre
        Label titre = new Label("Gestion des Voyageurs");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#2c3e50"));

        // Barre d'outils
        HBox barreOutils = creerBarreOutils();

        // Tableau des voyageurs
        tableauVoyageurs = creerTableauVoyageurs();

        conteneurPrincipal.getChildren().addAll(titre, barreOutils, tableauVoyageurs);
    }

    private HBox creerBarreOutils() {
        HBox barreOutils = new HBox(10);
        barreOutils.setAlignment(Pos.CENTER_LEFT);
        barreOutils.setPadding(new Insets(10));

        // Champ de recherche
        champRecherche = new TextField();
        champRecherche.setPromptText("Rechercher un voyageur...");
        champRecherche.setPrefWidth(300);
        champRecherche.textProperty().addListener((obs, oldText, newText) -> rechercherVoyageurs());

        // Boutons
        Button boutonAjouter = new Button("➕ Ajouter");
        Button boutonModifier = new Button("✏️ Modifier");
        Button boutonSupprimer = new Button("🗑️ Supprimer");
        Button boutonActualiser = new Button("🔄 Actualiser");

        // Style des boutons
        String styleButton = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;";
        boutonAjouter.setStyle(styleButton);
        boutonModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonActualiser.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        // Événements
        boutonAjouter.setOnAction(e -> ouvrirFormulaireVoyageur(null));
        boutonModifier.setOnAction(e -> modifierVoyageurSelectionne());
        boutonSupprimer.setOnAction(e -> supprimerVoyageurSelectionne());
        boutonActualiser.setOnAction(e -> chargerVoyageurs());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        barreOutils.getChildren().addAll(
            new Label("Recherche:"), champRecherche, spacer,
            boutonAjouter, boutonModifier, boutonSupprimer, boutonActualiser
        );

        return barreOutils;
    }

    private TableView<Voyageur> creerTableauVoyageurs() {
        TableView<Voyageur> tableau = new TableView<>();
        tableau.setItems(listeVoyageurs);
        tableau.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableau.getColumns().clear(); // Pour éviter tout ajout automatique de colonne


        // Colonnes
        TableColumn<Voyageur, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomVoyageur"));
        colNom.setPrefWidth(120);

        TableColumn<Voyageur, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenomVoyageur"));
        colPrenom.setPrefWidth(120);

        TableColumn<Voyageur, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephoneVoyageur"));
        colTelephone.setPrefWidth(120);

        TableColumn<Voyageur, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailVoyageur"));
        colEmail.setPrefWidth(200);

        TableColumn<Voyageur, LocalDate> colDateNaissance = new TableColumn<>("Date de naissance");
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("dateNaissanceVoyageur"));
        colDateNaissance.setPrefWidth(130);

        TableColumn<Voyageur, String> colPieceIdentite = new TableColumn<>("Pièce d'identité");
        colPieceIdentite.setCellValueFactory(new PropertyValueFactory<>("pieceIdentite"));
        colPieceIdentite.setPrefWidth(120);

        TableColumn<Voyageur, String> colNumeroPiece = new TableColumn<>("Numéro pièce");
        colNumeroPiece.setCellValueFactory(new PropertyValueFactory<>("numeroPiece"));
        colNumeroPiece.setPrefWidth(120);

        tableau.getColumns().addAll(colNom, colPrenom, colTelephone, colEmail, 
                                   colDateNaissance, colPieceIdentite, colNumeroPiece);
        

        // Double-clic pour modifier
        tableau.setRowFactory(tv -> {
            TableRow<Voyageur> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ouvrirFormulaireVoyageur(row.getItem());
                }
            });
            return row;
        });

        return tableau;
    }

    private void chargerVoyageurs() {
        List<Voyageur> voyageurs = voyageurDAO.obtenirTousLesVoyageurs();
        listeVoyageurs.clear();
        listeVoyageurs.addAll(voyageurs);
    }

    private void rechercherVoyageurs() {
        String critere = champRecherche.getText();
        if (critere == null || critere.trim().isEmpty()) {
            chargerVoyageurs();
        } else {
            List<Voyageur> resultats = voyageurDAO.rechercherVoyageurs(critere.trim());
            listeVoyageurs.clear();
            listeVoyageurs.addAll(resultats);
        }
    }

    private void modifierVoyageurSelectionne() {
        Voyageur voyageurSelectionne = tableauVoyageurs.getSelectionModel().getSelectedItem();
        if (voyageurSelectionne != null) {
            ouvrirFormulaireVoyageur(voyageurSelectionne);
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un voyageur à modifier.");
        }
    }

    private void supprimerVoyageurSelectionne() {
        Voyageur voyageurSelectionne = tableauVoyageurs.getSelectionModel().getSelectedItem();
        if (voyageurSelectionne != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer le voyageur");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + voyageurSelectionne.getNomCompletVoyageur() + " ?");

            Optional<ButtonType> resultat = confirmation.showAndWait();
            if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                if (voyageurDAO.supprimerVoyageur(voyageurSelectionne.getIdVoyageur())) {
                    chargerVoyageurs();
                    afficherAlerte("Succès", "Voyageur supprimé avec succès.");
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la suppression du voyageur.");
                }
            }
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un voyageur à supprimer.");
        }
    }

    private void ouvrirFormulaireVoyageur(Voyageur voyageur) {
        Stage fenetreFormulaire = new Stage();
        fenetreFormulaire.initModality(Modality.APPLICATION_MODAL);
        fenetreFormulaire.setTitle(voyageur == null ? "Ajouter un voyageur" : "Modifier le voyageur");

        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(20));

        // Champs du formulaire
        TextField champNom = new TextField(voyageur != null ? voyageur.getNomVoyageur() : "");
        champNom.setPromptText("Nom");

        TextField champPrenom = new TextField(voyageur != null ? voyageur.getPrenomVoyageur() : "");
        champPrenom.setPromptText("Prénom");

        TextField champTelephone = new TextField(voyageur != null ? voyageur.getTelephoneVoyageur() : "");
        champTelephone.setPromptText("Téléphone");

        TextField champEmail = new TextField(voyageur != null ? voyageur.getEmailVoyageur() : "");
        champEmail.setPromptText("Email");

        DatePicker champDateNaissance = new DatePicker(voyageur != null ? voyageur.getDateNaissanceVoyageur() : null);
        champDateNaissance.setPromptText("Date de naissance");

        TextArea champAdresse = new TextArea(voyageur != null ? voyageur.getAdresseVoyageur() : "");
        champAdresse.setPromptText("Adresse");
        champAdresse.setPrefRowCount(3);

        ComboBox<String> champPieceIdentite = new ComboBox<>();
        champPieceIdentite.getItems().addAll("CNIB", "Passeport", "Permis de conduire", "Autre");
        if (voyageur != null) {
            champPieceIdentite.setValue(voyageur.getPieceIdentite());
        }

        TextField champNumeroPiece = new TextField(voyageur != null ? voyageur.getNumeroPiece() : "");
        champNumeroPiece.setPromptText("Numéro de la pièce");

        // Boutons
        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER);

        Button boutonSauvegarder = new Button("💾 Sauvegarder");
        Button boutonAnnuler = new Button("❌ Annuler");

        boutonSauvegarder.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;");
        boutonAnnuler.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");

        boutons.getChildren().addAll(boutonSauvegarder, boutonAnnuler);

        // Événements
        boutonSauvegarder.setOnAction(e -> {
            if (validerFormulaire(champNom, champPrenom, champTelephone)) {
                Voyageur nouveauVoyageur = voyageur != null ? voyageur : new Voyageur();
                
                nouveauVoyageur.setNomVoyageur(champNom.getText().trim());
                nouveauVoyageur.setPrenomVoyageur(champPrenom.getText().trim());
                nouveauVoyageur.setTelephoneVoyageur(champTelephone.getText().trim());
                nouveauVoyageur.setEmailVoyageur(champEmail.getText().trim());
                nouveauVoyageur.setDateNaissanceVoyageur(champDateNaissance.getValue());
                nouveauVoyageur.setAdresseVoyageur(champAdresse.getText().trim());
                nouveauVoyageur.setPieceIdentite(champPieceIdentite.getValue());
                nouveauVoyageur.setNumeroPiece(champNumeroPiece.getText().trim());

                boolean succes = voyageur == null ? 
                    voyageurDAO.creerVoyageur(nouveauVoyageur) : 
                    voyageurDAO.mettreAJourVoyageur(nouveauVoyageur);

                if (succes) {
                    chargerVoyageurs();
                    afficherAlerte("Succès", "Voyageur " + (voyageur == null ? "ajouté" : "modifié") + " avec succès.");
                    fenetreFormulaire.close();
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la sauvegarde du voyageur.");
                }
            }
        });

        boutonAnnuler.setOnAction(e -> fenetreFormulaire.close());

        conteneur.getChildren().addAll(
            new Label("Nom:"), champNom,
            new Label("Prénom:"), champPrenom,
            new Label("Téléphone:"), champTelephone,
            new Label("Email:"), champEmail,
            new Label("Date de naissance:"), champDateNaissance,
            new Label("Adresse:"), champAdresse,
            new Label("Pièce d'identité:"), champPieceIdentite,
            new Label("Numéro de pièce:"), champNumeroPiece,
            boutons
        );

        Scene scene = new Scene(new ScrollPane(conteneur), 400, 600);
        fenetreFormulaire.setScene(scene);
        fenetreFormulaire.showAndWait();
    }

    private boolean validerFormulaire(TextField nom, TextField prenom, TextField telephone) {
        if (nom.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "Le nom est obligatoire.");
            return false;
        }
        if (prenom.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "Le prénom est obligatoire.");
            return false;
        }
        if (telephone.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "Le téléphone est obligatoire.");
            return false;
        }
        return true;
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