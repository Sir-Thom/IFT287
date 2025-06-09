package SeauS.objets;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.bson.Document;

@Entity
public class Compagnie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int idCompagnie;
    public String nomCompagnie;
    public String adresse;
    @ManyToMany
    @JoinTable(name = "PARENT_ENFANT", joinColumns = @JoinColumn(name = "ID_ENFANT"), inverseJoinColumns = @JoinColumn(name = "ID_PARENT"))
    private final Set<Compagnie> parents = new HashSet<>();

    public Compagnie(Document d) {
        idCompagnie = d.getInteger("id");
        nomCompagnie = d.getString("nomCompagnie");
        adresse = d.getString("adresse");
    }


    public Compagnie() {
    }

    public Compagnie(int idCompagnie, String nomCompagnie, String adresse) {
        this.idCompagnie = idCompagnie;
        this.nomCompagnie = nomCompagnie;
        this.adresse = adresse;
    }


    public Compagnie(String nomCompagnie, String adresse) {
        this.nomCompagnie = nomCompagnie;
        this.adresse = adresse;
    }

    public int getIdCompagnie() {
        return idCompagnie;
    }

    public void setIdCompagnie(int idCompagnie) {
        this.idCompagnie = idCompagnie;
    }

    // getter for enfants
    public Set<Compagnie> getEnfants() {
        return parents;
    }

    public String getNomCompagnie() {
        return nomCompagnie;
    }

    public void setNomCompagnie(String nomCompagnie) {
        this.nomCompagnie = nomCompagnie;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "Compagnie{" + "idCompagnie=" + idCompagnie + ", nomCompagnie='" + nomCompagnie + '\'' + ", adresse='" + adresse + '\'' + '}';
    }

    public Set<Compagnie> getParents() {
        return parents;
    }

    public Document toDocument() {
        return new Document("id", idCompagnie).append("nomCompagnie", nomCompagnie).append("adresse", adresse);
    }
}
