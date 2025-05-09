
public class Peinture {
    private String texte;
    private int hauteur;
    private int largeur;

    public Peinture(String texte, int hauteur, int largeur) {
        this.texte = texte.toUpperCase();
        this.hauteur = hauteur;
        this.largeur = Math.max(largeur, 6); // Ensure minimum width
    }

    public String genererLigne(int ligne) {
        // Top and bottom borders
        if (ligne == 0 || ligne == hauteur - 1) {
            return repeatChar('-', largeur);
        }

        // Skip painting if tank is too small
        if (largeur < 6) {
            return repeatChar(' ', largeur);
        }

        // Calculate which character to display
        int textPos = ligne % texte.length();
        char c = texte.charAt(textPos);

        // Simple centered display with at least 1 space on each side
        StringBuilder sb = new StringBuilder();
        sb.append(' ').append(c);

        // Add spaces in the middle if there's room
        if (largeur > 4) {
            int middleSpaces = largeur - 4;
            sb.append(repeatChar(' ', middleSpaces));
            sb.append(c);
        }

        sb.append(' ');

        // Ensure we don't exceed the width
        return sb.toString().substring(0, Math.min(largeur, sb.length()));
    }

    private String repeatChar(char c, int count) {
        if (count <= 0) return "";
        return new String(new char[count]).replace('\0', c);
    }
}