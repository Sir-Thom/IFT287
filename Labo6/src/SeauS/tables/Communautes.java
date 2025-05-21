package SeauS.tables;

import SeauS.bdd.Connexion;
import SeauS.tuples.Communaute;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Communautes extends GestionTables {
    private final PreparedStatement stmtExisteCommunaute;
    private final PreparedStatement stmtInsertCommunaute;
    private final PreparedStatement stmtUpdateCommunaute;
    private final PreparedStatement stmtDeleteCommunaute;
    private final PreparedStatement stmtGetCommunauteByNom; // Renommé et utilisé pour getX
    private final PreparedStatement stmtGetCommunauteNomById; // Nom plus descriptif

    public Communautes(Connexion cx) throws SQLException {
        super(cx);

        stmtExisteCommunaute = cx.getConnection().prepareStatement(
                "SELECT idcommunaute, nom_communaute, nation, chef, coordonnees FROM communaute WHERE nom_communaute = ?");
        // Utilisé pour la méthode getCommunauteByNom, retourne un tuple
        stmtGetCommunauteByNom = cx.getConnection().prepareStatement(
                "SELECT idcommunaute, nom_communaute, nation, chef, coordonnees FROM communaute WHERE nom_communaute = ?");
        stmtInsertCommunaute = cx.getConnection().prepareStatement(
                "INSERT INTO communaute (nom_communaute, nation, chef, coordonnees) VALUES (?,?,?,?)");
        stmtUpdateCommunaute = cx.getConnection().prepareStatement(
                "UPDATE communaute SET nom_communaute = ?, nation = ?, chef = ?, coordonnees = ? WHERE nom_communaute = ?");
        stmtDeleteCommunaute = cx.getConnection().prepareStatement(
                "DELETE FROM communaute WHERE nom_communaute = ?");
        stmtGetCommunauteNomById = cx.getConnection().prepareStatement(
                "SELECT nom_communaute FROM communaute WHERE idcommunaute = ?");
    }

    public boolean existe(String nom) throws SQLException {
        stmtExisteCommunaute.setString(1, nom);
        return stmtExisteCommunaute.executeQuery().next();
    }

    public Communaute getCommunauteByNom(String nom) throws SQLException {
        stmtGetCommunauteByNom.setString(1, nom);
        try (ResultSet rs = stmtGetCommunauteByNom.executeQuery()) {
            if (rs.next()) {
                return new Communaute(
                        rs.getInt("idcommunaute"),
                        rs.getString("nom_communaute"),
                        rs.getString("nation"),
                        rs.getString("chef"),
                        rs.getString("coordonnees")
                );
            } else {
                return null; // Aucune communauté trouvée
            }
        }
    }

    public int ajouterCommunaute(Communaute communaute) throws SQLException {
        stmtInsertCommunaute.setString(1, communaute.nom);
        stmtInsertCommunaute.setString(2, communaute.nation);
        stmtInsertCommunaute.setString(3, communaute.chef);
        stmtInsertCommunaute.setString(4, communaute.coord);
        return stmtInsertCommunaute.executeUpdate();
    }

    public int modifierCommunaute(Communaute communaute, String nomActuel) throws SQLException {
        stmtUpdateCommunaute.setString(1, communaute.nom); // nouveau nom_communaute
        stmtUpdateCommunaute.setString(2, communaute.nation);
        stmtUpdateCommunaute.setString(3, communaute.chef);
        stmtUpdateCommunaute.setString(4, communaute.coord);
        stmtUpdateCommunaute.setString(5, nomActuel); // nom_communaute actuel pour la clause WHERE
        return stmtUpdateCommunaute.executeUpdate();
    }

    public int supprimerCommunaute(String nom) throws SQLException {
        stmtDeleteCommunaute.setString(1, nom);
        return stmtDeleteCommunaute.executeUpdate();
    }

    public String getNomCommunauteById(int id) throws SQLException {
        stmtGetCommunauteNomById.setInt(1, id);
        try (ResultSet rs = stmtGetCommunauteNomById.executeQuery()) {
            if (rs.next()) {
                return rs.getString("nom_communaute");
            } else {
                return null;
            }
        }
    }
}





