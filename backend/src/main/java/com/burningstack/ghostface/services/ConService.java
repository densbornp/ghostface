package com.burningstack.ghostface.services;

import com.burningstack.ghostface.ParamHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConService {

    private static String CONVERSION_SUCCESSFUL = "Conversion successful!";

    public ResponseEntity<Object> convert(String cookie, int type) {
        switch (type) {
            case ParamHelper.C_TYPE_NONE:
                return normalDetection();
            case ParamHelper.C_TYPE_GRID_BLACK:
                return blackGrid();
            case ParamHelper.C_TYPE_GRID_WHITE:
                return whiteGrid();
            case ParamHelper.C_TYPE_HIDE_HALF:
                return hideHalf();
            case ParamHelper.C_TYPE_EDGE_DETECTION:
                return edgeDetection();
            case ParamHelper.C_TYPE_HSV:
                return hsv();
            case ParamHelper.C_TYPE_BITWISE_NOT:
                return bitwiseNot();
            case ParamHelper.C_TYPE_BITWISE_NOT_GRAY:
                return bitwiseNotGray();
            case ParamHelper.C_TYPE_CARTOON:
                return cartoon();
            case ParamHelper.C_TYPE_COLOR_HIGHLIGHTING:
                return colorHighlighting();
            case ParamHelper.C_TYPE_FILTER_BLUE:
                return blueFilter();
        }
        return null;
    }

    private ResponseEntity<Object> normalDetection() {
        try {
            Process p = Runtime.getRuntime().exec("python3 ../../resources/python/opencv_face_detection.py");
            int exitCode = p.waitFor();
            if(exitCode == 0) {
                return ResponseEntity.status(HttpStatus.OK).body(CONVERSION_SUCCESSFUL);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return null;
    }

    private ResponseEntity<Object> blackGrid() {
        return null;
    }

    private ResponseEntity<Object> whiteGrid() {
        return null;
    }

    private ResponseEntity<Object> hideHalf() {
        return null;
    }

    private ResponseEntity<Object> edgeDetection() {
        return null;
    }

    private ResponseEntity<Object> hsv() {
        return null;
    }

    private ResponseEntity<Object> bitwiseNot() {
        return null;
    }

    private ResponseEntity<Object> bitwiseNotGray() {
        return null;
    }

    private ResponseEntity<Object> cartoon() {
        return null;
    }

    private ResponseEntity<Object> colorHighlighting() {
        return null;
    }

    private ResponseEntity<Object> blueFilter() {
        return null;
    }
}
