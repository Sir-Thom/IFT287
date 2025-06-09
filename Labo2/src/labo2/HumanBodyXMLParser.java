/** Classe principale pour le parser SAXP **/
package labo2;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class HumanBodyXMLParser extends DefaultHandler
{


    private HumanBody m_body;
    private HumanBodySystem m_currentSystem;
    private Flow m_currentFlow;
    private Connection m_currentConnection;

    public HumanBodyXMLParser()
    {
        super();
    }

    public HumanBody getM_body() {
        return m_body;
    }

    @Override
    public void startDocument()
    {
        System.out.println("Début de la lecture du fichier XML");
    }

    @Override
    public void startElement(String namespace, String lname, String qname, Attributes attr)
    {
        // Le gros du travail est dans cette méthode
        // En fonction de la balise lue, il faut créer les bons objets
        // Il y a deux possibilités ici :
        //   1 - On garde une référence vers le dernier objet créé et on l'utilise directement dans le parser
        //   2 - Chaque objet qui gère des listes permet de récupérer le dernier objet de la liste pour pouvoir le modifier ensuite
        if(qname.equals("MainBody"))
        {
            String bodyName = attr.getValue("bodyName");
            int bodyID = Integer.parseInt(attr.getValue("bodyID"));
            m_body = new HumanBody(bodyName, bodyID);
        }
        else if(qname.equals("Systems"))
        {
            // Nothing to do, just skip
        }
        else if(qname.equals("System"))
        {
            String systemeName = attr.getValue("name");
            int systemeId = Integer.parseInt(attr.getValue("id"));
            int systemeType = Integer.parseInt(attr.getValue("type"));
            this.m_currentSystem = new HumanBodySystem(systemeName, systemeId, systemeType);
            this.m_body.AddSystem(this.m_currentSystem);

            // Votre code ici
            // Utilisez la propriété currentSystem pour ajouter le prochain flow au bon système
        }else if (qname.equals("Flow")) {
            String flowName = attr.getValue("name");
            int flowId = Integer.parseInt(attr.getValue("id"));
            this.m_currentFlow = new Flow(flowName, flowId);
            this.m_currentSystem.AddFlow(this.m_currentFlow);
        } else if (!qname.equals("Organs")) {
            if (qname.equals("Organ")) {
                String organName = attr.getValue("name");
                int organId = Integer.parseInt(attr.getValue("id"));
                int organSysId = Integer.parseInt(attr.getValue("systemID"));
                Organ var8 = new Organ(organName, organId, organSysId);
                this.m_body.AddOrgan(var8);
            } else if (!qname.equals("Connectible") && !qname.equals("Connections")) {
                if (qname.equals("Connection")) {
                    int connId = Integer.parseInt(attr.getValue("id"));
                    this.m_currentConnection = new Connection(connId);
                    this.m_currentFlow.AddConnection(this.m_currentConnection);
                } else if (qname.equals("to")) {
                    int toId = Integer.parseInt(attr.getValue("id"));
                    this.m_currentConnection.AddTo(toId);
                } else {
                    String connectibleName = attr.getValue("name");
                    int connectibleId = Integer.parseInt(attr.getValue("id"));
                    Connectible connectible = new Connectible(qname, connectibleName, connectibleId);
                    String var21 = attr.getValue("startRadius");
                    if (var21 != null) {
                        connectible.setStartRadius(Double.parseDouble(var21));
                    }

                    var21 = attr.getValue("endRadius");
                    if (var21 != null) {
                        connectible.setEndRadius(Double.parseDouble(var21));
                    }

                    var21 = attr.getValue("length");
                    if (var21 != null) {
                        connectible.setLength(Double.parseDouble(var21));
                    }

                    var21 = attr.getValue("volume");
                    if (var21 != null) {
                        connectible.setVolume(Double.parseDouble(var21));
                    }

                    this.m_currentFlow.AddConnectible(connectible);
                }
            }
        }
        else // if(qname.equals("Flow")) et ainsi de suite
        {}
    }

    @Override
    public void endElement(String namespace, String lname, String qname)
    {
        // Il n'y a rien de particulier à faire ici
    }

    @Override
    public void endDocument()
    {
        System.out.println("Fin de la lecture du fichier XML");

    }
}
