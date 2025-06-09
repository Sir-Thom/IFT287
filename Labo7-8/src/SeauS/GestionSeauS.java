package SeauS;

import SeauS.gestion.GestionCommunaute;
import SeauS.bdd.Connexion;
import SeauS.gestion.GestionCompagnie;
import SeauS.gestion.GestionProjet;
import SeauS.collections.*;

import java.sql.SQLException;

public class GestionSeauS {
    private final Connexion cx;
    private final Compagnies compagnies;
    private final Communautes communautes;
    private final Projets projets;
    private final Parents parents;

    private final GestionCommunaute gestionCommunaute;
    private final GestionCompagnie gestionCompagnie;
    private final GestionProjet gestionProjet;

    public GestionSeauS(String server, String bd, String user, String password) throws SeauSException, SQLException {
        cx = new Connexion(server, bd, user, password);
        communautes =new Communautes(cx);
        compagnies = new Compagnies(cx);
        projets = new Projets(cx);
        parents = new Parents(cx);

        gestionCommunaute = new GestionCommunaute(communautes, projets);
        gestionCompagnie = new GestionCompagnie(compagnies, parents, projets);
        gestionProjet = new GestionProjet(projets);
    }


    public GestionCommunaute getGestionCommunaute() {
        return gestionCommunaute;
    }
    public GestionProjet getGestionProjet() {
        return gestionProjet;
    }
    public GestionCompagnie getGestionCompagnie() {
        return gestionCompagnie;
    }


    public void fermer() throws SQLException
    {
        getConnexion().fermer();
    }
    public Connexion getConnexion()
    {
        return cx;
    }
}
