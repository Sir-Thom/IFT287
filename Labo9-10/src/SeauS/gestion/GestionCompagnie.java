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

    public void ajouterCompagnie(String nomCompagnie, String adresse) throws SeauSException {
        if (compagnies.existe(nomCompagnie)) {
            throw new SeauSException("Une compagnie avec le nom '" + nomCompagnie + "' existe déjà.");
        }
        Compagnie nouvelleCompagnie = new Compagnie(nomCompagnie, adresse);
        compagnies.ajouterCompagnie(nouvelleCompagnie);
    }

    public void editerCompagnie(String nomActuel, String nouveauNomCompagnie, String nouvelleAdresse) throws SeauSException {
        Compagnie existante = compagnies.getCompagnieByNom(nomActuel);
        if (existante == null) {
            throw new SeauSException("La compagnie '" + nomActuel + "' n'existe pas.");
        }
        if (!nomActuel.equals(nouveauNomCompagnie) && compagnies.existe(nouveauNomCompagnie)) {
            throw new SeauSException("Une autre compagnie avec le nom '" + nouveauNomCompagnie + "' existe déjà.");
        }
        existante.setNomCompagnie(nouveauNomCompagnie);
        existante.setAdresse(nouvelleAdresse);
        compagnies.modifierCompagnie(existante);
    }

    public void supprimerCompagnie(String nomCompagnie) throws SeauSException {
        Compagnie c = compagnies.getCompagnieByNom(nomCompagnie);
        if (c == null) {
            throw new SeauSException("La compagnie '" + nomCompagnie + "' n'existe pas.");
        }
        if (!projets.getProjetsPourCompagnie(c.getIdCompagnie()).isEmpty()) {
            throw new SeauSException("Impossible de supprimer la compagnie, des projets y sont associés.");
        }
        compagnies.supprimerCompagnie(nomCompagnie);
    }

    public Compagnie afficherCompagnie(String nomCompagnie) throws SeauSException {
        Compagnie compagnie = compagnies.getCompagnieByNom(nomCompagnie);
        if (compagnie == null) {
            throw new SeauSException("La compagnie '" + nomCompagnie + "' n'a pas été trouvée.");
        }
        return compagnie;
    }

    public List<Projet> afficherProjetsCompagnie(String nomCompagnie) throws SeauSException {
        Compagnie compagnie = compagnies.getCompagnieByNom(nomCompagnie);
        if (compagnie == null) {
            throw new SeauSException("La compagnie '" + nomCompagnie + "' n'a pas été trouvée.");
        }
        return projets.getProjetsPourCompagnie(compagnie.getIdCompagnie());
    }

    public void ajouterParent(String nomCompagnieParent, String nomCompagnieEnfant) throws SeauSException {
        Compagnie parent = compagnies.getCompagnieByNom(nomCompagnieParent);
        Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);

        if (parent == null) throw new SeauSException("Compagnie parent '" + nomCompagnieParent + "' non trouvée.");
        if (enfant == null) throw new SeauSException("Compagnie enfant '" + nomCompagnieEnfant + "' non trouvée.");
        if (parent.getIdCompagnie() == enfant.getIdCompagnie()) throw new SeauSException("Une compagnie ne peut pas être son propre parent.");

        if (parents.existeRelation(parent.getIdCompagnie(), enfant.getIdCompagnie())) {
            throw new SeauSException("La relation parent-enfant existe déjà.");
        }
        parents.ajouterRelation(parent.getIdCompagnie(), enfant.getIdCompagnie());
    }

    public void enleverParent(String nomCompagnieParent, String nomCompagnieEnfant) throws SeauSException {
        Compagnie parent = compagnies.getCompagnieByNom(nomCompagnieParent);
        Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);

        if (parent == null) throw new SeauSException("Compagnie parent '" + nomCompagnieParent + "' non trouvée.");
        if (enfant == null) throw new SeauSException("Compagnie enfant '" + nomCompagnieEnfant + "' non trouvée.");

        if (!parents.existeRelation(parent.getIdCompagnie(), enfant.getIdCompagnie())) {
            throw new SeauSException("La relation parent-enfant n'existe pas.");
        }
        parents.supprimerRelation(parent.getIdCompagnie(), enfant.getIdCompagnie());
    }

    public List<Compagnie> afficherParents(String nomCompagnieEnfant) throws SeauSException {
        Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);
        if (enfant == null) {
            throw new SeauSException("Compagnie enfant '" + nomCompagnieEnfant + "' non trouvée.");
        }
        return parents.listerParentsEffectifs(enfant.getIdCompagnie());
    }


}
