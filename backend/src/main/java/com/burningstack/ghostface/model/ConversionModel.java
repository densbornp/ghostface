package com.burningstack.ghostface.model;

import lombok.Data;

@Data
public class ConversionModel {

    /*
     * Default values:
     * preTrainedModel: "default"
     * conversionType: "none"
     * imageScaleFactor: 1.05
     * minNeighbours: 3
     */

    private String preTrainedModel;
    private String conversionType;
    private Double imageScaleFactor;
    private Integer minNeighbours;

    public static ConversionModel validateModel(ConversionModel model) {
        if (model.getPreTrainedModel() == null) {
            model.setPreTrainedModel("default");
        }
        if (model.getConversionType() == null) {
            model.setConversionType("none");
        }
        if (model.getImageScaleFactor() == null) {
            model.setImageScaleFactor(1.05);
        }
        if (model.getMinNeighbours() == null) {
            model.setMinNeighbours(3);
        }
        return model;
    }
}
