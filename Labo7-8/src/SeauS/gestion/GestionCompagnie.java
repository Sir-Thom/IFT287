package SeauS.gestion;

import SeauS.SeauSException;
import SeauS.collections.Compagnies;
import SeauS.collections.Parents;
import SeauS.collections.Projets;
import SeauS.objets.Compagnie;
import SeauS.objets.Projet;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class GestionCompagnie extends GestionTransactions {

    private final Compagnies compagnies;
    private final Projets projets;
    private final Parents parents;

    public GestionCompagnie(Compagnies compagnies, Parents parents, Projets projets) {
        super(compagnies.getConnexion());
        this.compagnies = compagnies;
        this.projets = projets;
        this.parents = parents;
    }

    public void ajouterCompagnie(String nomCompagnie, String adresse)
            throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            if (compagnies.existe(nomCompagnie)) {
                throw new SeauSException("Une compagnie avec le nom '" + nomCompagnie + "' existe déjà.");
            }

            Compagnie nouvelleCompagnie = new Compagnie(nomCompagnie, adresse);
            compagnies.ajouterCompagnie(nouvelleCompagnie);

            cx.commit();
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    public void editerCompagnie(String nomActuel, String nouveauNomCompagnie, String nouvelleAdresse)
            throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            Compagnie existante = compagnies.getCompagnieByNom(nomActuel);
            if (existante == null) {
                throw new SeauSException("La compagnie '" + nomActuel + "' n'existe pas.");
            }

            if (!nomActuel.equals(nouveauNomCompagnie) && compagnies.existe(nouveauNomCompagnie)) {
                throw new SeauSException("Une autre compagnie avec le nom '" + nouveauNomCompagnie + "' existe déjà.");
            }

            existante.setNomCompagnie(nouveauNomCompagnie);
            existante.setAdresse(nouvelleAdresse);

            cx.commit();
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur lors de la modification: " + e.getMessage());
        }
    }

    public void supprimerCompagnie(String nomCompagnie)
            throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            Compagnie c = compagnies.getCompagnieByNom(nomCompagnie);
            if (c == null) {
                throw new SeauSException("La compagnie '" + nomCompagnie + "' n'existe pas.");
            }

            compagnies.supprimerCompagnie(nomCompagnie);

            cx.commit();
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    public String afficherCompagnie(String nomCompagnie)
            throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            Compagnie compagnie = compagnies.getCompagnieByNom(nomCompagnie);
            if (compagnie == null) {
                throw new SeauSException("La compagnie '" + nomCompagnie + "' n'a pas été trouvée.");
            }

            cx.commit();
            System.out.println(compagnie);
            return compagnie.toString();
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur système: " + e.getMessage());
        }
    }

    public List<Projet> afficherProjetsCompagnie(String nomCompagnie)
            throws SQLException, SeauSException {
        try {
            cx.demarreTransaction();

            Compagnie compagnie = compagnies.getCompagnieByNom(nomCompagnie);
            if (compagnie == null) {
                throw new SeauSException("La compagnie '" + nomCompagnie + "' n'a pas été trouvée.");
            }

            List<Projet> result = projets.getProjetsPourCompagnie(compagnie.getIdCompagnie());

            cx.commit();
            return result;
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur système: " + e.getMessage());
        }
    }

    public void ajouterParent(String nomCompagnieParent, String nomCompagnieEnfant)
            throws SQLException, SeauSException {
        EntityManager em = null;
        try {
            cx.demarreTransaction();
            em = cx.getConnection();

            Compagnie parent = compagnies.getCompagnieByNom(nomCompagnieParent);
            Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);


            if (parent == null) {
                throw new SeauSException("Compagnie parent '" + nomCompagnieParent + "' non trouvée.");
            }
            if (enfant == null) {
                throw new SeauSException("Compagnie enfant '" + nomCompagnieEnfant + "' non trouvée.");
            }
            if (parent.getIdCompagnie() == enfant.getIdCompagnie()) {
                throw new SeauSException("Une compagnie ne peut pas être son propre parent.");
            }


            if (enfant.getParents().contains(parent)) {
                throw new SeauSException("La relation parent-enfant existe déjà.");
            }


            parents.ajouterRelation(parent.getIdCompagnie(), enfant.getIdCompagnie());



            em.merge(enfant);
            em.merge(parent);


            cx.commit();
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur système lors de l'ajout du parent: " + e.getMessage());
        }
    }

    public void enleverParent(String nomCompagnieParent, String nomCompagnieEnfant)
            throws SQLException, SeauSException {
        EntityManager em = null;
        try {
            cx.demarreTransaction();
            em = cx.getConnection();

            Compagnie parent = compagnies.getCompagnieByNom(nomCompagnieParent);
            Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);

            if (parent == null) {
                throw new SeauSException("Compagnie parent '" + nomCompagnieParent + "' non trouvée.");
            }
            if (enfant == null) {
                throw new SeauSException("Compagnie enfant '" + nomCompagnieEnfant + "' non trouvée.");
            }

            if (!enfant.getParents().contains(parent)) {
                throw new SeauSException("La relation parent-enfant n'existe pas.");
            }
            parents.supprimerRelation(parent.getIdCompagnie(), enfant.getIdCompagnie());


            em.merge(enfant);
            em.merge(parent);

            cx.commit();
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur système lors de la suppression du parent: " + e.getMessage());
        }
    }

    public List<Compagnie> afficherParents(String nomCompagnieEnfant) throws SeauSException {

        try {
            cx.demarreTransaction();


            Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);
            if (enfant == null) {
                throw new SeauSException("Compagnie enfant '" + nomCompagnieEnfant + "' non trouvée.");
            }


            cx.getConnection().refresh(enfant);

            parents.listerParentsEffectifs(enfant.getIdCompagnie());

            List<Compagnie> result = new ArrayList<>(enfant.getParents());

            cx.commit();
            System.out.println("Parents de " + nomCompagnieEnfant + ": " + result);
            return result;
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) cx.rollback();
            throw new SeauSException("Erreur système lors de l'affichage des parents: " + e.getMessage());
        }
    }


}
