package com.sotragest.dao;

import com.sotragest.configuration.ConfigurationBaseDonnees;
import com.sotragest.modele.Bus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


// DAO pour la gestion des bus

public class BusDAO {

    public boolean creerBus(Bus bus) {
        String sql = """
            INSERT INTO bus (matricule, marque, modele, annee, capacite, couleur, climatisation, etat, kilometrage)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, bus.getMatricule());
            statement.setString(2, bus.getMarque());
            statement.setString(3, bus.getModele());
            statement.setInt(4, bus.getAnnee());
            statement.setInt(5, bus.getCapacite());
            statement.setString(6, bus.getCouleur());
            statement.setBoolean(7, bus.isClimatisation());
            statement.setString(8, bus.getEtat().name());
            statement.setDouble(9, bus.getKilometrage());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    bus.setIdBus(generatedKeys.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du bus : " + e.getMessage());
        }
        
        return false;
    }

    public List<Bus> obtenirTousLesBus() {
        List<Bus> busListe = new ArrayList<>();
        String sql = "SELECT * FROM bus WHERE actif_bus = TRUE ORDER BY matricule";
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                busListe.add(mapperBus(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des bus : " + e.getMessage());
        }
        
        return busListe;
    }

    public List<Bus> obtenirBusDisponibles() {
        List<Bus> busListe = new ArrayList<>();
        String sql = """
            SELECT * FROM bus 
            WHERE actif_bus = TRUE AND etat != 'HORS_SERVICE'
            ORDER BY matricule
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                busListe.add(mapperBus(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des bus disponibles : " + e.getMessage());
        }
        
        return busListe;
    }

    public boolean mettreAJourBus(Bus bus) {
        String sql = """
            UPDATE bus SET matricule = ?, marque = ?, modele = ?, annee = ?, 
            capacite = ?, couleur = ?, climatisation = ?, etat = ?, kilometrage = ?
            WHERE id_bus = ?
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setString(1, bus.getMatricule());
            statement.setString(2, bus.getMarque());
            statement.setString(3, bus.getModele());
            statement.setInt(4, bus.getAnnee());
            statement.setInt(5, bus.getCapacite());
            statement.setString(6, bus.getCouleur());
            statement.setBoolean(7, bus.isClimatisation());
            statement.setString(8, bus.getEtat().name());
            statement.setDouble(9, bus.getKilometrage());
            statement.setLong(10, bus.getIdBus());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du bus : " + e.getMessage());
        }
        
        return false;
    }

    public boolean supprimerBus(Long id) {
        String sql = "UPDATE bus SET actif_bus = FALSE WHERE id_bus = ?";
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du bus : " + e.getMessage());
        }
        
        return false;
    }

    private Bus mapperBus(ResultSet resultSet) throws SQLException {
        Bus bus = new Bus();
        bus.setIdBus(resultSet.getLong("id_bus"));
        bus.setMatricule(resultSet.getString("matricule"));
        bus.setMarque(resultSet.getString("marque"));
        bus.setModele(resultSet.getString("modele"));
        bus.setAnnee(resultSet.getInt("annee"));
        bus.setCapacite(resultSet.getInt("capacite"));
        bus.setCouleur(resultSet.getString("couleur"));
        bus.setClimatisation(resultSet.getBoolean("climatisation"));
        
        String etatString = resultSet.getString("etat");
        if (etatString != null) {
            bus.setEtat(Bus.EtatBus.valueOf(etatString));
        }
        
        bus.setKilometrage(resultSet.getDouble("kilometrage"));
        
        Timestamp dateAchat = resultSet.getTimestamp("date_achat");
        if (dateAchat != null) {
            bus.setDateAchat(dateAchat.toLocalDateTime());
        }
        
        bus.setActifBus(resultSet.getBoolean("actif_bus"));
        
        return bus;
    }
}