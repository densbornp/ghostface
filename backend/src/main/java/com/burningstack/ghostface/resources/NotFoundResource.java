package com.burningstack.ghostface.resources;

import java.io.InputStream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@ApplicationScoped
@Provider
public class NotFoundResource implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException exception) {
        InputStream resource = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("META-INF/resources/index.html");
        return resource == null ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.status(Response.Status.OK).entity(resource).build();
    }
}
