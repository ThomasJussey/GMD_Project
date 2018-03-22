package core;
import java.io.File;
import java.io.IOException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class DrugBankMatching {

    private String file_name;
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document document;

    public DrugBankMatching(String f) {
        try {
            this.file_name = f;
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
            document = builder.parse(new File(file_name));
        } catch (ParserConfigurationException e){
            System.out.println("Problem with the creation of the DocumentBuilder :\n");
            e.printStackTrace();
        } catch (SAXException e){
            System.out.println("SAX problem with the creation of the Document :\n");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IO problem with the creation of the Document :\n");
            e.printStackTrace();
        }
    }

    public String getFileName(){
        return this.file_name;
    }

    public String getXMLVersion(){
        return document.getXmlVersion();
    }

    public String getEncoding() {
        return document.getXmlEncoding();
    }

    public boolean getStandalone() {
        return document.getXmlStandalone();
    }

    public String getRoot(){
        Element root = document.getDocumentElement();
        return root.getNodeName();
    }

    public String getDisease(String symptom){           // Ceci est dégueulase et mérite d'être revus
        Element root =  document.getDocumentElement();
        Element drug = (Element)root.getElementsByTagName("drug").item(0);
        Element indication = (Element)drug.getElementsByTagName("indication").item(0);
        String description = indication.getTextContent();
        if (description.contains(symptom)){
            return ((Element)drug.getElementsByTagName("name")).getTextContent();
        }
    }


}
