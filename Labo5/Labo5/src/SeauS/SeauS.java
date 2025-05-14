package SeauS;

import java.io.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    private static PreparedStatement stmtExisteCompagnie;
    private static PreparedStatement stmtInsertCompagnie;
    private static PreparedStatement stmtUpdateCompagnie;
    private static PreparedStatement stmtDeleteCompagnie;
    private static PreparedStatement stmtSelectProjetsCompagnie;
    
    private static PreparedStatement stmtExisteProjet;
    private static PreparedStatement stmtInsertProjet;
    private static PreparedStatement stmtGetCommunauteNom;
    private static PreparedStatement stmtUpdateProjet;
    private static PreparedStatement stmtDeleteProjet;
    private static PreparedStatement stmtGetCompagnieNom;
    private static PreparedStatement stmtExisteCommunaute;
    private static PreparedStatement stmtInsertCommunaute;
    private static PreparedStatement stmtUpdateCommunaute;
    private static PreparedStatement stmtDeleteCommunaute;
    private static PreparedStatement stmtSelectProjetsCommunaute;
    
    private static PreparedStatement stmtExisteParent;
    private static PreparedStatement stmtAddParent;
    private static PreparedStatement stmtRemoveParent;
    private static PreparedStatement stmtSelectParents;
    
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
            cx = new Connexion(args[0], args[1], args[2], args[3]);
            initialiseStatements();
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
    
    private static void initialiseStatements()
            throws SQLException
    {
        // Initialisation des Statement
        // Ici, on veut que les Statement de type "Existe" soit des Select qui retourne toutes les colonnes d'une donnée,
        // ceux de type Insert vont une insertion dans la base de données (valeurs pour toutes les colonnes sauf l'id),
        // ceux de type Update vont faire une mise à jour d'une donnée (valeurs pour toutes les colonnes sauf l'id)
        // et ceux de type Delete vont faire une supression d'une donnée. Sauf pour le projet, on utilise le nom pour
        // trouver la donnée plutôt que l'id. Voici en exemple les requêtes pour la compagnie.
        stmtGetCommunauteNom = cx.getConnection().prepareStatement(
                "select nom_communaute from communaute where idcommunaute = ?");
        stmtGetCompagnieNom = cx.getConnection().prepareStatement(
                "select nom_compagnie from compagnie where idcompagnie = ?");
        stmtExisteCompagnie = cx.getConnection().prepareStatement(
                "select idcompagnie, nom_compagnie, adresse from compagnie where nom_compagnie = ?");
        stmtInsertCompagnie = cx.getConnection().prepareStatement(
                "insert into compagnie (nom_compagnie, adresse) values (?,?)");
        stmtUpdateCompagnie = cx.getConnection().prepareStatement(
                "update compagnie set nom_compagnie = ?, adresse = ? where nom_compagnie = ?");
        stmtDeleteCompagnie = cx.getConnection().prepareStatement(
                "delete from compagnie where nom_compagnie = ?");
        // Ce statement permet de lister tous les projets chapauté par une compagnie.
        stmtSelectProjetsCompagnie = cx.getConnection().prepareStatement(
                "select * from projet where idcompagnie = ?");

        // TODO : À compléter!!!
        stmtExisteCommunaute = cx.getConnection().prepareStatement("select idcommunaute, nom_communaute, nation, chef, coordonnees from communaute where nom_communaute = ?");
        stmtInsertCommunaute = cx.getConnection().prepareStatement("insert into communaute (nom_communaute, nation, chef, coordonnees) values (?,?,?,?)");
        stmtUpdateCommunaute = cx.getConnection().prepareStatement("update communaute set nom_communaute = ?, nation = ?, chef = ?, coordonnees = ? where nom_communaute = ?");
        stmtDeleteCommunaute = cx.getConnection().prepareStatement("delete from communaute where nom_communaute = ?");
        stmtSelectProjetsCommunaute = cx.getConnection().prepareStatement("select  * from projet where idcommunaute = ?");

        // Ce statement doit retourner les id, noms et adresses des deux compagnies (parent et enfant) dont on cherche
        // la relation (si elle existe). Il sera donc paramétré par l'id du parent et de celui de l'enfant.
        stmtExisteParent = cx.getConnection().prepareStatement("select p.idcompagnie AS parent_id, p.nom_compagnie AS parent_nom, p.adresse AS parent_adresse,\n" +
                "\n" +
                "e.idcompagnie AS enfant_id, e.nom_compagnie AS enfant_nom, e.adresse AS enfant_adresse\n" +
                "\n" +
                "FROM Parent\n" +
                "\n" +
                "INNER JOIN Compagnie p ON Parent.idparent = p.idcompagnie\n" +
                "\n" +
                "INNER JOIN Compagnie e ON Parent.idenfant = e.idcompagnie\n" +
                "\n" +
                "WHERE Parent.idparent = ? AND Parent.idenfant = ?");

        // Ce statement doit insérer un nouveau couple parent, enfant dans la table Parent
        stmtAddParent = cx.getConnection().prepareStatement("insert into Parent (idparent, idenfant) values (?,?)");

        // Ce statement doit supprimer un couple parent, enfant existant dans la table Parent
        stmtRemoveParent = cx.getConnection().prepareStatement("DELETE FROM Parent WHERE idparent = ? AND idenfant = ?");

        // Ce statement doit retourner id, nom et adresse de tous les parents d'une compagnie.
        stmtSelectParents = cx.getConnection().prepareStatement(
                "SELECT c.idcompagnie, c.nom_compagnie, c.adresse " +
                        "FROM Parent p " +
                        "JOIN Compagnie c ON p.idparent = c.idcompagnie " +
                        "WHERE p.idenfant = ?"
        );

        // Statements pour les projets. On sélectionne un projet par son id (numérique).
        // N'oubliez pas les clés étrangères (idcommunaute et idcompagnie)
        stmtExisteProjet = cx.getConnection().prepareStatement("SELECT * FROM Projet WHERE idprojet = ?");
        // Laissé en exemple pour montrer l'utilisation de to_date de java.sql
        stmtInsertProjet = cx.getConnection().prepareStatement(
                "insert into projet (idcommunaute, idcompagnie, budget_initial, budget_final, charge_projet, date_annonce, date_debut, date_fin, etat_avancement) values (?, ?, ?, ?, ?, to_date(?, 'YYYY-MM-DD'), to_date(?, 'YYYY-MM-DD'), to_date(?, 'YYYY-MM-DD'), ?)");

        stmtUpdateProjet = cx.getConnection().prepareStatement("UPDATE Projet SET idcommunaute = ?, idcompagnie = ?, budget_initial = ?, budget_final = ?, charge_projet = ?, \" +\n" +
                "    \"date_annonce = to_date(?, 'YYYY-MM-DD'), date_debut = to_date(?, 'YYYY-MM-DD'), date_fin = to_date(?, 'YYYY-MM-DD'), \" +\n" +
                "    \"etat_avancement = ? WHERE idprojet = ?");
        stmtDeleteProjet = cx.getConnection().prepareStatement("DELETE FROM Projet WHERE idprojet = ?");
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
                ajouterCompagnie(nom, adresse);
            }
            // *******************
            // SUPPRIMER COMPAGNIE
            // *******************
            else if (command.equals("supprimerCompagnie"))
            {
                String nom = readString(tokenizer);
                supprimerCompagnie(nom);
            }
            // *******************
            // EDITER COMPAGNIE
            // *******************
            else if (command.equals("editerCompagnie"))
            {
                String nomActuel = readString(tokenizer);
                String nouveauNom = readString(tokenizer);
                String adresse = readString(tokenizer);
                editerCompagnie(nomActuel, nouveauNom, adresse);
            }
            // *******************
            // AFFICHER COMPAGNIE
            // *******************
            else if (command.equals("afficherCompagnie"))
            {
                String nom = readString(tokenizer);
                afficherCompagnie(nom);
            }
            // *******************
            // ENLEVER PARENT
            // *******************
            else if (command.equals("enleverParent"))
            {
                String idParent = readString(tokenizer);
                String idEnfant = readString(tokenizer);
                enleverParent(idParent, idEnfant);
            }
            // *******************
            // AJOUTER PARENT
            // *******************
            else if (command.equals("ajouterParent"))
            {
                String idParent = readString(tokenizer);
                String idEnfant = readString(tokenizer);
                ajouterParent(idEnfant, idParent);
            }
            // *******************
            // SUPPRIMER COMMUNAUTE
            // *******************
            else if (command.equals("supprimerCommunaute"))
            {
                String nom = readString(tokenizer);
                supprimerCommunaute(nom);
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
                ajouterCommunaute(nom, nation, chef, coord);
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
                editerCommunaute(nomActuel, nouveauNom, nation, chef, coord);
            }
            // *******************
            // AFFICHER COMMUNAUTE
            // *******************
            else if (command.equals("afficherCommunaute"))
            {
                String nom = readString(tokenizer);
                afficherCommunaute(nom);
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
                ajouterProjet(idCommunaute, idCompagnie, budgetInitial, budgetFinal, charge, dateAnnonce, dateDebut, dateFin, etatAvancement);
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
                editerProjet(idProjet, idCommunaute, idCompagnie, budgetInitial, budgetFinal, charge, dateAnnonce, dateDebut, dateFin, etatAvancement);
            }
            // *******************
            // AFFICHER PROJET
            // *******************
            else if (command.equals("afficherProjet"))
            {
                int idProjet = readInt(tokenizer);
                afficherProjet(idProjet);
            }
            // *******************
            // SUPPRIMER PROJET
            // *******************
            else if (command.equals("supprimerProjet"))
            {
                int idProjet = readInt(tokenizer);
                supprimerProjet(idProjet);
            }
            // *********************
            // AFFICHER LA LISTE DE tous les projets d'une compagnie
            // *********************
            else if (command.equals("listerProjetsCompagnie"))
            {
                String nom = readString(tokenizer);
                afficherProjetsCompagnie(nom);
            }
            // *********************
            // AFFICHER LA LISTE DE tous les projets dans une communaute
            // *********************
            else if (command.equals("listerProjetsCommunaute"))
            {
                String nom = readString(tokenizer);
                afficherProjetsCommunaute(nom);
            }
            // *********************
            // AFFICHER LA LISTE DES COMPAGNIES PARENTES D'UNE COMPAGNIE
            // *********************
            else if (command.equals("afficherParents"))
            {
                String nom = readString(tokenizer);
                afficherParents(nom);
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

    private static void ajouterCommunaute(String nom, String nation, String chef, String coord)
            throws SQLException, SeauSException
    {
        try
        {
            // Vérifier que la communauté n'existe pas déjà
            stmtExisteCommunaute.setString(1, nom);
            ResultSet rsetCommunaute = stmtExisteCommunaute.executeQuery();
            if (rsetCommunaute.next())
            {
                rsetCommunaute.close();
                throw new SeauSException("Communauté existe déjà : " + nom);
            }
            rsetCommunaute.close();

            // Création de la communauté
            stmtInsertCommunaute.setString(1, nom);
            stmtInsertCommunaute.setString(2, nation);
            stmtInsertCommunaute.setString(3, chef);
            stmtInsertCommunaute.setString(4, coord);
            stmtInsertCommunaute.executeUpdate();

            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    private static void afficherCommunaute(String nom)
            throws SQLException, SeauSException
    {
        try
        {
            // Vérifier si la communauté existe
            stmtExisteCommunaute.setString(1, nom);
            ResultSet rset = stmtExisteCommunaute.executeQuery();
            if (!rset.next())
            {
                rset.close();
                throw new SeauSException("Communauté inexistante: " + nom);
            }

            System.out.println("idcommunaute nom_communaute nation chef coordonnees");
            System.out.println(rset.getInt(1) + " " + rset.getString(2) + " " + rset.getString(3)
                    + " " + rset.getString(4) + " " + rset.getString(5));
            rset.close();

            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    private static void editerCommunaute(String nomActuel, String nouveauNom, String nation, String chef, String coord)
            throws SQLException, SeauSException
    {
        try
        {
            // Vérifie s'il existe une communaute
            stmtExisteCommunaute.setString(1, nomActuel);
            ResultSet rset = stmtExisteCommunaute.executeQuery();
            if (!rset.next())
            {
                rset.close();
                throw new SeauSException("Communauté inexistante : " + nomActuel);
            }

            // Enregistrement du prêt.
            stmtUpdateCommunaute.setString(1, nouveauNom);
            stmtUpdateCommunaute.setString(2, nation);
            stmtUpdateCommunaute.setString(3, chef);
            stmtUpdateCommunaute.setString(4, coord);
            // Corrected: Set the 5th parameter on stmtUpdateCommunaute for the WHERE clause
            stmtUpdateCommunaute.setString(5, nomActuel);
            int rowsAffected = stmtUpdateCommunaute.executeUpdate();

            if(rowsAffected == 0) {
                // This case should ideally not be reached if the existence check passed,
                // unless the community was deleted concurrently.
                throw new SeauSException("Communauté supprimée par une autre transaction ou nom actuel incorrect");
            }
            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }


    private static void supprimerCommunaute(String nom)
            throws SQLException, SeauSException
    {
        try
        {
            // Vérifie si le membre existe et son nombre de prêt en cours
            stmtExisteCommunaute.setString(1, nom);
            ResultSet rsetCommunaute = stmtExisteCommunaute.executeQuery();
            if (!rsetCommunaute.next())
            {
                rsetCommunaute.close();
                throw new SeauSException("Communauté inexistante: " + nom);
            }
            int idCommunaute = rsetCommunaute.getInt(1);
            rsetCommunaute.close();

            // Vérifie si la communauté a des projets (on ne supprime pas s'il y a des projets).
            stmtSelectProjetsCommunaute.setInt(1, idCommunaute);
            ResultSet rsetRes = stmtSelectProjetsCommunaute.executeQuery();
            if (rsetRes.next())
            {
                rsetRes.close();
                throw new SeauSException("Communauté " + idCommunaute + " a des projets. Ne peut pas supprimer.");
            }
            rsetRes.close();

            // Suppression du membre
            stmtDeleteCommunaute.setString(1, nom);
            if(stmtDeleteCommunaute.executeUpdate() == 0)
                throw new SeauSException("Communauté " + nom + " inexistante");

            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    private static void afficherProjetsCommunaute(String nom)
            throws SQLException, SeauSException
    {
        try {
            int idComm;

            stmtExisteCommunaute.setString(1, nom);
            try (ResultSet commRs = stmtExisteCommunaute.executeQuery()) {
                if (commRs.next()) {
                    idComm = commRs.getInt("idcommunaute");
                } else {

                    throw new SeauSException("La communauté '" + nom + "' n'existe pas.");
                }
            }

            stmtSelectProjetsCommunaute.setInt(1, idComm);
            try (ResultSet projetsRs = stmtSelectProjetsCommunaute.executeQuery()) {
                System.out.println("Liste des projets pour la communauté '" + nom + "' (ID: " + idComm + "):");
                System.out.println("idProjet idCommunaute idCompagnie budgetInitial budgetFinal charge dateAnnonce dateDebut dateFin etatAvancement");

                boolean foundProjects = false;
                while (projetsRs.next()) {
                    foundProjects = true;
                    System.out.println(
                            projetsRs.getInt("idprojet") + " " +
                                    projetsRs.getInt("idcommunaute") + " " +
                                    projetsRs.getInt("idcompagnie") + " " +
                                    projetsRs.getFloat("budget_initial") + " " +
                                    projetsRs.getFloat("budget_final") + " " +
                                    projetsRs.getString("charge_projet") + " " + // Ensure 'charge_projet' is correct
                                    projetsRs.getDate("date_annonce") + " " +
                                    projetsRs.getDate("date_debut") + " " +
                                    projetsRs.getDate("date_fin") + " " +
                                    projetsRs.getString("etat_avancement")
                    );
                }

                if (!foundProjects) {
                    System.out.println("Aucun projet trouvé pour cette communauté.");
                }
            }

            cx.commit();
        } catch (SQLException sqlEx) {
            cx.rollback();
            throw sqlEx;
        }
    }

    private static void ajouterCompagnie(String nom, String adresse)
            throws SQLException, SeauSException
    {
        try {
            // Vérifier existence
            stmtExisteCompagnie.setString(1, nom);
            try (ResultSet rs = stmtExisteCompagnie.executeQuery()) {
                if (rs.next()) {
                    throw new SeauSException("Compagnie '" + nom + "' existe déjà");
                }
            }

            // Insertion
            stmtInsertCompagnie.setString(1, nom);
            stmtInsertCompagnie.setString(2, adresse);
            stmtInsertCompagnie.executeUpdate();

            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
        // TODO : à compléter! Inspirez vous de ajouterCommunaute
    }

    private static void afficherCompagnie(String nom)
            throws SQLException, SeauSException
    {
        try {
            stmtExisteCompagnie.setString(1, nom);
            try (ResultSet rs = stmtExisteCompagnie.executeQuery()) {
                if (!rs.next()) {
                    throw new SeauSException("Compagnie '" + nom + "' introuvable");
                }
                System.out.println("ID: " + rs.getInt("idcompagnie"));
                System.out.println("Nom: " + rs.getString("nom_compagnie"));
                System.out.println("Adresse: " + rs.getString("adresse"));
            }
            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }

        // TODO : à compléter! Inspirez vous de afficherCommunaute
    }

    private static void editerCompagnie(String nomActuel, String nouveauNom, String adresse)
            throws SQLException, SeauSException
    {
        try {
            // Vérifier existence
            stmtExisteCompagnie.setString(1, nomActuel);
            try (ResultSet rs = stmtExisteCompagnie.executeQuery()) {
                if (!rs.next()) {
                    throw new SeauSException("Compagnie '" + nomActuel + "' introuvable");
                }
            }

            // Mise à jour
            stmtUpdateCompagnie.setString(1, nouveauNom);
            stmtUpdateCompagnie.setString(2, adresse);
            stmtUpdateCompagnie.setString(3, nomActuel);
            stmtUpdateCompagnie.executeUpdate();

            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
        // TODO : à compléter! Inspirez vous de editerCommunaute
    }

    private static void supprimerCompagnie(String nom)
            throws SQLException, SeauSException
    {
        try {
            // Vérifier existence et obtenir l'ID
            int idCompagnie = -1;
            stmtExisteCompagnie.setString(1, nom);
            try (ResultSet rs = stmtExisteCompagnie.executeQuery()) {
                if (!rs.next()) {
                    throw new SeauSException("Compagnie '" + nom + "' introuvable");
                }
                idCompagnie = rs.getInt("idcompagnie");
            }

            // Vérifier les dépendances (projets)
            stmtSelectProjetsCompagnie.setInt(1, idCompagnie);
            try (ResultSet rs = stmtSelectProjetsCompagnie.executeQuery()) {
                if (rs.next()) {
                    throw new SeauSException("Impossible de supprimer: la compagnie a des projets associés");
                }
            }

            stmtDeleteCompagnie.setString(1, nom);
            if (stmtDeleteCompagnie.executeUpdate() == 0) {
                throw new SeauSException("Échec de suppression: compagnie '" + nom + "' introuvable");
            }

            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }



        // TODO : à compléter! Inspirez vous de supprimerCommunaute
    }

    private static void afficherProjetsCompagnie(String nom)
            throws SQLException, SeauSException
    {
        try {
            // Récupérer l'ID de la compagnie
            int idCompagnie = -1;
            stmtExisteCompagnie.setString(1, nom);
            try (ResultSet rs = stmtExisteCompagnie.executeQuery()) {
                if (!rs.next()) {
                    throw new SeauSException("Compagnie '" + nom + "' introuvable");
                }
                idCompagnie = rs.getInt("idcompagnie");
            }

            // Récupérer les projets
            stmtSelectProjetsCompagnie.setInt(1, idCompagnie);
            try (ResultSet rs = stmtSelectProjetsCompagnie.executeQuery()) {
                System.out.println("Projets de la compagnie '" + nom + "':");
                boolean hasProjects = false;
                while (rs.next()) {
                    hasProjects = true;
                    System.out.println("ID: " + rs.getInt("idprojet"));
                    System.out.println("Budget initial: " + rs.getFloat("budget_initial"));
                    System.out.println("Budget final: " + rs.getFloat("budget_final"));
                    System.out.println("Charge: " + rs.getString("charge_projet"));
                    System.out.println("Date annonce: " + rs.getDate("date_annonce"));
                    System.out.println("Date début: " + rs.getDate("date_debut"));
                    System.out.println("Date fin: " + rs.getDate("date_fin"));
                    System.out.println("État: " + rs.getString("etat_avancement"));
                    System.out.println("----------------------");
                }
                if (!hasProjects) {
                    System.out.println("Aucun projet trouvé");
                }
            }

            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
        // TODO : à compléter! Inspirez vous de afficherProjetsCommunaute
    }

    private static void ajouterProjet(int idCommunaute, int idCompagnie, float budgetInitial, float budgetFinal, String charge, String dateAnnonce, String dateDebut, String dateFin, String etatAvancement)
            throws SQLException, SeauSException
    {
        try {
            // Validation des budgets
            if (budgetInitial < 0) {
                throw new SeauSException("Budget initial doit être positif");
            }
            if (budgetFinal > 0 && budgetFinal < budgetInitial) {
                throw new SeauSException("Budget final doit être >= budget initial");
            }
            stmtGetCommunauteNom.setInt(1, idCommunaute);
            try (ResultSet rs = stmtGetCommunauteNom.executeQuery()) {
                if (!rs.next()) {
                    throw new SeauSException("Communauté introuvable");
                }
            }

            stmtGetCompagnieNom.setInt(1, idCompagnie);  // setInt pour ID numérique
            try (ResultSet rs = stmtGetCompagnieNom.executeQuery()) {
                if (!rs.next()) {
                    throw new SeauSException("Compagnie introuvable");
                }
            }


            // Insertion du projet
            stmtInsertProjet.setInt(1, idCommunaute);
            stmtInsertProjet.setInt(2, idCompagnie);
            stmtInsertProjet.setFloat(3, budgetInitial);
            stmtInsertProjet.setFloat(4, budgetFinal);
            stmtInsertProjet.setString(5, charge);
            stmtInsertProjet.setDate(6, Date.valueOf(dateAnnonce));
            stmtInsertProjet.setDate(7, Date.valueOf(dateDebut));
            stmtInsertProjet.setDate(8, Date.valueOf(dateFin));
            stmtInsertProjet.setString(9, etatAvancement);
            stmtInsertProjet.executeUpdate();
            // print sql statement
            System.out.println("SQL Statement: " + stmtInsertProjet.toString());
            cx.commit();

        } catch (SQLException e) {
            cx.rollback();
            throw new SQLException("Erreur SQL lors de l'ajout du projet: " + e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
        } catch (SeauSException e) {
            cx.rollback();
            throw e;
        } catch (Exception e) {

            cx.rollback();
            throw new SeauSException("Une erreur inattendue est survenue lors de l'ajout du projet: " + e.getMessage());
        }
        // TODO : à compléter! Inspirez vous de ajouterCommunaute. Regardez editerProjet pour voir comment gérer les dates
        // Attention : ici, il n'y a pas moyen de vérifier qu'un projet existe déjà. On assume seulement que c'est un
        // nouveau projet (pas de vérification à faire)
    }

    private static void afficherProjet(int idProjet)
            throws SQLException, SeauSException
    {
        try
        {
            stmtExisteProjet.setInt(1, idProjet);
            ResultSet rset = stmtExisteProjet.executeQuery();

            if (rset.next()) // Crucial: Check if a row exists and move cursor
            {
                System.out.println("Détails du Projet ID: " + rset.getInt("idprojet"));
                System.out.println("  ID Communauté: " + rset.getInt("idcommunaute"));
                // To display names, you might need stmtGetCommunauteNom similar to how it's done elsewhere
                // For now, displaying IDs.
                System.out.println("  ID Compagnie: " + rset.getInt("idcompagnie"));
                // To display names, you might need stmtGetCompagnieNom
                System.out.println("  Budget Initial: " + rset.getFloat("budget_initial"));
                System.out.println("  Budget Final: " + rset.getFloat("budget_final"));
                System.out.println("  Chargé de projet: " + rset.getString("charge_projet"));
                System.out.println("  Date Annonce: " + rset.getDate("date_annonce"));
                System.out.println("  Date Début: " + rset.getDate("date_debut"));
                System.out.println("  Date Fin: " + rset.getDate("date_fin"));
                System.out.println("  État Avancement: " + rset.getString("etat_avancement"));
            }
            else
            {
                throw new SeauSException("Projet " + idProjet + " inexistant.");
            }
            rset.close();
            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
        // TODO : à compléter! Inspirez vous de afficherCommunaute. Utilisez la méthode getDate d'un resultSet pour les dates
    }

    // Laissé à titre d'exemple.
    private static void editerProjet(int idProjet, int idCommunaute, int idCompagnie, float budgetInitial, float budgetFinal, String charge, String dateAnnonce, String dateDebut, String dateFin, String etatAvancement)
            throws SQLException, SeauSException
    {
        try
        {
            // Vérifier que le projet n'existe pas déjà
            stmtExisteProjet.setInt(1, idProjet);
            ResultSet rset = stmtExisteProjet.executeQuery();
            if (rset.next())
            {
                rset.close();
                throw new SeauSException("Projet existe déjà : " + idProjet);
            }
            rset.close();

            // Création du projet
            stmtUpdateProjet.setInt(1, idCommunaute);
            stmtUpdateProjet.setInt(2, idCompagnie);
            stmtUpdateProjet.setFloat(3, budgetInitial);
            stmtUpdateProjet.setFloat(4, budgetFinal);
            stmtUpdateProjet.setString(5, charge);
            stmtUpdateProjet.setDate(6, Date.valueOf(dateAnnonce));
            stmtUpdateProjet.setDate(7, Date.valueOf(dateDebut));
            stmtUpdateProjet.setDate(8, Date.valueOf(dateFin));
            stmtUpdateProjet.setString(9, etatAvancement);
            stmtUpdateProjet.setInt(10, idProjet);
            stmtUpdateProjet.executeUpdate();

            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    private static void supprimerProjet(int idProjet)
            throws SQLException, SeauSException
    {
        try {
            // Vérifier existence
            stmtExisteProjet.setInt(1, idProjet);
            try (ResultSet rs = stmtExisteProjet.executeQuery()) {
                if (!rs.next()) {
                    throw new SeauSException("Projet ID " + idProjet + " introuvable");
                }
            }

            // Suppression
            stmtDeleteProjet.setInt(1, idProjet);
            if (stmtDeleteProjet.executeUpdate() == 0) {
                throw new SeauSException("Échec de suppression: projet ID " + idProjet + " introuvable");
            }

            cx.commit();
        } catch (Exception e) {
            cx.rollback();
            throw e;
        }
        // TODO : à compléter! Inspirez vous de supprimerCommunaute
    }

    private static void ajouterParent(String nomParent, String nomEnfant)
            throws SQLException, SeauSException
    {
        // TODO : à compléter! Suivez les étapes en commentaires
        try
        {
            // 1. On Vérifie si la compagnie parente existe :
            stmtExisteCompagnie.setString(1, nomParent);
            ResultSet rsetParent = stmtExisteCompagnie.executeQuery();
            if (!rsetParent.next())
            {
                rsetParent.close();
                throw new SeauSException("Parent n'existe pas: " + nomParent);
            }
            // 2. On obtient l'id du parent avec le resultSet puis on ferme le ResultSet:
            int idParent = rsetParent.getInt(1);
            rsetParent.close();

            // 3. On vérifie si la compagnie enfant existe (à compléter)!!!:
            stmtExisteCompagnie.setString(1, nomEnfant);
            ResultSet rsetEnfant = stmtExisteCompagnie.executeQuery();
            if (!rsetEnfant.next())
            {
                rsetEnfant.close();
                throw new SeauSException("Enfant n'existe pas: " + nomEnfant);
            }

            // 4. On obtient l'id de l'enfant avec le resultSet puis on ferme le ResultSet:
            int idEnfant = rsetEnfant.getInt("idcompagnie");


            // 5. On vérifie si la relation dans la table existe déjà entre les 2. Si oui, il faut lancer une exception.
            stmtExisteParent.setInt(1, idParent);
            stmtExisteParent.setInt(2, idEnfant);
            ResultSet rset = stmtExisteParent.executeQuery();
            if (rset.next())
            {
                rset.close();
                throw new SeauSException("Relation parent-enfant entre " + nomParent + " et " + nomEnfant + " existe déjà.");
            }
            rset.close();

            // 6. On ajoute le membre.
            stmtAddParent.setInt(1, idParent);
            stmtAddParent.setInt(2, idEnfant);
            stmtAddParent.executeUpdate();
            
            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    private static void enleverParent(String nomParent, String nomEnfant)
            throws SQLException, SeauSException
    {
        // TODO : à compléter! Suivez les étapes en commentaires
        try
        {
            // 1. Vérifier si la compagnie parente existe
            stmtExisteCompagnie.setString(1, nomParent);
            ResultSet rsetParent = stmtExisteCompagnie.executeQuery();
            if (!rsetParent.next())
            {
                rsetParent.close();
                throw new SeauSException("Parent n'existe pas: " + nomParent);
            }

            // 2. Obtenir l'id du parent
            int idParent = rsetParent.getInt("idcompagnie");
            rsetParent.close();

            // 3. Vérifier si la compagnie enfant existe
            stmtExisteCompagnie.setString(1, nomEnfant);
            ResultSet rsetEnfant = stmtExisteCompagnie.executeQuery();
            if (!rsetEnfant.next())
            {
                rsetEnfant.close();
                throw new SeauSException("Enfant n'existe pas: " + nomEnfant);
            }

            // 4. Obtenir l'id de l'enfant
            int idEnfant = rsetEnfant.getInt("idcompagnie");
            rsetEnfant.close();

            // 5. Vérifier si la relation existe déjà
            stmtExisteParent.setInt(1, idParent);
            stmtExisteParent.setInt(2, idEnfant);
            ResultSet rset = stmtExisteParent.executeQuery();
            if (rset.next())
            {
                rset.close();
                throw new SeauSException("Relation parent-enfant existe déjà");
            }
            rset.close();

            // 6. Ajouter la relation
            stmtRemoveParent.setInt(1, idParent);
            stmtRemoveParent.setInt(2, idEnfant);
            stmtRemoveParent.executeUpdate();

            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
    }

    private static void afficherParents(String nom)
            throws SQLException, SeauSException
    {
        try
        {
            // Récupérer l'ID de la compagnie enfant
            int idCompagnie = -1;
            stmtExisteCompagnie.setString(1, nom);
            try (ResultSet rs = stmtExisteCompagnie.executeQuery()) {
                if (!rs.next()) {
                    throw new SeauSException("Compagnie '" + nom + "' introuvable");
                }
                idCompagnie = rs.getInt("idcompagnie");
            }

            // Récupérer les parents
            stmtSelectParents.setInt(1, idCompagnie);
            try (ResultSet rs = stmtSelectParents.executeQuery()) {
                System.out.println("Parents de '" + nom + "':");
                boolean hasParents = false;
                while (rs.next()) {
                    hasParents = true;
                    System.out.println("- " + rs.getString("nom_compagnie") +
                            " (ID: " + rs.getInt("idcompagnie") +
                            ", Adresse: " + rs.getString("adresse") + ")");
                }
                if (!hasParents) {
                    System.out.println("Aucun parent trouvé");
                }
            }

            // Commit
            cx.commit();
        }
        catch (Exception e)
        {
            cx.rollback();
            throw e;
        }
        // TODO : à compléter! Inspirez vous de afficherProjetsCommunaute
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
        System.out.println("  ajouterProjet <idProjet> <idCommunaute> <idCompagnie> <budgetInitial> <budgetFinal> <charge> <dateAnnonce> <dateDebut> <dateFin> <etatAvancement>");
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
