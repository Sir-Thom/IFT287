package spacex;

public class Citerne {
    private static final double PROPORTION_OXYGENE = 2.56;
    private static final double DENSITE_KEROSENE = 1.020;
    private static final double DENSITE_OXYGENE = 1.141;
    private static final double CM3_TO_M3 = 1.0E-6;

    private BlocMoteur blocMoteur;
    private double hauteur;

    public Citerne(double masseCombustible, BlocMoteur blocMoteur) {
        this.blocMoteur = blocMoteur;
        this.hauteur = calculerHauteur(masseCombustible);
    }

    public double getHauteur() {
        return hauteur;
    }

    private double calculerHauteur(double masseCombustible) {
        double masseKerosene = masseCombustible / (1 + PROPORTION_OXYGENE);
        double masseOxygene = masseCombustible - masseKerosene;

        double volumeKerosene = masseKerosene * 1000 / DENSITE_KEROSENE;
        double volumeOxygene = masseOxygene * 1000 / DENSITE_OXYGENE;

        double volumeTotal = (volumeKerosene + volumeOxygene) * CM3_TO_M3;

        double rayon = blocMoteur.calculerDiametreFusee() / 2.0;
        return volumeTotal / (Math.PI * rayon * rayon);
    }

    public String dessinerCiterne() {
        StringBuilder dessin = new StringBuilder();
        int hauteurEnMetres = (int) Math.round(hauteur);
        int largeurProfil = blocMoteur.calculerProfil();
        int largeurTotale = largeurProfil * 4; // Total width based on profile

        Peinture peinture = new Peinture("SpaceX Falcon " + blocMoteur.getNombreMoteurs(),
                hauteurEnMetres, largeurTotale); // Use largeurTotale

        for (int i = 0; i < hauteurEnMetres; i++) {
            dessin.append("|");
            dessin.append(peinture.genererLigne(i));
            dessin.append("|\n");
        }

        return dessin.toString();
    }
}