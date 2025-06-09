package labo2;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

// TODO : lire la classe pour comprendre comment elle fonctionne
public class HumanBody extends ObjetXMLJSON
{
    private String m_bodyName;
    private int m_bodyID;
    private List<HumanBodySystem> m_systems = new ArrayList<HumanBodySystem>();
    private List<Organ> m_organs = new ArrayList<Organ>();
    
    // Constructeur pour la partie 2 du devoir
    public HumanBody(String name, int id)
    {
        m_bodyName = name;
        m_bodyID = id;
    }
    
    // Constructeur pour la partie 3 du devoir
    public HumanBody(JsonObject obj)
    {
        m_bodyName = obj.getString("bodyName");
        m_bodyID = obj.getInt("bodyID");
        
        JsonArray systems = obj.getJsonArray("Systems");
        for(JsonValue v : systems)
        {
            m_systems.add(new HumanBodySystem((JsonObject)v));
        }
        
        JsonArray organs = obj.getJsonArray("Organs");
        for(JsonValue v : organs)
        {
            m_organs.add(new Organ((JsonObject)v));
        }
    }
    
    public void AddOrgan(Organ o)
    {
        m_organs.add(o);
    }
    
    public void AddSystem(HumanBodySystem s)
    {
        m_systems.add(s);
    }
    
    // Fonction qui convertit l'objet en JSON (Partie 2)
    public JsonGenerator toJson(JsonGenerator g)
    {
        g.writeStartObject();
        g.write("bodyName", m_bodyName);
        g.write("bodyID", m_bodyID);
        
        g.writeStartArray("Systems");
        for(HumanBodySystem s : m_systems)
        {
            g = s.toJson(g);
        }
        g.writeEnd();
        
        g.writeStartArray("Organs");
        for(Organ o : m_organs)
        {
            g = o.toJson(g);
        }
        g.writeEnd();
        g.writeEnd();
        return g;
    }
    
    // Fonction qui convertit l'objet en XML (Partie 3)
    public Element toXml(Document d)
    {
        Element e = d.createElement("MainBody");
        e.setAttribute("bodyName", m_bodyName);
        e.setAttribute("bodyID", Integer.toString(m_bodyID));
        
        Element s = d.createElement("Systems");
        for(HumanBodySystem hbs : m_systems)
        {
            s.appendChild(hbs.toXml(d));
        }
        e.appendChild(s);
        
        Element os = d.createElement("Organs");
        for(Organ o : m_organs)
        {
            os.appendChild(o.toXml(d));
        }
        e.appendChild(os);
        return e;
    }


}
