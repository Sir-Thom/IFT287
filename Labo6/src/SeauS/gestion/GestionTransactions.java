package SeauS.gestion;

import SeauS.bdd.Connexion;

public abstract class GestionTransactions {
    protected final Connexion cx;

    protected GestionTransactions(Connexion cx) {
        this.cx = cx;
    }
}
