package labo2;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Flow extends ObjetXMLJSON
{
    private String m_name;
    private int m_id;
    
    private List<Connectible> m_connectibles = new ArrayList<Connectible>();
    private List<Connection> m_connections = new ArrayList<Connection>();


    public Flow(String name, int id)
    {
        m_name = name;
        m_id = id;
    }
    public void AddConnectible(Connectible connectible) {

        this.m_connectibles.add(connectible);
    }

    public void AddConnection(Connection connection) {
        this.m_connections.add(connection);
    }

 public Flow(JsonObject obj)
    {
        m_name = obj.getString("name");
        m_id = obj.getInt("id");

        JsonArray connectiblesArray = obj.getJsonArray("Connectibles");
        for (JsonValue v : connectiblesArray) {
            if (v instanceof JsonObject) {
                m_connectibles.add(new Connectible((JsonObject)v));
            }
        }

        JsonArray connectionsArray = obj.getJsonArray("Connections");
        for (JsonValue v : connectionsArray) {
            if (v instanceof JsonObject) {
                m_connections.add(new Connection((JsonObject)v));
            }
        }
    }

    @Override
    public JsonGenerator toJson(JsonGenerator g) {
        g.writeStartObject()
            .write("name", m_name)
            .write("id", m_id)
            .writeStartArray("Connectibles");

        for (Connectible c : m_connectibles) {
            c.toJson(g);
        }

        g.writeEnd() // End of Connectibles array
         .writeStartArray("Connections");

        for (Connection conn : m_connections) {
            conn.toJson(g);
        }

        g.writeEnd() // End of Connections array
         .writeEnd(); // End of object

        return g;
    }

    @Override
    public Element toXml(Document d) {

        Element element = d.createElement("Flow");
        element.setAttribute("name", this.m_name);
        element.setAttribute("id", Integer.toString(this.m_id));
        Element connectibles = d.createElement("Connectible");

        for(Connectible connectible : this.m_connectibles) {
            connectibles.appendChild(connectible.toXml(d));
        }

        element.appendChild(connectibles);
        connectibles = d.createElement("Connections");

        for(Connection connection : this.m_connections) {
            connectibles.appendChild(connection.toXml(d));
        }

        element.appendChild(connectibles);
        return element;
    }
}
