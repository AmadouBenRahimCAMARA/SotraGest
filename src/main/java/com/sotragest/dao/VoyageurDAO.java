package com.sotragest.dao;

import com.sotragest.configuration.ConfigurationBaseDonnees;
import com.sotragest.modele.Voyageur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


// DAO pour la gestion des voyageurs

public class VoyageurDAO {

    public boolean creerVoyageur(Voyageur voyageur) {
        String sql = """
            INSERT INTO voyageurs (nom_voyageur, prenom_voyageur, telephone_voyageur, email_voyageur, date_naissance_voyageur, adresse_voyageur, piece_identite, numero_piece)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, voyageur.getNomVoyageur());
            statement.setString(2, voyageur.getPrenomVoyageur());
            statement.setString(3, voyageur.getTelephoneVoyageur());
            statement.setString(4, voyageur.getEmailVoyageur());
            statement.setDate(5, voyageur.getDateNaissanceVoyageur() != null ? 
                Date.valueOf(voyageur.getDateNaissanceVoyageur()) : null);
            statement.setString(6, voyageur.getAdresseVoyageur());
            statement.setString(7, voyageur.getPieceIdentite());
            statement.setString(8, voyageur.getNumeroPiece());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    voyageur.setIdVoyageur(generatedKeys.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du voyageur : " + e.getMessage());
        }
        
        return false;
    }

    public List<Voyageur> obtenirTousLesVoyageurs() {
        List<Voyageur> voyageurs = new ArrayList<>();
        String sql = "SELECT * FROM voyageurs ORDER BY nom_voyageur, prenom_voyageur";
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                voyageurs.add(mapperVoyageur(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des voyageurs : " + e.getMessage());
        }
        
        return voyageurs;
    }

    public List<Voyageur> rechercherVoyageurs(String critere) {
        List<Voyageur> voyageurs = new ArrayList<>();
        String sql = """
            SELECT * FROM voyageurs 
            WHERE LOWER(nom_voyageur) LIKE LOWER(?) OR LOWER(prenom_voyageur) LIKE LOWER(?) OR telephone_voyageur LIKE ?
            ORDER BY nom_voyageur, prenom_voyageur
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            String critereRecherche = "%" + critere + "%";
            statement.setString(1, critereRecherche);
            statement.setString(2, critereRecherche);
            statement.setString(3, critereRecherche);
            
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                voyageurs.add(mapperVoyageur(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de voyageurs : " + e.getMessage());
        }
        
        return voyageurs;
    }

    public boolean mettreAJourVoyageur(Voyageur voyageur) {
        String sql = """
            UPDATE voyageurs SET nom_voyageur = ?, prenom_voyageur = ?, telephone_voyageur = ?, email_voyageur = ?, 
            date_naissance_voyageur = ?, adresse_voyageur = ?, piece_identite = ?, numero_piece = ?
            WHERE id_voyageur = ?
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setString(1, voyageur.getNomVoyageur());
            statement.setString(2, voyageur.getPrenomVoyageur());
            statement.setString(3, voyageur.getTelephoneVoyageur());
            statement.setString(4, voyageur.getEmailVoyageur());
            statement.setDate(5, voyageur.getDateNaissanceVoyageur() != null ? 
                Date.valueOf(voyageur.getDateNaissanceVoyageur()) : null);
            statement.setString(6, voyageur.getAdresseVoyageur());
            statement.setString(7, voyageur.getPieceIdentite());
            statement.setString(8, voyageur.getNumeroPiece());
            statement.setLong(9, voyageur.getIdVoyageur());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du voyageur : " + e.getMessage());
        }
        
        return false;
    }

    public boolean supprimerVoyageur(Long id) {
        String sql = "DELETE FROM voyageurs WHERE id_voyageur = ?";
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du voyageur : " + e.getMessage());
        }
        
        return false;
    }

    private Voyageur mapperVoyageur(ResultSet resultSet) throws SQLException {
        Voyageur voyageur = new Voyageur();
        voyageur.setIdVoyageur(resultSet.getLong("id_voyageur"));
        voyageur.setNomVoyageur(resultSet.getString("nom_voyageur"));
        voyageur.setPrenomVoyageur(resultSet.getString("prenom_voyageur"));
        voyageur.setTelephoneVoyageur(resultSet.getString("telephone_voyageur"));
        voyageur.setEmailVoyageur(resultSet.getString("email_voyageur"));
        
        Date dateNaissance = resultSet.getDate("date_naissance_voyageur");
        if (dateNaissance != null) {
            voyageur.setDateNaissanceVoyageur(dateNaissance.toLocalDate());
        }
        
        voyageur.setAdresseVoyageur(resultSet.getString("adresse_voyageur"));
        voyageur.setPieceIdentite(resultSet.getString("piece_identite"));
        voyageur.setNumeroPiece(resultSet.getString("numero_piece"));
        voyageur.setDateInscription(resultSet.getTimestamp("date_inscription").toLocalDateTime());
        
        return voyageur;
    }
}