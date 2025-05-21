package SeauS.tables;

import SeauS.bdd.Connexion;
import SeauS.tuples.Compagnie;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Compagnies extends GestionTables {
        private final PreparedStatement stmtGetCompagnieByNom;
        private final PreparedStatement stmtGetCompagnieById;
        private final PreparedStatement stmtInsertCompagnie;
        private final PreparedStatement stmtUpdateCompagnie;
        private final PreparedStatement stmtDeleteCompagnieByNom;
        private final PreparedStatement stmtGetNomCompagnieById; // For stmtGetCompagnieNom

        public Compagnies(Connexion cx) throws SQLException {
            super(cx);

            // Renamed stmtExisteCompagnie to stmtGetCompagnieByNom for clarity, as it fetches data for the tuple
            stmtGetCompagnieByNom = cx.getConnection().prepareStatement(
                    "SELECT idcompagnie, nom_compagnie, adresse FROM compagnie WHERE nom_compagnie = ?");
            // Renamed stmtExisteCompagnie_id to stmtGetCompagnieById
            stmtGetCompagnieById = cx.getConnection().prepareStatement(
                    "SELECT idcompagnie, nom_compagnie, adresse FROM compagnie WHERE idcompagnie = ?");
            stmtInsertCompagnie = cx.getConnection().prepareStatement(
                    "INSERT INTO compagnie (nom_compagnie, adresse) VALUES (?,?)", PreparedStatement.RETURN_GENERATED_KEYS); // Assuming ID might be auto-generated
            stmtUpdateCompagnie = cx.getConnection().prepareStatement(
                    "UPDATE compagnie SET nom_compagnie = ?, adresse = ? WHERE nom_compagnie = ?"); // Identifier is nom_compagnie
            stmtDeleteCompagnieByNom = cx.getConnection().prepareStatement(
                    "DELETE FROM compagnie WHERE nom_compagnie = ?");
            stmtGetNomCompagnieById = cx.getConnection().prepareStatement(
                    "SELECT nom_compagnie FROM compagnie WHERE idcompagnie = ?");
        }


        public boolean existe(String nomCompagnie) throws SQLException {
            stmtGetCompagnieByNom.setString(1, nomCompagnie);
            try (ResultSet rs = stmtGetCompagnieByNom.executeQuery()) {
                return rs.next();
            }
        }


        public boolean existe(int idCompagnie) throws SQLException {
            stmtGetCompagnieById.setInt(1, idCompagnie);
            try (ResultSet rs = stmtGetCompagnieById.executeQuery()) {
                return rs.next();
            }
        }


        public Compagnie getCompagnieByNom(String nomCompagnie) throws SQLException {
            stmtGetCompagnieByNom.setString(1, nomCompagnie);
            try (ResultSet rs = stmtGetCompagnieByNom.executeQuery()) {
                if (rs.next()) {
                    return new Compagnie(
                            rs.getInt("idcompagnie"),
                            rs.getString("nom_compagnie"),
                            rs.getString("adresse")
                    );
                }
            }
            return null;
        }


        public Compagnie getCompagnieById(int idCompagnie) throws SQLException {
            stmtGetCompagnieById.setInt(1, idCompagnie);
            try (ResultSet rs = stmtGetCompagnieById.executeQuery()) {
                if (rs.next()) {
                    return new Compagnie(
                            rs.getInt("idcompagnie"),
                            rs.getString("nom_compagnie"),
                            rs.getString("adresse")
                    );
                }
            }
            return null;
        }


        public int ajouterCompagnie(Compagnie compagnie) throws SQLException {
            stmtInsertCompagnie.setString(1, compagnie.nomCompagnie);
            stmtInsertCompagnie.setString(2, compagnie.adresse);
            return stmtInsertCompagnie.executeUpdate();

        }


        public int editerCompagnie(Compagnie compagnie, String nomActuel) throws SQLException {
            stmtUpdateCompagnie.setString(1, compagnie.nomCompagnie); // Nouveau nom
            stmtUpdateCompagnie.setString(2, compagnie.adresse);    // Nouvelle adresse
            stmtUpdateCompagnie.setString(3, nomActuel);            // Nom actuel pour la clause WHERE
            return stmtUpdateCompagnie.executeUpdate();
        }


        public int supprimerCompagnie(String nomCompagnie) throws SQLException {
            stmtDeleteCompagnieByNom.setString(1, nomCompagnie);
            return stmtDeleteCompagnieByNom.executeUpdate();
        }

        public String getNomCompagnieById(int idCompagnie) throws SQLException {
            stmtGetNomCompagnieById.setInt(1, idCompagnie);
            try (ResultSet rs = stmtGetNomCompagnieById.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom_compagnie");
                }
            }
            return null;
        }


}
