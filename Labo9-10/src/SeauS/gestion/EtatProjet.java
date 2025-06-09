package SeauS.gestion;

public enum EtatProjet {
    PLANIFIE("Planifié"),
    EN_COURS("Encours"),
    TERMINE("Terminé");

    private final String nom;


    private EtatProjet(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return this.nom;
    }
}
