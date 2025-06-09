package SeauS.gestion;

public enum EtatProjet {
    PLANIFIE("Planifié"),
    EN_COURS("Encours"),
    TERMINE("Terminé");

    private final String libelle;

    EtatProjet(String libelle) {
        this.libelle = libelle;
    }


    public static EtatProjet fromString(String texte) {
        if (texte != null) {
            String texteNettoye = texte.trim();
            for (EtatProjet etat : EtatProjet.values()) {
                if (etat.libelle.equalsIgnoreCase(texteNettoye)) {
                    return etat;
                }
            }

            try {
                String nomConstante = texteNettoye.toUpperCase().replace(" ", "_");
                return EtatProjet.valueOf(nomConstante);
            } catch (IllegalArgumentException e) {

            }
        }
        throw new IllegalArgumentException("État d'avancement invalide : '" + texte + "'. Valeurs acceptées (ou leurs équivalents) : 'Planifié', 'En cours', 'Terminé'.");
    }

    @Override
    public String toString() {
        return this.libelle;
    }
}
