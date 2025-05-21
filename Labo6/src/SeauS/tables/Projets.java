package SeauS.tables;

import SeauS.bdd.Connexion;
import SeauS.tuples.Projet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Projets extends GestionTables{

    private final PreparedStatement stmtExisteProjet;
    private final PreparedStatement stmtInsertProjet;
    private final PreparedStatement stmtUpdateProjet;
    private final PreparedStatement stmtDeleteProjet;
    private final PreparedStatement stmtSelectProjetsCommunaute;

    public Projets(Connexion cx) throws SQLException {
        super(cx);
        stmtExisteProjet = cx.getConnection().prepareStatement("SELECT * FROM Projet WHERE idprojet = ?");
        stmtInsertProjet = cx.getConnection().prepareStatement(
                "insert into projet (idcommunaute, idcompagnie, budget_initial, budget_final, charge_projet, date_annonce, date_debut, date_fin, etat_avancement) values (?, ?, ?, ?, ?, to_date(?, 'YYYY-MM-DD'), to_date(?, 'YYYY-MM-DD'), to_date(?, 'YYYY-MM-DD'), ?)");
        stmtUpdateProjet = cx.getConnection().prepareStatement(
                "UPDATE Projet SET idcommunaute = ?, idcompagnie = ?, budget_initial = ?, budget_final = ?, charge_projet = ?, " +
                        "date_annonce = to_date(?, 'YYYY-MM-DD'), date_debut = to_date(?, 'YYYY-MM-DD'), date_fin = to_date(?, 'YYYY-MM-DD'), " +
                        "etat_avancement = ? WHERE idprojet = ?");
        stmtDeleteProjet = cx.getConnection().prepareStatement("DELETE FROM Projet WHERE idprojet = ?");
        stmtSelectProjetsCommunaute = cx.getConnection().prepareStatement("select * from projet where idcommunaute = ?"); // Moved from main class
    }

    public boolean existe(int idProjet) throws SQLException {
        stmtExisteProjet.setInt(1, idProjet);
        try (ResultSet rset = stmtExisteProjet.executeQuery()) {
            return rset.next();
        }
    }

    public Projet getProjet(int idProjet) throws SQLException {
        stmtExisteProjet.setInt(1, idProjet); // Reusing stmtExisteProjet for select, or create a new stmtGetProjetById
        try (ResultSet rset = stmtExisteProjet.executeQuery()) { // If reusing, ensure the SELECT * is part of the query
            if (rset.next()) {
                return new Projet(
                        rset.getInt("idprojet"),
                        rset.getInt("idcommunaute"),
                        rset.getInt("idcompagnie"),
                        rset.getFloat("budget_initial"),
                        rset.getFloat("budget_final"),
                        rset.getString("charge_projet"),
                        rset.getDate("date_annonce"),
                        rset.getDate("date_debut"),
                        rset.getDate("date_fin"),
                        rset.getString("etat_avancement")
                );
            }
            return null;
        }
    }

    /**
     * Adds a new project to the database.
     * @param projet The Projet tuple to add.
     * @return The number of rows modified.
     * @throws SQLException In case of an SQL error.
     */
    public int ajouterProjet(Projet projet) throws SQLException {
        stmtInsertProjet.setInt(1, projet.idCommunaute);
        stmtInsertProjet.setInt(2, projet.idCompagnie);
        stmtInsertProjet.setFloat(3, projet.budgetInitial);
        stmtInsertProjet.setFloat(4, projet.budgetFinal);
        stmtInsertProjet.setString(5, projet.chargeProjet);
        stmtInsertProjet.setString(6, projet.dateAnnonce.toString()); // Assuming Date objects are formatted as YYYY-MM-DD
        stmtInsertProjet.setString(7, projet.dateDebut.toString());
        stmtInsertProjet.setString(8, projet.dateFin.toString());
        stmtInsertProjet.setString(9, projet.etatAvancement);
        return stmtInsertProjet.executeUpdate();
    }

    /**
     * Edits an existing project in the database.
     * @param projet The Projet tuple with updated values and the ID.
     * @return The number of rows modified.
     * @throws SQLException In case of an SQL error.
     */
    public int editerProjet(Projet projet) throws SQLException {
        stmtUpdateProjet.setInt(1, projet.idCommunaute);
        stmtUpdateProjet.setInt(2, projet.idCompagnie);
        stmtUpdateProjet.setFloat(3, projet.budgetInitial);
        stmtUpdateProjet.setFloat(4, projet.budgetFinal);
        stmtUpdateProjet.setString(5, projet.chargeProjet);
        stmtUpdateProjet.setString(6, projet.dateAnnonce.toString());
        stmtUpdateProjet.setString(7, projet.dateDebut.toString());
        stmtUpdateProjet.setString(8, projet.dateFin.toString());
        stmtUpdateProjet.setString(9, projet.etatAvancement);
        stmtUpdateProjet.setInt(10, projet.idProjet); // WHERE clause parameter
        return stmtUpdateProjet.executeUpdate();
    }

    /**
     * Deletes a project by its ID.
     * @param idProjet The ID of the project to delete.
     * @return The number of rows modified.
     * @throws SQLException In case of an SQL error.
     */
    public int supprimerProjet(int idProjet) throws SQLException {
        stmtDeleteProjet.setInt(1, idProjet);
        return stmtDeleteProjet.executeUpdate();
    }

    /**
     * Retrieves all projects associated with a given community ID.
     * @param idCommunaute The ID of the community.
     * @return A list of Projet tuples.
     * @throws SQLException In case of an SQL error.
     */
    public List<Projet> getProjetsPourCommunaute(int idCommunaute) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        stmtSelectProjetsCommunaute.setInt(1, idCommunaute);
        try (ResultSet rset = stmtSelectProjetsCommunaute.executeQuery()) {
            while (rset.next()) {
                projets.add(new Projet(
                        rset.getInt("idprojet"),
                        rset.getInt("idcommunaute"),
                        rset.getInt("idcompagnie"),
                        rset.getFloat("budget_initial"),
                        rset.getFloat("budget_final"),
                        rset.getString("charge_projet"),
                        rset.getDate("date_annonce"),
                        rset.getDate("date_debut"),
                        rset.getDate("date_fin"),
                        rset.getString("etat_avancement")
                ));
            }
        }
        return projets;
    }

    /**
     * Retrieves all projects associated with a given company ID.
     * This method would typically be in the `Projets` table manager
     * as `listerProjetsCompagnie` is a transaction manager method
     * that calls this data layer method.
     * @param idCompagnie The ID of the company.
     * @return A list of Projet tuples.
     * @throws SQLException In case of an SQL error.
     */
    public List<Projet> getProjetsPourCompagnie(int idCompagnie) throws SQLException {
        List<Projet> projets = new ArrayList<>();
        // Assuming you have a PreparedStatement for this in your Projets class
        // private final PreparedStatement stmtSelectProjetsCompagnie;
        // initialized as: stmtSelectProjetsCompagnie = cx.getConnection().prepareStatement("select * from projet where idcompagnie = ?");

        // If you've moved stmtSelectProjetsCompagnie here:
        // stmtSelectProjetsCompagnie.setInt(1, idCompagnie);
        // try (ResultSet rset = stmtSelectProjetsCompagnie.executeQuery()) {
        //     while (rset.next()) {
        //         projets.add(new Projet(
        //             rset.getInt("idprojet"), // ... and so on for all fields
        //         ));
        //     }
        // }
        // return projets;

        // For now, using stmtExisteProjet as an example placeholder, but you should create a dedicated one.
        // Or better yet, ensure 'stmtSelectProjetsCompagnie' is properly declared and initialized above.
        // For example, if you already have it from your paste:
        //  stmtSelectProjetsCompagnie = cx.getConnection().prepareStatement("select * from projet where idcompagnie = ?");
        PreparedStatement stmtTempSelectProjetsCompagnie = cx.getConnection().prepareStatement("select * from projet where idcompagnie = ?");
        stmtTempSelectProjetsCompagnie.setInt(1, idCompagnie);
        try (ResultSet rset = stmtTempSelectProjetsCompagnie.executeQuery()) {
            while (rset.next()) {
                projets.add(new Projet(
                        rset.getInt("idprojet"),
                        rset.getInt("idcommunaute"),
                        rset.getInt("idcompagnie"),
                        rset.getFloat("budget_initial"),
                        rset.getFloat("budget_final"),
                        rset.getString("charge_projet"),
                        rset.getDate("date_annonce"),
                        rset.getDate("date_debut"),
                        rset.getDate("date_fin"),
                        rset.getString("etat_avancement")
                ));
            }
        } finally {
            stmtTempSelectProjetsCompagnie.close(); // Close temp statement
        }
        return projets;
    }
}
