/** Cette classe représente un Organ dans le fichier XML de base **/
package labo2;

import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Organ extends ObjetXMLJSON
{
    private String name;
    private int id;
    private int systemID;

    // Constructeur pour la partie 2 du devoir
    public Organ(String name, int id, int systemID)
    {
        this.name = name;
        this.id = id;
        this.systemID = systemID;
    }

    public Organ(JsonObject v) {
        this.name = v.getString("name");
        this.id = v.getInt("id");
        this.systemID = v.getInt("systemID");
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getSystemID() {
        return systemID;
    }

    @Override
    public JsonGenerator toJson(JsonGenerator g) {
        g.writeStartObject()
            .write("name", name)
            .write("id", id)
            .write("systemID", systemID)
            .writeEnd();
        return g;
    }

    @Override
    public Element toXml(Document d) {
        Element element = d.createElement("Organ");
        element.setAttribute("name", this.name);
        element.setAttribute("id", Integer.toString(this.id));
        element.setAttribute("systemID", Integer.toString(this.systemID));
        return element;
    }

    // TODO à compléter
}
