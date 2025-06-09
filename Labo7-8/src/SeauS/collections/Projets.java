package SeauS.collections;

import SeauS.bdd.Connexion;
import SeauS.objets.Projet;

import javax.persistence.TypedQuery;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Projets {

    private final Connexion cx;
    private final TypedQuery<Projet> stmtExisteProjet;
    private final TypedQuery<Projet> stmtSelectProjetsCommunaute;
    private final TypedQuery<Projet> stmtSelectProjetsCompagnie;
    public Projets(Connexion cx) {
        this.cx = cx;

        stmtExisteProjet = cx.getConnection().createQuery("SELECT p FROM Projet p WHERE p.id = :id", Projet.class);
        stmtSelectProjetsCommunaute = cx.getConnection().createQuery("select p from Projet p where p.communaute.idCommunaute = :idCommunaute", Projet.class);
        stmtSelectProjetsCompagnie = cx.getConnection().createQuery(
                "SELECT p FROM Projet p WHERE p.compagnie.idCompagnie = :idCompagnie", Projet.class);

    }
    public Connexion getConnexion()
    {
        return cx;
    }




    public Projet getProjet(int idProjet) {
        stmtExisteProjet.setParameter("id", idProjet);
        List<Projet> projets = stmtExisteProjet.getResultList();
        if(!projets.isEmpty())
        {
            return projets.get(0);
        }
        else
        {
            return null;
        }
    }
    public Projet getIdProjet(int idProjet) throws SQLException {
        stmtExisteProjet.setParameter("id", idProjet);
        List<Projet> projets = stmtExisteProjet.getResultList();
        if (projets.isEmpty()) {
            throw new SQLException("Project with ID '" + idProjet + "' does not exist.");
        }
        return projets.get(0);
    }

    public void ajouterProjet(Projet projet) {
        cx.getConnection().persist(projet);
    }




    public boolean supprimerProjet(Projet projet) throws SQLException {
            if(projet != null)
            {
                cx.getConnection().remove(projet);
                return true;
            }
            return false;
    }

        public List<Projet> getProjetsPourCommunaute(int idCommunaute) {
            stmtSelectProjetsCommunaute.setParameter("idCommunaute", idCommunaute);
            return stmtSelectProjetsCommunaute.getResultList();
        }


    public List<Projet> getProjetsPourCompagnie(int idCompagnie) {
        stmtSelectProjetsCompagnie.setParameter("idCompagnie", idCompagnie);
        return stmtSelectProjetsCompagnie.getResultList();
    }
}
