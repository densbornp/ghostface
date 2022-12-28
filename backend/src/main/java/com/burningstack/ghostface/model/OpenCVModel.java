package com.burningstack.ghostface.model;

import com.burningstack.ghostface.ParamHelper;

public class OpenCVModel {

    private int conversionType;
    private String preTrainedModelPath;
    private double imageScaleFactor;
    private int minNeighbours;

    public OpenCVModel(int preTrainedModel, int conversionType, double imageScaleFactor, int minNeighbours) {
        setPreTrainedModelPath(preTrainedModel);
        this.conversionType = conversionType;
        this.imageScaleFactor = imageScaleFactor;
        this.minNeighbours = minNeighbours;
    }

    private void setPreTrainedModelPath(int preTrainedModelPath) {
        String modelPath = "haarcascades/";
        if (preTrainedModelPath == ParamHelper.FRONTAL_FACE_DEFAULT) {
            this.preTrainedModelPath = modelPath + "haarcascade_frontalface_default.xml";
        } else if (preTrainedModelPath == ParamHelper.FRONTAL_FACE_ALT) {
            this.preTrainedModelPath = modelPath + "haarcascade_frontalface_alt.xml";
        } else if (preTrainedModelPath == ParamHelper.FRONTAL_FACE_ALT2) {
            this.preTrainedModelPath = modelPath + "haarcascade_frontalface_alt2.xml";
        } else if (preTrainedModelPath == ParamHelper.FRONTAL_FACE_ALT_TREE) {
            this.preTrainedModelPath = modelPath + "haarcascade_frontalface_alt_tree.xml";
        }
    }

    public int getConversionType() {
        return conversionType;
    }

    public String getPreTrainedModelPath() {
        return preTrainedModelPath;
    }

    public double getImageScaleFactor() {
        return imageScaleFactor;
    }

    public int getMinNeighbours() {
        return minNeighbours;
    }
}
