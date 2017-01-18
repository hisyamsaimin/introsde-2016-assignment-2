package introsde.assignment02.client;

import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;

import java.io.*;
import java.lang.Exception;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The Client class is used to evaluate the server implementation of the assignment.
 * 
 *
 *
 */
public class ClientApp {

  private static Client client;
  private static WebTarget service;
  private static String serverUri;
  private static String reqPath;
  private static XmlParser xmlParser;
  private static Response response;
  private static String responseBody;
  private static int responseStatus;
  private static String requestResult;
  private static PrintWriter xmlLogWriter;
  private static PrintWriter jsonLogWriter;

  /**
   * The main method runs every request according to the specifications of the
   * assignment.
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    System.out.println(">>>>> Server URL: https://agile-sierra-35039.herokuapp.com/");
    /*
    **********************************************************************************
    ************************************ CONFIG **************************************
    **********************************************************************************
    */
    serverUri = "https://agile-sierra-35039.herokuapp.com/";
    client = ClientBuilder.newClient(new ClientConfig());
    service = client.target( getBaseURI() );
    xmlLogWriter = new PrintWriter("client-server-xml.log", "UTF-8");
    jsonLogWriter = new PrintWriter("client-server-json.log", "UTF-8");

    /*
    **********************************************************************************
    * STEP 3.1 - Get all Persons and count if there are more than 3 persons in the db
    **********************************************************************************
    */
    reqPath = "/persons";

    // Perform XML Request
    performGetRequest(reqPath, "application/xml");

    // Parse response body - People List
    xmlParser = new XmlParser(responseBody);
    int peopleCount = xmlParser.countNodes("person");

    //Save first and last persons ids
    int firstPersonId = xmlParser.getPersonId(1);
    int lastPersonId = xmlParser.getPersonId(peopleCount);

    if ( peopleCount >= 3 )
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(1, "GET", reqPath, "application/xml", "");

    // Perform JSON Request
    performGetRequest(reqPath, "application/json");
    
    // Print JSON Request
    printRequestDetails(1, "GET", reqPath, "application/json", "");

    /*
    **********************************************************************************
    * STEP 3.2 - Print first person
    **********************************************************************************
    */
    String reqPath = "/persons/" + firstPersonId;

    // Perform XML Request
    performGetRequest(reqPath, "application/xml");

    // Parse response body - Single Person
    xmlParser = new XmlParser(responseBody);
    String oldname = xmlParser.getPersonFirstname();

    if ( responseStatus == 200 )
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(2, "GET", reqPath, "application/xml", "");

    // Perform JSON Request
    performGetRequest(reqPath, "application/json");

    // Print JSON Request
    printRequestDetails(2, "GET", reqPath, "application/json", "");

    /*
    **********************************************************************************
    * STEP 3.3 - Edit first person
    **********************************************************************************
    */
    reqPath = "/persons/" + firstPersonId;
    

    // Perform XML Request
    performPostPutRequest(
                reqPath, 
                "application/xml",
                "PUT", 
                "application/xml", 
                "<person><firstname>Ana XML</firstname></person>"
    );

    // Parse response body - Single Person
    xmlParser = new XmlParser(responseBody);
    String newname = xmlParser.getPersonFirstname();

    if ( oldname != newname )
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(3, "PUT", reqPath, "application/xml", "application/xml");

    // Perform JSON Request
    performPostPutRequest(reqPath, "application/json", "PUT", "application/json", "{\"firstname\":\"Ana JSON\"}");
    
    // Print JSON Request
    printRequestDetails(3, "PUT", reqPath, "application/json", "application/json");

    /*
    **********************************************************************************
    * STEP 3.4 - Create a person XML
    **********************************************************************************
    */
    reqPath = "/persons";

    // Perform XML Request
    performPostPutRequest(
                reqPath, 
                "application/xml",
                "POST", 
                "application/xml", 
                "<person>" +
                  "<firstname>Chuck XML</firstname>" +
                  "<lastname>Norris</lastname>" +
                  "<birthdate>1945-01-01</birthdate>" +
                  "<healthProfile>" +
                    "<measure>" +
                      "<value>190</value>" +
                      "<measureName>height</measureName>" +
                    "</measure>" +
                    "<measure>" +
                      "<value>90</value>" +
                      "<measureName>weight</measureName>" +
                    "</measure>" +
                  "</healthProfile>" +
                "</person>"
    );

    if ( responseStatus == 201 )
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(4, "POST", reqPath, "application/xml", "application/xml");

    /*
    **********************************************************************************
    * STEP 3.5 - Delete a person XML
    **********************************************************************************
    */
    reqPath = response.getLocation().getPath(); // Get the created person path from previous response

    // Request
    performDeleteRequest(reqPath);
    requestResult = "N/A"; // Performing the delete request is not enough to evaluate this request with 'OK' or 'ERROR'

    // Print Request
    printRequestDetails(5, "DELETE", reqPath, "application/xml", "");

    // Perform XML Request
    performGetRequest(reqPath, "application/xml");

    if ( responseStatus == 404 )
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(1, "GET", reqPath, "application/xml", "application/xml");

    /*
    **********************************************************************************
    * STEP 3.4 - Create a person JSON
    **********************************************************************************
    */
    reqPath = "/persons";

    // Perform JSON Request
    performPostPutRequest(
                reqPath, 
                "application/json",
                "POST", 
                "application/json", 
                "{" +
                  "\"firstname\": \"Chuck JSON\"," +
                  "\"lastname\": \"Norris\"," +
                  "\"birthdate\": \"1945-01-01\"" +
                "}"
    );

    if ( responseStatus == 201 )
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print JSON Request
    printRequestDetails(4, "POST", reqPath, "application/json", "application/json");

    /*
    **********************************************************************************
    * STEP 3.5 - Delete a person JSON
    **********************************************************************************
    */
    reqPath = response.getLocation().getPath(); // Get the created person path from previous response

    // Request
    performDeleteRequest(reqPath);
    requestResult = "N/A"; // Performing the delete request is not enough to evaluate this request with 'OK' or 'ERROR'

    // Print Request
    printRequestDetails(5, "DELETE", reqPath, "application/json", "");

    // Perform XML Request
    performGetRequest(reqPath, "application/json");

    if ( responseStatus == 404 )
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(1, "GET", reqPath, "application/json", "application/json");

    /*
    **********************************************************************************
    * STEP 3.6 - Get all Measure Types
    **********************************************************************************
    */
    reqPath = "/measureTypes";

    // Perform XML Request
    performGetRequest(reqPath, "application/xml");

    // Parse response body - Measure Types
    xmlParser = new XmlParser(responseBody);
    int measureTypesCount = xmlParser.countNodes("measureType");
    String[] measureTypes = xmlParser.getMeasureTypes();

    if ( measureTypesCount >= 3 )
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(9, "GET", reqPath, "application/xml", "");

    // Perform JSON Request
    performGetRequest(reqPath, "application/json");

    // Print JSON Request
    printRequestDetails(9, "GET", reqPath, "application/json", "");

    /*
    **********************************************************************************
    * STEP 3.7 - Get the history of each Measure Type for the First and Last Person
    **********************************************************************************
    */
    int personMeasuresCount = 0;
    int measureId = -1;
    String measureType = "";
    requestResult = "N/A";

    for(int i = 0; i < measureTypesCount; i++){
      reqPath = "/persons/" + firstPersonId + "/" + measureTypes[i];

      // Perform XML Request
      performGetRequest(reqPath, "application/xml");

      // Parse response body - First Person measures
      xmlParser = new XmlParser(responseBody);
      personMeasuresCount = personMeasuresCount + xmlParser.countNodes("measure");

      // Save a measure id and a measure type. Only if it hasn't been saved
      if ( xmlParser.countNodes("measure") > 0 && measureId == -1 ){
        measureId = xmlParser.getMeasureId();
        measureType = xmlParser.getMeasureName();
      }

      // Print XML Request
      printRequestDetails(6, "GET", reqPath, "application/xml", "");

      // Perform JSON Request
      performGetRequest(reqPath, "application/json");

      // Print JSON Request
      printRequestDetails(6, "GET", reqPath, "application/json", "");

      // Perform XML Request
      reqPath = "/persons/" + lastPersonId + "/" + measureTypes[i];

      performGetRequest(reqPath, "application/xml");

      // Print XML Request
      printRequestDetails(6, "GET", reqPath, "application/xml", "");

      // Parse response body - Last Person measures
      xmlParser = new XmlParser(responseBody);
      personMeasuresCount = personMeasuresCount + xmlParser.countNodes("measure");

      // Perform JSON Request
      performGetRequest(reqPath, "application/json");

      // Print JSON Request
      printRequestDetails(6, "GET", reqPath, "application/json", "");
    }

    // Check if at least one measure (any type) had been registered for the first or last user
    if(personMeasuresCount > 0)
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request - Result of requesting every measure history for the first and last person
    responseBody = "";
    responseStatus = 0;

    printRequestDetails(6, "", "", "", "");

    /*
    **********************************************************************************
    * STEP 3.8 - Get a measure from the first person
    **********************************************************************************
    */
    reqPath = "/persons/" + firstPersonId + "/" + measureType + "/" + measureId;

    // Perform XML Request
    performGetRequest(reqPath, "application/xml");

    if(responseStatus == 200)
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(7, "GET", reqPath, "application/xml", "");

    // Perform JSON Request
    performGetRequest(reqPath, "application/json");

    // Print JSON Request
    printRequestDetails(7, "GET", reqPath, "application/json", "");

    /*
    **********************************************************************************
    * STEP 3.9 - Create a new measure for a person
    **********************************************************************************
    */
    reqPath = "/persons/" + firstPersonId + "/" + measureType;
    requestResult = "N/A";

    // Perform XML Request
    performGetRequest(reqPath, "application/xml");

    // Parse response body - First Person measures
    xmlParser = new XmlParser(responseBody);
    int firstPersonMeasuresCount = xmlParser.countNodes("measure");

    // Print XML Request
    printRequestDetails(6, "GET", reqPath, "application/xml", "");

    // Perform JSON Request
    performGetRequest(reqPath, "application/json");

    // Print JSON Request
    printRequestDetails(6, "GET", reqPath, "application/json", "");

    // Perform XML Request
    performPostPutRequest(
                reqPath, 
                "application/xml",
                "POST", 
                "application/xml", 
                "<measure><value>123</value></measure>"
    );

    // Parse response body - Created measure
    xmlParser = new XmlParser(responseBody);

    // Save values for STEP 3.10
    int createdMeasureId = xmlParser.getMeasureId();
    String createdMeasureValue = xmlParser.getMeasureValue();

    // Print XML Request
    printRequestDetails(8, "POST", reqPath, "application/xml", "");

    // Perform JSON Request
    performPostPutRequest(
                reqPath, 
                "application/json",
                "POST", 
                "application/json", 
                "{ \"value\" : \"123\" }"
    );

    // Print JSON Request
    printRequestDetails(8, "POST", reqPath, "application/json", "");

    // Perform XML Request
    performGetRequest(reqPath, "application/xml");

    // Parse response body - First Person measures
    xmlParser = new XmlParser(responseBody);

    if( firstPersonMeasuresCount > xmlParser.countNodes("measures"))
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(6, "GET", reqPath, "application/xml", "");

    // Perform JSON Request
    performGetRequest(reqPath, "application/json");

    // Print JSON Request
    printRequestDetails(6, "GET", reqPath, "application/json", "");

    /*
    **********************************************************************************
    * STEP 3.10 - Edit a measure for a person
    **********************************************************************************
    */
    reqPath = "/persons/" + firstPersonId + "/" + measureType + "/" + createdMeasureId;

    // Perform XML Request
    performPostPutRequest(
                reqPath, 
                "application/xml",
                "PUT", 
                "application/xml", 
                "<measure><value>456</value></measure>"
    );

    requestResult = "N/A"; // Just performing the PUT request is not enough to determine the result of this request

    // Print XML Request
    printRequestDetails(10, "PUT", reqPath, "application/xml", "");

    // Perform JSON Request
    performPostPutRequest(
                reqPath, 
                "application/json",
                "PUT", 
                "application/json", 
                "{\"value\" : \"456\" }"
    );

    // Print XML Request
    printRequestDetails(10, "PUT", reqPath, "application/json", "");

    // Perform XML Request
    performGetRequest(reqPath, "application/xml");

    // Parse response body - get measure that was updated
    xmlParser = new XmlParser(responseBody);

    if ( createdMeasureValue != xmlParser.getMeasureValue())
      requestResult = "OK";
    else
      requestResult = "ERROR";

    // Print XML Request
    printRequestDetails(7, "GET", reqPath, "application/xml", "");

    // Perform JSON Request
    performGetRequest(reqPath, "application/json");

    // Print JSON Request
    printRequestDetails(7, "GET", reqPath, "application/json", "");

    xmlLogWriter.close();
    jsonLogWriter.close();
  }

  /**
   * Performs a POST or PUT request depending on the method specified. 
   * Saves the result in the global response variable.
   * 
   * @param path
   * @param accept
   * @param method
   * @param contentType
   * @param requestBody
   */
  private static void performPostPutRequest(String path, String accept, String method, String contentType, String requestBody){
    response = service.path(path).request().accept(accept).build(method, Entity.entity(requestBody, contentType)).invoke();
    loadResponseBodyAndStatus();
  }

  /**
   * Performs a GET request. Saves the result in the global response variable.
   * 
   * @param path
   * @param accept
   */
  private static void performGetRequest(String path, String accept){
    response = service.path(path).request().accept(accept).get();
    loadResponseBodyAndStatus();
  }

  /**
   * Performs a DELETE request. Saves the result in the global response variable.
   * 
   * @param path
   */
  private static void performDeleteRequest(String path){
    response = service.path(path).request().delete();
    loadResponseBodyAndStatus();
  }

  /**
   * It saves the reponse body and the response status in variables.
   * This method is called after every request is made.
   */
  private static void loadResponseBodyAndStatus(){
    responseBody = response.readEntity(String.class);
    responseStatus = response.getStatus();
  }

  /**
   * Prints into a log file the details of the requests made
   * 
   * @param n
   * @param method
   * @param path
   * @param accept
   * @param contentType
   * @throws TransformerException
   */
  private static void printRequestDetails(int n, String method, String path, String accept, String contentType) throws TransformerException {
    if(accept == "application/xml")
      printRequestDetailsXml(n, method, path, accept, contentType);
    else
      printRequestDetailsJson(n, method, path, accept, contentType);
  }

  /**
   * Prints the logs for the XML Requests
   * 
   * @param n
   * @param method
   * @param path
   * @param accept
   * @param contentType
   * @throws TransformerException
   */
  private static void printRequestDetailsXml(int n, String method, String path, String accept, String contentType) throws TransformerException {
    xmlLogWriter.println("Request #" + n + ": " + method + " " + path + " Accept:" + accept + " Content-type: " + contentType ); 
    xmlLogWriter.println("=> Result: " + requestResult);
    xmlLogWriter.println("=> HTTP Status: " + responseStatus);

    if( responseBody != null && !responseBody.isEmpty() )
      prettyPrintXml(responseBody);

    xmlLogWriter.println("");
  }

  /**
   * Method used to print XML responses with the correct identation
   * 
   * @param input					The xml in string
   * @throws TransformerException
   */
  private static void prettyPrintXml(String input) throws TransformerException {
    Source inputXml = new StreamSource(new StringReader(input));
    StreamResult outputXml = new StreamResult(new StringWriter());

    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    transformer.transform(inputXml, outputXml);

    xmlLogWriter.println(outputXml.getWriter().toString());
  }

  /**
   * Prints the logs for the JSON Requests
   * 
   * @param n
   * @param method
   * @param path
   * @param accept
   * @param contentType
   * @throws TransformerException
   */
  private static void printRequestDetailsJson(int n, String method, String path, String accept, String contentType) throws TransformerException {
    jsonLogWriter.println("Request #" + n + ": " + method + " " + path + " Accept:" + accept + " Content-type: " + contentType ); 
    jsonLogWriter.println("=> Result: " + requestResult);
    jsonLogWriter.println("=> HTTP Status: " + responseStatus);

    if( responseBody != null && !responseBody.isEmpty() )
      jsonLogWriter.println(responseBody);

    jsonLogWriter.println("");
  }
  
  /**
   * @return 	THe URI object of the server
   */
  private static URI getBaseURI() {
    return UriBuilder.fromUri(serverUri).build();
  }
}
