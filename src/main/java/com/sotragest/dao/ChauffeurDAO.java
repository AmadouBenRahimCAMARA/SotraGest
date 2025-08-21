package com.sotragest.dao;

import com.sotragest.configuration.ConfigurationBaseDonnees;
import com.sotragest.modele.Chauffeur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


// DAO pour la gestion des chauffeurs
 
public class ChauffeurDAO {

    public boolean creerChauffeur(Chauffeur chauffeur) {
        String sql = """
            INSERT INTO chauffeurs (nom_chauffeur, prenom_chauffeur, telephone_chauffeur, email_chauffeur, date_naissance_chauffeur, adresse_chauffeur, 
            numero_permis, date_obtention_permis, date_expiration_permis, categorie_permis, salaire)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, chauffeur.getNomChauffeur());
            statement.setString(2, chauffeur.getPrenomChauffeur());
            statement.setString(3, chauffeur.getTelephoneChauffeur());
            statement.setString(4, chauffeur.getEmailChauffeur());
            statement.setDate(5, chauffeur.getDateNaissanceChauffeur() != null ? 
                Date.valueOf(chauffeur.getDateNaissanceChauffeur()) : null);
            statement.setString(6, chauffeur.getAdresseChauffeur());
            statement.setString(7, chauffeur.getNumeroPermis());
            statement.setDate(8, chauffeur.getDateObtentionPermis() != null ? 
                Date.valueOf(chauffeur.getDateObtentionPermis()) : null);
            statement.setDate(9, chauffeur.getDateExpirationPermis() != null ? 
                Date.valueOf(chauffeur.getDateExpirationPermis()) : null);
            statement.setString(10, chauffeur.getCategoriePermis());
            statement.setDouble(11, chauffeur.getSalaire());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    chauffeur.setIdChauffeur(generatedKeys.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du chauffeur : " + e.getMessage());
        }
        
        return false;
    }

    public List<Chauffeur> obtenirTousLesChauffeurs() {
        List<Chauffeur> chauffeurs = new ArrayList<>();
        String sql = "SELECT * FROM chauffeurs WHERE actif_chauffeur = TRUE ORDER BY nom_chauffeur, prenom_chauffeur";
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                chauffeurs.add(mapperChauffeur(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des chauffeurs : " + e.getMessage());
        }
        
        return chauffeurs;
    }

    public List<Chauffeur> obtenirChauffeursDisponibles() {
        List<Chauffeur> chauffeurs = new ArrayList<>();
        String sql = """
            SELECT * FROM chauffeurs 
            WHERE actif_chauffeur = TRUE AND date_expiration_permis > CURRENT_DATE
            ORDER BY nom_chauffeur, prenom_chauffeur
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                chauffeurs.add(mapperChauffeur(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des chauffeurs disponibles : " + e.getMessage());
        }
        
        return chauffeurs;
    }

    public boolean mettreAJourChauffeur(Chauffeur chauffeur) {
        String sql = """
            UPDATE chauffeurs SET nom_chauffeur = ?, prenom_chauffeur = ?, telephone_chauffeur = ?, email_chauffeur = ?, 
            date_naissance_chauffeur = ?, adresse_chauffeur = ?, numero_permis = ?, date_obtention_permis = ?, 
            date_expiration_permis = ?, categorie_permis = ?, salaire = ?
            WHERE id_chauffeur = ?
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setString(1, chauffeur.getNomChauffeur());
            statement.setString(2, chauffeur.getPrenomChauffeur());
            statement.setString(3, chauffeur.getTelephoneChauffeur());
            statement.setString(4, chauffeur.getEmailChauffeur());
            statement.setDate(5, chauffeur.getDateNaissanceChauffeur() != null ? 
                Date.valueOf(chauffeur.getDateNaissanceChauffeur()) : null);
            statement.setString(6, chauffeur.getAdresseChauffeur());
            statement.setString(7, chauffeur.getNumeroPermis());
            statement.setDate(8, chauffeur.getDateObtentionPermis() != null ? 
                Date.valueOf(chauffeur.getDateObtentionPermis()) : null);
            statement.setDate(9, chauffeur.getDateExpirationPermis() != null ? 
                Date.valueOf(chauffeur.getDateExpirationPermis()) : null);
            statement.setString(10, chauffeur.getCategoriePermis());
            statement.setDouble(11, chauffeur.getSalaire());
            statement.setLong(12, chauffeur.getIdChauffeur());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du chauffeur : " + e.getMessage());
        }
        
        return false;
    }

    public boolean supprimerChauffeur(Long id) {
        String sql = "UPDATE chauffeurs SET actif_chauffeur = FALSE WHERE id_chauffeur = ?";
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du chauffeur : " + e.getMessage());
        }
        
        return false;
    }

    private Chauffeur mapperChauffeur(ResultSet resultSet) throws SQLException {
        Chauffeur chauffeur = new Chauffeur();
        chauffeur.setIdChauffeur(resultSet.getLong("id_chauffeur"));
        chauffeur.setNomChauffeur(resultSet.getString("nom_chauffeur"));
        chauffeur.setPrenomChauffeur(resultSet.getString("prenom_chauffeur"));
        chauffeur.setTelephoneChauffeur(resultSet.getString("telephone_chauffeur"));
        chauffeur.setEmailChauffeur(resultSet.getString("email_chauffeur"));
        
        Date dateNaissance = resultSet.getDate("date_naissance_chauffeur");
        if (dateNaissance != null) {
            chauffeur.setDateNaissanceChauffeur(dateNaissance.toLocalDate());
        }
        
        chauffeur.setAdresseChauffeur(resultSet.getString("adresse_chauffeur"));
        chauffeur.setNumeroPermis(resultSet.getString("numero_permis"));
        
        Date dateObtention = resultSet.getDate("date_obtention_permis");
        if (dateObtention != null) {
            chauffeur.setDateObtentionPermis(dateObtention.toLocalDate());
        }
        
        Date dateExpiration = resultSet.getDate("date_expiration_permis");
        if (dateExpiration != null) {
            chauffeur.setDateExpirationPermis(dateExpiration.toLocalDate());
        }
        
        chauffeur.setCategoriePermis(resultSet.getString("categorie_permis"));
        chauffeur.setDateEmbauche(resultSet.getTimestamp("date_embauche").toLocalDateTime());
        chauffeur.setSalaire(resultSet.getDouble("salaire"));
        chauffeur.setActif(resultSet.getBoolean("actif_chauffeur"));
        
        return chauffeur;
    }
}