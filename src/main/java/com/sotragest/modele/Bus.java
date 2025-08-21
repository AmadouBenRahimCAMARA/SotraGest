package com.sotragest.modele;

import java.time.LocalDateTime;


// Classe représentant un bus

public class Bus {
    public enum EtatBus {
        EXCELLENT("Excellent"),
        BON("Bon"),
        MOYEN("Moyen"),
        MAUVAIS("Mauvais"),
        HORS_SERVICE("Hors service");

        private final String libelle;
        EtatBus(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    private Long id_bus;
    private String matricule;
    private String marque;
    private String modele;
    private int annee;
    private int capacite;
    private EtatBus etat;
    private double kilometrage;
    private LocalDateTime dateAchat;
    private LocalDateTime dernierEntretien;
    private LocalDateTime prochainEntretien;
    private String couleur;
    private boolean climatisation;
    private boolean actif_bus;

    public Bus() {
        this.dateAchat = LocalDateTime.now();
        this.etat = EtatBus.BON;
        this.actif_bus = true;
    }

    public Bus(String matricule, String marque, String modele, int annee,
               int capacite, String couleur, boolean climatisation) {
        this();
        this.matricule = matricule;
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.capacite = capacite;
        this.couleur = couleur;
        this.climatisation = climatisation;
    }

    // Getters et Setters
    public Long getIdBus() { return id_bus; }
    public void setIdBus(Long id) { this.id_bus = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public int getAnnee() { return annee; }
    public void setAnnee(int annee) { this.annee = annee; }

    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }

    public EtatBus getEtat() { return etat; }
    public void setEtat(EtatBus etat) { this.etat = etat; }

    public double getKilometrage() { return kilometrage; }
    public void setKilometrage(double kilometrage) { this.kilometrage = kilometrage; }

    public LocalDateTime getDateAchat() { return dateAchat; }
    public void setDateAchat(LocalDateTime dateAchat) { this.dateAchat = dateAchat; }

    public LocalDateTime getDernierEntretien() { return dernierEntretien; }
    public void setDernierEntretien(LocalDateTime dernierEntretien) { this.dernierEntretien = dernierEntretien; }

    public LocalDateTime getProchainEntretien() { return prochainEntretien; }
    public void setProchainEntretien(LocalDateTime prochainEntretien) { this.prochainEntretien = prochainEntretien; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    public boolean isClimatisation() { return climatisation; }
    public void setClimatisation(boolean climatisation) { this.climatisation = climatisation; }

    public boolean isActifBus() { return actif_bus; }
    public void setActifBus(boolean actif) { this.actif_bus = actif; }

    public String getDescriptionComplete() {
        return marque + " " + modele + " (" + matricule + ")";
    }

    public boolean estDisponible() {
        return actif_bus && etat != EtatBus.HORS_SERVICE;
    }

    @Override
    public String toString() {
        return getDescriptionComplete() + " - " + capacite + " places";
    }
}