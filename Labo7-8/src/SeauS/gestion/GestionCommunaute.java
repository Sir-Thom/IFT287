package SeauS.gestion;

import SeauS.SeauSException;
import SeauS.collections.Communautes;
import SeauS.collections.Projets;
import SeauS.objets.Communaute;
import SeauS.objets.Projet;

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
            throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            if (communautes.existe(nom)) {
                throw new SeauSException("Communauté existe déjà : " + nom);
            }

            Communaute communaute = new Communaute(nom, nation, chef, coord);
            communautes.ajouterCommunaute(communaute);

            cx.commit();
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    public void editerCommunaute(String nomActuel, String nouveauNom, String nation, String chef, String coord)
            throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            Communaute existante = communautes.getCommunauteByNom(nomActuel);
            if (existante == null) {
                throw new SeauSException("Communauté n'existe pas : " + nomActuel);
            }

            if (!nomActuel.equals(nouveauNom) && communautes.existe(nouveauNom)) {
                throw new SeauSException("Une autre communauté avec le nom '" + nouveauNom + "' existe déjà.");
            }

            existante.setNom(nouveauNom);
            existante.setNation(nation);
            existante.setChef(chef);
            existante.setCoord(coord);

            cx.commit();
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur lors de la modification: " + e.getMessage());
        }
    }

    public void supprimerCommunaute(String nom)
            throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            Communaute c = communautes.getCommunauteByNom(nom);
            if (c == null) {
                throw new SeauSException("Communauté n'existe pas : " + nom);
            }

            communautes.supprimerCommunaute(nom);

            cx.commit();
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    public Communaute afficherCommunaute(String nom) throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            Communaute communaute = communautes.getCommunauteByNom(nom);
            if (communaute == null) {
                throw new SeauSException("Communauté n'existe pas : " + nom);
            }

            cx.commit();
            System.out.println(communaute);
            return communaute;
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur lors de la récupération: " + e.getMessage());
        }
    }

    public List<Projet> afficherProjetsCommunaute(String nomCommunaute)
            throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            Communaute communaute = communautes.getCommunauteByNom(nomCommunaute);
            if (communaute == null) {
                throw new SeauSException("La communauté '" + nomCommunaute + "' n'a pas été trouvée.");
            }

            List<Projet> result = projets.getProjetsPourCommunaute(communaute.getIdCommunaute());

            cx.commit();
            System.out.println(result.toString());
            return result;
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur système: " + e.getMessage());
        }
    }
}
