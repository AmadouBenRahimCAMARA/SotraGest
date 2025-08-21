package com.sotragest.modele;


// Classe représentant un agent avec accès restreint au système

public class Agent extends Utilisateur {
    private String posteTravail;

    public Agent() {
        super();
        this.niveauAcces = "RESTREINT";
    }

    public Agent(String identifiant, String motDePasse, String nom, 
                String prenom, String telephone, String email, String posteTravail) {
        super(identifiant, motDePasse, nom, prenom, telephone, email);
        this.niveauAcces = "RESTREINT";
        this.posteTravail = posteTravail;
    }

    public String getNiveauAcces() { return niveauAcces; }
    public void setNiveauAcces(String niveauAcces) { this.niveauAcces = niveauAcces; }

    public String getPosteTravail() { return posteTravail; }
    public void setPosteTravail(String posteTravail) { this.posteTravail = posteTravail; }

    // Permissions limitées pour les agents
    public boolean peutVendreTickets() { return true; }
    public boolean peutGererVoyageurs() { return true; }
    public boolean peutConsulterTrajets() { return true; }
}