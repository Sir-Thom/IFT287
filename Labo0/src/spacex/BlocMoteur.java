package spacex;

public class BlocMoteur {
    public static final double MASSE_PAR_MOTEUR = 470;
    public static final double POUSSEE_PAR_MOTEUR = 180.1;
    public static final double DIAMETRE_MOTEUR = 1.013;

    private int nombreMoteurs;
    private double diametreFusee;

    public BlocMoteur(double masseFusee) {
        this.nombreMoteurs = calculerNombreMoteurs(masseFusee);
        this.diametreFusee = calculerDiametreFusee();
    }

    public int getNombreMoteurs() {
        return nombreMoteurs;
    }

    public double calculerDiametreFusee() {
        return Math.sqrt((Math.pow(DIAMETRE_MOTEUR / 2, 2) * nombreMoteurs / 0.8) * 2);
    }

    private int calculerNombreMoteurs(double masseFusee) {
        return (int) (masseFusee / (MASSE_PAR_MOTEUR * POUSSEE_PAR_MOTEUR)) + 1;
    }

    public int calculerProfil() {
        return (int) (diametreFusee / DIAMETRE_MOTEUR);
    }

    public String dessinerBlocMoteur() {
        StringBuilder dessin = new StringBuilder();
        int largeurProfil = Math.max(1, calculerProfil());

        // Top line (joint)
        dessin.append("/");
        dessin.append(repeatChar('_', largeurProfil * 4 +2));
        dessin.append("\\\n");

        // Engines
        for (int i = 0; i < largeurProfil; i++) {
            dessin.append("/WW");
            if (i < largeurProfil - 1) dessin.append("\\");
        }
        dessin.append("\n");

        for (int i = 0; i < largeurProfil; i++) {
            for (int j = 0; j < largeurProfil; j++) { // Repeat for each engine
                dessin.append("\\/WW");
            }
            dessin.append("\n");
            break; // Only one row of engines is needed
        }

        return dessin.toString();
    }

    private String repeatChar(char c, int count) {
        if (count <= 0) return "";
        return new String(new char[count]).replace('\0', c);
    }
}