package com.sotragest.controleur;

import com.sotragest.dao.UtilisateurDAO;
import com.sotragest.modele.Utilisateur;
import com.sotragest.vue.FenetreConnexion;
import com.sotragest.vue.FenetrePrincipale;


// Contrôleur pour la gestion de la connexion

public class ControleurConnexion {
    private FenetreConnexion vue;
    private UtilisateurDAO utilisateurDAO;

    public ControleurConnexion(FenetreConnexion vue) {
        this.vue = vue;
        this.utilisateurDAO = new UtilisateurDAO();
    }

    public void tenterConnexion() {
        String identifiant = vue.getIdentifiant();
        String motDePasse = vue.getMotDePasse();

        // Validation des champs
        if (identifiant == null || identifiant.trim().isEmpty()) {
            vue.afficherErreur("L'identifiant est obligatoire");
            return;
        }

        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            vue.afficherErreur("Le mot de passe est obligatoire");
            return;
        }

        // Tentative d'authentification
        Utilisateur utilisateur = utilisateurDAO.authentifier(identifiant.trim(), motDePasse);

        if (utilisateur != null) {
            vue.viderErreur();
            ouvrirFenetrePrincipale(utilisateur);
        } else {
            vue.afficherErreur("Identifiant ou mot de passe incorrect");
        }
    }

    private void ouvrirFenetrePrincipale(Utilisateur utilisateur) {
        try {
            FenetrePrincipale fenetrePrincipale = new FenetrePrincipale(utilisateur);
            fenetrePrincipale.afficher(vue.getStage());
        } catch (Exception e) {
            vue.afficherErreur("Erreur lors de l'ouverture de l'application : " + e.getMessage());
            e.printStackTrace();
        }
    }
}