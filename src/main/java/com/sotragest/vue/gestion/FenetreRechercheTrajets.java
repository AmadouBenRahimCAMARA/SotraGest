package com.sotragest.vue.gestion;

import com.sotragest.dao.TrajetDAO;
import com.sotragest.modele.Trajet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


// Fenêtre de recherche de trajets

public class FenetreRechercheTrajets {
    private VBox conteneurPrincipal;
    private TableView<Trajet> tableauTrajets;
    private ObservableList<Trajet> listeTrajets;
    private TrajetDAO trajetDAO;
    
    private TextField champVilleDepart;
    private TextField champVilleArrivee;
    private DatePicker dateDebut;
    private DatePicker dateFin;
    private ComboBox<String> comboStatut;

    public FenetreRechercheTrajets() {
        this.trajetDAO = new TrajetDAO();
        this.listeTrajets = FXCollections.observableArrayList();
        initialiserInterface();
        chargerTrajets();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre
        Label titre = new Label("Recherche de Trajets");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#2c3e50"));

        // Formulaire de recherche
        VBox formulaireRecherche = creerFormulaireRecherche();

        // Tableau des trajets
        tableauTrajets = creerTableauTrajets();

        conteneurPrincipal.getChildren().addAll(titre, formulaireRecherche, tableauTrajets);
    }

    private VBox creerFormulaireRecherche() {
        VBox formulaire = new VBox(15);
        formulaire.setPadding(new Insets(20));
        formulaire.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        Label titreFormulaire = new Label("Critères de recherche");
        titreFormulaire.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Ligne 1: Villes
        HBox ligneVilles = new HBox(15);
        ligneVilles.setAlignment(Pos.CENTER_LEFT);

        Label labelDepart = new Label("Ville de départ:");
        champVilleDepart = new TextField();
        champVilleDepart.setPromptText("Ex: Abidjan");
        champVilleDepart.setPrefWidth(150);

        Label labelArrivee = new Label("Ville d'arrivée:");
        champVilleArrivee = new TextField();
        champVilleArrivee.setPromptText("Ex: Bouaké");
        champVilleArrivee.setPrefWidth(150);

        ligneVilles.getChildren().addAll(labelDepart, champVilleDepart, labelArrivee, champVilleArrivee);

        // Ligne 2: Dates et statut
        HBox ligneDatesStatut = new HBox(15);
        ligneDatesStatut.setAlignment(Pos.CENTER_LEFT);

        Label labelDateDebut = new Label("Du:");
        dateDebut = new DatePicker();
        dateDebut.setPromptText("Date début");

        Label labelDateFin = new Label("Au:");
        dateFin = new DatePicker();
        dateFin.setPromptText("Date fin");

        Label labelStatut = new Label("Statut:");
        comboStatut = new ComboBox<>();
        comboStatut.getItems().addAll("Tous", "PROGRAMME", "EN_COURS", "TERMINE", "ANNULE", "REPORTE");
        comboStatut.setValue("Tous");

        ligneDatesStatut.getChildren().addAll(labelDateDebut, dateDebut, labelDateFin, dateFin, labelStatut, comboStatut);

        // Boutons
        HBox boutons = new HBox(10);
        boutons.setAlignment(Pos.CENTER_LEFT);

        Button boutonRechercher = new Button("🔍 Rechercher");
        Button boutonVider = new Button("🗑️ Vider");
        Button boutonTout = new Button("📋 Tout afficher");

        boutonRechercher.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
        boutonVider.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5;");
        boutonTout.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;");

        boutonRechercher.setOnAction(e -> rechercherTrajets());
        boutonVider.setOnAction(e -> viderFormulaire());
        boutonTout.setOnAction(e -> chargerTrajets());

        boutons.getChildren().addAll(boutonRechercher, boutonVider, boutonTout);

        formulaire.getChildren().addAll(titreFormulaire, ligneVilles, ligneDatesStatut, boutons);
        return formulaire;
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

        TableColumn<Trajet, String> colDateArrivee = new TableColumn<>("Arrivée estimée");
        colDateArrivee.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateHeureArriveeEstimee() != null ?
                cellData.getValue().getDateHeureArriveeEstimee().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : ""));
        colDateArrivee.setPrefWidth(140);

        TableColumn<Trajet, String> colBus = new TableColumn<>("Bus");
        colBus.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getBus() != null ? cellData.getValue().getBus().getDescriptionComplete() : ""));
        colBus.setPrefWidth(180);

        TableColumn<Trajet, String> colChauffeur = new TableColumn<>("Chauffeur");
        colChauffeur.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getChauffeur() != null ? cellData.getValue().getChauffeur().getNomCompletChauffeur() : ""));
        colChauffeur.setPrefWidth(150);

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

        // Colonne avec indicateur de disponibilité
        TableColumn<Trajet, String> colDisponibilite = new TableColumn<>("Disponibilité");
        colDisponibilite.setCellValueFactory(cellData -> {
            Trajet trajet = cellData.getValue();
            String statut = trajet.aDesPlacesDisponibles() && 
                           trajet.getStatutTrajet() == Trajet.StatutTrajet.PROGRAMME ? "✅ Disponible" : "❌ Indisponible";
            return new javafx.beans.property.SimpleStringProperty(statut);
        });
        colDisponibilite.setPrefWidth(100);

        tableau.getColumns().addAll(colDepart, colArrivee, colDateDepart, colDateArrivee, 
                                   colBus, colChauffeur, colPrix, colPlaces, colStatut, colDisponibilite);

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
                    } else if (!trajet.aDesPlacesDisponibles()) {
                        setStyle("-fx-background-color: #fff3e0;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        return tableau;
    }

    private void chargerTrajets() {
        List<Trajet> trajets = trajetDAO.obtenirTousLesTrajets();
        listeTrajets.clear();
        listeTrajets.addAll(trajets);
    }

    private void rechercherTrajets() {
        String villeDepart = champVilleDepart.getText().trim();
        String villeArrivee = champVilleArrivee.getText().trim();
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();

        // Validation des dates
        if (debut != null && fin != null && debut.isAfter(fin)) {
            afficherAlerte("Erreur", "La date de début doit être antérieure à la date de fin.");
            return;
        }

        LocalDateTime dateTimeDebut = debut != null ? debut.atStartOfDay() : null;
        LocalDateTime dateTimeFin = fin != null ? fin.atTime(23, 59, 59) : null;

        // Recherche
        List<Trajet> resultats = trajetDAO.rechercherTrajets(
            villeDepart.isEmpty() ? null : villeDepart,
            villeArrivee.isEmpty() ? null : villeArrivee,
            dateTimeDebut,
            dateTimeFin
        );

        // Filtrer par statut si nécessaire
        String statutSelectionne = comboStatut.getValue();
        if (!"Tous".equals(statutSelectionne)) {
            resultats = resultats.stream()
                .filter(t -> t.getStatutTrajet().name().equals(statutSelectionne))
                .toList();
        }

        listeTrajets.clear();
        listeTrajets.addAll(resultats);

        // Afficher le nombre de résultats
        afficherAlerte("Résultats", "Recherche terminée: " + resultats.size() + " trajet(s) trouvé(s).");
    }

    private void viderFormulaire() {
        champVilleDepart.clear();
        champVilleArrivee.clear();
        dateDebut.setValue(null);
        dateFin.setValue(null);
        comboStatut.setValue("Tous");
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