package labo2;

import java.util.ArrayList;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HumanBodySystem extends ObjetXMLJSON
{
    private String name;
    private int id; // Changed to String based on DTD CDATA #REQUIRED
    private int type;
    private List<Flow> flows;

    public HumanBodySystem(String name, int id, int type)
    {
        this.name = name;
        this.id = id;
        this.type = type;
        this.flows = new ArrayList<>();
    }

public HumanBodySystem(JsonObject obj)
{
    this.name = obj.getString("name");
    this.id = obj.getInt("id");
    this.type = obj.getInt("type");

    JsonArray flowsArray = obj.getJsonArray("Flows");
    this.flows = new ArrayList<>();
    for (JsonValue v : flowsArray) {
        if (v instanceof JsonObject) {
            this.flows.add(new Flow((JsonObject)v));
        }
    }
}

    @Override
    public JsonGenerator toJson(JsonGenerator g) {
        g.writeStartObject()
            .write("name", name)
            .write("id", id)
            .write("type", type)
            .writeStartArray("Flows");

        for (Flow flow : flows) {
            flow.toJson(g);
        }

        g.writeEnd() // End of Flows array
         .writeEnd(); // End of object
        return g;
    }
    public void AddFlow(Flow flow) {
        this.flows.add(flow);
    }
    @Override
    public Element toXml(Document d) {
        Element systemElement = d.createElement("System");
        systemElement.setAttribute("name", name);
        systemElement.setAttribute("id", String.valueOf(id));
        systemElement.setAttribute("type", String.valueOf(type));

        for (Flow flow : flows) {
            Element flowElement = flow.toXml(d);
            systemElement.appendChild(flowElement);
        }

        return systemElement;
    }
    // TODO à implémenter
}
