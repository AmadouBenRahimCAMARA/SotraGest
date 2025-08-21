package com.sotragest.dao;

import com.sotragest.configuration.ConfigurationBaseDonnees;
import com.sotragest.modele.Agent;
import com.sotragest.modele.Gerant;
import com.sotragest.modele.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    // Surcharge principale utilisée par le contrôleur
    public Utilisateur authentifier(String identifiant, String motDePasse) {
        return authentifier(identifiant, motDePasse, false);
    }

    // Méthode protégée contre la récursion infinie
    public Utilisateur authentifier(String identifiant, String motDePasse, boolean dejaCorrige) {
        String sql = "SELECT * FROM utilisateurs WHERE identifiant = ? AND actif_utilisateur = TRUE";

        System.out.println("🔍 Tentative de connexion pour : " + identifiant);

        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {

            statement.setString(1, identifiant);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String motDePasseHache = resultSet.getString("mot_de_passe");
                System.out.println("✅ Utilisateur trouvé dans la base");
                System.out.println("🔐 Hash stocké : " + motDePasseHache.substring(0, 15) + "...");
                System.out.println("🔑 Mot de passe saisi : '" + motDePasse + "'");

                try {
                    boolean motDePasseValide = BCrypt.checkpw(motDePasse, motDePasseHache);
                    System.out.println("🔑 Vérification mot de passe : " + (motDePasseValide ? "✅ VALIDE" : "❌ INVALIDE"));

                    if (motDePasseValide) {
                        System.out.println("🎉 Authentification réussie !");
                        return mapperUtilisateur(resultSet);
                    } else {
                        System.out.println("❌ Mot de passe incorrect");
                        if ("admin".equals(identifiant) && !dejaCorrige) {
                            System.out.println("🔧 Tentative de correction du hash admin...");
                            if (ConfigurationBaseDonnees.forcerReinitialisationAdmin()) {
                                System.out.println("🔄 Hash corrigé - nouvelle tentative...");
                                return authentifier(identifiant, motDePasse, true);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de la vérification BCrypt : " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("❌ Utilisateur non trouvé : " + identifiant);

                try (Statement stmt = connexion.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateurs");
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("📊 Nombre d'utilisateurs dans la base : " + count);
                        if (count == 0) {
                            System.out.println("⚠️ Table utilisateurs vide - réinitialisation...");
                            ConfigurationBaseDonnees.initialiserBaseDonnees();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'authentification : " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public boolean creerUtilisateur(Utilisateur utilisateur) {
        String sql = """
            INSERT INTO utilisateurs (identifiant, mot_de_passe, nom_utilisateur, prenom_utilisateur, telephone_utilisateur, email_utilisateur, type_utilisateur, niveau_acces, poste_travail)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, utilisateur.getIdentifiant());
            statement.setString(2, BCrypt.hashpw(utilisateur.getMotDePasse(), BCrypt.gensalt()));
            statement.setString(3, utilisateur.getNomUtilisateur());
            statement.setString(4, utilisateur.getPrenomUtilisateur());
            statement.setString(5, utilisateur.getTelephoneUtilisateur());
            statement.setString(6, utilisateur.getEmailUtilisateur());

            if (utilisateur instanceof Gerant) {
                statement.setString(7, "GERANT");
                statement.setString(8, "COMPLET");
                statement.setString(9, null);
            } else if (utilisateur instanceof Agent) {
                statement.setString(7, "AGENT");
                statement.setString(8, "RESTREINT");
                statement.setString(9, ((Agent) utilisateur).getPosteTravail());
            }

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    utilisateur.setIdUtilisateur(generatedKeys.getLong(1));
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de l'utilisateur : " + e.getMessage());
        }

        return false;
    }

    public List<Utilisateur> obtenirTousLesUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs ORDER BY nom_utilisateur, prenom_utilisateur";

        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                utilisateurs.add(mapperUtilisateur(resultSet));
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }

        return utilisateurs;
    }

    public boolean mettreAJourUtilisateur(Utilisateur utilisateur) {
        String sql = """
            UPDATE utilisateurs SET nom_utilisateur = ?, prenom_utilisateur = ?, telephone_utilisateur = ?, email_utilisateur = ?, poste_travail = ?
            WHERE id_utilisateur = ?
        """;

        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {

            statement.setString(1, utilisateur.getNomUtilisateur());
            statement.setString(2, utilisateur.getPrenomUtilisateur());
            statement.setString(3, utilisateur.getTelephoneUtilisateur());
            statement.setString(4, utilisateur.getEmailUtilisateur());

            if (utilisateur instanceof Agent) {
                statement.setString(5, ((Agent) utilisateur).getPosteTravail());
            } else {
                statement.setString(5, null);
            }

            statement.setLong(6, utilisateur.getIdUtilisateur());
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }

        return false;
    }

    public boolean supprimerUtilisateur(Long id) {
        String sql = "UPDATE utilisateurs SET actif_utilisateur = FALSE WHERE id_utilisateur = ?";

        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {

            statement.setLong(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }

        return false;
    }
    
    public boolean mettreAJourNomCompagnie(Long idUtilisateur, String nomCompagnie) {
        String sql = "UPDATE utilisateurs SET nom_compagnie = ? WHERE id_utilisateur = ?";
        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {
            statement.setString(1, nomCompagnie);
            statement.setLong(2, idUtilisateur);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur MAJ nom compagnie : " + e.getMessage());
            return false;
        }
    }

    private Utilisateur mapperUtilisateur(ResultSet resultSet) throws SQLException {
        String typeUtilisateur = resultSet.getString("type_utilisateur");
        Utilisateur utilisateur;

        if ("GERANT".equals(typeUtilisateur)) {
            utilisateur = new Gerant();
        } else {
            Agent agent = new Agent();
            agent.setPosteTravail(resultSet.getString("poste_travail"));
            utilisateur = agent;
        }

        utilisateur.setIdUtilisateur(resultSet.getLong("id_utilisateur"));
        utilisateur.setIdentifiant(resultSet.getString("identifiant"));
        utilisateur.setMotDePasse(resultSet.getString("mot_de_passe"));
        utilisateur.setNomUtilisateur(resultSet.getString("nom_utilisateur"));
        utilisateur.setPrenomUtilisateur(resultSet.getString("prenom_utilisateur"));
        utilisateur.setTelephoneUtilisateur(resultSet.getString("telephone_utilisateur"));
        utilisateur.setEmailUtilisateur(resultSet.getString("email_utilisateur"));
        utilisateur.setDateCreationUtilisateur(resultSet.getTimestamp("date_creation_utilisateur").toLocalDateTime());
        utilisateur.setActifUtilisateur(resultSet.getBoolean("actif_utilisateur"));
        utilisateur.setNomCompagnie(resultSet.getString("nom_compagnie"));

        return utilisateur;
    }

    public boolean reinitialiserMotDePasseAdmin() {
        String sql = "UPDATE utilisateurs SET mot_de_passe = ? WHERE identifiant = 'admin'";

        try (Connection connexion = ConfigurationBaseDonnees.obtenirConnexion();
             PreparedStatement statement = connexion.prepareStatement(sql)) {

            String nouveauHash = BCrypt.hashpw("admin", BCrypt.gensalt());
            statement.setString(1, nouveauHash);

            int rowsAffected = statement.executeUpdate();
            System.out.println("🔄 Mot de passe admin réinitialisé : " + (rowsAffected > 0 ? "✅" : "❌"));
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erreur lors de la réinitialisation du mot de passe : " + e.getMessage());
        }

        return false;
    }
}
