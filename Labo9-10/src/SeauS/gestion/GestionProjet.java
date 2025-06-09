package SeauS.gestion;

import SeauS.SeauSException;
import SeauS.bdd.Connexion;
import SeauS.collections.Communautes;
import SeauS.collections.Compagnies;
import SeauS.collections.Projets;
import SeauS.objets.Communaute;
import SeauS.objets.Compagnie;
import SeauS.objets.Projet;
import com.mongodb.client.MongoCollection;
import javax.persistence.EntityManager;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GestionProjet extends GestionTransactions {
    private final Projets projets;
    private final Communautes communautes;
    private final Compagnies compagnies;
    private final Connexion cx;

    public GestionProjet(Projets projets, Communautes communautes, Compagnies compagnies) {
        super(projets.getConnexion());

        this.cx = projets.getConnexion();
        this.communautes = communautes;
        this.compagnies = compagnies;
        this.projets = projets;
    }


    public void ajouterProjet(int idCommunaute, int idCompagnie, double budgetInitial,
                              double budgetFinal, String charge, String dateAnnonceStr,
                              String dateDebutStr, String dateFinStr, EtatProjet etatAvancement)
            throws SeauSException {

        String nomCommunaute = communautes.getNomCommunauteById(idCommunaute);
        Communaute communaute = communautes.getCommunauteByNom(nomCommunaute);
        if (communaute == null) throw new SeauSException("Communauté avec ID " + idCommunaute + " non trouvée.");

        Compagnie compagnie = compagnies.getCompagnieById(idCompagnie);
        if (compagnie == null) throw new SeauSException("Compagnie avec ID " + idCompagnie + " non trouvée.");

        Projet nouveauProjet = new Projet();
        nouveauProjet.setCharge(charge);
        nouveauProjet.setBudgetInit(budgetInitial);
        nouveauProjet.setBudgetFinal(budgetFinal);
        nouveauProjet.setDateAnnonce(parseDate(dateAnnonceStr));
        nouveauProjet.setDateDebut(parseDate(dateDebutStr));
        nouveauProjet.setDateFin(parseDate(dateFinStr));
        nouveauProjet.setEtatAvancement(etatAvancement);
        nouveauProjet.setCommunaute(communaute);
        nouveauProjet.setCompagnie(compagnie);

        projets.ajouterProjet(nouveauProjet);
    }

    public void editerProjet(int idProjet, int idCommunaute, int idCompagnie, double budgetInitial,
                             double budgetFinal, String charge, String dateAnnonceStr,
                             String dateDebutStr, String dateFinStr, EtatProjet etatAvancement)
            throws SeauSException {

        Projet projetExistant = projets.getProjet(idProjet);
        if (projetExistant == null) throw new SeauSException("Projet avec ID '" + idProjet + "' non trouvé.");

        String nomCommunaute = communautes.getNomCommunauteById(idCommunaute);
        Communaute communaute = communautes.getCommunauteByNom(nomCommunaute);
        if (communaute == null) throw new SeauSException("Communauté avec ID " + idCommunaute + " non trouvée.");

        Compagnie compagnie = compagnies.getCompagnieById(idCompagnie);
        if (compagnie == null) throw new SeauSException("Compagnie avec ID " + idCompagnie + " non trouvée.");

        projetExistant.setCharge(charge);
        projetExistant.setBudgetInit(budgetInitial);
        projetExistant.setBudgetFinal(budgetFinal);
        projetExistant.setDateAnnonce(parseDate(dateAnnonceStr));
        projetExistant.setDateDebut(parseDate(dateDebutStr));
        projetExistant.setDateFin(parseDate(dateFinStr));
        projetExistant.setEtatAvancement(etatAvancement);
        projetExistant.setCommunaute(communaute);
        projetExistant.setCompagnie(compagnie);

        projets.modifierProjet(projetExistant);
    }

    public Projet afficherProjet(int idProjet) throws SeauSException {
        Projet projet = projets.getProjet(idProjet);
        if (projet == null) {
            throw new SeauSException("Projet avec ID '" + idProjet + "' non trouvé.");
        }
        return projet;
    }

    public void supprimerProjet(int idProjet) throws SeauSException {
        if (!projets.existe(idProjet)) {
            throw new SeauSException("Projet avec ID '" + idProjet + "' non trouvé.");
        }
        projets.supprimerProjet(idProjet);
    }

    private Date parseDate(String dateStr) throws SeauSException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return (Date) new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            throw new SeauSException("Format de date invalide pour '" + dateStr + "'. Utilisez yyyy-MM-dd.");
        }
    }

}
