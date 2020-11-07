package com.dsecsoftware.ghostface.controller;

import com.dsecsoftware.ghostface.services.ConversionService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/convert")
public class ConversionController {

    private ConversionService conversionService;

    @RequestMapping(method = RequestMethod.POST)
    public void convertImage() {
        conversionService.convert();
    }
}
