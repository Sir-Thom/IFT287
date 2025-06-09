/**
 * Cette classe représente l'ensemble des Connectibles possibles.
 * Elle possède un attribut Type qui permet de retrouver le nom de la balise associé au XML
 * Elle gère aussi l'optionalité des attributs
 * **/

package labo2;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Connectible extends ObjetXMLJSON
{
    private String m_connectibleType;
    private String m_connectibleName;
    private int m_id;
    private double m_startRadius;
    private boolean m_hasStartRadius;
    private double m_endRadius;
    private boolean m_hasEndRadius;
    private double m_volume;
    private boolean m_hasVolume;
    private double m_length;
    private boolean m_hasLength;



    // Constructeur pour la partie A du devoir
    public Connectible(String type, String name, int id)
    {
        super();
        m_connectibleType = type;
        m_connectibleName = name;
        m_id = id;
        
        m_hasStartRadius = false;
        m_hasEndRadius = false;
        this.m_hasLength = false;
        this.m_hasVolume = false;
    }
    
    // Constructeur pour la partie B du devoir
    public Connectible(JsonObject obj)
    {
        m_connectibleType = obj.getString("type");
        m_connectibleName = obj.getString("name");
        m_id = obj.getInt("id");
        JsonNumber n = obj.getJsonNumber("startRadius");
        if(n != null)
        {
            m_startRadius = n.doubleValue();
            m_hasStartRadius = true;
        }
        n = obj.getJsonNumber("endRadius");
        if(n != null)
        {
            m_endRadius = n.doubleValue();
            m_hasEndRadius = true;
        }
        n = obj.getJsonNumber("length");
        if (n != null) {
            this.m_length = n.doubleValue();
            this.m_hasLength = true;
        }

        n = obj.getJsonNumber("volume");
        if (n != null) {
            this.m_volume = n.doubleValue();
            this.m_hasVolume = true;
        }
        // if ...
    }
    
    public void setStartRadius(double v)
    {
        m_startRadius = v;
        m_hasStartRadius = true;
    }
    
    public void setEndRadius(double v)
    {
        m_endRadius = v;
        m_hasEndRadius = true;
        //m_endRadius = ... TODO
    }
    
    public void setLength(double v)
    {
        // TODO
        m_length = v;
        m_hasLength = true;
    }
    
    public void setVolume(double v)
    {
        this.m_volume = v;
        this.m_hasVolume = true;
    }
    // Fonction qui convertit l'objet en JSON (Partie A)
    public JsonGenerator toJson(JsonGenerator g)
    {
        g.writeStartObject();
        g.write("type", m_connectibleType);
        g.write("name", m_connectibleName);
        g.write("id", m_id);
        if(m_hasStartRadius)
            g.write("startRadius", m_startRadius);
        if(m_hasEndRadius) {
            g.write("endRadius", this.m_endRadius);
        }
        if(m_hasLength){
            g.write("length", this.m_length);
        }

        g.writeEnd();
        return g;
    }
    
    // Fonction qui convertit l'objet en XML (Partie B)
    public Element toXml(Document d)
    {
        Element e = d.createElement(m_connectibleType);
        e.setAttribute("name", m_connectibleName);
        e.setAttribute("id", Integer.toString(m_id));
        if(m_hasStartRadius)
            e.setAttribute("startRadius", Double.toString(m_startRadius));
        if(m_hasEndRadius)
            e.setAttribute("endRadius", Double.toString(m_endRadius));
        if(m_hasLength)
            e.setAttribute("length", Double.toString(this.m_length));
        if(m_hasVolume)
            e.setAttribute("volume", Double.toString(this.m_volume));

        return e;
    }
}
