package SeauS.collections;

import SeauS.bdd.Connexion;

import javax.persistence.EntityManager;

public abstract class GestionTables {
    protected final Connexion cx;
    protected final EntityManager em;

    public GestionTables(Connexion cx) {
        this.cx = cx;
        this.em = cx.getConnection();
    }

    public Connexion getConnexion() {
        return cx;
    }
}
