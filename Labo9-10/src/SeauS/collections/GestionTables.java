package SeauS.collections;

import SeauS.bdd.Connexion;

import javax.persistence.EntityManager;

public abstract class GestionTables {
    protected final Connexion cx;

    public GestionTables(Connexion cx) {
        this.cx = cx;

    }

    public Connexion getConnexion() {
        return cx;
    }
}
