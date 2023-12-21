package com.burningstack.ghostface.services;

import com.burningstack.ghostface.ParamHelper;
import com.burningstack.ghostface.storage.ImageStorage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MultivaluedMap;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@ApplicationScoped
@Slf4j
public class UploadFileService {

    public ImageStorage processUploadedImage(MultipartFormDataInput file) {
        Map<String, List<InputPart>> uploadForm = file.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get(ParamHelper.UPLOADED_IMAGE);
        for (InputPart inputPart : inputParts) {
            try {
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                String fileName = getFileName(header);
                // Convert the uploaded file to InputStream
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                return new ImageStorage(ImageIO.read(inputStream), fileName);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

        }
        return null;
    }

    public InputStream getImageAsInputStream(MultipartFormDataInput file) {
        Map<String, List<InputPart>> uploadForm = file.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get(ParamHelper.UPLOADED_IMAGE);
        for (InputPart inputPart : inputParts) {
            try {
                // Convert the uploaded file to InputStream
                return inputPart.getBody(InputStream.class, null);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

        }
        return null;
    }

    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                return name[1].trim().replace("\"", "");
            }
        }
        return "unknown";
    }
}
