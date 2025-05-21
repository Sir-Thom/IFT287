package SeauS;

import SeauS.bdd.Connexion;

import java.io.*;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.text.*;

/**
 * Interface du système de gestion SeauS
 *
 * <pre>
 * 
 * Vincent Ducharme
 * Université de Sherbrooke
 * Version 1.0 - 9 juillet 2016
 * IFT287 - Exploitation de BD relationnelles et OO
 *
 *  Mariane Maynard
 *  Université de Sherbrooke
 *  Version 2.0 - 7 mai 2025
 *  IFT287 - Exploitation de BD relationnelles et OO
 * 
 * Ce programme permet d'appeler les transactions de base du système
 * SeauS. Il gere des compagnies, des communautés et des
 * projets. Les données sont conservées dans une base de
 * données relationnelles accedée avec JDBC. Pour une liste des
 * transactions traitées, voir la méthode afficherAide(). L'application
 * n'utilise pas l'architecture 3-tiers.
 *
 * Parametres
 * 0- site du serveur SQL ("local" ou "dinf")
 * 1- nom de la BD
 * 2- user id pour établir une connexion avec le serveur SQL
 * 3- mot de passe pour le user id
 * 4- fichier de transaction [optionnel]
 *           si non specifié, les transactions sont lues au
 *           clavier (System.in)
 *
 * Pré-condition
 *   La base de données de la bibliothèque doit exister
 *
 * Post-condition
 *   Le programme effectue les maj associées à chaque
 *   transaction
 * </pre>
 */
public class SeauS
{
    // Déclaration de la Connexion (unique, doit toujours être la même!) et des Statement utilisés
    private static Connexion cx;
    private static GestionSeauS seaus;
    
    /**
     * Ouverture de la BD, traitement des transactions et fermeture de la BD.
     */
    public static void main(String[] args)
            throws Exception
    {
        // validation du nombre de parametres
        if (args.length < 4)
        {
            System.out.println("Usage: java SeauS <serveur> <bd> <user> <password> [<fichier-transactions>]");
            System.out.println(Connexion.serveursSupportes());
            return;
        }
        
        cx = null;
        BufferedReader reader = null;
        try
        {
            seaus = new GestionSeauS(args[0], args[1], args[2], args[3]);
            cx = seaus.getConnexion();
            String nomFichier = null;
            if(args.length == 5)
                nomFichier = args[4];
            reader = ouvrirFichier(nomFichier);
            traiterTransactions(reader, args.length == 5);
            
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
        }
        finally
        {
            if(reader != null)
                reader.close();
            
            if(cx != null)
                cx.fermer();
        }
    }

    /**
     * Traitement des transactions SeauS
     * 
     * @param reader Buffer à utiliser pour lire les commandes
     * @param echo Indique si le programme doit faire l'echo des commandes lues
     */
    private static void traiterTransactions(BufferedReader reader, boolean echo)
            throws Exception
    {
        afficherAide();
        String transaction = lireTransaction(reader, echo);
        while (!finTransaction(transaction))
        {
            // Découpage de la transaction en mots
            StringTokenizer tokenizer = new StringTokenizer(transaction, " ");
            if (tokenizer.hasMoreTokens())
                executerTransaction(tokenizer);
            transaction = lireTransaction(reader, echo);
        }
    }
    
    /**
     * Crée le BufferedReader associé au fichier d'entrée spécifié. Si aucun fichier n'est spécifié,
     * utilise System.in
     * 
     * @param nomFichier Nom du fichier à ouvrir. Si null, utilise System.in
     */
    private static BufferedReader ouvrirFichier(String nomFichier)
            throws FileNotFoundException
    {
        if (nomFichier == null)
            // Lecture au clavier
            return new BufferedReader(new InputStreamReader(System.in));
        else
            // Lecture dans le fichier passé en paramètre
            return new BufferedReader(new InputStreamReader(new FileInputStream(nomFichier)));
    }

    /**
     * Lecture d'une transaction
     * 
     * @param reader Buffer à utiliser pour lire les commandes
     * @param echo Indique si le programme doit faire l'echo des commandes lues
     */
    private static String lireTransaction(BufferedReader reader, boolean echo)
            throws IOException
    {
        System.out.print("> ");
        String transaction = reader.readLine();
        // Echo si la lecture se fait dans un fichier
        if (echo)
            System.out.println(transaction);
        return transaction;
    }

    /**
     * Decodage et traitement d'une transaction
     * 
     * @param tokenizer La commande et ses paramètres
     */
    private static void executerTransaction(StringTokenizer tokenizer)
            throws Exception
    {
        try
        {
            String command = tokenizer.nextToken();

            // *******************
            // HELP
            // *******************
            if (command.equals("aide"))
            {
                afficherAide();
            }
            // *******************
            // AJOUTER COMPAGNIE
            // *******************
            else if (command.equals("ajouterCompagnie"))
            {
                String nom = readString(tokenizer);
                String adresse = readString(tokenizer);
                seaus.getGestionCompagnie().ajouterCompagnie(nom, adresse);
            }
            // *******************
            // SUPPRIMER COMPAGNIE
            // *******************
            else if (command.equals("supprimerCompagnie"))
            {
                String nom = readString(tokenizer);
                seaus.getGestionCompagnie().supprimerCompagnie(nom);
            }
            // *******************
            // EDITER COMPAGNIE
            // *******************
            else if (command.equals("editerCompagnie"))
            {
                String nomActuel = readString(tokenizer);
                String nouveauNom = readString(tokenizer);
                String adresse = readString(tokenizer);
                seaus.getGestionCompagnie().editerCompagnie(nomActuel, nouveauNom, adresse);
            }
            // *******************
            // AFFICHER COMPAGNIE
            // *******************
            else if (command.equals("afficherCompagnie"))
            {
                String nom = readString(tokenizer);
                seaus.getGestionCompagnie().afficherCompagnie(nom);
            }
            // *******************
            // ENLEVER PARENT
            // *******************
            else if (command.equals("enleverParent"))
            {
                String idParent = readString(tokenizer);
                String idEnfant = readString(tokenizer);
                seaus.getGestionCompagnie().enleverParent(idParent, idEnfant);
            }
            // *******************
            // AJOUTER PARENT
            // *******************
            else if (command.equals("ajouterParent"))
            {
                String idParent = readString(tokenizer);
                String idEnfant = readString(tokenizer);
                seaus.getGestionCompagnie().ajouterParent(idEnfant, idParent);
            }
            // *******************
            // SUPPRIMER COMMUNAUTE
            // *******************
            else if (command.equals("supprimerCommunaute"))
            {
                String nom = readString(tokenizer);
                seaus.getGestionCommunaute().supprimerCommunaute(nom);
            }
            // *******************
            // AJOUTER COMMUNAUTE
            // *******************
            else if (command.equals("ajouterCommunaute"))
            {
                String nom = readString(tokenizer);
                String nation = readString(tokenizer);
                String chef = readString(tokenizer);
                String coord = readString(tokenizer);
                seaus.getGestionCommunaute().ajouterCommunaute(nom, nation, chef, coord);
            }
            // *******************
            // EDITER COMMUNAUTE
            // *******************
            else if (command.equals("editerCommunaute"))
            {
                String nomActuel = readString(tokenizer);
                String nouveauNom = readString(tokenizer);
                String nation = readString(tokenizer);
                String chef = readString(tokenizer);
                String coord = readString(tokenizer);
                seaus.getGestionCommunaute().editerCommunaute(nomActuel, nouveauNom, nation, chef, coord);
            }
            // *******************
            // AFFICHER COMMUNAUTE
            // *******************
            else if (command.equals("afficherCommunaute"))
            {
                String nom = readString(tokenizer);
                seaus.getGestionCommunaute().afficherCommunaute(nom);
            }
            // *******************
            // AJOUTER PROJET
            // *******************
            else if (command.equals("ajouterProjet"))
            {
                int idCommunaute = readInt(tokenizer);
                int idCompagnie = readInt(tokenizer);
                float budgetInitial = readFloat(tokenizer);
                float budgetFinal = readFloat(tokenizer);
                String charge = readString(tokenizer);
                String dateAnnonce = readDate(tokenizer);
                String dateDebut = readDate(tokenizer);
                String dateFin = readDate(tokenizer);
                String etatAvancement = readString(tokenizer);
                seaus.getGestionProjet().ajouterProjet(idCommunaute, idCompagnie, budgetInitial, budgetFinal, charge, dateAnnonce, dateDebut, dateFin, etatAvancement);
            }
            // *******************
            // EDITER PROJET
            // *******************
            else if (command.equals("editerProjet"))
            {
                int idProjet = readInt(tokenizer);
                int idCommunaute = readInt(tokenizer);
                int idCompagnie = readInt(tokenizer);
                float budgetInitial = readFloat(tokenizer);
                float budgetFinal = readFloat(tokenizer);
                String charge = readString(tokenizer);
                String dateAnnonce = readDate(tokenizer);
                String dateDebut = readDate(tokenizer);
                String dateFin = readDate(tokenizer);
                String etatAvancement = readString(tokenizer);
                seaus.getGestionProjet().editerProjet(idProjet, idCommunaute, idCompagnie, budgetInitial, budgetFinal, charge, dateAnnonce, dateDebut, dateFin, etatAvancement);
            }
            // *******************
            // AFFICHER PROJET
            // *******************
            else if (command.equals("afficherProjet"))
            {
                int idProjet = readInt(tokenizer);
                seaus.getGestionProjet().afficherProjet(idProjet);
            }
            // *******************
            // SUPPRIMER PROJET
            // *******************
            else if (command.equals("supprimerProjet"))
            {
                int idProjet = readInt(tokenizer);
                seaus.getGestionProjet().supprimerProjet(idProjet);
            }
            // *********************
            // AFFICHER LA LISTE DE tous les projets d'une compagnie
            // *********************
            else if (command.equals("listerProjetsCompagnie"))
            {
                String nom = readString(tokenizer);
                seaus.getGestionCompagnie().afficherProjetsCompagnie(nom);
            }
            // *********************
            // AFFICHER LA LISTE DE tous les projets dans une communaute
            // *********************
            else if (command.equals("listerProjetsCommunaute"))
            {
                String nom = readString(tokenizer);
                seaus.getGestionCommunaute().afficherProjetsCommunaute(nom);
            }
            // *********************
            // AFFICHER LA LISTE DES COMPAGNIES PARENTES D'UNE COMPAGNIE
            // *********************
            else if (command.equals("afficherParents"))
            {
                String nom = readString(tokenizer);
                seaus.getGestionCompagnie().afficherParents(nom);
            }
            // *********************
            // Commentaire : ligne debutant par --
            // *********************
            else if (command.equals("--"))
            { 
                // Ne rien faire, c'est un commentaire
            }
            // ***********************
            // TRANSACTION INCONNUE
            // ***********************
            else
            {
                System.out.println("  Transactions non reconnue.  Essayer \"aide\"");
            }
        }
        catch (SQLException e)
        {
            System.out.println("** Erreur SQL - Ne devrait arriver que s'il y a une perte de connexion avec la BD.** \n" + e);
        }
        catch (SeauSException e)
        {
            System.out.println("** " + e);
        }
    }

    /** 
     * Affiche le menu des transactions acceptees par le systeme 
     */
    private static void afficherAide()
    {
        System.out.println();
        System.out.println("Chaque transaction comporte un nom et une liste d'arguments");
        System.out.println("separes par des espaces. La liste peut etre vide.");
        System.out.println(" Les dates sont en format yyyy-mm-dd.");
        System.out.println("");
        System.out.println("Les transactions sont:");
        System.out.println("  aide");
        System.out.println("  exit");
        System.out.println("  ajouterProjet <idCommunaute> <idCompagnie> <budgetInitial> <budgetFinal> <charge> <dateAnnonce> <dateDebut> <dateFin> <etatAvancement>");
        System.out.println("  ajouterCommunaute <nom> <nation> <chef> <coordonnees>");
        System.out.println("  ajouterCompagnie <nom> <adresse>");
        System.out.println("  ajouterParent <nomParent> <nomEnfant>");
        System.out.println("  editerProjet <idProjet>");
        System.out.println("  editerCommunaute <nom>");
        System.out.println("  editerCompagnie <nom>");
        System.out.println("  supprimerProjet <idProjet>");
        System.out.println("  supprimerCommunaute <nom>");
        System.out.println("  supprimerCompagnie <nom>");
        System.out.println("  enleverParent <nomParent> <nomEnfant>");
        System.out.println("  afficherProjet <idProjet>");
        System.out.println("  afficherCommunaute <nom>");
        System.out.println("  afficherCompagnie <nom>");
        System.out.println("  afficherParents <nomEnfant>");
        System.out.println("  listerProjetsCompagnie <nom_compagnie>");
        System.out.println("  listerProjetsCommuaute <nom_communaute>");
    }

    /**
     * Vérifie si la fin du traitement des transactions est atteinte.
     * 
     * @param transaction La transaction courante à vérifier
     */
    private static boolean finTransaction(String transaction)
    {
        // Fin de fichier atteinte
        if (transaction == null)
            return true;

        StringTokenizer tokenizer = new StringTokenizer(transaction, " ");

        // Ligne ne contenant que des espaces
        if (!tokenizer.hasMoreTokens())
            return false;

        // Commande "exit"
        String commande = tokenizer.nextToken();
        return commande.equals("exit");
    }

    /**
     *  Lecture d'une chaine de caractères de la transaction entrée a l'écran
     *  
     *  @param tokenizer Liste des paramètres
     */
    private static String readString(StringTokenizer tokenizer)
            throws SeauSException
    {
        if (tokenizer.hasMoreElements())
        {
            return tokenizer.nextToken();
        }
        else
        {
            throw new SeauSException("Autre paramètre attendu");
        }
    }

    /**
     * Lecture d'un int java de la transaction entrée a l'écran
     * 
     * @param tokenizer Liste des paramètres
     */
    private static int readInt(StringTokenizer tokenizer)
            throws SeauSException
    {
        if (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            try
            {
                return Integer.parseInt(token);
            }
            catch (NumberFormatException e)
            {
                throw new SeauSException("Nombre attendu a la place de \"" + token + "\"");
            }
        }
        else
        {
            throw new SeauSException("Autre paramètre attendu");
        }
    }

    /**
     * Lecture d'un float java de la transaction entrée a l'écran
     *
     * @param tokenizer Liste des paramètres
     */
    private static float readFloat(StringTokenizer tokenizer)
            throws SeauSException
    {
        if (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            try
            {
                return Float.parseFloat(token);
            }
            catch (NumberFormatException e)
            {
                throw new SeauSException("Nombre attendu a la place de \"" + token + "\"");
            }
        }
        else
        {
            throw new SeauSException("Autre paramètre attendu");
        }
    }

    /**
     * Lecture d'un long java de la transaction entrée a l'écran
     * 
     * @param tokenizer Liste des paramètres
     */
    private static long readLong(StringTokenizer tokenizer)
            throws SeauSException
    {
        if (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            try
            {
                return Long.valueOf(token).longValue();
            }
            catch (NumberFormatException e)
            {
                throw new SeauSException("Nombre attendu a la place de \"" + token + "\"");
            }
        }
        else
        {
            throw new SeauSException("Autre paramètre attendu");
        }
    }

    /**
     * Lecture d'une date en format YYYY-MM-DD
     * 
     * @param tokenizer Liste des paramètres
     */
    private static String readDate(StringTokenizer tokenizer)
            throws SeauSException
    {
        if (tokenizer.hasMoreElements())
        {
            String token = tokenizer.nextToken();
            try
            {
                SimpleDateFormat formatAMJ = new SimpleDateFormat("yyyy-MM-dd");
                formatAMJ.setLenient(false);
                formatAMJ.parse(token);
                return token;
            }
            catch (ParseException e)
            {
                throw new SeauSException("Date en format YYYY-MM-DD attendue à la place de \"" + token + "\"");
            }
        }
        else
        {
            throw new SeauSException("Autre paramètre attendu");
        }
    }
}// class
