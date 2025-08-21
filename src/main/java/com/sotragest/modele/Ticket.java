package com.sotragest.modele;

import java.time.LocalDateTime;


// Classe représentant un ticket de voyage

public class Ticket {
    public enum StatutTicket {
        VALIDE("Valide"),
        UTILISE("Utilisé"),
        ANNULE("Annulé"),
        EXPIRE("Expiré");

        private final String libelle;
        StatutTicket(String libelle) { this.libelle = libelle; }
        public String getLibelle() { return libelle; }
    }

    private Long id_ticket;
    private String numeroTicket;
    private Voyageur voyageur;
    private Trajet trajet;
    private int numeroSiege;
    private double prix;
    private StatutTicket statut_ticket;
    private LocalDateTime dateVente;
    private LocalDateTime dateUtilisation;
    private Utilisateur vendeur;
    private String codeQR;
    private String observationsTicket;

    public Ticket() {
        this.dateVente = LocalDateTime.now();
        this.statut_ticket = StatutTicket.VALIDE;
        this.numeroTicket = genererNumeroTicket();
    }

    public Ticket(Voyageur voyageur, Trajet trajet, int numeroSiege, Utilisateur vendeur) {
        this();
        this.voyageur = voyageur;
        this.trajet = trajet;
        this.numeroSiege = numeroSiege;
        this.prix = trajet.getPrix();
        this.vendeur = vendeur;
        this.codeQR = genererCodeQR();
    }

    // Getters et Setters
    public Long getIdTicket() { return id_ticket; }
    public void setIdTicket(Long id) { this.id_ticket = id; }

    public String getNumeroTicket() { return numeroTicket; }
    public void setNumeroTicket(String numeroTicket) { this.numeroTicket = numeroTicket; }

    public Voyageur getVoyageur() { return voyageur; }
    public void setVoyageur(Voyageur voyageur) { this.voyageur = voyageur; }

    public Trajet getTrajet() { return trajet; }
    public void setTrajet(Trajet trajet) { this.trajet = trajet; }

    public int getNumeroSiege() { return numeroSiege; }
    public void setNumeroSiege(int numeroSiege) { this.numeroSiege = numeroSiege; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public StatutTicket getStatutTicket() { return statut_ticket; }
    public void setStatutTicket(StatutTicket statut) { this.statut_ticket = statut; }

    public LocalDateTime getDateVente() { return dateVente; }
    public void setDateVente(LocalDateTime dateVente) { this.dateVente = dateVente; }

    public LocalDateTime getDateUtilisation() { return dateUtilisation; }
    public void setDateUtilisation(LocalDateTime dateUtilisation) { this.dateUtilisation = dateUtilisation; }

    public Utilisateur getVendeur() { return vendeur; }
    public void setVendeur(Utilisateur vendeur) { this.vendeur = vendeur; }

    public String getCodeQR() { return codeQR; }
    public void setCodeQR(String codeQR) { this.codeQR = codeQR; }

    public String getObservationsTicket() { return observationsTicket; }
    public void setObservationsTicket(String observationsTicket) { this.observationsTicket = observationsTicket; }

    private String genererNumeroTicket() {
        long timestamp = System.currentTimeMillis();
        return "TK" + timestamp;
    }

    private String genererCodeQR() {
        return "QR" + numeroTicket + System.currentTimeMillis();
    }

    public void utiliser() {
        this.statut_ticket = StatutTicket.UTILISE;
        this.dateUtilisation = LocalDateTime.now();
    }

    public void annuler() {
        this.statut_ticket = StatutTicket.ANNULE;
        if (trajet != null) {
            trajet.libererPlace();
        }
    }

    public boolean estValide() {
        return statut_ticket == StatutTicket.VALIDE && 
               trajet.getDateHeureDepart().isAfter(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return numeroTicket + " - " + voyageur.getNomCompletVoyageur() + 
               " (" + trajet.getItineraire() + ")";
    }
}