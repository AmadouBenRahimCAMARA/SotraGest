package com.sotragest.dao;

import com.sotragest.configuration.ConfigurationBaseDonnees;
import com.sotragest.modele.Bus;
import com.sotragest.modele.Chauffeur;
import com.sotragest.modele.Trajet;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


// DAO pour la gestion des trajets

public class TrajetDAO {
    private BusDAO busDAO = new BusDAO();
    private ChauffeurDAO chauffeurDAO = new ChauffeurDAO();

    public boolean creerTrajet(Trajet trajet) {
        String sql = """
            INSERT INTO trajets (ville_depart, ville_arrivee, date_heure_depart, date_heure_arrivee_estimee,
            bus_id, chauffeur_id, prix, nombre_places_disponibles, commentaires)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, trajet.getVilleDepart());
            statement.setString(2, trajet.getVilleArrivee());
            statement.setTimestamp(3, Timestamp.valueOf(trajet.getDateHeureDepart()));
            statement.setTimestamp(4, trajet.getDateHeureArriveeEstimee() != null ? 
                Timestamp.valueOf(trajet.getDateHeureArriveeEstimee()) : null);
            statement.setLong(5, trajet.getBus().getIdBus());
            statement.setLong(6, trajet.getChauffeur().getIdChauffeur());
            statement.setDouble(7, trajet.getPrix());
            statement.setInt(8, trajet.getNombrePlacesDisponibles());
            statement.setString(9, trajet.getCommentaires());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    trajet.setIdTrajet(generatedKeys.getLong(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du trajet : " + e.getMessage());
        }
        
        return false;
    }

    public List<Trajet> obtenirTousLesTrajets() {
        List<Trajet> trajets = new ArrayList<>();
        String sql = """
            SELECT t.*, b.*, c.* FROM trajets t
            LEFT JOIN bus b ON t.bus_id = b.id_bus
            LEFT JOIN chauffeurs c ON t.chauffeur_id = c.id_chauffeur
            ORDER BY t.date_heure_depart DESC
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                trajets.add(mapperTrajetComplet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des trajets : " + e.getMessage());
        }
        
        return trajets;
    }

    public List<Trajet> rechercherTrajets(String villeDepart, String villeArrivee, LocalDateTime dateDebut, LocalDateTime dateFin) {
        List<Trajet> trajets = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT t.*, b.*, c.* FROM trajets t
            LEFT JOIN bus b ON t.bus_id = b.id_bus
            LEFT JOIN chauffeurs c ON t.chauffeur_id = c.id_chauffeur
            WHERE 1=1
            """);
        
        List<Object> parametres = new ArrayList<>();
        
        if (villeDepart != null && !villeDepart.trim().isEmpty()) {
            sql.append(" AND LOWER(t.ville_depart) LIKE LOWER(?)");
            parametres.add("%" + villeDepart + "%");
        }
        
        if (villeArrivee != null && !villeArrivee.trim().isEmpty()) {
            sql.append(" AND LOWER(t.ville_arrivee) LIKE LOWER(?)");
            parametres.add("%" + villeArrivee + "%");
        }
        
        if (dateDebut != null) {
            sql.append(" AND t.date_heure_depart >= ?");
            parametres.add(Timestamp.valueOf(dateDebut));
        }
        
        if (dateFin != null) {
            sql.append(" AND t.date_heure_depart <= ?");
            parametres.add(Timestamp.valueOf(dateFin));
        }
        
        sql.append(" ORDER BY t.date_heure_depart");
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < parametres.size(); i++) {
                statement.setObject(i + 1, parametres.get(i));
            }
            
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                trajets.add(mapperTrajetComplet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de trajets : " + e.getMessage());
        }
        
        return trajets;
    }

    public List<Trajet> obtenirTrajetsDisponibles() {
        List<Trajet> trajets = new ArrayList<>();
        String sql = """
            SELECT t.*, b.*, c.* FROM trajets t
            LEFT JOIN bus b ON t.bus_id = b.id_bus
            LEFT JOIN chauffeurs c ON t.chauffeur_id = c.id_chauffeur
            WHERE t.statut_trajet = 'PROGRAMME' AND t.nombre_places_disponibles > 0 
            AND t.date_heure_depart > CURRENT_TIMESTAMP
            ORDER BY t.date_heure_depart
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                trajets.add(mapperTrajetComplet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des trajets disponibles : " + e.getMessage());
        }
        
        return trajets;
    }

    public boolean mettreAJourTrajet(Trajet trajet) {
        String sql = """
            UPDATE trajets SET ville_depart = ?, ville_arrivee = ?, date_heure_depart = ?, 
            date_heure_arrivee_estimee = ?, bus_id = ?, chauffeur_id = ?, prix = ?, 
            nombre_places_disponibles = ?, statut_trajet = ?, commentaires = ?
            WHERE id_trajet = ?
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setString(1, trajet.getVilleDepart());
            statement.setString(2, trajet.getVilleArrivee());
            statement.setTimestamp(3, Timestamp.valueOf(trajet.getDateHeureDepart()));
            statement.setTimestamp(4, trajet.getDateHeureArriveeEstimee() != null ? 
                Timestamp.valueOf(trajet.getDateHeureArriveeEstimee()) : null);
            statement.setLong(5, trajet.getBus().getIdBus());
            statement.setLong(6, trajet.getChauffeur().getIdChauffeur());
            statement.setDouble(7, trajet.getPrix());
            statement.setInt(8, trajet.getNombrePlacesDisponibles());
            statement.setString(9, trajet.getStatutTrajet().name());
            statement.setString(10, trajet.getCommentaires());
            statement.setLong(11, trajet.getIdTrajet());
            
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du trajet : " + e.getMessage());
        }
        
        return false;
    }

    public boolean supprimerTrajet(Long id) {
        String sql = "UPDATE trajets SET statut_trajet = 'ANNULE' WHERE id_trajet = ?";
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du trajet : " + e.getMessage());
        }
        
        return false;
    }

    private Trajet mapperTrajetComplet(ResultSet resultSet) throws SQLException {
        Trajet trajet = new Trajet();
        trajet.setIdTrajet(resultSet.getLong("id_trajet"));
        trajet.setVilleDepart(resultSet.getString("ville_depart"));
        trajet.setVilleArrivee(resultSet.getString("ville_arrivee"));
        trajet.setDateHeureDepart(resultSet.getTimestamp("date_heure_depart").toLocalDateTime());
        
        Timestamp arriveeEstimee = resultSet.getTimestamp("date_heure_arrivee_estimee");
        if (arriveeEstimee != null) {
            trajet.setDateHeureArriveeEstimee(arriveeEstimee.toLocalDateTime());
        }
        
        trajet.setPrix(resultSet.getDouble("prix"));
        trajet.setNombrePlacesDisponibles(resultSet.getInt("nombre_places_disponibles"));
        
        String statutString = resultSet.getString("statut_trajet");
        if (statutString != null) {
            trajet.setStatutTrajet(Trajet.StatutTrajet.valueOf(statutString));
        }
        
        trajet.setCommentaires(resultSet.getString("commentaires"));
        trajet.setDateCreationTrajet(resultSet.getTimestamp("date_creation_trajet").toLocalDateTime());
        
        // Mapper le bus si présent
        Long busId = resultSet.getLong("bus_id");
        if (busId != 0) {
            Bus bus = new Bus();
            bus.setIdBus(busId);
            bus.setMatricule(resultSet.getString("matricule"));
            bus.setMarque(resultSet.getString("marque"));
            bus.setModele(resultSet.getString("modele"));
            bus.setCapacite(resultSet.getInt("capacite"));
            trajet.setBus(bus);
        }
        
        // Mapper le chauffeur si présent
        Long chauffeurId = resultSet.getLong("chauffeur_id");
        if (chauffeurId != 0) {
            Chauffeur chauffeur = new Chauffeur();
            chauffeur.setIdChauffeur(chauffeurId);
            chauffeur.setNomChauffeur(resultSet.getString("nom_chauffeur"));
            chauffeur.setPrenomChauffeur(resultSet.getString("prenom_chauffeur"));
            chauffeur.setTelephoneChauffeur(resultSet.getString("telephone_chauffeur"));
            trajet.setChauffeur(chauffeur);
        }
        
        return trajet;
    }
}