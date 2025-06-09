package SeauS.objets;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Compagnie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int idCompagnie;
    public String nomCompagnie;
    public String adresse;
    @ManyToMany
    @JoinTable(
            name = "PARENT_ENFANT",
            joinColumns = @JoinColumn(name = "ID_ENFANT"),
            inverseJoinColumns = @JoinColumn(name = "ID_PARENT")
    )
    private Set<Compagnie> parents = new HashSet<>();




    public int getIdCompagnie() {
        return idCompagnie;
    }

    // getter for enfants
    public Set<Compagnie> getEnfants() {
        return parents;
    }



    public void setIdCompagnie(int idCompagnie) {
        this.idCompagnie = idCompagnie;
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

    @Override
    public String toString() {
        return "Compagnie{" +
                "idCompagnie=" + idCompagnie +
                ", nomCompagnie='" + nomCompagnie + '\'' +
                ", adresse='" + adresse + '\'' +
                '}';
    }

    public Set<Compagnie> getParents() {
        return parents;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compagnie compagnie = (Compagnie) o;

        if (idCompagnie == 0) return false;
        return idCompagnie == compagnie.idCompagnie;
    }

    @Override
    public int hashCode() {
        return idCompagnie != 0 ? Objects.hash(idCompagnie) : super.hashCode();
    }
}
