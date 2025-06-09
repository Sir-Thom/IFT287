package SeauS.collections;

import SeauS.bdd.Connexion;
import SeauS.objets.Compagnie;

import javax.persistence.TypedQuery;
import java.util.List;


public class Compagnies extends GestionTables {
    private final Connexion cx;
    private final TypedQuery<Compagnie> stmtGetCompagnieByNom;
    private final TypedQuery<Compagnie> stmtGetCompagnieById;


    public Compagnies(Connexion cx) {
        super(cx);


        this.cx = cx;
        stmtGetCompagnieByNom = cx.getConnection().createQuery(
                "SELECT c FROM Compagnie c WHERE c.nomCompagnie = :nom",
                Compagnie.class
        );

        stmtGetCompagnieById = cx.getConnection().createQuery(
                "SELECT c FROM Compagnie c WHERE c.idCompagnie = :id",
                Compagnie.class
        );


    }

    public boolean existe(String nomCompagnie) {
        stmtGetCompagnieByNom.setParameter("nom", nomCompagnie);
        return !stmtGetCompagnieByNom.getResultList().isEmpty();
    }


    public Compagnie getCompagnieByNom(String nomCompagnie) {
        stmtGetCompagnieByNom.setParameter("nom", nomCompagnie);
        List<Compagnie> result = stmtGetCompagnieByNom.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public Compagnie getCompagnieById(int idCompagnie) {
        stmtGetCompagnieById.setParameter("id", idCompagnie);
        List<Compagnie> result = stmtGetCompagnieById.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public void ajouterCompagnie(Compagnie compagnie) {
        cx.getConnection().persist(compagnie);
    }


    public int supprimerCompagnie(String nomCompagnie) {
        Compagnie c = getCompagnieByNom(nomCompagnie);
        if (c != null) {
            cx.getConnection().remove(c);
            return 1;
        }
        return 0;
    }


}
