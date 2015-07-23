package com.rest;

import DAL.IDAL;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/getcar/{carId}")
public class GetCar {
    @PathParam("carId")
    private String carId;

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media type "text/plain"
    @Produces(MediaType.TEXT_PLAIN)
    public String byId() {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/beansConfig.xml");
        IDAL dal = ((IDAL) context.getBean("iDAL")).getInstance();
        return dal.getCarFee(Integer.parseInt(carId));
    }


}
