package com.burningstack.ghostface.model;

import lombok.Data;

@Data
public class ConversionModel {

    /*
      Default values:
      preTrainedModel: 100
      conversionType: 1000
      imageScaleFactor: 1.05
      minNeighbours: 3
     */

    private Integer preTrainedModel;
    private Integer conversionType;
    private Double imageScaleFactor;
    private Integer minNeighbours;

    public static ConversionModel validateModel(ConversionModel model) {
        if (model.getPreTrainedModel() == null) {
            model.setPreTrainedModel(100);
        }
        if (model.getConversionType() == null) {
            model.setConversionType(1000);
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
