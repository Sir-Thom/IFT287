package SeauS.collections;

import SeauS.bdd.Connexion;
import SeauS.objets.Communaute;

import javax.persistence.TypedQuery;
import java.util.List;

public class Communautes extends GestionTables {

    private final Connexion cx;
    private final TypedQuery<Communaute> stmtExisteCommunaute;
    private final TypedQuery<Communaute> stmtGetCommunauteByNom;
    private final TypedQuery<String> stmtGetCommunauteNomById;

    public Communautes(Connexion cx) {
        super(cx);
        this.cx = cx;
        stmtExisteCommunaute = cx.getConnection().createQuery("SELECT c FROM Communaute c WHERE c.nom = :nom", Communaute.class);

        stmtGetCommunauteByNom = cx.getConnection().createQuery("SELECT c FROM Communaute c WHERE c.nom = :nom", Communaute.class);

        stmtGetCommunauteNomById = cx.getConnection().createQuery("SELECT c.nom FROM Communaute c WHERE c.idCommunaute = :idCommunaute", String.class);
    }


    public boolean existe(String nom) {
        stmtExisteCommunaute.setParameter("nom", nom);
        return !stmtExisteCommunaute.getResultList().isEmpty();
    }

    public Communaute getCommunauteByNom(String nom) {
        stmtGetCommunauteByNom.setParameter("nom", nom);
        List<Communaute> result = stmtGetCommunauteByNom.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public String getNomCommunauteById(int idCommunaute) {
        stmtGetCommunauteNomById.setParameter("idCommunaute", idCommunaute);
        List<String> result = stmtGetCommunauteNomById.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }


    public void ajouterCommunaute(Communaute communaute) {
        cx.getConnection().persist(communaute);
    }


    public void supprimerCommunaute(String nom) {
        Communaute c = getCommunauteByNom(nom);
        if (c != null) {
            cx.getConnection().remove(c);
        }
    }

}





