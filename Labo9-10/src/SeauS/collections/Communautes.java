package SeauS.collections;

import SeauS.bdd.Connexion;
import SeauS.objets.Communaute;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.result.DeleteResult;

import static com.mongodb.client.model.Filters.eq;
import javax.persistence.TypedQuery;
import java.util.List;

public class Communautes extends GestionTables {

    private final Connexion cx;
    private final MongoCollection<Document> collectionCommunautes;


    public Communautes(Connexion cx) {
        super(cx);
        this.cx = cx;
        this.collectionCommunautes = cx.getDatabase().getCollection("Communautes");

    }


    public boolean existe(String nom) {
        return collectionCommunautes.find(eq("nom", nom)).first() != null;
    }

    public Communaute getCommunauteByNom(String nom) {
        Document d = collectionCommunautes.find(eq("nom", nom)).first();
        return (d != null) ? new Communaute(d) : null;
    }

    public String getNomCommunauteById(int idCommunaute) {
        Document d = collectionCommunautes.find(eq("idCommunaute", idCommunaute)).first();
        return (d != null) ? d.getString("nom") : null;
    }


    public void ajouterCommunaute(Communaute communaute) {

        collectionCommunautes.insertOne(communaute.toDocument());

    }

    public void modifierCommunaute(Communaute communaute) {
        collectionCommunautes.updateOne(eq("nom", communaute.getNom()), communaute.toDocument());
    }


    public Boolean supprimerCommunaute(String nom) {
        DeleteResult result = collectionCommunautes.deleteOne(eq("nom", nom));
        return result.getDeletedCount() > 0;
    }

}





