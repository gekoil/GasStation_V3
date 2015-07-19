package com.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("getcar/{carId}")
public class GetCar {
    @PathParam("carId")
    private String carId;

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces(MediaType.TEXT_PLAIN)
    public String getClichedMessage() {
        // Return some cliched textual content
        return "Hello World";
    }

    //public static void main(String[] args) throws IOException, URISyntaxException {
    //    //ResourceConfig rc = new ResourceConfig(HelloWorld.class);
    //    //URI uri = new URI("http://localhost:8090");
    //    HttpServer server = HttpServerFactory.create("http://localhost:8080");
    //    server.start();

    //    System.out.println("Server running");
    //    System.out.println("Visit: http://localhost:8080/rest/getCar");
    //    System.out.println("Hit return to stop...");
    //    System.in.read();
    //    System.out.println("Stopping server");
    //    server.stop(0);
    //    System.out.println("Server stopped");
    //}


}
