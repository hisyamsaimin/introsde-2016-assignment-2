package introsde.assignment02.resources;
import introsde.assignment02.model.MeasureType;

import java.io.IOException;
import java.util.List;
// import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Path("/measureTypes")
public class MeasureTypeCollectionResource {

  // Allows to insert contextual objects into the class,
  // e.g. ServletContext, Request, Response, UriInfo
  @Context
  UriInfo uriInfo;
  @Context
  Request request;

  // Return the list of measureTypes to the user in the browser
  @GET
  @Produces({MediaType.TEXT_XML,  MediaType.APPLICATION_JSON ,  MediaType.APPLICATION_XML })
  public List<MeasureType> getMeasureTypes() {
    List<MeasureType> m_types = MeasureType.getAll();
    return m_types;
  }
}