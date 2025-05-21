package SeauS.gestion;

import SeauS.SeauSException;
import SeauS.tables.Projets;
import SeauS.tuples.Projet;

import java.sql.Date;
import java.sql.SQLException;

public class GestionProjet  extends GestionTransactions {
    private final Projets projets;

    public GestionProjet(Projets projets) {
        super(projets.getConnexion()); // Pass the connection from the table manager to the superclass
        this.projets = projets;
    }

    public void ajouterProjet(int idCommunaute, int idCompagnie, float budgetInitial,
                              float budgetFinal, String charge, String dateAnnonce,
                              String dateDebut, String dateFin, String etatAvancement)
            throws SQLException, SeauSException {
        try {
            // Convert String dates to java.sql.Date (you'll need a helper method for this)
            Date sqlDateAnnonce = Date.valueOf(dateAnnonce);
            Date sqlDateDebut = Date.valueOf(dateDebut);
            Date sqlDateFin = Date.valueOf(dateFin);

            // Create a Projet tuple
            // Assuming the ID is auto-generated, so we use the constructor without idProjet
            Projet nouveauProjet = new Projet(idCommunaute, idCompagnie, budgetInitial,
                    budgetFinal, charge, sqlDateAnnonce,
                    sqlDateDebut, sqlDateFin, etatAvancement);

            projets.ajouterProjet(nouveauProjet); // Call to the table manager
            cx.commit(); // Commit the transaction
        } catch (SQLException e) {
            cx.rollback(); // Rollback on SQL error
            throw e;
        }  catch (Exception e) {
            cx.rollback(); // Catch any other unexpected errors
            throw e;
        }
    }

    /**
     * Edits an existing project.
     * @param idProjet The ID of the project to edit.
     * @param idCommunaute The updated ID of the community.
     * @param idCompagnie The updated ID of the company.
     * @param budgetInitial The updated initial budget.
     * @param budgetFinal The updated final budget.
     * @param charge The updated person in charge.
     * @param dateAnnonce The updated announcement date.
     * @param dateDebut The updated start date.
     * @param dateFin The updated end date.
     * @param etatAvancement The updated progress status.
     * @throws SQLException In case of an SQL error.
     * @throws SeauSException If the project does not exist or other business rules are violated.
     */
    public void editerProjet(int idProjet, int idCommunaute, int idCompagnie, float budgetInitial,
                             float budgetFinal, String charge, String dateAnnonce,
                             String dateDebut, String dateFin, String etatAvancement)
            throws SQLException, SeauSException {
        try {
            // First, check if the project to edit exists
            if (!projets.existe(idProjet)) {
                throw new SeauSException("Project with ID '" + idProjet + "' does not exist.");
            }

            // Convert String dates to java.sql.Date
            Date sqlDateAnnonce = Date.valueOf(dateAnnonce);
            Date sqlDateDebut = Date.valueOf(dateDebut);
            Date sqlDateFin = Date.valueOf(dateFin);

            // Create a Projet tuple with updated information, including its ID
            Projet projetModifie = new Projet(idProjet, idCommunaute, idCompagnie, budgetInitial,
                    budgetFinal, charge, sqlDateAnnonce,
                    sqlDateDebut, sqlDateFin, etatAvancement);

            projets.editerProjet(projetModifie); // Call to the table manager
            cx.commit(); // Commit the transaction
        } catch (SQLException e) {
            cx.rollback();
            throw e;
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
    }

    /**
     * Displays the details of a specific project.
     * @param idProjet The ID of the project to display.
     * @return The Projet tuple.
     * @throws SQLException In case of an SQL error.
     * @throws SeauSException If the project is not found.
     */
    public Projet afficherProjet(int idProjet) throws SQLException, SeauSException {
        // Reads typically don't need commit/rollback if they are standalone
        Projet projet = projets.getProjet(idProjet);
        if (projet == null) {
            throw new SeauSException("Project with ID '" + idProjet + "' was not found.");
        }
        return projet;
    }

    /**
     * Deletes a project.
     * @param idProjet The ID of the project to delete.
     * @throws SQLException In case of an SQL error.
     * @throws SeauSException If the project does not exist.
     */
    public void supprimerProjet(int idProjet) throws SQLException, SeauSException {
        try {
            if (!projets.existe(idProjet)) {
                throw new SeauSException("Project with ID '" + idProjet + "' does not exist.");
            }
            projets.supprimerProjet(idProjet); // Call to the table manager
            cx.commit(); // Commit the transaction
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
    }

}
