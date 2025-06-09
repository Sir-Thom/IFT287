package SeauS.gestion;

import SeauS.SeauSException;
import SeauS.collections.Communautes;
import SeauS.collections.Projets;
import SeauS.objets.Communaute;
import SeauS.objets.Projet;

import java.sql.SQLException;
import java.util.List;

public class GestionCommunaute extends GestionTransactions {

    private final Communautes communautes;
    private final Projets projets;

    public GestionCommunaute(Communautes communautes, Projets projets) {
        super(communautes.getConnexion());
        this.communautes = communautes;
        this.projets = projets;
    }


    public void ajouterCommunaute(String nom, String nation, String chef, String coord) throws SeauSException {
        if (communautes.existe(nom)) {
            throw new SeauSException("Une communauté avec le nom '" + nom + "' existe déjà.");
        }
        Communaute nouvelleCommunaute = new Communaute(nom, nation, chef, coord);
        communautes.ajouterCommunaute(nouvelleCommunaute);
    }



    public void editerCommunaute(String nomActuel, String nouveauNom, String nation, String chef, String coord) throws SeauSException {
    Communaute communaute = communautes.getCommunauteByNom(nomActuel);
        if (communaute == null) {
        throw new SeauSException("La communauté '" + nomActuel + "' n'existe pas.");
    }

    // Vérifie si le nouveau nom est déjà pris par une autre communauté
        if (!nomActuel.equals(nouveauNom) && communautes.existe(nouveauNom)) {
        throw new SeauSException("Une autre communauté avec le nom '" + nouveauNom + "' existe déjà.");
    }

    // Met à jour les propriétés de l'objet
        communaute.setNom(nouveauNom);
        communaute.setNation(nation);
        communaute.setChef(chef);
        communaute.setCoord(coord);

    // Persiste les changements dans la base de données
        communautes.modifierCommunaute(communaute);
    }

    public void supprimerCommunaute(String nom)
            throws SQLException, SeauSException {
        Communaute communaute = communautes.getCommunauteByNom(nom);
        if (communaute == null) {
            throw new SeauSException("La communauté '" + nom + "' n'existe pas.");
        }

        if (!projets.getProjetsPourCommunaute(communaute.getIdCommunaute()).isEmpty()) {
            throw new SeauSException("Impossible de supprimer la communauté '" + nom + "' car des projets y sont associés.");
        }

        if (!communautes.supprimerCommunaute(nom)) {
            throw new SeauSException("La suppression de la communauté '" + nom + "' a échoué.");
        }
    }

    public Communaute afficherCommunaute(String nom) throws SQLException, SeauSException {
        Communaute communaute = communautes.getCommunauteByNom(nom);
        if (communaute == null) {
            throw new SeauSException("La communauté '" + nom + "' n'existe pas.");
        }
        return communaute;
    }


    public List<Projet> afficherProjetsCommunaute(String nomCommunaute) throws SeauSException {
        Communaute communaute = communautes.getCommunauteByNom(nomCommunaute);
        if (communaute == null) {
            throw new SeauSException("La communauté '" + nomCommunaute + "' n'existe pas.");
        }
        return projets.getProjetsPourCommunaute(communaute.getIdCommunaute());
    }
}
