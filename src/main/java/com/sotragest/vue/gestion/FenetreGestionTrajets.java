package com.sotragest.vue.gestion;

import com.sotragest.dao.*;
import com.sotragest.modele.*;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


// Fenêtre de gestion des trajets

public class FenetreGestionTrajets {
    private VBox conteneurPrincipal;
    private TableView<Trajet> tableauTrajets;
    private ObservableList<Trajet> listeTrajets;
    private TrajetDAO trajetDAO;
    private BusDAO busDAO;
    private ChauffeurDAO chauffeurDAO;

    public FenetreGestionTrajets() {
        this.trajetDAO = new TrajetDAO();
        this.busDAO = new BusDAO();
        this.chauffeurDAO = new ChauffeurDAO();
        this.listeTrajets = FXCollections.observableArrayList();
        initialiserInterface();
        chargerTrajets();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre
        Label titre = new Label("Gestion des Trajets");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#2c3e50"));

        // Barre d'outils
        HBox barreOutils = creerBarreOutils();

        // Tableau des trajets
        tableauTrajets = creerTableauTrajets();

        conteneurPrincipal.getChildren().addAll(titre, barreOutils, tableauTrajets);
    }

    private HBox creerBarreOutils() {
        HBox barreOutils = new HBox(10);
        barreOutils.setAlignment(Pos.CENTER_LEFT);
        barreOutils.setPadding(new Insets(10));

        Button boutonAjouter = new Button("➕ Ajouter un trajet");
        Button boutonModifier = new Button("✏️ Modifier");
        Button boutonSupprimer = new Button("🗑️ Annuler");
        Button boutonActualiser = new Button("🔄 Actualiser");

        // Style des boutons
        boutonAjouter.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonActualiser.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        // Événements
        boutonAjouter.setOnAction(e -> ouvrirFormulaireTrajet(null));
        boutonModifier.setOnAction(e -> modifierTrajetSelectionne());
        boutonSupprimer.setOnAction(e -> annulerTrajetSelectionne());
        boutonActualiser.setOnAction(e -> chargerTrajets());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        barreOutils.getChildren().addAll(boutonAjouter, boutonModifier, boutonSupprimer, spacer, boutonActualiser);
        return barreOutils;
    }

    private TableView<Trajet> creerTableauTrajets() {
        TableView<Trajet> tableau = new TableView<>();
        tableau.setItems(listeTrajets);

        // Colonnes
        TableColumn<Trajet, String> colDepart = new TableColumn<>("Départ");
        colDepart.setCellValueFactory(new PropertyValueFactory<>("villeDepart"));
        colDepart.setPrefWidth(120);

        TableColumn<Trajet, String> colArrivee = new TableColumn<>("Arrivée");
        colArrivee.setCellValueFactory(new PropertyValueFactory<>("villeArrivee"));
        colArrivee.setPrefWidth(120);

        TableColumn<Trajet, String> colDateDepart = new TableColumn<>("Date/Heure départ");
        colDateDepart.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateHeureDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colDateDepart.setPrefWidth(140);

        TableColumn<Trajet, String> colBus = new TableColumn<>("Bus");
        colBus.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getBus() != null ? cellData.getValue().getBus().getDescriptionComplete() : ""));
        colBus.setPrefWidth(180);

        TableColumn<Trajet, String> colChauffeur = new TableColumn<>("Chauffeur");
        colChauffeur.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getChauffeur() != null ? cellData.getValue().getChauffeur().getNomCompletChauffeur() : ""));
        colChauffeur.setPrefWidth(180);

        TableColumn<Trajet, String> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.0f FCFA", cellData.getValue().getPrix())));
        colPrix.setPrefWidth(100);

        TableColumn<Trajet, Integer> colPlaces = new TableColumn<>("Places dispo.");
        colPlaces.setCellValueFactory(new PropertyValueFactory<>("nombrePlacesDisponibles"));
        colPlaces.setPrefWidth(100);

        TableColumn<Trajet, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatutTrajet().getLibelle()));
        colStatut.setPrefWidth(100);

        tableau.getColumns().addAll(colDepart, colArrivee, colDateDepart, colBus, colChauffeur, colPrix, colPlaces, colStatut);

        // Style conditionnel pour les lignes
        tableau.setRowFactory(tv -> new TableRow<Trajet>() {
            @Override
            protected void updateItem(Trajet trajet, boolean empty) {
                super.updateItem(trajet, empty);
                if (empty || trajet == null) {
                    setStyle("");
                } else {
                    if (trajet.getStatutTrajet() == Trajet.StatutTrajet.ANNULE) {
                        setStyle("-fx-background-color: #ffebee;");
                    } else if (trajet.getStatutTrajet() == Trajet.StatutTrajet.TERMINE) {
                        setStyle("-fx-background-color: #e8f5e8;");
                    } else if (trajet.getStatutTrajet() == Trajet.StatutTrajet.EN_COURS) {
                        setStyle("-fx-background-color: #fff3e0;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Double-clic pour modifier
        tableau.setRowFactory(tv -> {
            TableRow<Trajet> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ouvrirFormulaireTrajet(row.getItem());
                }
            });
            return row;
        });

        return tableau;
    }

    private void chargerTrajets() {
        List<Trajet> trajets = trajetDAO.obtenirTousLesTrajets();
        listeTrajets.clear();
        listeTrajets.addAll(trajets);
    }

    private void modifierTrajetSelectionne() {
        Trajet trajetSelectionne = tableauTrajets.getSelectionModel().getSelectedItem();
        if (trajetSelectionne != null) {
            ouvrirFormulaireTrajet(trajetSelectionne);
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un trajet à modifier.");
        }
    }

    private void annulerTrajetSelectionne() {
        Trajet trajetSelectionne = tableauTrajets.getSelectionModel().getSelectedItem();
        if (trajetSelectionne != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation d'annulation");
            confirmation.setHeaderText("Annuler le trajet");
            confirmation.setContentText("Êtes-vous sûr de vouloir annuler le trajet " + trajetSelectionne.getItineraire() + " ?");

            Optional<ButtonType> resultat = confirmation.showAndWait();
            if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                if (trajetDAO.supprimerTrajet(trajetSelectionne.getIdTrajet())) {
                    chargerTrajets();
                    afficherAlerte("Succès", "Trajet annulé avec succès.");
                } else {
                    afficherAlerte("Erreur", "Erreur lors de l'annulation du trajet.");
                }
            }
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un trajet à annuler.");
        }
    }

    private void ouvrirFormulaireTrajet(Trajet trajet) {
        Stage fenetreFormulaire = new Stage();
        fenetreFormulaire.initModality(Modality.APPLICATION_MODAL);
        fenetreFormulaire.setTitle(trajet == null ? "Ajouter un trajet" : "Modifier le trajet");

        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(20));

        // Champs du formulaire
        TextField champVilleDepart = new TextField(trajet != null ? trajet.getVilleDepart() : "");
        champVilleDepart.setPromptText("Ville de départ");

        TextField champVilleArrivee = new TextField(trajet != null ? trajet.getVilleArrivee() : "");
        champVilleArrivee.setPromptText("Ville d'arrivée");

        DatePicker champDateDepart = new DatePicker();
        if (trajet != null) {
            champDateDepart.setValue(trajet.getDateHeureDepart().toLocalDate());
        }

        Spinner<Integer> champHeureDepart = new Spinner<>(0, 23, 
            trajet != null ? trajet.getDateHeureDepart().getHour() : 8);
        champHeureDepart.setEditable(true);

        Spinner<Integer> champMinuteDepart = new Spinner<>(0, 59, 
            trajet != null ? trajet.getDateHeureDepart().getMinute() : 0, 15);
        champMinuteDepart.setEditable(true);

        DatePicker champDateArrivee = new DatePicker();
        if (trajet != null && trajet.getDateHeureArriveeEstimee() != null) {
            champDateArrivee.setValue(trajet.getDateHeureArriveeEstimee().toLocalDate());
        }

        Spinner<Integer> champHeureArrivee = new Spinner<>(0, 23, 
            trajet != null && trajet.getDateHeureArriveeEstimee() != null ? 
            trajet.getDateHeureArriveeEstimee().getHour() : 12);
        champHeureArrivee.setEditable(true);

        Spinner<Integer> champMinuteArrivee = new Spinner<>(0, 59, 
            trajet != null && trajet.getDateHeureArriveeEstimee() != null ? 
            trajet.getDateHeureArriveeEstimee().getMinute() : 0, 15);
        champMinuteArrivee.setEditable(true);

        ComboBox<Bus> comboBus = new ComboBox<>();
        List<Bus> busDisponibles = busDAO.obtenirBusDisponibles();
        comboBus.setItems(FXCollections.observableArrayList(busDisponibles));
        if (trajet != null) {
            comboBus.setValue(trajet.getBus());
        }

        ComboBox<Chauffeur> comboChauffeur = new ComboBox<>();
        List<Chauffeur> chauffeursDisponibles = chauffeurDAO.obtenirChauffeursDisponibles();
        comboChauffeur.setItems(FXCollections.observableArrayList(chauffeursDisponibles));
        if (trajet != null) {
            comboChauffeur.setValue(trajet.getChauffeur());
        }

        TextField champPrix = new TextField(trajet != null ? String.valueOf(trajet.getPrix()) : "");
        champPrix.setPromptText("Prix en FCFA");

        ComboBox<Trajet.StatutTrajet> comboStatut = new ComboBox<>();
        comboStatut.getItems().addAll(Trajet.StatutTrajet.values());
        if (trajet != null) {
            comboStatut.setValue(trajet.getStatutTrajet());
        } else {
            comboStatut.setValue(Trajet.StatutTrajet.PROGRAMME);
        }

        TextArea champCommentaires = new TextArea(trajet != null ? trajet.getCommentaires() : "");
        champCommentaires.setPromptText("Commentaires ou remarques...");
        champCommentaires.setPrefRowCount(3);

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
            if (validerFormulaire(champVilleDepart, champVilleArrivee, champDateDepart, comboBus, comboChauffeur, champPrix)) {
                Trajet nouveauTrajet = trajet != null ? trajet : new Trajet();
                
                nouveauTrajet.setVilleDepart(champVilleDepart.getText().trim());
                nouveauTrajet.setVilleArrivee(champVilleArrivee.getText().trim());
                
                LocalDateTime dateHeureDepart = LocalDateTime.of(
                    champDateDepart.getValue(),
                    LocalTime.of(champHeureDepart.getValue(), champMinuteDepart.getValue())
                );
                nouveauTrajet.setDateHeureDepart(dateHeureDepart);
                
                if (champDateArrivee.getValue() != null) {
                    LocalDateTime dateHeureArrivee = LocalDateTime.of(
                        champDateArrivee.getValue(),
                        LocalTime.of(champHeureArrivee.getValue(), champMinuteArrivee.getValue())
                    );
                    nouveauTrajet.setDateHeureArriveeEstimee(dateHeureArrivee);
                }
                
                nouveauTrajet.setBus(comboBus.getValue());
                nouveauTrajet.setChauffeur(comboChauffeur.getValue());
                nouveauTrajet.setPrix(Double.parseDouble(champPrix.getText().trim()));
                nouveauTrajet.setStatutTrajet(comboStatut.getValue());
                nouveauTrajet.setCommentaires(champCommentaires.getText().trim());
                
                // Définir le nombre de places disponibles si c'est un nouveau trajet
                if (trajet == null && comboBus.getValue() != null) {
                    nouveauTrajet.setNombrePlacesDisponibles(comboBus.getValue().getCapacite());
                }

                boolean succes = trajet == null ? 
                    trajetDAO.creerTrajet(nouveauTrajet) : 
                    trajetDAO.mettreAJourTrajet(nouveauTrajet);

                if (succes) {
                    chargerTrajets();
                    afficherAlerte("Succès", "Trajet " + (trajet == null ? "ajouté" : "modifié") + " avec succès.");
                    fenetreFormulaire.close();
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la sauvegarde du trajet.");
                }
            }
        });

        boutonAnnuler.setOnAction(e -> fenetreFormulaire.close());

        conteneur.getChildren().addAll(
            new Label("Ville de départ:"), champVilleDepart,
            new Label("Ville d'arrivée:"), champVilleArrivee,
            new Label("Date de départ:"), champDateDepart,
            new Label("Heure de départ:"), new HBox(5, champHeureDepart, new Label("h"), champMinuteDepart),
            new Label("Date d'arrivée estimée:"), champDateArrivee,
            new Label("Heure d'arrivée estimée:"), new HBox(5, champHeureArrivee, new Label("h"), champMinuteArrivee),
            new Label("Bus:"), comboBus,
            new Label("Chauffeur:"), comboChauffeur,
            new Label("Prix (FCFA):"), champPrix,
            new Label("Statut:"), comboStatut,
            new Label("Commentaires:"), champCommentaires,
            boutons
        );

        Scene scene = new Scene(new ScrollPane(conteneur), 450, 700);
        fenetreFormulaire.setScene(scene);
        fenetreFormulaire.showAndWait();
    }

    private boolean validerFormulaire(TextField villeDepart, TextField villeArrivee, DatePicker dateDepart, 
                                    ComboBox<Bus> comboBus, ComboBox<Chauffeur> comboChauffeur, TextField prix) {
        if (villeDepart.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "La ville de départ est obligatoire.");
            return false;
        }
        if (villeArrivee.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "La ville d'arrivée est obligatoire.");
            return false;
        }
        if (dateDepart.getValue() == null) {
            afficherAlerte("Erreur de validation", "La date de départ est obligatoire.");
            return false;
        }
        if (comboBus.getValue() == null) {
            afficherAlerte("Erreur de validation", "Veuillez sélectionner un bus.");
            return false;
        }
        if (comboChauffeur.getValue() == null) {
            afficherAlerte("Erreur de validation", "Veuillez sélectionner un chauffeur.");
            return false;
        }
        try {
            double prixValue = Double.parseDouble(prix.getText().trim());
            if (prixValue <= 0) {
                afficherAlerte("Erreur de validation", "Le prix doit être supérieur à 0.");
                return false;
            }
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de validation", "Le prix doit être un nombre valide.");
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