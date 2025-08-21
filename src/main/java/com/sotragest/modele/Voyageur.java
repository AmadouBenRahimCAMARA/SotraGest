package com.sotragest.modele;

import java.time.LocalDate;
import java.time.LocalDateTime;


// Classe représentant un voyageur

public class Voyageur {
    private Long id_voyageur;
    private String nom_voyageur;
    private String prenom_voyageur;
    private String telephone_voyageur;
    private String email_voyageur;
    private LocalDate dateNaissance_voyageur;
    private String adresse_voyageur;
    private String pieceIdentite;
    private String numeroPiece;
    private LocalDateTime dateInscription;

    public Voyageur() {
        this.dateInscription = LocalDateTime.now();
    }

    public Voyageur(String nom, String prenom, String telephone, String email,
                   LocalDate dateNaissance, String adresse, String pieceIdentite, String numeroPiece) {
        this();
        this.nom_voyageur = nom;
        this.prenom_voyageur = prenom;
        this.telephone_voyageur = telephone;
        this.email_voyageur = email;
        this.dateNaissance_voyageur = dateNaissance;
        this.adresse_voyageur = adresse;
        this.pieceIdentite = pieceIdentite;
        this.numeroPiece = numeroPiece;
    }

    // Getters et Setters
    public Long getIdVoyageur() { return id_voyageur; }
    public void setIdVoyageur(Long id) { this.id_voyageur = id; }

    public String getNomVoyageur() { return nom_voyageur; }
    public void setNomVoyageur(String nom) { this.nom_voyageur = nom; }

    public String getPrenomVoyageur() { return prenom_voyageur; }
    public void setPrenomVoyageur(String prenom) { this.prenom_voyageur = prenom; }

    public String getTelephoneVoyageur() { return telephone_voyageur; }
    public void setTelephoneVoyageur(String telephone) { this.telephone_voyageur = telephone; }

    public String getEmailVoyageur() { return email_voyageur; }
    public void setEmailVoyageur(String email) { this.email_voyageur = email; }

    public LocalDate getDateNaissanceVoyageur() { return dateNaissance_voyageur; }
    public void setDateNaissanceVoyageur(LocalDate dateNaissance) { this.dateNaissance_voyageur = dateNaissance; }

    public String getAdresseVoyageur() { return adresse_voyageur; }
    public void setAdresseVoyageur(String adresse) { this.adresse_voyageur = adresse; }

    public String getPieceIdentite() { return pieceIdentite; }
    public void setPieceIdentite(String pieceIdentite) { this.pieceIdentite = pieceIdentite; }

    public String getNumeroPiece() { return numeroPiece; }
    public void setNumeroPiece(String numeroPiece) { this.numeroPiece = numeroPiece; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }

    public String getNomCompletVoyageur() {
        return prenom_voyageur + " " + nom_voyageur;
    }

    @Override
    public String toString() {
        return getNomCompletVoyageur() + " (" + telephone_voyageur + ")";
    }
}
