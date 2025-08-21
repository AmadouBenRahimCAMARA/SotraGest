package com.sotragest.modele;

import java.time.LocalDateTime;


// Classe de base pour tous les utilisateurs du système

public abstract class Utilisateur {
    protected Long id_utilisateur;
    protected String identifiant;
    protected String motDePasse;
    protected String nom_utilisateur;
    protected String prenom_utilisateur;
    protected String telephone_utilisateur;
    protected String email_utilisateur;
    protected LocalDateTime dateCreation_utilisateur;
    protected String niveauAcces;
    protected boolean actif_utilisateur;
    protected String nom_compagnie;

    public Utilisateur() {
        this.dateCreation_utilisateur = LocalDateTime.now();
        this.actif_utilisateur = true;
    }

    public Utilisateur(String identifiant, String motDePasse, String nom, 
                      String prenom, String telephone, String email) {
        this();
        this.identifiant = identifiant;
        this.motDePasse = motDePasse;
        this.nom_utilisateur = nom;
        this.prenom_utilisateur = prenom;
        this.telephone_utilisateur = telephone;
        this.email_utilisateur = email;
    }

    // Getters et Setters
    public Long getIdUtilisateur() { return id_utilisateur; }
    public void setIdUtilisateur(Long id) { this.id_utilisateur = id; }

    public String getIdentifiant() { return identifiant; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getNomUtilisateur() { return nom_utilisateur; }
    public void setNomUtilisateur(String nom) { this.nom_utilisateur = nom; }

    public String getPrenomUtilisateur() { return prenom_utilisateur; }
    public void setPrenomUtilisateur(String prenom) { this.prenom_utilisateur = prenom; }

    public String getTelephoneUtilisateur() { return telephone_utilisateur; }
    public void setTelephoneUtilisateur(String telephone) { this.telephone_utilisateur = telephone; }

    public String getEmailUtilisateur() { return email_utilisateur; }
    public void setEmailUtilisateur(String email) { this.email_utilisateur = email; }

    public LocalDateTime getDateCreationUtilisateur() { return dateCreation_utilisateur; }
    public void setDateCreationUtilisateur(LocalDateTime dateCreation) { this.dateCreation_utilisateur = dateCreation; }

    public boolean isActifUtilisateur() { return actif_utilisateur; }
    public void setActifUtilisateur(boolean actif) { this.actif_utilisateur = actif; }
    
    public String getNomCompagnie() { return nom_compagnie; }
    public void setNomCompagnie(String nom_compagnie) { this.nom_compagnie = nom_compagnie; }

    public String getNomCompletUtilisateur() {
        return prenom_utilisateur + " " + nom_utilisateur;
    }
    
    
    public boolean peutAccederAuxStatistiques() { return false; }
    public boolean peutGererLesAgents() { return false; }
    public boolean peutGererLesBus() { return false; }
    public boolean peutGererLesChauffeurs() { return false; }
    public boolean peutGererLesTrajets() { return false; }

    @Override
    public String toString() {
        return getNomCompletUtilisateur();
    }
}