package rest;

import javax.ws.rs.*;

@Path("/")
public class CarpoolRest {

    @GET
    public String getCarpoolByDay(@QueryParam("date") String date){
        return "Not implemented";
    }
}
