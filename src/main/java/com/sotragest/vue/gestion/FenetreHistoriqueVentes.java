package com.sotragest.vue.gestion;

import com.sotragest.dao.TicketDAO;
import com.sotragest.modele.Ticket;
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
import java.util.Optional;

import com.sotragest.modele.*;


// Fenêtre d'historique des ventes
 
public class FenetreHistoriqueVentes {
    private VBox conteneurPrincipal;
    private Utilisateur vendeur;
    private TableView<Ticket> tableauTickets;
    private ObservableList<Ticket> listeTickets;
    private TicketDAO ticketDAO;
    private TextField champRecherche;
    private DatePicker dateDebut;
    private DatePicker dateFin;
    private Label labelStatistiques;

    public FenetreHistoriqueVentes() {
        this.ticketDAO = new TicketDAO();
        this.listeTickets = FXCollections.observableArrayList();
        initialiserInterface();
        chargerTickets();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre
        Label titre = new Label("Historique des Ventes");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#2c3e50"));

        // Filtres et recherche
        VBox filtres = creerFiltres();

        // Statistiques
        labelStatistiques = new Label();
        labelStatistiques.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        labelStatistiques.setTextFill(Color.web("#27ae60"));

        // Tableau des tickets
        tableauTickets = creerTableauTickets();

        conteneurPrincipal.getChildren().addAll(titre, filtres, labelStatistiques, tableauTickets);
    }

    private VBox creerFiltres() {
        VBox conteneurFiltres = new VBox(15);
        conteneurFiltres.setPadding(new Insets(15));
        conteneurFiltres.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        Label titreFiltres = new Label("Filtres et Recherche");
        titreFiltres.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Ligne 1: Recherche
        HBox ligneRecherche = new HBox(10);
        ligneRecherche.setAlignment(Pos.CENTER_LEFT);

        Label labelRecherche = new Label("Recherche:");
        champRecherche = new TextField();
        champRecherche.setPromptText("Numéro ticket, nom voyageur, trajet...");
        champRecherche.setPrefWidth(300);
        champRecherche.textProperty().addListener((obs, oldText, newText) -> rechercherTickets());

        Button boutonViderRecherche = new Button("🗑️");
        boutonViderRecherche.setOnAction(e -> {
            champRecherche.clear();
            chargerTickets();
        });

        ligneRecherche.getChildren().addAll(labelRecherche, champRecherche, boutonViderRecherche);

        // Ligne 2: Filtres par date
        HBox ligneDates = new HBox(15);
        ligneDates.setAlignment(Pos.CENTER_LEFT);

        Label labelDateDebut = new Label("Du:");
        dateDebut = new DatePicker();
        dateDebut.setPromptText("Date début");

        Label labelDateFin = new Label("Au:");
        dateFin = new DatePicker();
        dateFin.setPromptText("Date fin");

        Button boutonFiltrer = new Button("🔍 Filtrer");
        boutonFiltrer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
        boutonFiltrer.setOnAction(e -> filtrerParPeriode());

        Button boutonTout = new Button("📋 Tout afficher");
        boutonTout.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5;");
        boutonTout.setOnAction(e -> {
            dateDebut.setValue(null);
            dateFin.setValue(null);
            chargerTickets();
        });

        ligneDates.getChildren().addAll(labelDateDebut, dateDebut, labelDateFin, dateFin, boutonFiltrer, boutonTout);

        conteneurFiltres.getChildren().addAll(titreFiltres, ligneRecherche, ligneDates);
        return conteneurFiltres;
    }

    private TableView<Ticket> creerTableauTickets() {
        TableView<Ticket> tableau = new TableView<>();
        tableau.setItems(listeTickets);

        // Colonnes
        TableColumn<Ticket, String> colNumero = new TableColumn<>("N° Ticket");
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numeroTicket"));
        colNumero.setPrefWidth(120);

        TableColumn<Ticket, String> colVoyageur = new TableColumn<>("Voyageur");
        colVoyageur.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVoyageur().getNomCompletVoyageur()));
        colVoyageur.setPrefWidth(150);

        TableColumn<Ticket, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getVoyageur().getTelephoneVoyageur()));
        colTelephone.setPrefWidth(120);

        TableColumn<Ticket, String> colTrajet = new TableColumn<>("Trajet");
        colTrajet.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTrajet().getItineraire()));
        colTrajet.setPrefWidth(200);

        TableColumn<Ticket, String> colDateDepart = new TableColumn<>("Date départ");
        colDateDepart.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getTrajet().getDateHeureDepart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colDateDepart.setPrefWidth(130);

        TableColumn<Ticket, Integer> colSiege = new TableColumn<>("Siège");
        colSiege.setCellValueFactory(new PropertyValueFactory<>("numeroSiege"));
        colSiege.setPrefWidth(60);

        TableColumn<Ticket, String> colPrix = new TableColumn<>("Prix");
        colPrix.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%.0f FCFA", cellData.getValue().getPrix())));
        colPrix.setPrefWidth(100);

        TableColumn<Ticket, String> colStatut = new TableColumn<>("Statut");
        colStatut.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatutTicket().getLibelle()));
        colStatut.setPrefWidth(80);

        TableColumn<Ticket, String> colDateVente = new TableColumn<>("Date vente");
        colDateVente.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateVente().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colDateVente.setPrefWidth(130);

        TableColumn<Ticket, String> colVendeur = new TableColumn<>("Vendeur");
        colVendeur.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getVendeur() != null ? cellData.getValue().getVendeur().getNomCompletUtilisateur() : ""));
        colVendeur.setPrefWidth(120);

        // Colonne d'actions
        TableColumn<Ticket, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(120);
        colActions.setCellFactory(param -> new TableCell<Ticket, Void>() {
            private final Button boutonAnnuler = new Button("❌");
            private final Button boutonReimprimer = new Button("🖨️");

            {
                boutonAnnuler.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 3;");
                boutonReimprimer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 3;");
                
                boutonAnnuler.setOnAction(e -> {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    annulerTicket(ticket);
                });
                
                boutonReimprimer.setOnAction(e -> {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    reimprimerTicket(ticket);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Ticket ticket = getTableView().getItems().get(getIndex());
                    HBox boutons = new HBox(5);
                    boutons.setAlignment(Pos.CENTER);
                    
                    if (ticket.getStatutTicket() == Ticket.StatutTicket.VALIDE) {
                        boutons.getChildren().addAll(boutonAnnuler, boutonReimprimer);
                    } else {
                        boutons.getChildren().add(boutonReimprimer);
                    }
                    
                    setGraphic(boutons);
                }
            }
        });

        tableau.getColumns().addAll(colNumero, colVoyageur, colTelephone, colTrajet, colDateDepart, 
                                   colSiege, colPrix, colStatut, colDateVente, colVendeur, colActions);

        return tableau;
    }

    private void chargerTickets() {
        List<Ticket> tickets = ticketDAO.obtenirTousLesTickets();
        listeTickets.clear();
        listeTickets.addAll(tickets);
        mettreAJourStatistiques();
    }

    private void rechercherTickets() {
        String critere = champRecherche.getText();
        if (critere == null || critere.trim().isEmpty()) {
            chargerTickets();
        } else {
            List<Ticket> resultats = ticketDAO.rechercherTickets(critere.trim());
            listeTickets.clear();
            listeTickets.addAll(resultats);
            mettreAJourStatistiques();
        }
    }

    private void filtrerParPeriode() {
        LocalDate debut = dateDebut.getValue();
        LocalDate fin = dateFin.getValue();

        if (debut != null && fin != null) {
            if (debut.isAfter(fin)) {
                afficherAlerte("Erreur", "La date de début doit être antérieure à la date de fin.");
                return;
            }

            LocalDateTime dateTimeDebut = debut.atStartOfDay();
            LocalDateTime dateTimeFin = fin.atTime(23, 59, 59);

            List<Ticket> tickets = ticketDAO.obtenirTicketsParPeriode(dateTimeDebut, dateTimeFin);
            listeTickets.clear();
            listeTickets.addAll(tickets);
            mettreAJourStatistiques();
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner une période complète.");
        }
    }

    private void mettreAJourStatistiques() {
        int nombreTickets = listeTickets.size();
        double chiffreAffaires = listeTickets.stream()
            .filter(t -> t.getStatutTicket() != Ticket.StatutTicket.ANNULE)
            .mapToDouble(Ticket::getPrix)
            .sum();

        long ticketsValides = listeTickets.stream()
            .filter(t -> t.getStatutTicket() == Ticket.StatutTicket.VALIDE)
            .count();

        long ticketsUtilises = listeTickets.stream()
            .filter(t -> t.getStatutTicket() == Ticket.StatutTicket.UTILISE)
            .count();

        long ticketsAnnules = listeTickets.stream()
            .filter(t -> t.getStatutTicket() == Ticket.StatutTicket.ANNULE)
            .count();

        labelStatistiques.setText(String.format(
            "📊 Total: %d tickets | 💰 CA: %.0f FCFA | ✅ Valides: %d | ✈️ Utilisés: %d | ❌ Annulés: %d",
            nombreTickets, chiffreAffaires, ticketsValides, ticketsUtilises, ticketsAnnules
        ));
    }

    private void annulerTicket(Ticket ticket) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation d'annulation");
        confirmation.setHeaderText("Annuler le ticket");
        confirmation.setContentText("Êtes-vous sûr de vouloir annuler le ticket " + ticket.getNumeroTicket() + " ?");

        Optional<ButtonType> resultat = confirmation.showAndWait();
        if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
            if (ticketDAO.annulerTicket(ticket.getIdTicket())) {
                chargerTickets();
                afficherAlerte("Succès", "Ticket annulé avec succès.");
            } else {
                afficherAlerte("Erreur", "Erreur lors de l'annulation du ticket.");
            }
        }
    }

    private void reimprimerTicket(Ticket ticket) {
        try {
            com.sotragest.utilitaires.GenerateurPDF.genererTicketPDF(ticket, ticket.getVendeur());
            afficherAlerte("Succès", "Ticket réimprimé avec succès.");
        } catch (Exception e) {
            afficherAlerte("Erreur", "Erreur lors de la réimpression: " + e.getMessage());
        }
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