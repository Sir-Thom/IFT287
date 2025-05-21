package SeauS.gestion;

import SeauS.SeauSException;
import SeauS.tables.Compagnies;
import SeauS.tables.Parents;
import SeauS.tables.Projets;
import SeauS.tuples.Compagnie;
import SeauS.tuples.Projet;

import java.sql.SQLException;
import java.util.List;


public class GestionCompagnie extends GestionTransactions {

    private final Compagnies compagnies; // Renommé pour clarté
    private final Projets projets;       // Nouveau champ pour le gestionnaire de Projets
    private final Parents parents;       // Nouveau champ pour le gestionnaire de Parents

    public GestionCompagnie(Compagnies compagnies, Parents parents,Projets projets) {
        super(compagnies.getConnexion()); // La connexion est partagée
        this.compagnies = compagnies;
        this.projets = projets;
        this.parents = parents;
    }


    public void ajouterCompagnie(String nomCompagnie, String adresse)
            throws SQLException, SeauSException {
        try {
            if (compagnies.existe(nomCompagnie)) {
                throw new SeauSException("Une compagnie avec le nom '" + nomCompagnie + "' existe déjà.");
            }
            Compagnie nouvelleCompagnie = new Compagnie(nomCompagnie, adresse);
            compagnies.ajouterCompagnie(nouvelleCompagnie);
            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
    }


    public void editerCompagnie(String nomActuel, String nouveauNomCompagnie, String nouvelleAdresse)
            throws SQLException, SeauSException {
        try {
            Compagnie compagnieActuelle = compagnies.getCompagnieByNom(nomActuel);
            if (compagnieActuelle == null) {
                throw new SeauSException("La compagnie '" + nomActuel + "' n'existe pas.");
            }

            // Si le nom change, vérifier que le nouveau nom n'est pas déjà utilisé par une AUTRE compagnie
            if (!nomActuel.equals(nouveauNomCompagnie) && compagnies.existe(nouveauNomCompagnie)) {
                throw new SeauSException("Une autre compagnie avec le nom '" + nouveauNomCompagnie + "' existe déjà.");
            }

            Compagnie compagnieModifiee = new Compagnie(compagnieActuelle.idCompagnie, nouveauNomCompagnie, nouvelleAdresse);
            compagnies.editerCompagnie(compagnieModifiee, nomActuel);
            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
    }


    public void supprimerCompagnie(String nomCompagnie)
            throws SQLException, SeauSException {
        try {
            if (!compagnies.existe(nomCompagnie)) {
                throw new SeauSException("La compagnie '" + nomCompagnie + "' n'existe pas.");
            }
            // TODO: Ajouter la vérification des dépendances (projets, relations parent/enfant) avant suppression
            // par exemple, si la compagnie est parent, ou a des projets, etc.
            compagnies.supprimerCompagnie(nomCompagnie);
            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
    }


    public Compagnie afficherCompagnie(String nomCompagnie) throws SQLException, SeauSException {
        try {
            Compagnie compagnie = compagnies.getCompagnieByNom(nomCompagnie);
            if (compagnie == null) {
                throw new SeauSException("La compagnie '" + nomCompagnie + "' n'a pas été trouvée.");
            }
            return compagnie;

        } catch (SQLException e) {

            throw e;
        }
    }


    public List<Projet> afficherProjetsCompagnie(String nomCompagnie) throws SQLException, SeauSException {
        try {
            Compagnie compagnie = compagnies.getCompagnieByNom(nomCompagnie);
            if (compagnie == null) {
                throw new SeauSException("La compagnie '" + nomCompagnie + "' n'a pas été trouvée pour lister ses projets.");
            }

            return projets.getProjetsPourCompagnie(compagnie.idCompagnie);
        } catch (SQLException e) {
            throw e;
        }
    }


    public void ajouterRelationParentEnfant(String nomCompagnieParent, String nomCompagnieEnfant)
            throws SQLException, SeauSException {
        try {
            Compagnie parent = compagnies.getCompagnieByNom(nomCompagnieParent);
            Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);

            if (parent == null) {
                throw new SeauSException("Compagnie parent '" + nomCompagnieParent + "' non trouvée.");
            }
            if (enfant == null) {
                throw new SeauSException("Compagnie enfant '" + nomCompagnieEnfant + "' non trouvée.");
            }
            if (parent.idCompagnie == enfant.idCompagnie) {
                throw new SeauSException("Une compagnie ne peut pas être son propre parent.");
            }


             parents.existeRelation(parent.idCompagnie, enfant.idCompagnie);


            parents.ajouterRelation(parent.idCompagnie, enfant.idCompagnie);
            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
    }

    public void supprimerRelationParentEnfant(String nomCompagnieParent, String nomCompagnieEnfant)
            throws SQLException, SeauSException {
        try {
            Compagnie parent = compagnies.getCompagnieByNom(nomCompagnieParent);
            Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);

            if (parent == null) {
                throw new SeauSException("Compagnie parent '" + nomCompagnieParent + "' non trouvée.");
            }
            if (enfant == null) {
                throw new SeauSException("Compagnie enfant '" + nomCompagnieEnfant + "' non trouvée.");
            }

            // Suppose que parentsTable a une méthode supprimerRelation(int idParent, int idEnfant)
            // qui utilise "DELETE FROM Parent WHERE idparent = ? AND idenfant = ?"
            int rowsAffected = parents.supprimerRelation(parent.idCompagnie, enfant.idCompagnie);
            if (rowsAffected == 0) {
                // Optionnel: lever une exception si la relation n'existait pas.
                // throw new SeauSException("La relation parent-enfant spécifiée n'existe pas.");
            }
            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
    }


    public List<Compagnie> listerParentsDeCompagnie(String nomCompagnieEnfant) throws SQLException, SeauSException {
        try {
            Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);
            if (enfant == null) {
                throw new SeauSException("Compagnie enfant '" + nomCompagnieEnfant + "' non trouvée.");
            }

            // Suppose que parentsTable a une méthode listerParentsEffectifs(int idEnfant)
            // qui utilise "SELECT c.idcompagnie, c.nom_compagnie, c.adresse FROM Parent p JOIN Compagnie c ON p.idparent = c.idcompagnie WHERE p.idenfant = ?"
            return parents.listerParentsEffectifs(enfant.idCompagnie);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void ajouterParent(String nomCompagnieEnfant, String nomCompagnieParent) // Note parameter order from your initial snippet
            throws SQLException, SeauSException {
        try {
            // Retrieve actual Compagnie objects to get their IDs
            Compagnie parent = compagnies.getCompagnieByNom(nomCompagnieParent);
            Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);

            if (parent == null) {
                throw new SeauSException("Parent company '" + nomCompagnieParent + "' not found.");
            }
            if (enfant == null) {
                throw new SeauSException("Child company '" + nomCompagnieEnfant + "' not found.");
            }
            if (parent.idCompagnie == enfant.idCompagnie) {
                throw new SeauSException("A company cannot be its own parent.");
            }

            // Use the existeRelation method from Parents table manager
            if (parents.existeRelation(parent.idCompagnie, enfant.idCompagnie)) {
                throw new SeauSException("The parent-child relationship already exists.");
            }

            // Call the table manager's method
            parents.ajouterRelation(parent.idCompagnie, enfant.idCompagnie);
            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
    }


    public void enleverParent(String nomCompagnieParent, String nomCompagnieEnfant) // Note parameter order from your initial snippet
            throws SQLException, SeauSException {
        try {
            // Retrieve actual Compagnie objects to get their IDs
            Compagnie parent = compagnies.getCompagnieByNom(nomCompagnieParent);
            Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);

            if (parent == null) {
                throw new SeauSException("Parent company '" + nomCompagnieParent + "' not found.");
            }
            if (enfant == null) {
                throw new SeauSException("Child company '" + nomCompagnieEnfant + "' not found.");
            }

            // Call the table manager's method
            int rowsAffected = parents.supprimerRelation(parent.idCompagnie, enfant.idCompagnie);
            if (rowsAffected == 0) {
                throw new SeauSException("The specified parent-child relationship does not exist.");
            }
            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
    }

    public List<Compagnie> afficherParents(String nomCompagnieEnfant) throws SQLException, SeauSException {
        // Retrieve the child company to get its ID
        Compagnie enfant = compagnies.getCompagnieByNom(nomCompagnieEnfant);
        if (enfant == null) {
            throw new SeauSException("Child company '" + nomCompagnieEnfant + "' not found.");
        }

        // Call the table manager's method
        return parents.listerParentsEffectifs(enfant.idCompagnie);
    }

}
