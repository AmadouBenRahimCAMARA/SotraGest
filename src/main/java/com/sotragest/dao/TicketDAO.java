package com.sotragest.dao;

import com.sotragest.configuration.ConfigurationBaseDonnees;
import com.sotragest.modele.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


// DAO pour la gestion des tickets

public class TicketDAO {

    public boolean creerTicket(Ticket ticket) {
        String sql = """
            INSERT INTO tickets (numero_ticket, voyageur_id, trajet_id, numero_siege, prix, vendeur_id, code_qr, observations_ticket)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, ticket.getNumeroTicket());
            statement.setLong(2, ticket.getVoyageur().getIdVoyageur());
            statement.setLong(3, ticket.getTrajet().getIdTrajet());
            statement.setInt(4, ticket.getNumeroSiege());
            statement.setDouble(5, ticket.getPrix());
            statement.setLong(6, ticket.getVendeur().getIdUtilisateur());
            statement.setString(7, ticket.getCodeQR());
            statement.setString(8, ticket.getObservationsTicket());
            
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ticket.setIdTicket(generatedKeys.getLong(1));
                }
                
                // Mettre à jour le nombre de places disponibles du trajet
                mettreAJourPlacesTrajet(ticket.getTrajet().getIdTrajet(), -1);
                
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création du ticket : " + e.getMessage());
        }
        
        return false;
    }

    public List<Ticket> obtenirTousLesTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = """
            SELECT t.*, v.nom_voyageur as v_nom, v.prenom_voyageur as v_prenom, v.telephone_voyageur as v_telephone,
            tr.ville_depart, tr.ville_arrivee, tr.date_heure_depart, tr.prix as tr_prix,
            u.nom_utilisateur as u_nom, u.prenom_utilisateur as u_prenom, u.nom_compagnie as u_compagnie
            FROM tickets t
            LEFT JOIN voyageurs v ON t.voyageur_id = v.id_voyageur
            LEFT JOIN trajets tr ON t.trajet_id = tr.id_trajet
            LEFT JOIN utilisateurs u ON t.vendeur_id = u.id_utilisateur
            ORDER BY t.date_vente DESC
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                tickets.add(mapperTicketComplet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des tickets : " + e.getMessage());
        }
        
        return tickets;
    }

    public List<Ticket> obtenirTicketsParPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = """
            SELECT t.*, v.nom_voyageur as v_nom, v.prenom_voyageur as v_prenom, v.telephone_voyageur as v_telephone,
            tr.ville_depart, tr.ville_arrivee, tr.date_heure_depart, tr.prix as tr_prix,
            u.nom_utilisateur as u_nom, u.prenom_utilisateur as u_prenom, u.nom_compagnie as u_compagnie
            FROM tickets t
            LEFT JOIN voyageurs v ON t.voyageur_id = v.id_voyageur
            LEFT JOIN trajets tr ON t.trajet_id = tr.id_trajet
            LEFT JOIN utilisateurs u ON t.vendeur_id = u.id_utilisateur
            WHERE t.date_vente BETWEEN ? AND ?
            ORDER BY t.date_vente DESC
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setTimestamp(1, Timestamp.valueOf(dateDebut));
            statement.setTimestamp(2, Timestamp.valueOf(dateFin));
            
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                tickets.add(mapperTicketComplet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des tickets par période : " + e.getMessage());
        }
        
        return tickets;
    }

    public boolean annulerTicket(Long ticketId) {
        String sql = "UPDATE tickets SET statut_ticket = 'ANNULE' WHERE id_ticket = ?";
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setLong(1, ticketId);
            int rowsAffected = statement.executeUpdate();
            
            if (rowsAffected > 0) {
                // Récupérer l'ID du trajet pour libérer une place
                String sqlTrajet = "SELECT trajet_id FROM tickets WHERE id_ticket = ?";
                try (PreparedStatement stmtTrajet = connexion.prepareStatement(sqlTrajet)) {
                    stmtTrajet.setLong(1, ticketId);
                    ResultSet rs = stmtTrajet.executeQuery();
                    if (rs.next()) {
                        mettreAJourPlacesTrajet(rs.getLong("trajet_id"), 1);
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'annulation du ticket : " + e.getMessage());
        }
        
        return false;
    }

    public List<Ticket> rechercherTickets(String critere) {
        List<Ticket> tickets = new ArrayList<>();
        String sql = """
            SELECT t.*, v.nom_voyageur as v_nom, v.prenom_voyageur as v_prenom, v.telephone_voyageur as v_telephone,
            tr.ville_depart, tr.ville_arrivee, tr.date_heure_depart, tr.prix as tr_prix,
            u.nom_utilisateur as u_nom, u.prenom_utilisateur as u_prenom, u.nom_compagnie as u_compagnie
            FROM tickets t
            LEFT JOIN voyageurs v ON t.voyageur_id = v.id_voyageur
            LEFT JOIN trajets tr ON t.trajet_id = tr.id_trajet
            LEFT JOIN utilisateurs u ON t.vendeur_id = u.id_utilisateur
            WHERE t.numero_ticket LIKE ? OR LOWER(v.nom_voyageur) LIKE LOWER(?) OR LOWER(v.prenom_voyageur) LIKE LOWER(?)
            OR LOWER(tr.ville_depart) LIKE LOWER(?) OR LOWER(tr.ville_arrivee) LIKE LOWER(?)
            ORDER BY t.date_vente DESC
            """;
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            String critereRecherche = "%" + critere + "%";
            for (int i = 1; i <= 5; i++) {
                statement.setString(i, critereRecherche);
            }
            
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                tickets.add(mapperTicketComplet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de tickets : " + e.getMessage());
        }
        
        return tickets;
    }

    private void mettreAJourPlacesTrajet(Long trajetId, int changement) throws SQLException {
        String sql = "UPDATE trajets SET nombre_places_disponibles = nombre_places_disponibles + ? WHERE id_trajet = ?";
        
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            
            statement.setInt(1, changement);
            statement.setLong(2, trajetId);
            statement.executeUpdate();
        }
    }

    private Ticket mapperTicketComplet(ResultSet resultSet) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setIdTicket(resultSet.getLong("id_ticket"));
        ticket.setNumeroTicket(resultSet.getString("numero_ticket"));
        ticket.setNumeroSiege(resultSet.getInt("numero_siege"));
        ticket.setPrix(resultSet.getDouble("prix"));
        
        String statutString = resultSet.getString("statut_ticket");
        if (statutString != null) {
            ticket.setStatutTicket(Ticket.StatutTicket.valueOf(statutString));
        }
        
        ticket.setDateVente(resultSet.getTimestamp("date_vente").toLocalDateTime());
        
        Timestamp dateUtilisation = resultSet.getTimestamp("date_utilisation");
        if (dateUtilisation != null) {
            ticket.setDateUtilisation(dateUtilisation.toLocalDateTime());
        }
        
        ticket.setCodeQR(resultSet.getString("code_qr"));
        ticket.setObservationsTicket(resultSet.getString("observations_ticket"));
        
        // Mapper le voyageur
        Voyageur voyageur = new Voyageur();
        voyageur.setIdVoyageur(resultSet.getLong("voyageur_id"));
        voyageur.setNomVoyageur(resultSet.getString("v_nom"));
        voyageur.setPrenomVoyageur(resultSet.getString("v_prenom"));
        voyageur.setTelephoneVoyageur(resultSet.getString("v_telephone"));
        ticket.setVoyageur(voyageur);
        
        // Mapper le trajet (simplifié)
        Trajet trajet = new Trajet();
        trajet.setIdTrajet(resultSet.getLong("trajet_id"));
        trajet.setVilleDepart(resultSet.getString("ville_depart"));
        trajet.setVilleArrivee(resultSet.getString("ville_arrivee"));
        trajet.setDateHeureDepart(resultSet.getTimestamp("date_heure_depart").toLocalDateTime());
        trajet.setPrix(resultSet.getDouble("tr_prix"));
        ticket.setTrajet(trajet);
        
        // Mapper le vendeur (simplifié)
        if (resultSet.getString("u_nom") != null) {
            Agent vendeur = new Agent();
            vendeur.setIdUtilisateur(resultSet.getLong("vendeur_id"));
            vendeur.setNomUtilisateur(resultSet.getString("u_nom"));
            vendeur.setPrenomUtilisateur(resultSet.getString("u_prenom"));
            vendeur.setNomCompagnie(resultSet.getString("u_compagnie"));
            ticket.setVendeur(vendeur);
        }
        
        return ticket;
    }
}