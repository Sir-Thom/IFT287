package SeauS.objets;

import SeauS.gestion.EtatProjet;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class Projet {
    @Id
    @GeneratedValue
    private int id;
    private String charge;
    private Enum etatAvancement;
    private Date dateDebut;
    private Date dateFin;
    private Date dateAnnonce;
    private double budgetInit;
    private double budgetFinal;

    @ManyToOne
    private Compagnie compagnie;

    @ManyToOne
    private Communaute communaute;

    public Communaute getCommunaute() {
        return communaute;
    }

    public void setCommunaute(Communaute communaute) {
        this.communaute = communaute;
    }

    public Compagnie getCompagnie() {
        return compagnie;
    }

    public void setCompagnie(Compagnie compagnie) {
        this.compagnie = compagnie;
    }

    public EtatProjet getEtatAvancement() {
        return (EtatProjet) etatAvancement;
    }

    public void setEtatAvancement(EtatProjet etatAvancement) {
        this.etatAvancement = etatAvancement;
    }

    public int getId() {
        return id;
    }


    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public Date getDateAnnonce() {
        return dateAnnonce;
    }

    public void setDateAnnonce(Date dateAnnonce) {
        this.dateAnnonce = dateAnnonce;
    }

    public double getBudgetInit() {
        return budgetInit;
    }

    public void setBudgetInit(double budgetInit) {
        this.budgetInit = budgetInit;
    }

    public double getBudgetFinal() {
        return budgetFinal;
    }

    public void setBudgetFinal(double budgetFinal) {
        this.budgetFinal = budgetFinal;
    }

    @Override
    public String toString() {
        return "Projet{" +
                "id=" + id +
                ", charge='" + charge + '\'' +
                ", etatAvancement=" + etatAvancement +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", dateAnnonce=" + dateAnnonce +
                ", budgetInit=" + budgetInit +
                ", budgetFinal=" + budgetFinal +
                ", compagnie=" + (compagnie != null ? compagnie.getNomCompagnie() : "null") +
                ", communaute=" + (communaute != null ? communaute.getNom() : "null") +
                '}';
    }

}
