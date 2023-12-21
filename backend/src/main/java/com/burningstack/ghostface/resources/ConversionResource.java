package com.burningstack.ghostface.resources;

import com.burningstack.ghostface.model.ConversionModel;
import com.burningstack.ghostface.model.OpenCVModel;
import com.burningstack.ghostface.services.ConService;
import com.burningstack.ghostface.storage.StorageHandler;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class ConversionResource {

    @Inject
    private ConService conService;
    private static final String VALID_COOKIE_MISSING = "Valid Cookie missing!";
    @Inject
    private StorageHandler storageHandler;

    @POST
    @Path("/convert")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response convertImage(@CookieParam(value = "user_session") Cookie cookie, ConversionModel conversionModel) {
        if (cookie == null || !storageHandler.isClientActive(cookie.getValue())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(VALID_COOKIE_MISSING).build();
        }
        ConversionModel validatedModel = ConversionModel.validateModel(conversionModel);
        OpenCVModel model = new OpenCVModel(validatedModel);
        return conService.convert(cookie.getValue(), model);
    }
}
