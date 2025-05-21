package SeauS.tables;

import SeauS.bdd.Connexion;
import SeauS.tuples.Compagnie;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Parents extends GestionTables{


    private final PreparedStatement stmtExisteRelation;
    private final PreparedStatement stmtAddParent;
    private final PreparedStatement stmtRemoveParent;
    private final PreparedStatement stmtSelectParents;

    public Parents(Connexion cx) throws SQLException {
        super(cx); // Call the super constructor to get the connection

        // Initialize PreparedStatements
        stmtExisteRelation = cx.getConnection().prepareStatement(
                "SELECT 1 FROM Parent WHERE idparent = ? AND idenfant = ?");

        stmtAddParent = cx.getConnection().prepareStatement(
                "INSERT INTO Parent (idparent, idenfant) VALUES (?,?)");

        stmtRemoveParent = cx.getConnection().prepareStatement(
                "DELETE FROM Parent WHERE idparent = ? AND idenfant = ?");

        stmtSelectParents = cx.getConnection().prepareStatement(
                "SELECT c.idcompagnie, c.nom_compagnie, c.adresse " +
                        "FROM Parent p " +
                        "JOIN Compagnie c ON p.idparent = c.idcompagnie " +
                        "WHERE p.idenfant = ?"
        );
    }

    /**
     * Checks if a specific parent-child relationship exists.
     *
     * @param idParent The ID of the parent company.
     * @param idEnfant The ID of the child company.
     * @return `true` if the relationship exists, `false` otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean existeRelation(int idParent, int idEnfant) throws SQLException {
        stmtExisteRelation.setInt(1, idParent);
        stmtExisteRelation.setInt(2, idEnfant);
        try (ResultSet rset = stmtExisteRelation.executeQuery()) {
            return rset.next();
        }
    }

    /**
     * Adds a new parent-child relationship to the database.
     *
     * @param idParent The ID of the parent company.
     * @param idEnfant The ID of the child company.
     * @return The number of rows modified (should be 1 on success).
     * @throws SQLException If a database access error occurs.
     */
    public int ajouterRelation(int idParent, int idEnfant) throws SQLException {
        stmtAddParent.setInt(1, idParent);
        stmtAddParent.setInt(2, idEnfant);
        return stmtAddParent.executeUpdate();
    }

    /**
     * Removes an existing parent-child relationship from the database.
     *
     * @param idParent The ID of the parent company.
     * @param idEnfant The ID of the child company.
     * @return The number of rows modified (should be 1 on success, 0 if not found).
     * @throws SQLException If a database access error occurs.
     */
    public int supprimerRelation(int idParent, int idEnfant) throws SQLException {
        stmtRemoveParent.setInt(1, idParent);
        stmtRemoveParent.setInt(2, idEnfant);
        return stmtRemoveParent.executeUpdate();
    }

    /**
     * Lists all direct parent companies for a given child company.
     *
     * @param idEnfant The ID of the child company.
     * @return A `List` of `Compagnie` tuples representing the direct parents.
     * @throws SQLException If a database access error occurs.
     */
    public List<Compagnie> listerParentsEffectifs(int idEnfant) throws SQLException {
        List<Compagnie> parents = new ArrayList<>();
        stmtSelectParents.setInt(1, idEnfant);
        try (ResultSet rset = stmtSelectParents.executeQuery()) {
            while (rset.next()) {
                parents.add(new Compagnie(
                        rset.getInt("idcompagnie"),
                        rset.getString("nom_compagnie"),
                        rset.getString("adresse")
                ));
            }
        }
        return parents;
    }
}
