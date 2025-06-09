package SeauS.collections;

import SeauS.bdd.Connexion;
import SeauS.objets.Projet;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.ascending;
import java.util.Date;

import com.mongodb.client.MongoCursor;
import org.bson.Document;

import com.mongodb.client.MongoCollection;
import javax.persistence.TypedQuery;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Projets {
    private MongoCollection<Document> collectionProjets;
    private final Connexion cx;
   // private final TypedQuery<Projet> stmtExisteProjet;
    //private final TypedQuery<Projet> stmtSelectProjetsCommunaute;
    //private final TypedQuery<Projet> stmtSelectProjetsCompagnie;
    public Projets(Connexion cx) {
        this.cx = cx;

       /* stmtExisteProjet = cx.getConnection().createQuery("SELECT p FROM Projet p WHERE p.id = :id", Projet.class);
        stmtSelectProjetsCommunaute = cx.getConnection().createQuery("select p from Projet p where p.communaute.idCommunaute = :idCommunaute", Projet.class);
        stmtSelectProjetsCompagnie = cx.getConnection().createQuery(
                "SELECT p FROM Projet p WHERE p.compagnie.idCompagnie = :idCompagnie", Projet.class);*/
        collectionProjets = cx.getDatabase().getCollection("Projets");

    }

    public Connexion getConnexion()
    {
        return cx;
    }

    public boolean existe(int idProjet)
    {
        return collectionProjets.find(eq("id", idProjet)).first() != null;
    }


    public Projet getProjet(int idProjet) {
        Document d = collectionProjets.find(eq("id", idProjet)).first();
        if(d != null)
        {
            return new Projet(d);
        }
        return null;

    }


    public void ajouterProjet(Projet projet){
        collectionProjets.insertOne(projet.toDocument());

    }

    public void modifierProjet(Projet projet){
        collectionProjets.updateOne(eq("id", projet.getId()), projet.toDocument());
    }


    public boolean supprimerProjet(int idProjet) {
        return collectionProjets.deleteOne(eq("id", idProjet)).getDeletedCount() > 0;
    }

    public List<Projet> getProjetsPourCommunaute(int idCommunaute) {
        List<Projet> projets = new ArrayList<>();
        try (MongoCursor<Document> cursor = collectionProjets.find(eq("communaute.id", idCommunaute)).iterator()) {
            while (cursor.hasNext()) {
                projets.add(new Projet(cursor.next()));
            }
        }
        return projets;
    }


    public List<Projet> getProjetsPourCompagnie(int idCompagnie) {
        List<Projet> projets = new ArrayList<>();
        try (MongoCursor<Document> cursor = collectionProjets.find(eq("compagnie.id", idCompagnie)).iterator()) {
            while (cursor.hasNext()) {
                projets.add(new Projet(cursor.next()));
            }
        }
        return projets;
    }
}
