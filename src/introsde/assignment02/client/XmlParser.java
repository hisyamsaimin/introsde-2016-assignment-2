package introsde.assignment02.client;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;


public class XmlParser {  
  Document doc;
  XPath xpath;
  String xmlString;

  public XmlParser(String xmlString) throws ParserConfigurationException, SAXException, IOException {
    this.xmlString = xmlString;
    loadXML();
  }
  
  public void loadXML() throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    domFactory.setNamespaceAware(true);
    DocumentBuilder builder = domFactory.newDocumentBuilder();
    InputSource xmlStringAsSource = new InputSource(new StringReader(xmlString));
    doc = builder.parse(xmlStringAsSource);

    //creating xpath object
    getXPathObj();
  }
  
  public void getXPathObj() {
    XPathFactory factory = XPathFactory.newInstance();
    xpath = factory.newXPath();
  }

  /**
  * Return the number of nodes with the given node name
  * @param nodeName     The name of the nodes to be counted in the loaded xml.
  * @return             The number of nodes found
  */
  public int countNodes(String nodeName) throws XPathExpressionException {
    XPathExpression expr = xpath.compile("//" + nodeName);
    NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

    return (int) nodes.getLength();
  }

  /**
  * Return the id of a person on the people list xml given it's position on the list
  * NOTE: The people list xml string should have been loaded before performing this method
  * @param position   The position of the person we want the id from
  * @return           The id of the person
  */
  public int getPersonId(int position) throws XPathExpressionException {
    XPathExpression expr = xpath.compile("//person[" + position + "]");
    Node person = (Node) expr.evaluate(doc, XPathConstants.NODE);

    Element personElement = (Element) person;

    return Integer.parseInt( personElement.getElementsByTagName("personId").item(0).getTextContent() );
  }

  /**
  * Return the firstname of a person
  * NOTE: The single person xml string should have been loaded before performing this method
  * @return     The firstname of a person
  */
  public String getPersonFirstname() throws XPathExpressionException {
    XPathExpression expr = xpath.compile("//firstname");
    Node firstname = (Node) expr.evaluate(doc, XPathConstants.NODE);

    return firstname.getTextContent();
  }

  /**
  * Return a String array with the names of the available Measure Types
  * NOTE: The measure types xml string should have been loaded before performing this method
  * @return     The measure types in a String array
  */
  public String[] getMeasureTypes() throws XPathExpressionException {
    XPathExpression expr = xpath.compile("//measureType");
    NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
    int measureTypeNumber = nodes.getLength();
    String[] measureTypeNames = new String[ measureTypeNumber ];

    for(int i = 0; i < measureTypeNumber; i++){
      measureTypeNames[i] = nodes.item(i).getTextContent();
    }

    return measureTypeNames;
  }

  /**
  * Return the id of a measure
  * NOTE: The measures list xml string should have been loaded before performing this method
  * @return     The measure id of the first measure in the list
  */
  public int getMeasureId() throws XPathExpressionException {
    XPathExpression expr = xpath.compile("//measure[1]");
    Node measureNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

    Element measureElement = (Element) measureNode;

    return Integer.parseInt( measureElement.getElementsByTagName("mid").item(0).getTextContent() );
  }

  /**
  * Return the name of the measure type of a measure
  * NOTE: The measures list xml string should have been loaded before performing this method
  * @return     The name of the measure type of the first measure in the list
  */
  public String getMeasureName() throws XPathExpressionException {
    XPathExpression expr = xpath.compile("//measure[1]");
    Node measureNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

    Element measureElement = (Element) measureNode;

    return measureElement.getElementsByTagName("measureName").item(0).getTextContent();
  }

  /**
  * Return the value of a measure
  * NOTE: The measures list xml string should have been loaded before performing this method
  * @return     The value a measure
  */
  public String getMeasureValue() throws XPathExpressionException {
    XPathExpression expr = xpath.compile("//measure[1]");
    Node measureNode = (Node) expr.evaluate(doc, XPathConstants.NODE);

    Element measureElement = (Element) measureNode;

    return measureElement.getElementsByTagName("value").item(0).getTextContent();
  }
}