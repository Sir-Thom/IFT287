package spacex;

public class Cargo {
    private double masse;

    public Cargo(double masse) {
        this.masse = masse;
    }

    public double getMasse() {
        return masse;
    }

    public String dessinerCargo(int largeurProfil) {
        StringBuilder dessin = new StringBuilder();
        int width = Math.max(4, largeurProfil * 4); // Ensure minimum width

        // Draw the rocket tip with dynamic width
        dessin.append(repeatChar(' ', width / 2 - 1)).append("  /\\\n");
        dessin.append(repeatChar(' ', width / 2 - 3)).append("  /  \\\n");
        dessin.append(repeatChar(' ', width / 2 - 5)).append(" /    \\\n");
        dessin.append(repeatChar(' ', width / 2 - 7)).append("/      \\\n");

        return dessin.toString();
    }

    private String repeatChar(char c, int count) {
        if (count <= 0) return "";
        return new String(new char[count]).replace('\0', c);
    }
}