package com.burningstack.ghostface;

/**
 * This class is a collection of constants used for the conversion of an image
 */
public abstract class ParamHelper {

  private ParamHelper() {}

  public static final String UPLOADED_IMAGE = "imageFile";
  public static final String CONVERSION_TYPE_PARAM = "type";
  // Conversion types
  // New type -> add hundreds, kind of similar type -> add ones or tens
  public static final int C_TYPE_NONE = 1000;
  public static final int C_TYPE_GRID_BLACK = 1100;
  public static final int C_TYPE_GRID_WHITE = 1101;
  public static final int C_TYPE_HIDE_HALF = 1200;
  public static final int C_TYPE_EDGE_DETECTION = 1300;
  public static final int C_TYPE_HSV = 1400;
  public static final int C_TYPE_BITWISE_NOT = 1500;
  public static final int C_TYPE_BITWISE_NOT_GRAY = 1501;
  public static final int C_TYPE_CARTOON = 1600;
  public static final int C_TYPE_COLOR_HIGHLIGHTING = 1700;
  public static final int C_TYPE_FILTER_BLUE = 1800;
}
