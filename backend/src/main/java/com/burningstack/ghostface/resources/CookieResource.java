package com.burningstack.ghostface.resources;

import com.burningstack.ghostface.storage.StorageHandler;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;

@Path("/cookie")
public class CookieResource {

    @Inject
    private StorageHandler storageHandler;

    @POST
    public Response setCookie(@CookieParam(value = "user_session") Cookie cookie) {
        if (cookie != null && storageHandler.isClientActive(cookie.getValue())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cookie already set!").build();
        }
        String cookieValue = storageHandler.createCookie();
        NewCookie newCookie = new NewCookie.Builder(StorageHandler.COOKIE_NAME)
            .path("/")
            .secure(true)
            .httpOnly(true)
            .path(StorageHandler.COOKIE_PATH)
            .value(cookieValue)
            .build();
        storageHandler.printActiveClients();
        URI uri = UriBuilder.newInstance()
          .path("/cookie")
          .build(newCookie);
        return Response.created(uri).cookie(newCookie).build();
    }

    @GET
    public Response isCookieAvailable(@CookieParam(value = "user_session") Cookie cookie) {
        if (cookie == null || !storageHandler.isClientActive(cookie.getValue())) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok().build();
    }

}
