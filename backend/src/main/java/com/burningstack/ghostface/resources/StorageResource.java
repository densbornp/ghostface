package com.burningstack.ghostface.resources;

import com.burningstack.ghostface.services.StorageService;
import com.burningstack.ghostface.storage.StorageHandler;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;


@Path("/")
@Slf4j
public class StorageResource {

    @Inject
    private StorageService storageService;

    @Inject
    private StorageHandler storageHandler;
    private static final String VALID_COOKIE_MISSING = "Valid Cookie missing!";

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@CookieParam(value = "user_session") Cookie cookie, MultipartFormDataInput file) {

        try {
            if (cookie == null || !storageHandler.isClientActive(cookie.getValue())) {
                return Response.status(Response.Status.BAD_REQUEST).entity(VALID_COOKIE_MISSING).build();
            }
            return this.storageService.storeImage(cookie.getValue(), file);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @GET
    @Path("/download")
    public Response downloadImage(@CookieParam(value = "user_session") Cookie cookie) {
        if (cookie == null || !storageHandler.isClientActive(cookie.getValue())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(VALID_COOKIE_MISSING).build();
        }
        return this.storageService.download(cookie.getValue());
    }

    @GET
    @Path("/image")
    public Response getImage(@CookieParam(value = "user_session") Cookie cookie) {
        if (cookie == null || !storageHandler.isClientActive(cookie.getValue())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(VALID_COOKIE_MISSING).build();
        }
        return this.storageService.getImage(cookie.getValue());
    }

    @GET
    @Path("/tmpImage")
    public Response getTmpConvertedImage(
            @CookieParam(value = "user_session") Cookie cookie) {
        if (cookie == null || !storageHandler.isClientActive(cookie.getValue())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(VALID_COOKIE_MISSING).build();
        }
        return this.storageService.getTmpConvertedImage(cookie.getValue());
    }
}
