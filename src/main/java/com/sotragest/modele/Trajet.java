package com.sotragest.modele;

import java.time.LocalDateTime;


// Classe représentant un trajet

public class Trajet {
    public enum StatutTrajet {
        PROGRAMME("Programmé"),
        EN_COURS("En cours"),
        TERMINE("Terminé"),
        ANNULE("Annulé"),
        REPORTE("Reporté");

        private final String libelle;
        StatutTrajet(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    private Long id_trajet;
    private String villeDepart;
    private String villeArrivee;
    private LocalDateTime dateHeureDepart;
    private LocalDateTime dateHeureArriveeEstimee;
    private LocalDateTime dateHeureArriveeReelle;
    private Bus bus;
    private Chauffeur chauffeur;
    private double prix;
    private int nombrePlacesDisponibles;
    private StatutTrajet statut_trajet;
    private String commentaires;
    private LocalDateTime dateCreation_trajet;

    public Trajet() {
        this.dateCreation_trajet = LocalDateTime.now();
        this.statut_trajet = StatutTrajet.PROGRAMME;
    }

    public Trajet(String villeDepart, String villeArrivee, LocalDateTime dateHeureDepart,
                 LocalDateTime dateHeureArriveeEstimee, Bus bus, Chauffeur chauffeur, double prix) {
        this();
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.dateHeureDepart = dateHeureDepart;
        this.dateHeureArriveeEstimee = dateHeureArriveeEstimee;
        this.bus = bus;
        this.chauffeur = chauffeur;
        this.prix = prix;
        this.nombrePlacesDisponibles = bus != null ? bus.getCapacite() : 0;
    }

    // Getters et Setters
    public Long getIdTrajet() { return id_trajet; }
    public void setIdTrajet(Long id) { this.id_trajet = id; }

    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }

    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }

    public LocalDateTime getDateHeureDepart() { return dateHeureDepart; }
    public void setDateHeureDepart(LocalDateTime dateHeureDepart) { this.dateHeureDepart = dateHeureDepart; }

    public LocalDateTime getDateHeureArriveeEstimee() { return dateHeureArriveeEstimee; }
    public void setDateHeureArriveeEstimee(LocalDateTime dateHeureArriveeEstimee) { this.dateHeureArriveeEstimee = dateHeureArriveeEstimee; }

    public LocalDateTime getDateHeureArriveeReelle() { return dateHeureArriveeReelle; }
    public void setDateHeureArriveeReelle(LocalDateTime dateHeureArriveeReelle) { this.dateHeureArriveeReelle = dateHeureArriveeReelle; }

    public Bus getBus() { return bus; }
    public void setBus(Bus bus) { 
        this.bus = bus;
        if (bus != null) {
            this.nombrePlacesDisponibles = bus.getCapacite();
        }
    }

    public Chauffeur getChauffeur() { return chauffeur; }
    public void setChauffeur(Chauffeur chauffeur) { this.chauffeur = chauffeur; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getNombrePlacesDisponibles() { return nombrePlacesDisponibles; }
    public void setNombrePlacesDisponibles(int nombrePlacesDisponibles) { this.nombrePlacesDisponibles = nombrePlacesDisponibles; }

    public StatutTrajet getStatutTrajet() { return statut_trajet; }
    public void setStatutTrajet(StatutTrajet statut) { this.statut_trajet = statut; }

    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }

    public LocalDateTime getDateCreationTrajet() { return dateCreation_trajet; }
    public void setDateCreationTrajet(LocalDateTime dateCreation) { this.dateCreation_trajet = dateCreation; }

    public String getItineraire() {
        return villeDepart + " → " + villeArrivee;
    }

    public boolean aDesPlacesDisponibles() {
        return nombrePlacesDisponibles > 0;
    }

    public void reserverPlace() {
        if (aDesPlacesDisponibles()) {
            nombrePlacesDisponibles--;
        }
    }

    public void libererPlace() {
        if (bus != null && nombrePlacesDisponibles < bus.getCapacite()) {
            nombrePlacesDisponibles++;
        }
    }

    @Override
    public String toString() {
        return getItineraire() + " - " + dateHeureDepart.toLocalDate() + 
               " à " + dateHeureDepart.toLocalTime() + " (" + prix + " FCFA)";
    }
}