package com.sotragest.modele;


// Classe représentant un gérant avec accès complet au système

public class Gerant extends Utilisateur {
    public Gerant() {
        super();
        this.niveauAcces = "COMPLET";
    }

    public Gerant(String identifiant, String motDePasse, String nom, 
                 String prenom, String telephone, String email) {
        super(identifiant, motDePasse, nom, prenom, telephone, email);
        this.niveauAcces = "COMPLET";
    }

    public String getNiveauAcces() { return niveauAcces; }
    public void setNiveauAcces(String niveauAcces) { this.niveauAcces = niveauAcces; }

    @Override
    public boolean peutAccederAuxStatistiques() { return true; }
    @Override
    public boolean peutGererLesAgents() { return true; }
    @Override
    public boolean peutGererLesBus() { return true; }
    @Override
    public boolean peutGererLesChauffeurs() { return true; }
    @Override
    public boolean peutGererLesTrajets() { return true; }
}