package com.sotragest.vue.gestion;

import com.sotragest.dao.*;
import com.sotragest.modele.Utilisateur;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Tableau de bord principal de l'application
public class FenetreTableauDeBord {
    private Utilisateur utilisateurConnecte;
    private VBox conteneurPrincipal;
    private ScrollPane scrollPane;

    // DAOs pour récupérer les statistiques
    private VoyageurDAO voyageurDAO = new VoyageurDAO();
    private ChauffeurDAO chauffeurDAO = new ChauffeurDAO();
    private BusDAO busDAO = new BusDAO();
    private TrajetDAO trajetDAO = new TrajetDAO();
    private TicketDAO ticketDAO = new TicketDAO();

    public FenetreTableauDeBord(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        initialiserInterface();
    }

    // Initialise tous les composants du tableau de bord
    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;" + "-fx-text-fill: #2c3e50;");
        conteneurPrincipal.setPrefHeight(1000); // Agrandit la hauteur

        // Titre et informations de bienvenue
        creerEnteteTableauDeBord();

        // Cartes de statistiques
        creerCartesStatistiques();

        // Informations rapides
        creerInformationsRapides();

        // Espacement final pour éviter que le bas soit collé
        Region espaceFinal = new Region();
        espaceFinal.setPrefHeight(40);
        conteneurPrincipal.getChildren().add(espaceFinal);

        // ScrollPane pour rendre l'interface scrollable si besoin
        scrollPane = new ScrollPane(conteneurPrincipal);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent;");
    }

    // En-tête du tableau de bord (bienvenue, rôle, date)
    private void creerEnteteTableauDeBord() {
        VBox entete = new VBox(10);
        entete.setAlignment(Pos.CENTER_LEFT);

        Label titreBienvenue = new Label("Bienvenue, " + utilisateurConnecte.getNomCompletUtilisateur() + " à " + utilisateurConnecte.getNomCompagnie() + " !");
        titreBienvenue.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titreBienvenue.setTextFill(Color.web("#2c3e50"));

        String typeUtilisateur = utilisateurConnecte instanceof com.sotragest.modele.Gerant ? "Gérant" : "Agent";
        Label roleUtilisateur = new Label("Connecté en tant que " + typeUtilisateur);
        roleUtilisateur.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
        roleUtilisateur.setTextFill(Color.web("#7f8c8d"));

        LocalDateTime maintenant = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy - HH:mm");
        Label dateHeure = new Label(maintenant.format(formatter));
        dateHeure.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        dateHeure.setTextFill(Color.web("#95a5a6"));

        entete.getChildren().addAll(titreBienvenue, roleUtilisateur, dateHeure);
        conteneurPrincipal.getChildren().add(entete);
    }

    // Création des cartes de statistiques (voyageurs, chauffeurs, etc.)
    private void creerCartesStatistiques() {
        Label titreStatistiques = new Label("Statistiques en temps réel");
        titreStatistiques.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titreStatistiques.setTextFill(Color.web("#2c3e50"));
        titreStatistiques.setPadding(new Insets(10, 0, 10, 0));

        GridPane grileStatistiques = new GridPane();
        grileStatistiques.setHgap(20);
        grileStatistiques.setVgap(20);
        grileStatistiques.setPadding(new Insets(10));

        // Obtenir les statistiques depuis les DAOs
        int nombreVoyageurs = voyageurDAO.obtenirTousLesVoyageurs().size();
        int nombreChauffeurs = chauffeurDAO.obtenirTousLesChauffeurs().size();
        int nombreBus = busDAO.obtenirTousLesBus().size();
        int nombreTrajets = trajetDAO.obtenirTousLesTrajets().size();
        int nombreTickets = ticketDAO.obtenirTousLesTickets().size();

        // Créer les cartes avec icônes et couleurs
        VBox carteVoyageurs = creerCarteStatistique(FontAwesomeIcon.USERS, "Voyageurs", String.valueOf(nombreVoyageurs), "#3498db");
        VBox carteChauffeurs = creerCarteStatistique(FontAwesomeIcon.USER_SECRET, "Chauffeurs", String.valueOf(nombreChauffeurs), "#e67e22");
        VBox carteBus = creerCarteStatistique(FontAwesomeIcon.BUS, "Bus", String.valueOf(nombreBus), "#f39c12");
        VBox carteTrajets = creerCarteStatistique(FontAwesomeIcon.ROAD, "Trajets", String.valueOf(nombreTrajets), "#9b59b6");
        VBox carteTickets = creerCarteStatistique(FontAwesomeIcon.TICKET, "Tickets vendus", String.valueOf(nombreTickets), "#27ae60");

        // Ajouter les cartes à la grille
        grileStatistiques.add(carteVoyageurs, 0, 0);
        grileStatistiques.add(carteChauffeurs, 1, 0);
        grileStatistiques.add(carteBus, 2, 0);
        grileStatistiques.add(carteTrajets, 0, 1);
        grileStatistiques.add(carteTickets, 1, 1);

        conteneurPrincipal.getChildren().addAll(titreStatistiques, grileStatistiques);
    }

    // Création d'une carte individuelle
    private VBox creerCarteStatistique(FontAwesomeIcon icone, String titre, String valeur, String couleur) {
        VBox carte = new VBox(10);
        carte.setAlignment(Pos.CENTER);
        carte.setPadding(new Insets(20));
        carte.setPrefWidth(200);
        carte.setPrefHeight(120);
        carte.setStyle(
            "-fx-background-color: #d6c2a9; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        FontAwesomeIconView labelIcone = new FontAwesomeIconView(icone);
        labelIcone.setSize("2em");
        labelIcone.setFill(Color.web("#000000"));

        Label labelTitre = new Label(titre);
        labelTitre.setFont(Font.font("Arial", FontWeight.MEDIUM, 14));
        labelTitre.setTextFill(Color.web("#000000"));

        Label labelValeur = new Label(valeur);
        labelValeur.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        labelValeur.setTextFill(Color.web(couleur));

        carte.getChildren().addAll(labelIcone, labelTitre, labelValeur);
        return carte;
    }

    // Informations contextuelles rapides (trajets du jour, chauffeurs, bus...)
    private void creerInformationsRapides() {
        Label titreInfos = new Label("Informations rapides");
        titreInfos.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titreInfos.setTextFill(Color.web("#2c3e50"));
        titreInfos.setPadding(new Insets(20, 0, 10, 0));

        VBox panneauInfos = new VBox(15);
        panneauInfos.setPadding(new Insets(20));
        panneauInfos.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #bdc3c7; " +
            "-fx-border-width: 1; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );

        // Trajets du jour
        int trajetsAujourdhui = trajetDAO.rechercherTrajets(null, null,
            LocalDateTime.now().withHour(0).withMinute(0),
            LocalDateTime.now().withHour(23).withMinute(59)).size();
        
        FontAwesomeIconView iconeTrajet = new FontAwesomeIconView(FontAwesomeIcon.BUS);
        iconeTrajet.setSize("1.2em");
        Label infoTrajetsAujourdhui = new Label("Trajets programmés aujourd'hui : " + trajetsAujourdhui, iconeTrajet);
        infoTrajetsAujourdhui.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        infoTrajetsAujourdhui.setTextFill(Color.web("#2c3e50"));

        // Bus disponibles
        int busDisponibles = busDAO.obtenirBusDisponibles().size();
        FontAwesomeIconView iconeBus = new FontAwesomeIconView(busDisponibles == 0 ? FontAwesomeIcon.TIMES_CIRCLE : FontAwesomeIcon.CHECK_CIRCLE);
        iconeBus.setSize("1.2em");
        Label infoBusDisponibles = new Label("Bus disponibles pour trajets : " + busDisponibles, iconeBus);
        infoBusDisponibles.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        infoBusDisponibles.setTextFill(Color.web("#2c3e50"));
        
        // Chauffeurs disponibles
        int chauffeursDisponibles = chauffeurDAO.obtenirChauffeursDisponibles().size();
        FontAwesomeIconView iconeChauffeur = new FontAwesomeIconView(chauffeursDisponibles == 0 ? FontAwesomeIcon.WARNING : FontAwesomeIcon.USER_SECRET);
        iconeChauffeur.setSize("1.2em");
        Label infoChauffeurs = new Label("Chauffeurs avec permis valide : " + chauffeursDisponibles, iconeChauffeur);
        infoChauffeurs.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        infoChauffeurs.setTextFill(Color.web("#2c3e50"));

        panneauInfos.getChildren().addAll(infoTrajetsAujourdhui, infoBusDisponibles, infoChauffeurs);
        conteneurPrincipal.getChildren().addAll(titreInfos, panneauInfos);
    }

    // Renvoie le contenu complet à afficher dans la scène
    public ScrollPane obtenirContenu() {
        return scrollPane;
    }
}
