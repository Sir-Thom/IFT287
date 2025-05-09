

public class Fusee {
    public static final double ACCELERATION = 9.8;
    public static final double MASSEMOTEUR = 470;
    public static final double VITESSE = 9700;
    public static final double IMPULSION = 282;
    BlocMoteur blocMoteur;
    Citerne citerne;
    Cargo cargo;

    public Fusee(int hauteur, double masseCargo) {
        // Initialize Cargo first to prevent NullPointerException
        cargo = new Cargo(masseCargo);

        // Now initialize BlocMoteur and Citerne which depend on cargo
        blocMoteur = new BlocMoteur(calculerMasseSeche(masseCargo));
        citerne = new Citerne(calculerMasseCombustible(), blocMoteur);

        System.out.println("Masse Combustible : " + this.calculerMasseCombustible() + " kg");
    }

    public double calculerMasseSeche() {
        return cargo.getMasse() + MASSEMOTEUR;
    }

    private double calculerMasseSeche(double masseCargo) {
        return masseCargo + BlocMoteur.MASSE_PAR_MOTEUR;
    }

    public double calculerMasseTotal() {
        double masseSeche = calculerMasseSeche();
        return masseSeche * Math.exp(VITESSE / (IMPULSION * ACCELERATION));
    }

    public double calculerMasseCombustible() {
        double masseTotale = calculerMasseTotal();
        double masseSeche = calculerMasseSeche();
        return masseTotale - masseSeche;
    }

    public static void main(String[] args) {
        Fusee fusee = new Fusee(1, 0.0);
        System.out.println(fusee.dessiner());
        System.out.println("Hauteur de la citerne : " + fusee.citerne.getHauteur() + " m");
    }

    public String dessiner() {
        StringBuilder dessin = new StringBuilder();
        dessin.append(cargo.dessinerCargo(blocMoteur.calculerProfil()));
        dessin.append(citerne.dessinerCiterne());
        dessin.append(blocMoteur.dessinerBlocMoteur());
        return dessin.toString();
    }
}