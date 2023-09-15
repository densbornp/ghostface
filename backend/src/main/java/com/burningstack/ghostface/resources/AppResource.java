package com.burningstack.ghostface.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/")
public class AppResource {

    @GET
    @Path("/faq")
    public String faq() {
        return "forward:/";
    }

    @GET
    @Path("/policy")
    public String policy() {
        return "forward:/";
    }
}
