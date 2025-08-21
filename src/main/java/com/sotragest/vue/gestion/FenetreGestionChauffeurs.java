package com.sotragest.vue.gestion;

import com.sotragest.dao.ChauffeurDAO;
import com.sotragest.modele.Chauffeur;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


//Fenêtre de gestion des chauffeurs

public class FenetreGestionChauffeurs {
    private VBox conteneurPrincipal;
    private TableView<Chauffeur> tableauChauffeurs;
    private ObservableList<Chauffeur> listeChauffeurs;
    private ChauffeurDAO chauffeurDAO;

    public FenetreGestionChauffeurs() {
        this.chauffeurDAO = new ChauffeurDAO();
        this.listeChauffeurs = FXCollections.observableArrayList();
        initialiserInterface();
        chargerChauffeurs();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre
        Label titre = new Label("Gestion des Chauffeurs");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#2c3e50"));

        // Barre d'outils
        HBox barreOutils = creerBarreOutils();

        // Tableau des chauffeurs
        tableauChauffeurs = creerTableauChauffeurs();

        conteneurPrincipal.getChildren().addAll(titre, barreOutils, tableauChauffeurs);
    }

    private HBox creerBarreOutils() {
        HBox barreOutils = new HBox(10);
        barreOutils.setAlignment(Pos.CENTER_LEFT);
        barreOutils.setPadding(new Insets(10));

        Button boutonAjouter = new Button("➕ Ajouter un chauffeur");
        Button boutonModifier = new Button("✏️ Modifier");
        Button boutonSupprimer = new Button("🗑️ Supprimer");
        Button boutonActualiser = new Button("🔄 Actualiser");

        // Style des boutons
        boutonAjouter.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonActualiser.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        // Événements
        boutonAjouter.setOnAction(e -> ouvrirFormulaireChauffeur(null));
        boutonModifier.setOnAction(e -> modifierChauffeurSelectionne());
        boutonSupprimer.setOnAction(e -> supprimerChauffeurSelectionne());
        boutonActualiser.setOnAction(e -> chargerChauffeurs());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        barreOutils.getChildren().addAll(boutonAjouter, boutonModifier, boutonSupprimer, spacer, boutonActualiser);
        return barreOutils;
    }

    private TableView<Chauffeur> creerTableauChauffeurs() {
        TableView<Chauffeur> tableau = new TableView<>();
        tableau.setItems(listeChauffeurs);

        // Colonnes
        TableColumn<Chauffeur, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomChauffeur"));
        colNom.setPrefWidth(120);

        TableColumn<Chauffeur, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenomChauffeur"));
        colPrenom.setPrefWidth(120);

        TableColumn<Chauffeur, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephoneChauffeur"));
        colTelephone.setPrefWidth(120);

        TableColumn<Chauffeur, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailChauffeur"));
        colEmail.setPrefWidth(180);

        TableColumn<Chauffeur, String> colNumeroPermis = new TableColumn<>("N° Permis");
        colNumeroPermis.setCellValueFactory(new PropertyValueFactory<>("numeroPermis"));
        colNumeroPermis.setPrefWidth(120);

        TableColumn<Chauffeur, String> colCategoriePermis = new TableColumn<>("Catégorie");
        colCategoriePermis.setCellValueFactory(new PropertyValueFactory<>("categoriePermis"));
        colCategoriePermis.setPrefWidth(80);

        TableColumn<Chauffeur, String> colDateExpiration = new TableColumn<>("Expiration permis");
        colDateExpiration.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateExpirationPermis() != null ?
                cellData.getValue().getDateExpirationPermis().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""));
        colDateExpiration.setPrefWidth(120);

        TableColumn<Chauffeur, String> colSalaire = new TableColumn<>("Salaire");
        colSalaire.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.0f FCFA", cellData.getValue().getSalaire())));
        colSalaire.setPrefWidth(120);

        TableColumn<Chauffeur, String> colValiditePermis = new TableColumn<>("Permis valide");
        colValiditePermis.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().permisValide() ? "✅ Oui" : "❌ Non"));
        colValiditePermis.setPrefWidth(100);

        TableColumn<Chauffeur, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isActifChauffeur() ? "✅ Actif" : "❌ Inactif"));
        colStatut.setPrefWidth(80);

        tableau.getColumns().addAll(colNom, colPrenom, colTelephone, colEmail, colNumeroPermis, 
                                   colCategoriePermis, colDateExpiration, colSalaire, colValiditePermis, colStatut);

        // Style conditionnel pour les lignes
        tableau.setRowFactory(tv -> new TableRow<Chauffeur>() {
            @Override
            protected void updateItem(Chauffeur chauffeur, boolean empty) {
                super.updateItem(chauffeur, empty);
                if (empty || chauffeur == null) {
                    setStyle("");
                } else {
                    if (!chauffeur.permisValide()) {
                        setStyle("-fx-background-color: #ffebee;");
                    } else if (!chauffeur.isActifChauffeur()) {
                        setStyle("-fx-background-color: #f5f5f5;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Double-clic pour modifier
        tableau.setRowFactory(tv -> {
            TableRow<Chauffeur> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ouvrirFormulaireChauffeur(row.getItem());
                }
            });
            return row;
        });

        return tableau;
    }

    private void chargerChauffeurs() {
        List<Chauffeur> chauffeurs = chauffeurDAO.obtenirTousLesChauffeurs();
        listeChauffeurs.clear();
        listeChauffeurs.addAll(chauffeurs);
    }

    private void modifierChauffeurSelectionne() {
        Chauffeur chauffeurSelectionne = tableauChauffeurs.getSelectionModel().getSelectedItem();
        if (chauffeurSelectionne != null) {
            ouvrirFormulaireChauffeur(chauffeurSelectionne);
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un chauffeur à modifier.");
        }
    }

    private void supprimerChauffeurSelectionne() {
        Chauffeur chauffeurSelectionne = tableauChauffeurs.getSelectionModel().getSelectedItem();
        if (chauffeurSelectionne != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer le chauffeur");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + chauffeurSelectionne.getNomCompletChauffeur() + " ?");

            Optional<ButtonType> resultat = confirmation.showAndWait();
            if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                if (chauffeurDAO.supprimerChauffeur(chauffeurSelectionne.getIdChauffeur())) {
                    chargerChauffeurs();
                    afficherAlerte("Succès", "Chauffeur supprimé avec succès.");
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la suppression du chauffeur.");
                }
            }
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un chauffeur à supprimer.");
        }
    }

    private void ouvrirFormulaireChauffeur(Chauffeur chauffeur) {
        Stage fenetreFormulaire = new Stage();
        fenetreFormulaire.initModality(Modality.APPLICATION_MODAL);
        fenetreFormulaire.setTitle(chauffeur == null ? "Ajouter un chauffeur" : "Modifier le chauffeur");

        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(20));

        // Champs du formulaire
        TextField champNom = new TextField(chauffeur != null ? chauffeur.getNomChauffeur() : "");
        champNom.setPromptText("Nom");

        TextField champPrenom = new TextField(chauffeur != null ? chauffeur.getPrenomChauffeur() : "");
        champPrenom.setPromptText("Prénom");

        TextField champTelephone = new TextField(chauffeur != null ? chauffeur.getTelephoneChauffeur() : "");
        champTelephone.setPromptText("Téléphone");

        TextField champEmail = new TextField(chauffeur != null ? chauffeur.getEmailChauffeur() : "");
        champEmail.setPromptText("Email");

        DatePicker champDateNaissance = new DatePicker(chauffeur != null ? chauffeur.getDateNaissanceChauffeur() : null);
        champDateNaissance.setPromptText("Date de naissance");

        TextArea champAdresse = new TextArea(chauffeur != null ? chauffeur.getAdresseChauffeur() : "");
        champAdresse.setPromptText("Adresse");
        champAdresse.setPrefRowCount(3);

        TextField champNumeroPermis = new TextField(chauffeur != null ? chauffeur.getNumeroPermis() : "");
        champNumeroPermis.setPromptText("Numéro de permis");

        DatePicker champDateObtention = new DatePicker(chauffeur != null ? chauffeur.getDateObtentionPermis() : null);
        champDateObtention.setPromptText("Date d'obtention du permis");

        DatePicker champDateExpiration = new DatePicker(chauffeur != null ? chauffeur.getDateExpirationPermis() : null);
        champDateExpiration.setPromptText("Date d'expiration du permis");

        ComboBox<String> champCategoriePermis = new ComboBox<>();
        champCategoriePermis.getItems().addAll("A", "B", "C", "D", "E");
        if (chauffeur != null) {
            champCategoriePermis.setValue(chauffeur.getCategoriePermis());
        }

        TextField champSalaire = new TextField(chauffeur != null ? String.valueOf(chauffeur.getSalaire()) : "");
        champSalaire.setPromptText("Salaire");

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
            if (validerFormulaire(champNom, champPrenom, champTelephone, champNumeroPermis, champSalaire)) {
                Chauffeur nouveauChauffeur = chauffeur != null ? chauffeur : new Chauffeur();
                
                nouveauChauffeur.setNomChauffeur(champNom.getText().trim());
                nouveauChauffeur.setPrenomChauffeur(champPrenom.getText().trim());
                nouveauChauffeur.setTelephoneChauffeur(champTelephone.getText().trim());
                nouveauChauffeur.setEmailChauffeur(champEmail.getText().trim());
                nouveauChauffeur.setDateNaissanceChauffeur(champDateNaissance.getValue());
                nouveauChauffeur.setAdresseChauffeur(champAdresse.getText().trim());
                nouveauChauffeur.setNumeroPermis(champNumeroPermis.getText().trim());
                nouveauChauffeur.setDateObtentionPermis(champDateObtention.getValue());
                nouveauChauffeur.setDateExpirationPermis(champDateExpiration.getValue());
                nouveauChauffeur.setCategoriePermis(champCategoriePermis.getValue());
                
                try {
                    nouveauChauffeur.setSalaire(Double.parseDouble(champSalaire.getText().trim()));
                } catch (NumberFormatException ex) {
                    nouveauChauffeur.setSalaire(0);
                }

                boolean succes = chauffeur == null ? 
                    chauffeurDAO.creerChauffeur(nouveauChauffeur) : 
                    chauffeurDAO.mettreAJourChauffeur(nouveauChauffeur);

                if (succes) {
                    chargerChauffeurs();
                    afficherAlerte("Succès", "Chauffeur " + (chauffeur == null ? "ajouté" : "modifié") + " avec succès.");
                    fenetreFormulaire.close(); 
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la sauvegarde du chauffeur.");
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
            new Label("Numéro de permis:"), champNumeroPermis,
            new Label("Date d'obtention:"), champDateObtention,
            new Label("Date d'expiration:"), champDateExpiration,
            new Label("Catégorie de permis:"), champCategoriePermis,
            new Label("Salaire:"), champSalaire,
            boutons
        );

        Scene scene = new Scene(new ScrollPane(conteneur), 450, 700);
        fenetreFormulaire.setScene(scene);
        fenetreFormulaire.showAndWait();
    }

    private boolean validerFormulaire(TextField nom, TextField prenom, TextField telephone, 
                                    TextField numeroPermis, TextField salaire) {
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
        if (numeroPermis.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "Le numéro de permis est obligatoire.");
            return false;
        }
        try {
            Double.parseDouble(salaire.getText().trim());
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de validation", "Le salaire doit être un nombre valide.");
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