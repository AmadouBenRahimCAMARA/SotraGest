package com.sotragest.vue.gestion;

import com.sotragest.dao.BusDAO;
import com.sotragest.modele.Bus;
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

import java.util.List;
import java.util.Optional;


// Fenêtre de gestion des bus

public class FenetreGestionBus {
    private VBox conteneurPrincipal;
    private TableView<Bus> tableauBus;
    private ObservableList<Bus> listeBus;
    private BusDAO busDAO;

    public FenetreGestionBus() {
        this.busDAO = new BusDAO();
        this.listeBus = FXCollections.observableArrayList();
        initialiserInterface();
        chargerBus();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre
        Label titre = new Label("Gestion des Bus");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#2c3e50"));

        // Barre d'outils
        HBox barreOutils = creerBarreOutils();

        // Tableau des bus
        tableauBus = creerTableauBus();

        conteneurPrincipal.getChildren().addAll(titre, barreOutils, tableauBus);
    }

    private HBox creerBarreOutils() {
        HBox barreOutils = new HBox(10);
        barreOutils.setAlignment(Pos.CENTER_LEFT);
        barreOutils.setPadding(new Insets(10));

        Button boutonAjouter = new Button("➕ Ajouter un bus");
        Button boutonModifier = new Button("✏️ Modifier");
        Button boutonSupprimer = new Button("🗑️ Supprimer");
        Button boutonActualiser = new Button("🔄 Actualiser");

        // Style des boutons
        boutonAjouter.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonActualiser.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        // Événements
        boutonAjouter.setOnAction(e -> ouvrirFormulaireBus(null));
        boutonModifier.setOnAction(e -> modifierBusSelectionne());
        boutonSupprimer.setOnAction(e -> supprimerBusSelectionne());
        boutonActualiser.setOnAction(e -> chargerBus());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        barreOutils.getChildren().addAll(boutonAjouter, boutonModifier, boutonSupprimer, spacer, boutonActualiser);
        return barreOutils;
    }

    private TableView<Bus> creerTableauBus() {
        TableView<Bus> tableau = new TableView<>();
        tableau.setItems(listeBus);
        tableau.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableau.getColumns().clear(); // Pour éviter tout ajout automatique de colonne

        // Colonnes
        TableColumn<Bus, String> colMatricule = new TableColumn<>("Matricule");
        colMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        colMatricule.setPrefWidth(120);

        TableColumn<Bus, String> colMarque = new TableColumn<>("Marque");
        colMarque.setCellValueFactory(new PropertyValueFactory<>("marque"));
        colMarque.setPrefWidth(100);

        TableColumn<Bus, String> colModele = new TableColumn<>("Modèle");
        colModele.setCellValueFactory(new PropertyValueFactory<>("modele"));
        colModele.setPrefWidth(120);

        TableColumn<Bus, Integer> colAnnee = new TableColumn<>("Année");
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colAnnee.setPrefWidth(80);

        TableColumn<Bus, Integer> colCapacite = new TableColumn<>("Capacité");
        colCapacite.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        colCapacite.setPrefWidth(80);

        TableColumn<Bus, String> colCouleur = new TableColumn<>("Couleur");
        colCouleur.setCellValueFactory(new PropertyValueFactory<>("couleur"));
        colCouleur.setPrefWidth(100);

        TableColumn<Bus, String> colClimatisation = new TableColumn<>("Climatisation");
        colClimatisation.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isClimatisation() ? "✅ Oui" : "❌ Non"));
        colClimatisation.setPrefWidth(100);

        TableColumn<Bus, String> colEtat = new TableColumn<>("État");
        colEtat.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEtat().getLibelle()));
        colEtat.setPrefWidth(120);

        TableColumn<Bus, String> colKilometrage = new TableColumn<>("Kilométrage");
        colKilometrage.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.0f km", cellData.getValue().getKilometrage())));
        colKilometrage.setPrefWidth(100);

        TableColumn<Bus, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isActifBus() ? "✅ Actif" : "❌ Inactif"));
        colStatut.setPrefWidth(80);

        tableau.getColumns().addAll(colMatricule, colMarque, colModele, colAnnee, colCapacite, 
                                   colCouleur, colClimatisation, colEtat, colKilometrage, colStatut);
        
        
        // Style conditionnel pour les lignes
        tableau.setRowFactory(tv -> new TableRow<Bus>() {
            @Override
            protected void updateItem(Bus bus, boolean empty) {
                super.updateItem(bus, empty);
                if (empty || bus == null) {
                    setStyle("");
                } else {
                    if (bus.getEtat() == Bus.EtatBus.HORS_SERVICE) {
                        setStyle("-fx-background-color: #ffebee;");
                    } else if (bus.getEtat() == Bus.EtatBus.EXCELLENT) {
                        setStyle("-fx-background-color: #e8f5e8;");
                    } else if (bus.getEtat() == Bus.EtatBus.MAUVAIS) {
                        setStyle("-fx-background-color: #fff3e0;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Double-clic pour modifier
        tableau.setRowFactory(tv -> {
            TableRow<Bus> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ouvrirFormulaireBus(row.getItem());
                }
            });
            return row;
        });

        return tableau;
    }

    private void chargerBus() {
        List<Bus> bus = busDAO.obtenirTousLesBus();
        listeBus.clear();
        listeBus.addAll(bus);
    }

    private void modifierBusSelectionne() {
        Bus busSelectionne = tableauBus.getSelectionModel().getSelectedItem();
        if (busSelectionne != null) {
            ouvrirFormulaireBus(busSelectionne);
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un bus à modifier.");
        }
    }

    private void supprimerBusSelectionne() {
        Bus busSelectionne = tableauBus.getSelectionModel().getSelectedItem();
        if (busSelectionne != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer le bus");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le bus " + busSelectionne.getMatricule() + " ?");

            Optional<ButtonType> resultat = confirmation.showAndWait();
            if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                if (busDAO.supprimerBus(busSelectionne.getIdBus())) {
                    chargerBus();
                    afficherAlerte("Succès", "Bus supprimé avec succès.");
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la suppression du bus.");
                }
            }
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un bus à supprimer.");
        }
    }

    private void ouvrirFormulaireBus(Bus bus) {
        Stage fenetreFormulaire = new Stage();
        fenetreFormulaire.initModality(Modality.APPLICATION_MODAL);
        fenetreFormulaire.setTitle(bus == null ? "Ajouter un bus" : "Modifier le bus");

        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(20));

        // Champs du formulaire
        TextField champMatricule = new TextField(bus != null ? bus.getMatricule() : "");
        champMatricule.setPromptText("Matricule (ex: AA-001-CI)");

        TextField champMarque = new TextField(bus != null ? bus.getMarque() : "");
        champMarque.setPromptText("Marque");

        TextField champModele = new TextField(bus != null ? bus.getModele() : "");
        champModele.setPromptText("Modèle");

        Spinner<Integer> champAnnee = new Spinner<>(1990, 2030, bus != null ? bus.getAnnee() : 2020);
        champAnnee.setEditable(true);

        Spinner<Integer> champCapacite = new Spinner<>(10, 100, bus != null ? bus.getCapacite() : 25);
        champCapacite.setEditable(true);

        TextField champCouleur = new TextField(bus != null ? bus.getCouleur() : "");
        champCouleur.setPromptText("Couleur");

        CheckBox champClimatisation = new CheckBox("Climatisation");
        if (bus != null) {
            champClimatisation.setSelected(bus.isClimatisation());
        }

        ComboBox<Bus.EtatBus> champEtat = new ComboBox<>();
        champEtat.getItems().addAll(Bus.EtatBus.values());
        if (bus != null) {
            champEtat.setValue(bus.getEtat());
        } else {
            champEtat.setValue(Bus.EtatBus.BON);
        }

        TextField champKilometrage = new TextField(bus != null ? String.valueOf(bus.getKilometrage()) : "0");
        champKilometrage.setPromptText("Kilométrage");

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
            if (validerFormulaire(champMatricule, champMarque, champModele, champKilometrage)) {
                Bus nouveauBus = bus != null ? bus : new Bus();
                
                nouveauBus.setMatricule(champMatricule.getText().trim());
                nouveauBus.setMarque(champMarque.getText().trim());
                nouveauBus.setModele(champModele.getText().trim());
                nouveauBus.setAnnee(champAnnee.getValue());
                nouveauBus.setCapacite(champCapacite.getValue());
                nouveauBus.setCouleur(champCouleur.getText().trim());
                nouveauBus.setClimatisation(champClimatisation.isSelected());
                nouveauBus.setEtat(champEtat.getValue());
                
                try {
                    nouveauBus.setKilometrage(Double.parseDouble(champKilometrage.getText().trim()));
                } catch (NumberFormatException ex) {
                    nouveauBus.setKilometrage(0);
                }

                boolean succes = bus == null ? 
                    busDAO.creerBus(nouveauBus) : 
                    busDAO.mettreAJourBus(nouveauBus);

                if (succes) {
                    chargerBus();
                    afficherAlerte("Succès", "Bus " + (bus == null ? "ajouté" : "modifié") + " avec succès.");
                    fenetreFormulaire.close();
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la sauvegarde du bus.");
                }
            }
        });

        boutonAnnuler.setOnAction(e -> fenetreFormulaire.close());

        conteneur.getChildren().addAll(
            new Label("Matricule:"), champMatricule,
            new Label("Marque:"), champMarque,
            new Label("Modèle:"), champModele,
            new Label("Année:"), champAnnee,
            new Label("Capacité:"), champCapacite,
            new Label("Couleur:"), champCouleur,
            champClimatisation,
            new Label("État:"), champEtat,
            new Label("Kilométrage:"), champKilometrage,
            boutons
        );

        Scene scene = new Scene(new ScrollPane(conteneur), 400, 600);
        fenetreFormulaire.setScene(scene);
        fenetreFormulaire.showAndWait();
    }

    private boolean validerFormulaire(TextField matricule, TextField marque, TextField modele, TextField kilometrage) {
        if (matricule.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "Le matricule est obligatoire.");
            return false;
        }
        if (marque.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "La marque est obligatoire.");
            return false;
        }
        if (modele.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "Le modèle est obligatoire.");
            return false;
        }
        try {
            Double.parseDouble(kilometrage.getText().trim());
        } catch (NumberFormatException e) {
            afficherAlerte("Erreur de validation", "Le kilométrage doit être un nombre valide.");
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