package com.sotragest.modele;

import java.time.LocalDate;
import java.time.LocalDateTime;


// Classe représentant un chauffeur

public class Chauffeur {
    private Long id_chauffeur;
    private String nom_chauffeur;
    private String prenom_chauffeur;
    private String telephone_chauffeur;
    private String email_chauffeur;
    private LocalDate dateNaissance_chauffeur;
    private String adresse_chauffeur;
    private String numeroPermis;
    private LocalDate dateObtentionPermis;
    private LocalDate dateExpirationPermis;
    private String categoriePermis;
    private LocalDateTime dateEmbauche;
    private double salaire;
    private boolean actif_chauffeur;

    public Chauffeur() {
        this.dateEmbauche = LocalDateTime.now();
        this.actif_chauffeur = true;
    }

    public Chauffeur(String nom, String prenom, String telephone, String email,
                    LocalDate dateNaissance, String adresse, String numeroPermis,
                    LocalDate dateObtentionPermis, LocalDate dateExpirationPermis,
                    String categoriePermis, double salaire) {
        this();
        this.nom_chauffeur = nom;
        this.prenom_chauffeur = prenom;
        this.telephone_chauffeur = telephone;
        this.email_chauffeur = email;
        this.dateNaissance_chauffeur = dateNaissance;
        this.adresse_chauffeur = adresse;
        this.numeroPermis = numeroPermis;
        this.dateObtentionPermis = dateObtentionPermis;
        this.dateExpirationPermis = dateExpirationPermis;
        this.categoriePermis = categoriePermis;
        this.salaire = salaire;
    }

    // Getters et Setters
    public Long getIdChauffeur() { return id_chauffeur; }
    public void setIdChauffeur(Long id) { this.id_chauffeur = id; }

    public String getNomChauffeur() { return nom_chauffeur; }
    public void setNomChauffeur(String nom) { this.nom_chauffeur = nom; }

    public String getPrenomChauffeur() { return prenom_chauffeur; }
    public void setPrenomChauffeur(String prenom) { this.prenom_chauffeur = prenom; }

    public String getTelephoneChauffeur() { return telephone_chauffeur; }
    public void setTelephoneChauffeur(String telephone) { this.telephone_chauffeur = telephone; }

    public String getEmailChauffeur() { return email_chauffeur; }
    public void setEmailChauffeur(String email) { this.email_chauffeur = email; }

    public LocalDate getDateNaissanceChauffeur() { return dateNaissance_chauffeur; }
    public void setDateNaissanceChauffeur(LocalDate dateNaissance) { this.dateNaissance_chauffeur = dateNaissance; }

    public String getAdresseChauffeur() { return adresse_chauffeur; }
    public void setAdresseChauffeur(String adresse) { this.adresse_chauffeur = adresse; }

    public String getNumeroPermis() { return numeroPermis; }
    public void setNumeroPermis(String numeroPermis) { this.numeroPermis = numeroPermis; }

    public LocalDate getDateObtentionPermis() { return dateObtentionPermis; }
    public void setDateObtentionPermis(LocalDate dateObtentionPermis) { this.dateObtentionPermis = dateObtentionPermis; }

    public LocalDate getDateExpirationPermis() { return dateExpirationPermis; }
    public void setDateExpirationPermis(LocalDate dateExpirationPermis) { this.dateExpirationPermis = dateExpirationPermis; }

    public String getCategoriePermis() { return categoriePermis; }
    public void setCategoriePermis(String categoriePermis) { this.categoriePermis = categoriePermis; }

    public LocalDateTime getDateEmbauche() { return dateEmbauche; }
    public void setDateEmbauche(LocalDateTime dateEmbauche) { this.dateEmbauche = dateEmbauche; }

    public double getSalaire() { return salaire; }
    public void setSalaire(double salaire) { this.salaire = salaire; }

    public boolean isActifChauffeur() { return actif_chauffeur; }
    public void setActif(boolean actif) { this.actif_chauffeur = actif; }

    public String getNomCompletChauffeur() {
        return prenom_chauffeur + " " + nom_chauffeur;
    }

    public boolean permisValide() {
        return dateExpirationPermis.isAfter(LocalDate.now());
    }

    @Override
    public String toString() {
        return getNomCompletChauffeur() + " - " + numeroPermis;
    }
}