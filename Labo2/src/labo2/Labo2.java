package labo2;

import org.w3c.dom.Document;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Labo2 {

    private static final String CMD_IMPORTER = "importer";
    private static final String CMD_EXPORTER = "exporter";
    private static final String CMD_QUITTER = "quitter";
    private static final String TYPE_XML = "xml";
    private static final String TYPE_JSON = "json";

    private static HumanBody corps;

    public static void main(String[] args) {
        try
        {
            // Il est possible que vous ayez à déplacer la connexion ailleurs.
            // N'hésitez pas à le faire!
            BufferedReader reader = ouvrirFichier(args);
            String transaction = lireTransaction(reader);
            while (!finTransaction(transaction))
            {
                executerTransaction(transaction);
                transaction = lireTransaction(reader);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    private static String getExtensionFichier(String nomFichier) {
        if(nomFichier.lastIndexOf(".") != -1 && nomFichier.lastIndexOf(".") != 0)
            return nomFichier.substring(nomFichier.lastIndexOf(".")+1);
        else return "";
    }

    /**
     * Decodage et traitement d'une transaction
     */
    static void executerTransaction(String transaction) throws Exception, IFT287Exception
    {
        try
        {
            System.out.print(transaction + " ");
            // Decoupage de la transaction en mots
            StringTokenizer tokenizer = new StringTokenizer(transaction, " ");
            if (tokenizer.hasMoreTokens())
            {
                String mode = tokenizer.nextToken();
                String nomFichier = readString(tokenizer);
                String extension = getExtensionFichier(nomFichier);

                if (mode.equals(CMD_IMPORTER)){
                    if(extension.equals(TYPE_XML)){
                        System.out.println("Debut de l'importation du fichier XML " + nomFichier);
                        // Votre code d'importation XML ici avec SAXParser (Partie 2)
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        factory.setValidating(true);
                        SAXParser parser = factory.newSAXParser();
                        HumanBodyXMLParser humanBodyXMLParser = new HumanBodyXMLParser();
                        parser.parse(new File(nomFichier), humanBodyXMLParser);
                        System.out.println("Importation terminee.");
                        corps = humanBodyXMLParser.getM_body();



                    }
                    else if (extension.equals(TYPE_JSON)){
                        System.out.println("Debut de l'importation du fichier JSON " + nomFichier);
                        //Votre code d'importation JSON ici avec JSON Object Model (Partie 3)
                        JsonReader jsonReader = Json.createReader(new FileReader(nomFichier));
                        JsonStructure jsonStructure = jsonReader.read();
                        corps = new HumanBody((JsonObject)jsonStructure);
                        System.out.println("Conversion terminee.");

                    }
                    else {
                        System.out.println("Le système ne supporte actuellement pas l'importation des fichiers au format " + extension);
                    }
                }
                else if (mode.equals(CMD_EXPORTER)){
                    if(extension.equals(TYPE_XML)){
                        System.out.println("Debut de l'exportation vers le fichier XML " + nomFichier);
                        // Votre code d'exportation XML ici avec DOM (Partie 3)
                        FileOutputStream file = new FileOutputStream(nomFichier);
                        PrintStream printStream = new PrintStream(file);
                        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                        document.appendChild(corps.toXml(document));
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        transformer.setOutputProperty("doctype-system", "HumanBody.dtd");
                        transformer.setOutputProperty("indent", "yes");
                        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                        DOMSource domSource = new DOMSource(document);
                        StreamResult streamResult = new StreamResult(printStream);
                        transformer.transform(domSource, streamResult);


                    }
                    else if (extension.equals(TYPE_JSON)){
                        System.out.println("Debut de l'exportation vers le fichier JSON " + nomFichier);
                        //Votre code d'exportation JSON ici avec JSON Generator (Partie 2)
                        HashMap map = new HashMap(1);
                        map.put("javax.json.stream.JsonGenerator.prettyPrinting", true);
                        FileWriter writer = new FileWriter(nomFichier);
                        JsonGeneratorFactory factory = Json.createGeneratorFactory(map);
                        JsonGenerator generator = factory.createGenerator(writer);
                        corps.toJson(generator);
                        generator.close();




                    }
                    else {
                        System.out.println("Le système ne supporte actuellement pas l'exportation vers les fichiers au format " + extension);
                    }
                }
                else{
                    System.out.println("Commande inconnue, choisir entre 'importer' ou 'exporter'");
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(" " + e.toString());
        }
    }


    // ****************************************************************
    // *   Les methodes suivantes n'ont pas besoin d'etre modifiees   *
    // ****************************************************************

    /**
     * Ouvre le fichier de transaction, ou lit à partir de System.in
     */
    public static BufferedReader ouvrirFichier(String[] args) throws FileNotFoundException {
        if (args.length < 1)
            // Lecture au clavier
            return new BufferedReader(new InputStreamReader(System.in));
        else
            // Lecture dans le fichier passe en parametre
            return new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
    }

    /**
     * Lecture d'une transaction
     */
    static String lireTransaction(BufferedReader reader) throws IOException
    {
        return reader.readLine();
    }

    /**
     * Verifie si la fin du traitement des transactions est atteinte.
     */
    static boolean finTransaction(String transaction)
    {
        // fin de fichier atteinte
        return (transaction == null || transaction.equals(CMD_QUITTER));
    }

    /** Lecture d'une chaine de caracteres de la transaction entree a l'ecran */
    static String readString(StringTokenizer tokenizer) throws Exception
    {
        if (tokenizer.hasMoreElements())
            return tokenizer.nextToken();
        else
            throw new Exception("Autre parametre attendu");
    }

}
