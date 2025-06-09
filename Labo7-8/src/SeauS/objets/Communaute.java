package SeauS.objets;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Communaute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCommunaute;

    private String nom;
    private String nation;
    private String chef;
    private String coord;


    public Communaute() {
    }

    public Communaute(String nom, String nation, String chef, String coord) {
        this.nom = nom;
        this.nation = nation;
        this.chef = chef;
        this.coord = coord;
    }

    public Communaute(int idCommunaute, String nom, String nation, String chef, String coord) {
        this.idCommunaute = idCommunaute;
        this.nom = nom;
        this.nation = nation;
        this.chef = chef;
        this.coord = coord;
    }

    public int getIdCommunaute() {
        return idCommunaute;
    }


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getChef() {
        return chef;
    }

    public void setChef(String chef) {
        this.chef = chef;
    }

    public String getCoord() {
        return coord;
    }

    public void setCoord(String coord) {
        this.coord = coord;
    }


    @Override
    public String toString() {
        return "Communaute{" +
                "idCommunaute=" + idCommunaute +
                ", nom='" + nom + '\'' +
                ", nation='" + nation + '\'' +
                ", chef='" + chef + '\'' +
                ", coordonnees='" + coord + '\'' +
                '}';
    }
}

