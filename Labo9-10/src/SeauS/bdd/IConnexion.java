package SeauS.bdd;

/**
 * Interface pour les gestionnaire d'une connexion.<br><br>
 *
 * <pre>
 * Pré-condition
 *   Le driver approprié à l'implémentation choisie doit être accessible.
 *
 * Post-condition
 *   La connexion est ouverte en mode autocommit false et sérialisable,
 *   (s'il est supporté par le serveur).
 * </pre>
 * <br>
 * IFT287 - Exploitation de BD relationnelles et OO
 *
 * @author Frédéric Bergeron - Université de Sherbrooke
 * @version Version 1.0 - 08 avril 2022
 */

public interface IConnexion {

    /**
     * Ferme une connexion
     */
    void fermer() throws Exception;

    /**
     * Lance une transaction avec une base de données
     */
    void demarreTransaction() throws Exception;

    /**
     * Exécute la transaction courante
     */
    void commit() throws Exception;

    /**
     * Annule la transaction courante
     */
    void rollback() throws Exception;

}