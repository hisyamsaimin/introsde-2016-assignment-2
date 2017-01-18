package introsde.assignment02.resources;
import introsde.assignment02.model.Person;

import java.io.IOException;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;


@Path("/persons")
public class PersonCollectionResource {

  // Allows to insert contextual objects into the class,
  // e.g. ServletContext, Request, Response, UriInfo
  @Context
  UriInfo uriInfo;
  @Context
  Request request;

  // Return the list of people to the user in the browser
  @GET
  @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
  public List<Person> getPersons() {
    List<Person> people = Person.getAll();
    return people;
  }

  @POST
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response createPerson(Person person) throws IOException {
    person.setMeasures(person.getCurrentMeasures());    
    Person createdPerson = Person.createPerson(person);
    URI location = UriBuilder.fromUri(uriInfo.getAbsolutePath())
                             .path(Integer.toString(createdPerson.getPersonId()))
                             .build();
    return Response.status(Status.CREATED).entity(createdPerson).location(location).build();
  }

  // Let the PersonResource class to handle operations on a single Person
  @Path("{personId}")
  public PersonResource getPersonResource(@PathParam("personId") int id) {
    return new PersonResource(uriInfo, request, id);
  }
}