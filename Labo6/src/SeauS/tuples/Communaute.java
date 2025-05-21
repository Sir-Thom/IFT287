package SeauS.tuples;

public class Communaute {
    public int idCommunaute;
    public String nom;
    public String nation;
    public String chef;
    public String coord;

    public Communaute( String nom, String nation, String chef, String coord) {
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

