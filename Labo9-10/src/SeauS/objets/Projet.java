package SeauS.objets;

import SeauS.gestion.EtatProjet;
import org.bson.Document;

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
    private EtatProjet etatAvancement;
    private Date dateDebut;
    private Date dateFin;
    private Date dateAnnonce;
    private double budgetInit;
    private double budgetFinal;

    @ManyToOne
    private Compagnie compagnie;

    @ManyToOne
    private Communaute communaute;

    public Projet(Document d) {
        id = d.getInteger("id");
        charge = d.getString("charge");
        if (d.getString("etatAvancement") != null) {
            etatAvancement = EtatProjet.valueOf(d.getString("etatAvancement"));
        }
        dateDebut = d.getDate("dateDebut");
        dateFin = d.getDate("dateFin");
        dateAnnonce = d.getDate("dateAnnonce");
        budgetInit = d.getDouble("budgetInit");
        budgetFinal = d.getDouble("budgetFinal");


    }

    public Projet() {

    }



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
        return etatAvancement;
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
        return "Projet{" + "id=" + id + ", charge='" + charge + '\'' + ", etatAvancement=" + etatAvancement + ", dateDebut=" + dateDebut + ", dateFin=" + dateFin + ", dateAnnonce=" + dateAnnonce + ", budgetInit=" + budgetInit + ", budgetFinal=" + budgetFinal + ", compagnie=" + (compagnie != null ? compagnie.getNomCompagnie() : "null") + ", communaute=" + (communaute != null ? communaute.getNom() : "null") + '}';
    }

    public Document toDocument() {
        return new Document("id", id).append("charge", charge).append("etatAvancement", etatAvancement != null ? etatAvancement.name() : null).append("dateDebut", dateDebut).append("dateFin", dateFin).append("dateAnnonce", dateAnnonce).append("budgetInit", budgetInit).append("budgetFinal", budgetFinal).append("compagnie", compagnie != null ? compagnie.toDocument() : null).append("communaute", communaute != null ? communaute.toDocument() : null);
    }

}
