package SeauS.collections;

import SeauS.bdd.Connexion;
import SeauS.objets.Compagnie;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.result.DeleteResult;

import static com.mongodb.client.model.Filters.eq;
import javax.persistence.TypedQuery;
import java.util.List;


public class Compagnies extends GestionTables {
    private final Connexion cx;
    private final MongoCollection<Document> collectionCompagnies;



    public Compagnies(Connexion cx) {
        super(cx);
        this.cx = cx;
        this.collectionCompagnies = cx.getDatabase().getCollection("Compagnies");


    }

    public boolean existe(String nomCompagnie) {
        return collectionCompagnies.find(eq("nomCompagnie", nomCompagnie)).first() != null;

    }

    public Compagnie getCompagnieByNom(String nomCompagnie) {
        Document d = collectionCompagnies.find(eq("nomCompagnie", nomCompagnie)).first();
        return (d != null) ? new Compagnie(d) : null;
    }


    public Compagnie getCompagnieById(int idCompagnie) {
        Document d = collectionCompagnies.find(eq("idCompagnie", idCompagnie)).first();
        return (d != null) ? new Compagnie(d) : null;
    }


    public void ajouterCompagnie(Compagnie compagnie) {
        collectionCompagnies.insertOne(compagnie.toDocument());
    }


    public boolean supprimerCompagnie(String nomCompagnie) {
        DeleteResult result = collectionCompagnies.deleteOne(eq("nomCompagnie", nomCompagnie));
        return result.getDeletedCount() > 0;
    }


}
