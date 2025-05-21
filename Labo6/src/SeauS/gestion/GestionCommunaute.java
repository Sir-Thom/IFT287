package SeauS.gestion;

import SeauS.SeauSException;
import SeauS.tables.Communautes;
import SeauS.tables.Projets;
import SeauS.tuples.Communaute;
import SeauS.tuples.Projet;

import java.sql.SQLException;
import java.util.List;

public class GestionCommunaute extends GestionTransactions {

    private final Communautes communautes;
    private final Projets projets;

    public GestionCommunaute(Communautes communautes, Projets projets) {
        super(communautes.getConnexion());

        this.communautes = communautes;
        this.projets = projets;
    }

    public void ajouterCommunaute(String nom, String nation, String chef, String coord)
            throws SQLException, SeauSException
    {
        try
        {
            // Vérifier que la communauté n'existe pas déjà
            if (communautes.existe(nom))
            {
                throw new SeauSException("Communauté existe déjà : " + nom);
            }

            // Création de la communauté
            Communaute communaute = new Communaute(nom, nation, chef, coord);
            communautes.ajouterCommunaute(communaute);

            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    public void editerCommunaute(String nomActuel, String nouveauNom, String nation, String chef, String coord)
            throws SQLException, SeauSException {
        try {

            if (!communautes.existe(nomActuel)) {
                throw new SeauSException("Communauté n'existe pas : " + nomActuel);
            }

            if (!nomActuel.equals(nouveauNom) && communautes.existe(nouveauNom)) {
                throw new SeauSException("Une autre communauté avec le nom '" + nouveauNom + "' existe déjà.");
            }

            // Création du tuple avec les nouvelles valeurs
            Communaute communauteModifiee = new Communaute(nouveauNom, nation, chef, coord);
            communautes.modifierCommunaute(communauteModifiee, nomActuel);

            // Commit
            cx.commit();
        } catch (SQLException | SeauSException e) {
            cx.rollback();
            throw e;
        }
    }

    public void supprimerCommunaute(String nom)
            throws SQLException, SeauSException {
        try {
            // Vérifier que la communauté existe
            if (!communautes.existe(nom)) {
                throw new SeauSException("Communauté n'existe pas : " + nom);
            }


            communautes.supprimerCommunaute(nom);

            // Commit
            cx.commit();
        } catch (SQLException | SeauSException e) {
            cx.rollback();
            throw e;
        }
    }

    public void afficherCommunaute(String nom) throws SQLException, SeauSException {
        try {


            Communaute communaute = communautes.getCommunauteByNom(nom);

            if (communaute != null) {
                System.out.println("Détails de la communauté : ");
                System.out.println("ID: " + communaute.idCommunaute);
                System.out.println("Nom: " + communaute.nom);
                System.out.println("Nation: " + communaute.nation);
                System.out.println("Chef: " + communaute.chef);
                System.out.println("Coordonnées: " + communaute.coord);
            } else {
                throw new SeauSException("Communauté n'existe pas : " + nom);

            }

        } catch (SQLException e) {
            cx.rollback();
            throw e; // Relancer l'exception SQL
        }
    }
    public List<Projet> afficherProjetsCommunaute(String nomCommunaute) throws SQLException, SeauSException {
        // First, get the community to retrieve its ID
        Communaute communaute = communautes.getCommunauteByNom(nomCommunaute);
        if (communaute == null) {
            throw new SeauSException("The community '" + nomCommunaute + "' was not found to list its projects.");
        }
        // Then, use the Projets table manager to get projects for this community ID
        return projets.getProjetsPourCommunaute(communaute.idCommunaute);
    }


    // TODO : implémenter le reste des fonctions
}
