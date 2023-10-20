package com.burningstack.ghostface.model;

import com.burningstack.ghostface.ParamHelper;
import lombok.Data;

@Data
public class OpenCVModel {

    private String conversionType;
    private String preTrainedModelPath;
    private double imageScaleFactor;
    private int minNeighbours;

    public OpenCVModel(ConversionModel conversionModel) {
        setPreTrainedModelPath(conversionModel.getPreTrainedModel());
        this.conversionType = conversionModel.getConversionType();
        this.imageScaleFactor = conversionModel.getImageScaleFactor();
        this.minNeighbours = conversionModel.getMinNeighbours();
    }

    private void setPreTrainedModelPath(String preTrainedModelPath) {
        String modelPath = "haarcascades/";
        if (preTrainedModelPath.equals(ParamHelper.FRONTAL_FACE_DEFAULT)) {
            this.preTrainedModelPath = modelPath + "haarcascade_frontalface_default.xml";
        } else if (preTrainedModelPath.equals(ParamHelper.FRONTAL_FACE_ALT)) {
            this.preTrainedModelPath = modelPath + "haarcascade_frontalface_alt.xml";
        } else if (preTrainedModelPath.equals(ParamHelper.FRONTAL_FACE_ALT2)) {
            this.preTrainedModelPath = modelPath + "haarcascade_frontalface_alt2.xml";
        } else if (preTrainedModelPath.equals(ParamHelper.FRONTAL_FACE_ALT_TREE)) {
            this.preTrainedModelPath = modelPath + "haarcascade_frontalface_alt_tree.xml";
        }
    }
}
