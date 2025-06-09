package SeauS.gestion;

import SeauS.SeauSException;
import SeauS.bdd.Connexion;
import SeauS.collections.Projets;
import SeauS.objets.Communaute;
import SeauS.objets.Compagnie;
import SeauS.objets.Projet;

import javax.persistence.EntityManager;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GestionProjet extends GestionTransactions {
    private final Projets projets;
    private final Connexion cx;
    private final EntityManager em;

    public GestionProjet(Projets projets) {
        super(projets.getConnexion());

        this.cx = projets.getConnexion();
        this.em = this.cx.getConnection();
        this.projets = projets;
    }


    public void ajouterProjet(int idCommunaute, int idCompagnie, double budgetInitial,
                              double budgetFinal, String charge, String dateAnnonceStr,
                              String dateDebutStr, String dateFinStr, String etatAvancementStr)
            throws SeauSException {
        try {
            cx.demarreTransaction();
            Communaute communaute = em.find(Communaute.class, idCommunaute);
            if (communaute == null) {
                throw new SeauSException("Communauté avec ID " + idCommunaute + " non trouvée.");
            }
            Compagnie compagnie = em.find(Compagnie.class, idCompagnie);
            if (compagnie == null) {
                throw new SeauSException("Compagnie avec ID " + idCompagnie + " non trouvée.");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            Date utilDateAnnonce = dateAnnonceStr != null && !dateAnnonceStr.isEmpty()
                    ? new Date(sdf.parse(dateAnnonceStr).getTime()) : null;

            Date utilDateDebut = dateDebutStr != null && !dateDebutStr.isEmpty()
                    ? new Date(sdf.parse(dateDebutStr).getTime()) : null;

            Date utilDateFin = dateFinStr != null && !dateFinStr.isEmpty()
                    ? new Date(sdf.parse(dateFinStr).getTime()) : null;
            EtatProjet etatAvancement = null;
            if (etatAvancementStr != null && !etatAvancementStr.trim().isEmpty()) {
                try {
                    etatAvancement = EtatProjet.fromString(etatAvancementStr);
                } catch (IllegalArgumentException e) {
                    throw new SeauSException(e.getMessage());
                }
            }

            Projet nouveauProjet = new Projet();

            nouveauProjet.setCharge(charge);
            nouveauProjet.setBudgetInit(budgetInitial);
            nouveauProjet.setBudgetFinal(budgetFinal);
            nouveauProjet.setDateAnnonce(utilDateAnnonce);
            nouveauProjet.setDateDebut(utilDateDebut);
            nouveauProjet.setDateFin(utilDateFin);
            nouveauProjet.setEtatAvancement(etatAvancement);
            nouveauProjet.setCommunaute(communaute);
            nouveauProjet.setCompagnie(compagnie);

            projets.ajouterProjet(nouveauProjet);
            cx.commit();
        } catch (ParseException e) {
            if (cx.getConnection().getTransaction().isActive()) {
                cx.rollback();
            }
            throw new SeauSException("Format de date invalide. Utilisez yyyy-MM-dd.");
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) {
                cx.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) {
                cx.rollback();
            }
            throw new SeauSException("Erreur lors de l'ajout du projet: " + e.getMessage());
        }
    }

    public void editerProjet(int idProjet, int idCommunaute, int idCompagnie, double budgetInitial,
                             double budgetFinal, String charge, String dateAnnonceStr,
                             String dateDebutStr, String dateFinStr, String etatAvancementStr)
            throws SeauSException {
        try {
            cx.demarreTransaction();
            Projet projetExistant = projets.getProjet(idProjet);
            if (projetExistant == null) {
                throw new SeauSException("Projet avec ID '" + idProjet + "' non trouvé pour édition.");
            }


            Communaute communaute = em.find(Communaute.class, idCommunaute);
            if (communaute == null) {
                throw new SeauSException("Communauté avec ID " + idCommunaute + " non trouvée.");
            }
            Compagnie compagnie = em.find(Compagnie.class, idCompagnie);
            if (compagnie == null) {
                throw new SeauSException("Compagnie avec ID " + idCompagnie + " non trouvée.");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date utilDateAnnonce = dateAnnonceStr != null && !dateAnnonceStr.isEmpty() ? (Date) sdf.parse(dateAnnonceStr) : null;
            Date utilDateDebut = dateDebutStr != null && !dateDebutStr.isEmpty() ? (Date) sdf.parse(dateDebutStr) : null;
            Date utilDateFin = dateFinStr != null && !dateFinStr.isEmpty() ? (Date) sdf.parse(dateFinStr) : null;


            EtatProjet etatAvancement = null;
            if (etatAvancementStr != null && !etatAvancementStr.trim().isEmpty()) {
                try {
                    etatAvancement = EtatProjet.fromString(etatAvancementStr);
                } catch (IllegalArgumentException e) {
                    throw new SeauSException(e.getMessage());
                }
            }


            projetExistant.setCharge(charge);

            projetExistant.setBudgetInit(budgetInitial);
            projetExistant.setBudgetFinal(budgetFinal);
            projetExistant.setDateAnnonce(utilDateAnnonce);
            projetExistant.setDateDebut(utilDateDebut);
            projetExistant.setDateFin(utilDateFin);
            projetExistant.setEtatAvancement(etatAvancement);
            projetExistant.setCommunaute(communaute);
            projetExistant.setCompagnie(compagnie);

            cx.commit();
        } catch (ParseException e) {
            if (cx.getConnection().getTransaction().isActive()) {
                cx.rollback();
            }
            throw new SeauSException("Format de date invalide. Utilisez yyyy-MM-dd.");
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) {
                cx.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) {
                cx.rollback();
            }
            throw new SeauSException("Erreur lors de l'édition du projet: " + e.getMessage());
        }
    }

    public Projet afficherProjet(int idProjet) throws SeauSException {
        boolean txActive = false;
        try {
            cx.demarreTransaction();
            txActive = true;

            Projet projet = projets.getProjet(idProjet);

            if (projet == null) {

                throw new SeauSException("Projet avec ID '" + idProjet + "' non trouvé.");
            }


            cx.commit();
            txActive = false;
            System.out.println(projet);
            return projet;

        } catch (SeauSException e) {
            if (txActive) {
                try {
                    cx.rollback();
                } catch (Exception rbEx) {
                    System.err.println("Erreur lors du rollback après SeauSException: " + rbEx.getMessage());
                }
            }
            throw e;
        } catch (Exception e) {
            if (txActive) {
                try {
                    cx.rollback();
                } catch (Exception rbEx) {
                    System.err.println("Erreur lors du rollback après Exception: " + rbEx.getMessage());
                }
            }
            throw new SeauSException("Erreur système lors de l'affichage du projet avec ID '" + idProjet + "': " + e.getMessage());
        }
    }

    public void supprimerProjet(int idProjet) throws SeauSException {
        try {
            cx.demarreTransaction();

            Projet projetASupprimer = projets.getProjet(idProjet);
            if (projetASupprimer == null) {
                throw new SeauSException("Projet avec ID '" + idProjet + "' non trouvé pour suppression.");
            }

            projets.supprimerProjet(projetASupprimer);
            cx.commit();
        } catch (SeauSException e) {
            if (cx.getConnection().getTransaction().isActive()) {
                cx.rollback();
            }
            throw e;
        } catch (Exception e) {
            if (cx.getConnection().getTransaction().isActive()) {
                cx.rollback();
            }
            throw new SeauSException("Erreur système lors de la suppression: " + e.getMessage());
        }
    }

}
