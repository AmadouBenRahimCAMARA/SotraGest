package com.sotragest.vue.gestion;

import com.sotragest.dao.UtilisateurDAO;
import com.sotragest.modele.Agent;
import com.sotragest.modele.Utilisateur;
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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


// Fenêtre de gestion des agents

public class FenetreGestionAgents {
    private VBox conteneurPrincipal;
    private TableView<Utilisateur> tableauAgents;
    private ObservableList<Utilisateur> listeAgents;
    private UtilisateurDAO utilisateurDAO;
    private Utilisateur utilisateurConnecte;

    public FenetreGestionAgents(Utilisateur utilisateurConnecte) {
        this.utilisateurDAO = new UtilisateurDAO();
        this.utilisateurConnecte = utilisateurConnecte;
        this.listeAgents = FXCollections.observableArrayList();
        initialiserInterface();
        chargerAgents();
    }

    private void initialiserInterface() {
        conteneurPrincipal = new VBox(20);
        conteneurPrincipal.setPadding(new Insets(20));
        conteneurPrincipal.setStyle("-fx-background-color: #ecf0f1;");

        // Titre
        Label titre = new Label("Gestion des Agents");
        titre.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titre.setTextFill(Color.web("#2c3e50"));

        // Barre d'outils
        HBox barreOutils = creerBarreOutils();

        // Tableau des agents
        tableauAgents = creerTableauAgents();

        conteneurPrincipal.getChildren().addAll(titre, barreOutils, tableauAgents);
    }

    private HBox creerBarreOutils() {
        HBox barreOutils = new HBox(10);
        barreOutils.setAlignment(Pos.CENTER_LEFT);
        barreOutils.setPadding(new Insets(10));

        Button boutonAjouter = new Button("➕ Ajouter un agent");
        Button boutonModifier = new Button("✏️ Modifier");
        Button boutonSupprimer = new Button("🗑️ Supprimer");
        Button boutonActualiser = new Button("🔄 Actualiser");

        // Style des boutons
        boutonAjouter.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        boutonActualiser.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

        // Événements
        boutonAjouter.setOnAction(e -> ouvrirFormulaireAgent(null));
        boutonModifier.setOnAction(e -> modifierAgentSelectionne());
        boutonSupprimer.setOnAction(e -> supprimerAgentSelectionne());
        boutonActualiser.setOnAction(e -> chargerAgents());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        barreOutils.getChildren().addAll(boutonAjouter, boutonModifier, boutonSupprimer, spacer, boutonActualiser);
        return barreOutils;
    }

    private TableView<Utilisateur> creerTableauAgents() {
        TableView<Utilisateur> tableau = new TableView<>();
        tableau.setItems(listeAgents);
        tableau.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableau.getColumns().clear(); // Pour éviter tout ajout automatique de colonne

        // Colonnes
        TableColumn<Utilisateur, String> colIdentifiant = new TableColumn<>("Identifiant");
        colIdentifiant.setCellValueFactory(new PropertyValueFactory<>("identifiant"));
        colIdentifiant.setPrefWidth(120);

        TableColumn<Utilisateur, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomUtilisateur"));
        colNom.setPrefWidth(120);

        TableColumn<Utilisateur, String> colPrenom = new TableColumn<>("Prénom");
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenomUtilisateur"));
        colPrenom.setPrefWidth(120);

        TableColumn<Utilisateur, String> colTelephone = new TableColumn<>("Téléphone");
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephoneUtilisateur"));
        colTelephone.setPrefWidth(120);

        TableColumn<Utilisateur, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("emailUtilisateur"));
        colEmail.setPrefWidth(200);

        TableColumn<Utilisateur, String> colPoste = new TableColumn<>("Poste de travail");
        colPoste.setCellValueFactory(cellData -> {
            Utilisateur user = cellData.getValue();
            String poste = user instanceof Agent ? ((Agent) user).getPosteTravail() : "";
            return new javafx.beans.property.SimpleStringProperty(poste);
        });
        colPoste.setPrefWidth(150);

        TableColumn<Utilisateur, String> colDateCreation = new TableColumn<>("Date création");
        colDateCreation.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDateCreationUtilisateur().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        colDateCreation.setPrefWidth(120);

        TableColumn<Utilisateur, String> colActif = new TableColumn<>("Statut");
        colActif.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isActifUtilisateur() ? "✅ Actif" : "❌ Inactif"));
        colActif.setPrefWidth(80);

        tableau.getColumns().addAll(colIdentifiant, colNom, colPrenom, colTelephone, colEmail, colPoste, colDateCreation, colActif);

        // Double-clic pour modifier
        tableau.setRowFactory(tv -> {
            TableRow<Utilisateur> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    ouvrirFormulaireAgent(row.getItem());
                }
            });
            return row;
        });

        return tableau;
    }

    private void chargerAgents() {
        List<Utilisateur> utilisateurs = utilisateurDAO.obtenirTousLesUtilisateurs();
        // Filtrer pour ne garder que les agents
        List<Utilisateur> agents = utilisateurs.stream()
            .filter(u -> u instanceof Agent)
            .toList();
        
        listeAgents.clear();
        listeAgents.addAll(agents);
    }

    private void modifierAgentSelectionne() {
        Utilisateur agentSelectionne = tableauAgents.getSelectionModel().getSelectedItem();
        if (agentSelectionne != null) {
            ouvrirFormulaireAgent(agentSelectionne);
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un agent à modifier.");
        }
    }

    private void supprimerAgentSelectionne() {
        Utilisateur agentSelectionne = tableauAgents.getSelectionModel().getSelectedItem();
        if (agentSelectionne != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer l'agent");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer " + agentSelectionne.getNomCompletUtilisateur() + " ?");

            Optional<ButtonType> resultat = confirmation.showAndWait();
            if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                if (utilisateurDAO.supprimerUtilisateur(agentSelectionne.getIdUtilisateur())) {
                    chargerAgents();
                    afficherAlerte("Succès", "Agent supprimé avec succès.");
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la suppression de l'agent.");
                }
            }
        } else {
            afficherAlerte("Aucune sélection", "Veuillez sélectionner un agent à supprimer.");
        }
    }

    private void ouvrirFormulaireAgent(Utilisateur agent) {
        Stage fenetreFormulaire = new Stage();
        fenetreFormulaire.initModality(Modality.APPLICATION_MODAL);
        fenetreFormulaire.setTitle(agent == null ? "Ajouter un agent" : "Modifier l'agent");

        VBox conteneur = new VBox(15);
        conteneur.setPadding(new Insets(20));

        // Champs du formulaire
        TextField champIdentifiant = new TextField(agent != null ? agent.getIdentifiant() : "");
        champIdentifiant.setPromptText("Identifiant de connexion");

        PasswordField champMotDePasse = new PasswordField();
        champMotDePasse.setPromptText(agent == null ? "Mot de passe" : "Nouveau mot de passe (laisser vide pour conserver)");

        TextField champNom = new TextField(agent != null ? agent.getNomUtilisateur() : "");
        champNom.setPromptText("Nom");

        TextField champPrenom = new TextField(agent != null ? agent.getPrenomUtilisateur() : "");
        champPrenom.setPromptText("Prénom");

        TextField champTelephone = new TextField(agent != null ? agent.getTelephoneUtilisateur() : "");
        champTelephone.setPromptText("Téléphone");

        TextField champEmail = new TextField(agent != null ? agent.getEmailUtilisateur() : "");
        champEmail.setPromptText("Email");

        TextField champPoste = new TextField();
        if (agent instanceof Agent) {
            champPoste.setText(((Agent) agent).getPosteTravail());
        }
        champPoste.setPromptText("Poste de travail");

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
            if (validerFormulaire(champIdentifiant, champNom, champPrenom, champTelephone, champMotDePasse, agent == null)) {
                Agent nouvelAgent = agent instanceof Agent ? (Agent) agent : new Agent();
                
                nouvelAgent.setIdentifiant(champIdentifiant.getText().trim());
                nouvelAgent.setNomUtilisateur(champNom.getText().trim());
                nouvelAgent.setPrenomUtilisateur(champPrenom.getText().trim());
                nouvelAgent.setTelephoneUtilisateur(champTelephone.getText().trim());
                nouvelAgent.setEmailUtilisateur(champEmail.getText().trim());
                nouvelAgent.setPosteTravail(champPoste.getText().trim());
                
                // Mot de passe seulement si fourni
                if (!champMotDePasse.getText().trim().isEmpty()) {
                    nouvelAgent.setMotDePasse(champMotDePasse.getText().trim());
                }

                boolean succes = (agent == null);
                if(succes) {
                    utilisateurDAO.creerUtilisateur(nouvelAgent);
                    utilisateurDAO.mettreAJourNomCompagnie(nouvelAgent.getIdUtilisateur(),utilisateurConnecte.getNomCompagnie());
                } else {
                    utilisateurDAO.mettreAJourUtilisateur(nouvelAgent);
                }
                if (succes) {
                    chargerAgents();
                    afficherAlerte("Succès", "Agent " + (agent == null ? "ajouté" : "modifié") + " avec succès.");
                    fenetreFormulaire.close();
                } else {
                    afficherAlerte("Erreur", "Erreur lors de la sauvegarde de l'agent.");
                }
            }
        });

        boutonAnnuler.setOnAction(e -> fenetreFormulaire.close());

        conteneur.getChildren().addAll(
            new Label("Identifiant:"), champIdentifiant,
            new Label("Mot de passe:"), champMotDePasse,
            new Label("Nom:"), champNom,
            new Label("Prénom:"), champPrenom,
            new Label("Téléphone:"), champTelephone,
            new Label("Email:"), champEmail,
            new Label("Poste de travail:"), champPoste,
            boutons
        );

        Scene scene = new Scene(new ScrollPane(conteneur), 400, 500);
        fenetreFormulaire.setScene(scene);
        fenetreFormulaire.showAndWait();
    }

    private boolean validerFormulaire(TextField identifiant, TextField nom, TextField prenom, 
                                    TextField telephone, PasswordField motDePasse, boolean nouveauAgent) {
        if (identifiant.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "L'identifiant est obligatoire.");
            return false;
        }
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
        if (nouveauAgent && motDePasse.getText().trim().isEmpty()) {
            afficherAlerte("Erreur de validation", "Le mot de passe est obligatoire pour un nouvel agent.");
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