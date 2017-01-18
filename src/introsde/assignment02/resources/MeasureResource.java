package introsde.assignment02.resources;
import introsde.assignment02.model.Measure;

import java.io.IOException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

public class MeasureResource {
  @Context
  UriInfo uriInfo;
  @Context
  Request request;
  int id;

  public MeasureResource(UriInfo uriInfo, Request request, int id) {
    this.uriInfo = uriInfo;
    this.request = request;
    this.id = id;
  }

  @GET
  @Produces({ MediaType.TEXT_XML, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Measure getMeasure() {
    Measure measure = Measure.getMeasureById(this.id);
    return measure;
  }

  @PUT
  @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Response putMeasure(Measure measure) {
    Response res;
    Measure existing_measure = Measure.getMeasureById(this.id);

    if (existing_measure == null) {
      res = Response.status(Status.NOT_FOUND).build();
    } else {
      measure = Measure.updateMeasure(existing_measure, measure);
      res = Response.status(Status.OK).entity(measure).location(uriInfo.getAbsolutePath()).build();
    }
    return res;
  }
}