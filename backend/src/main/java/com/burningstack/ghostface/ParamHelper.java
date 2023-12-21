package com.burningstack.ghostface;

/**
 * This class is a collection of constants used for the conversion of an image
 */
public abstract class ParamHelper {

    private ParamHelper() {
    }

    public static final String UPLOADED_IMAGE = "imageFile";

    // OpenCV settings
    public static final String PRETRAINED_MODEL = "preTrainedModel";
    public static final String CONVERSION_TYPE_PARAM = "conversionType";
    public static final String IMAGE_SCALE_FACTOR = "imageScaleFactor";
    public static final String MIN_NEIGHBOURS = "minNeighbours";

    // Pretrained model haarcascades
    public static final String FRONTAL_FACE_DEFAULT = "default";
    public static final String FRONTAL_FACE_ALT = "alternative";
    public static final String FRONTAL_FACE_ALT2 = "alternative_2";
    public static final String FRONTAL_FACE_ALT_TREE = "alternative_tree";

    // Conversion types
    public static final String C_TYPE_NONE = "none";
    public static final String C_TYPE_GRID_BLACK = "black_grid";
    public static final String C_TYPE_GRID_WHITE = "white_grid";
    public static final String C_TYPE_HIDE_HALF = "hide_half";
    public static final String C_TYPE_EDGE_DETECTION = "edges";
    public static final String C_TYPE_HSV = "hsv_color";
    public static final String C_TYPE_BITWISE_NOT = "invert_color";
    public static final String C_TYPE_BITWISE_NOT_GRAY = "invert_gray_color";
    public static final String C_TYPE_CARTOON = "cartoon";
    public static final String C_TYPE_COLOR_HIGHLIGHTING = "highlight_black_color";
    public static final String C_TYPE_FILTER_BLUE = "blue_filter";
}
