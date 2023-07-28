package com.burningstack.ghostface.controller;

import com.burningstack.ghostface.ParamHelper;
import com.burningstack.ghostface.model.OpenCVModel;
import com.burningstack.ghostface.services.ConService;
import com.burningstack.ghostface.storage.StorageHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
public class ConversionController {

    @Inject
    private ConService conService;
    private static final String VALID_COOKIE_MISSING = "Valid Cookie missing!";
    @Inject
    private StorageHandler storageHandler;

    public ConversionController() {
    }

    @PostMapping("/convert")
    public ResponseEntity<Object> convertImage(@CookieValue(value = "user_session", defaultValue = "") String cookie,
                                               @RequestParam(value = ParamHelper.PRETRAINED_MODEL, defaultValue = "100") int preTrainedModel,
                                               @RequestParam(value = ParamHelper.CONVERSION_TYPE_PARAM, defaultValue = "1000") int conversionType,
                                                @RequestParam(value = ParamHelper.IMAGE_SCALE_FACTOR, defaultValue = "1.05") double imageScaleFactor, @RequestParam(value = ParamHelper.MIN_NEIGHBOURS, defaultValue = "3") int minNeighbours) {
        if (cookie.isEmpty() || !storageHandler.isClientActive(cookie)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(VALID_COOKIE_MISSING);
        }
        OpenCVModel model = new OpenCVModel(preTrainedModel, conversionType, imageScaleFactor, minNeighbours);
        return conService.convert(cookie, model);
    }
}
