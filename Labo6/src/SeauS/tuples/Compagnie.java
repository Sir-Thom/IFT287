package SeauS.tuples;

public class Compagnie {
    public int idCompagnie;
    public String nomCompagnie;
    public String adresse;

    public Compagnie(int idCompagnie, String nomCompagnie, String adresse) {
        this.idCompagnie = idCompagnie;
        this.nomCompagnie = nomCompagnie;
        this.adresse = adresse;
    }


    public Compagnie(String nomCompagnie, String adresse) {
        this.nomCompagnie = nomCompagnie;
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "Compagnie{" +
                "idCompagnie=" + idCompagnie +
                ", nomCompagnie='" + nomCompagnie + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }
}
