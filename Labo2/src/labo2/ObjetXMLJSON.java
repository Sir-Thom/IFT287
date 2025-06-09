package labo2;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.json.JsonObject;
import javax.json.stream.JsonGenerator;

public abstract class ObjetXMLJSON {
    public ObjetXMLJSON(JsonObject obj) throws IFT287Exception {
        throw new IFT287Exception("Ce constructeur doit être implémenté par la classe héritant de ObjetXMLJSON.");
    }

    public ObjetXMLJSON() {

    }

    public abstract JsonGenerator toJson(JsonGenerator g);

    public abstract Element toXml(Document d);
}
