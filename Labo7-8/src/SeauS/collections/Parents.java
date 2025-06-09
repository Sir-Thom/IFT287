package SeauS.collections;

import SeauS.SeauSException;
import SeauS.bdd.Connexion;
import SeauS.objets.Compagnie;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;

public class Parents extends GestionTables {
    private final TypedQuery<Long> stmtCountRelation;
    private final TypedQuery<Compagnie> stmtGetParents;

    public Parents(Connexion cx) {
        super(cx);
        EntityManager em = cx.getConnection();

        stmtCountRelation = em.createQuery(
                "SELECT COUNT(p) " +
                        "FROM Compagnie c JOIN c.parents p " +
                        "WHERE c.idCompagnie = :idEnfant AND p.idCompagnie = :idParent",
                Long.class
        );


        stmtGetParents = em.createQuery(
                "SELECT p FROM Compagnie c JOIN c.parents p " +
                        "WHERE c.idCompagnie = :idEnfant",
                Compagnie.class
        );
    }

    public boolean existeRelation(int idParent, int idEnfant) {
        stmtCountRelation.setParameter("idParent", idParent);
        stmtCountRelation.setParameter("idEnfant", idEnfant);
        Long count = stmtCountRelation.getSingleResult();
        return count != null && count > 0;
    }


    public void ajouterRelation(int idParent, int idEnfant) throws SeauSException {
        EntityManager em = cx.getConnection();
        boolean startedTransaction = false;

        try {
            if (!em.getTransaction().isActive()) {
                cx.demarreTransaction();
                startedTransaction = true;
            }

            if (existeRelation(idParent, idEnfant)) {
                throw new SeauSException("La relation existe déjà");
            }

            Compagnie parent = em.find(Compagnie.class, idParent);
            Compagnie enfant = em.find(Compagnie.class, idEnfant);

            if (parent == null || enfant == null) {
                throw new SeauSException("Compagnie introuvable");
            }

            // Correction : Ajouter l'enfant à la liste des enfants du parent
            parent.getEnfants().add(enfant);

            if (startedTransaction) {
                cx.commit();
            }
        } catch (Exception e) {
            if (startedTransaction) {
                cx.rollback();
            }
            throw new SeauSException("Erreur lors de l'ajout: " + e.getMessage());
        }
    }

    public void supprimerRelation(int idParent, int idEnfant) throws SeauSException {
        EntityManager em = cx.getConnection();
        boolean startedTransaction = false;

        try {
            if (!em.getTransaction().isActive()) {
                cx.demarreTransaction();
                startedTransaction = true;
            }

            if (!existeRelation(idParent, idEnfant)) {
                throw new SeauSException("La relation n'existe pas");
            }

            Compagnie parent = em.find(Compagnie.class, idParent);
            Compagnie enfant = em.find(Compagnie.class, idEnfant);

            if (parent == null || enfant == null) {
                throw new SeauSException("Compagnie introuvable");
            }

            parent.getEnfants().removeIf(e -> Objects.equals(e.getIdCompagnie(), idEnfant));
            enfant.getParents().removeIf(p -> Objects.equals(p.getIdCompagnie(), idParent));


            if (startedTransaction) {
                cx.commit();
            }
        } catch (Exception e) {
            if (startedTransaction) {
                cx.rollback();
            }
            throw new SeauSException("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    public List<Compagnie> listerParentsEffectifs(int idEnfant) {
        stmtGetParents.setParameter("idEnfant", idEnfant);
        return stmtGetParents.getResultList();
    }
}