/** Cette classe représente la connection entre deux Connectible dans le fichier XML de base **/
package labo2;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Connection extends ObjetXMLJSON
{
    private int m_id;
    private List<Integer> m_to = new ArrayList<Integer>();
    
    // Constructeur pour la partie 2 du devoir
    public Connection(int id)
    {
        m_id= id;
    }

    public void AddTo(int m_id) {

        this.m_to.add(m_id);
    }
    // Constructeur pour la partie 3 du devoir
    public Connection(JsonObject obj)
    {
        this.m_id = obj.getInt("id");

        for(JsonValue value : obj.getJsonArray("to")) {
            this.m_to.add(((JsonNumber)value).intValue());
        }

    }
    
    // Fonction qui convertit l'objet en JSON (Partie 2)
    public JsonGenerator toJson(JsonGenerator g)
    {
        g.writeStartObject();
        g.write("id", this.m_id);
        g.writeStartArray("to");

        for(Integer var3 : this.m_to) {
            g.write(var3);
        }

        g.writeEnd();
        g.writeEnd();



        return g;
    }
    
    // Fonction qui convertit l'objet en XML (Partie 3)
    public Element toXml(Document d)
    {
        Element element = d.createElement("Connection");
        element.setAttribute("id", Integer.toString(this.m_id));

        for(Integer var4 : this.m_to) {
            Element var5 = d.createElement("to");
            var5.setAttribute("id", Integer.toString(var4));
            element.appendChild(var5);
        }
        return element;

    }
}
