package com.sotragest.vue.gestion;

import com.sotragest.dao.*;
import com.sotragest.modele.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// Fenêtre des statistiques

public class FenetreStatistiques {
    private VBox conteneurPrincipal;
    private ScrollPane scrollPane; // Ajout du ScrollPane
    private VoyageurDAO voyageurDAO;
    private ChauffeurDAO chauffeurDAO;
    private BusDAO busDAO;
    private TrajetDAO trajetDAO;
    private TicketDAO ticketDAO;

    public FenetreStatistiques() {
        this.voyageurDAO = new VoyageurDAO();
        this.chauffeurDAO = new ChauffeurDAO();
        this.busDAO = new BusDAO();
        this.trajetDAO = new TrajetDAO();
        this.ticketDAO = new TicketDAO();
        initialiserInterface();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre
        Label titre = new Label("Statistiques et Analyses");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#2c3e50"));

        // Statistiques générales
        VBox statistiquesGenerales = creerStatistiquesGenerales();

        // Graphiques
        HBox graphiques = creerGraphiques();

        // Tableaux de données
        VBox tableauxDonnees = creerTableauxDonnees();

        conteneurPrincipal.getChildren().addAll(titre, statistiquesGenerales, graphiques, tableauxDonnees);

        // Encapsuler le conteneur dans un ScrollPane
        scrollPane = new ScrollPane(conteneurPrincipal);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background: #ecf0f1;");
    }

    private VBox creerStatistiquesGenerales() {
        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(20));
        conteneur.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        FontAwesomeIconView iconeTitre = new FontAwesomeIconView(FontAwesomeIcon.BAR_CHART);
        iconeTitre.setSize("1.5em");
        Label titreSection = new Label("Statistiques Générales", iconeTitre);
        titreSection.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titreSection.setTextFill(Color.web("#2c3e50"));

        // Récupérer les données
        List<Voyageur> voyageurs = voyageurDAO.obtenirTousLesVoyageurs();
        List<Chauffeur> chauffeurs = chauffeurDAO.obtenirTousLesChauffeurs();
        List<Bus> bus = busDAO.obtenirTousLesBus();
        List<Trajet> trajets = trajetDAO.obtenirTousLesTrajets();
        List<Ticket> tickets = ticketDAO.obtenirTousLesTickets();

        // Calculs
        double chiffreAffairesTotal = tickets.stream()
            .filter(t -> t.getStatutTicket() != Ticket.StatutTicket.ANNULE)
            .mapToDouble(Ticket::getPrix)
            .sum();

        long trajetsAujourdhui = trajets.stream()
            .filter(t -> t.getDateHeureDepart().toLocalDate().equals(LocalDate.now()))
            .count();

        long busDisponibles = bus.stream()
            .filter(Bus::estDisponible)
            .count();

        long chauffeursActifs = chauffeurs.stream()
            .filter(c -> c.isActifChauffeur() && c.permisValide())
            .count();

        // Grille de statistiques
        GridPane grille = new GridPane();
        grille.setHgap(20);
        grille.setVgap(15);
        grille.setPadding(new Insets(10));

        // Ligne 1
        grille.add(creerCarteStatistique(FontAwesomeIcon.USERS, "Total Voyageurs", String.valueOf(voyageurs.size()), "#3498db"), 0, 0);
        grille.add(creerCarteStatistique(FontAwesomeIcon.USER_SECRET, "Chauffeurs Actifs", String.valueOf(chauffeursActifs), "#e67e22"), 1, 0);
        grille.add(creerCarteStatistique(FontAwesomeIcon.BUS, "Bus Disponibles", String.valueOf(busDisponibles), "#f39c12"), 2, 0);
        grille.add(creerCarteStatistique(FontAwesomeIcon.ROAD, "Total Trajets", String.valueOf(trajets.size()), "#9b59b6"), 3, 0);

        // Ligne 2
        grille.add(creerCarteStatistique(FontAwesomeIcon.TICKET, "Tickets Vendus", String.valueOf(tickets.size()), "#9b59b6"), 0, 1);
        grille.add(creerCarteStatistique(FontAwesomeIcon.MONEY, "CA Total", String.format("%.0f FCFA", chiffreAffairesTotal), "#27ae60"), 1, 1);
        grille.add(creerCarteStatistique(FontAwesomeIcon.CALENDAR, "Trajets Aujourd'hui", String.valueOf(trajetsAujourdhui), "#3498db"), 2, 1);
        grille.add(creerCarteStatistique(FontAwesomeIcon.LINE_CHART, "CA Moyen/Ticket", 
            String.format("%.0f FCFA", tickets.isEmpty() ? 0 : chiffreAffairesTotal / tickets.size()), "#1abc9c"), 3, 1);

        conteneur.getChildren().addAll(titreSection, grille);
        return conteneur;
    }

    private VBox creerCarteStatistique(FontAwesomeIcon icone, String titre, String valeur, String couleur) {
        VBox carte = new VBox(8);
        carte.setAlignment(Pos.CENTER);
        carte.setPadding(new Insets(15));
        carte.setPrefWidth(180);
        carte.setPrefHeight(100);
        carte.setStyle(
            "-fx-background-color: #e5d3b8; " +
            "-fx-border-color: " + couleur + "; " +
            "-fx-border-width: 2; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8;"
        );

        FontAwesomeIconView labelIcone = new FontAwesomeIconView(icone);
        labelIcone.setSize("2em");
        labelIcone.setFill(Color.web("#000000"));

        Label labelTitre = new Label(titre);
        labelTitre.setFont(Font.font("Arial", FontWeight.MEDIUM, 12));
        labelTitre.setTextFill(Color.web("#000000"));

        Label labelValeur = new Label(valeur);
        labelValeur.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        labelValeur.setTextFill(Color.web(couleur));

        carte.getChildren().addAll(labelIcone, labelTitre, labelValeur);
        return carte;
    }

    private HBox creerGraphiques() {
        HBox conteneur = new HBox(20);
        conteneur.setPadding(new Insets(10));

        // Graphique des ventes par statut
        PieChart graphiqueStatuts = creerGraphiqueStatutsTickets();
        
        // Graphique des trajets par mois
        BarChart<String, Number> graphiqueTrajets = creerGraphiqueTrajetsParMois();

        conteneur.getChildren().addAll(graphiqueStatuts, graphiqueTrajets);
        return conteneur;
    }

    private PieChart creerGraphiqueStatutsTickets() {
        List<Ticket> tickets = ticketDAO.obtenirTousLesTickets();
        
        Map<Ticket.StatutTicket, Long> statutsCount = tickets.stream()
            .collect(Collectors.groupingBy(Ticket::getStatutTicket, Collectors.counting()));

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Répartition des Tickets par Statut");
        pieChart.setPrefSize(350, 300);

        for (Map.Entry<Ticket.StatutTicket, Long> entry : statutsCount.entrySet()) {
            PieChart.Data slice = new PieChart.Data(entry.getKey().getLibelle(), entry.getValue());
            pieChart.getData().add(slice);
        }

        return pieChart;
    }

    private BarChart<String, Number> creerGraphiqueTrajetsParMois() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Nombre de Trajets par Mois");
        barChart.setPrefSize(400, 300);

        List<Trajet> trajets = trajetDAO.obtenirTousLesTrajets();
        
        Map<String, Long> trajetsParMois = trajets.stream()
            .collect(Collectors.groupingBy(
                t -> t.getDateHeureDepart().format(DateTimeFormatter.ofPattern("MM/yyyy")),
                Collectors.counting()
            ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Trajets");

        for (Map.Entry<String, Long> entry : trajetsParMois.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        barChart.getData().add(series);
        return barChart;
    }

    private VBox creerTableauxDonnees() {
        VBox conteneur = new VBox(20);

        // Top 5 des trajets les plus fréquentés
        VBox topTrajets = creerTopTrajets();
        
        // Statistiques des chauffeurs
        VBox statsChauffeurs = creerStatistiquesChauffeurs();

        conteneur.getChildren().addAll(topTrajets, statsChauffeurs);
        return conteneur;
    }

    private VBox creerTopTrajets() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(15));
        conteneur.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        FontAwesomeIconView iconeTitre = new FontAwesomeIconView(FontAwesomeIcon.TROPHY);
        iconeTitre.setSize("1.5em");
        Label titre = new Label("Top 5 des Trajets les Plus Demandés", iconeTitre);
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web("#2c3e50"));

        // Calculer les trajets les plus fréquentés
        List<Ticket> tickets = ticketDAO.obtenirTousLesTickets();
        Map<String, Long> trajetsCount = tickets.stream()
            .filter(t -> t.getStatutTicket() != Ticket.StatutTicket.ANNULE)
            .collect(Collectors.groupingBy(
                t -> t.getTrajet().getItineraire(),
                Collectors.counting()
            ));

        VBox listeTop = new VBox(5);
        trajetsCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .forEach(entry -> {
                HBox ligne = new HBox(10);
                ligne.setAlignment(Pos.CENTER_LEFT);
                ligne.setPadding(new Insets(5));
                ligne.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5;");

                Label trajet = new Label(entry.getKey());
                trajet.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label count = new Label(entry.getValue() + " tickets");
                count.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                count.setTextFill(Color.web("#27ae60"));

                ligne.getChildren().addAll(trajet, spacer, count);
                listeTop.getChildren().add(ligne);
            });

        conteneur.getChildren().addAll(titre, listeTop);
        return conteneur;
    }

    private VBox creerStatistiquesChauffeurs() {
        VBox conteneur = new VBox(10);
        conteneur.setPadding(new Insets(15));
        conteneur.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        FontAwesomeIconView iconeTitre = new FontAwesomeIconView(FontAwesomeIcon.USER_SECRET);
        iconeTitre.setSize("1.5em");
        Label titre = new Label("Statistiques des Chauffeurs", iconeTitre);
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titre.setTextFill(Color.web("#2c3e50"));

        List<Chauffeur> chauffeurs = chauffeurDAO.obtenirTousLesChauffeurs();
        List<Trajet> trajets = trajetDAO.obtenirTousLesTrajets();

        // Calculer les statistiques
        long chauffeursActifs = chauffeurs.stream().filter(Chauffeur::isActifChauffeur).count();
        long chauffeursPermisValide = chauffeurs.stream().filter(Chauffeur::permisValide).count();
        double salaireMoyen = chauffeurs.stream()
            .filter(Chauffeur::isActifChauffeur)
            .mapToDouble(Chauffeur::getSalaire)
            .average()
            .orElse(0);

        // Chauffeur le plus actif
        Map<String, Long> trajetsParChauffeur = trajets.stream()
            .filter(t -> t.getChauffeur() != null)
            .collect(Collectors.groupingBy(
                t -> t.getChauffeur().getNomCompletChauffeur(),
                Collectors.counting()
            ));

        String chauffeurPlusActif = trajetsParChauffeur.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getKey() + " (" + entry.getValue() + " trajets)")
            .orElse("Aucun");

        VBox stats = new VBox(8);
        stats.getChildren().addAll(
            creerLigneStatistique("Total chauffeurs:", String.valueOf(chauffeurs.size())),
            creerLigneStatistique("Chauffeurs actifs:", String.valueOf(chauffeursActifs)),
            creerLigneStatistique("Permis valides:", String.valueOf(chauffeursPermisValide)),
            creerLigneStatistique("Salaire moyen:", String.format("%.0f FCFA", salaireMoyen)),
            creerLigneStatistique("Plus actif:", chauffeurPlusActif)
        );

        conteneur.getChildren().addAll(titre, stats);
        return conteneur;
    }

    private HBox creerLigneStatistique(String label, String valeur) {
        HBox ligne = new HBox(10);
        ligne.setAlignment(Pos.CENTER_LEFT);
        ligne.setPadding(new Insets(3));

        Label labelTexte = new Label(label);
        labelTexte.setFont(Font.font("Arial", FontWeight.MEDIUM, 13));
        labelTexte.setPrefWidth(150);

        Label valeurTexte = new Label(valeur);
        valeurTexte.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        valeurTexte.setTextFill(Color.web("#2c3e50"));

        ligne.getChildren().addAll(labelTexte, valeurTexte);
        return ligne;
    }

    public ScrollPane obtenirContenu() {
        return scrollPane; 
    }
}
