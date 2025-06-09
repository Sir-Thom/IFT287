package SeauS.collections;

import SeauS.SeauSException;
import SeauS.bdd.Connexion;
import SeauS.objets.Compagnie;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import com.mongodb.client.model.Updates;


import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

import java.util.ArrayList;
import java.util.List;

public class Parents extends GestionTables {
    private final MongoCollection<Document> collectionCompagnies;
    private final Connexion cx;


    public Parents(Connexion cx) {
        super(cx);
        this.cx = cx;
        this.collectionCompagnies = cx.getDatabase().getCollection("Compagnies");
    }


    public boolean existeRelation(int idParent, int idEnfant) {
            Document child = collectionCompagnies.find(
                    eq("idCompagnie", idEnfant)
            ).projection(
                    new Document("parents", 1)
            ).first();

            if (child != null && child.get("parents") instanceof List) {
                List<Integer> parentIds = (List<Integer>) child.get("parents");
                return parentIds.contains(idParent);
            }
            return false;
    }



    public void ajouterRelation(int idParent, int idEnfant) throws SeauSException {
        if (idParent == idEnfant) {
            throw new SeauSException("Une compagnie ne peut pas être son propre parent.");
        }
        // Use $addToSet to add the parent ID to the child's 'parents' array, preventing duplicates.
        collectionCompagnies.updateOne(
                eq("idCompagnie", idEnfant),
                Updates.addToSet("parents", idParent)
        );
    }

    /**
     * Removes a parent-child relationship.
     * @param idParent The ID of the parent company.
     * @param idEnfant The ID of the child company.
     */
    public void supprimerRelation(int idParent, int idEnfant) {
        // Use $pull to remove the parent ID from the child's 'parents' array.
        collectionCompagnies.updateOne(
                eq("idCompagnie", idEnfant),
                Updates.pull("parents", idParent)
        );
    }

    /**
     * Lists all parent companies for a given child.
     * @param idEnfant The ID of the child company.
     * @return A list of parent Compagnie objects.
     */
    public List<Compagnie> listerParentsEffectifs(int idEnfant) {
        List<Compagnie> parents = new ArrayList<>();

        // First, find the child document to get the list of parent IDs.
        Document enfant = collectionCompagnies.find(eq("idCompagnie", idEnfant)).first();

        if (enfant != null && enfant.get("parents") != null) {
            List<Integer> parentIds = (List<Integer>) enfant.get("parents");
            if (!parentIds.isEmpty()) {
                // Then, find all companies whose ID is in the parentIds list.
                try (MongoCursor<Document> cursor = collectionCompagnies.find(in("idCompagnie", parentIds)).iterator()) {
                    while (cursor.hasNext()) {
                        parents.add(new Compagnie(cursor.next()));
                    }
                }
            }
        }
        return parents;
    }
}