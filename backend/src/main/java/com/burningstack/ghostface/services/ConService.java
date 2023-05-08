package com.burningstack.ghostface.services;

import com.burningstack.ghostface.ParamHelper;
import com.burningstack.ghostface.model.OpenCVModel;
import com.burningstack.ghostface.storage.ImageStorage;
import com.burningstack.ghostface.storage.StorageHandler;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ConService {

    private StorageHandler storageHandler;
    private ImageStorage imgStorage;
    private BufferedImage bufferedImage;
    private MediaType contentType;
    private CascadeClassifier faceDetector;
    private double scaleFactor;
    private int minNeighbours;
    private int minFaceSize;
    private Mat originalImage;
    private MatOfRect faceDetections;

    public ResponseEntity<Object> convert(String cookie, OpenCVModel model) {
        switch (model.getConversionType()) {
            case ParamHelper.C_TYPE_NONE:
                return normalDetection(cookie, model);
            case ParamHelper.C_TYPE_GRID_BLACK:
                return blackGrid(cookie, model);
            case ParamHelper.C_TYPE_GRID_WHITE:
                return whiteGrid(cookie, model);
            case ParamHelper.C_TYPE_HIDE_HALF:
                return hideHalf(cookie, model);
            case ParamHelper.C_TYPE_EDGE_DETECTION:
                return edgeDetection(cookie, model);
            case ParamHelper.C_TYPE_HSV:
                return hsv(cookie, model);
            case ParamHelper.C_TYPE_BITWISE_NOT:
                return bitwiseNot(cookie, model);
            case ParamHelper.C_TYPE_BITWISE_NOT_GRAY:
                return bitwiseNotGray(cookie, model);
            case ParamHelper.C_TYPE_CARTOON:
                return cartoon(cookie, model);
            case ParamHelper.C_TYPE_COLOR_HIGHLIGHTING:
                return colorHighlighting(cookie, model);
            case ParamHelper.C_TYPE_FILTER_BLUE:
                return blueFilter(cookie, model);
            default:
                return null;
        }
    }

    /**
     *
     * @param cookie The cookie to assign the right user
     * @param model The OpenCVModel
     */
    private void initializeBasicSetup(String cookie, OpenCVModel model) {
        this.storageHandler = StorageHandler.getInstance();
        this.imgStorage = storageHandler.getImageStorage(cookie);
        this.bufferedImage = imgStorage.getImage();
        this.imgStorage.setConvertedImage(bufferedImage);
        this.contentType = imgStorage.getContentType();
        this.faceDetector = new CascadeClassifier(model.getPreTrainedModelPath());
        this.scaleFactor = model.getImageScaleFactor();
        this.minNeighbours = model.getMinNeighbours();
        this.originalImage = bufferedImage2Mat(bufferedImage);
        this.minFaceSize = Math.round(originalImage.rows() * 0.1f);
        this.faceDetections = new MatOfRect();
        this.faceDetector.detectMultiScale(originalImage, faceDetections, scaleFactor, minNeighbours, 0, new Size(minFaceSize, minFaceSize), new Size());
    }

    private byte[] bufferedImageToByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imgStorage.getConvertedImage(), imgStorage.getFileExtension(), baos);
        return baos.toByteArray();
    }

    private Mat bufferedImage2Mat(BufferedImage image) {
        image = convertTo3ByteBGRType(image);
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.put(0, 0, data);
        return mat;
    }

    private BufferedImage mat2BufferedImage(Mat matrix, String fileExtension) throws IOException {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode("." + fileExtension, matrix, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }

    /**
     * Change the type of bufferedImage to TYPE_3BYTE_BGR
     * because bufferedImage2Mat may return int[] and that would break the code
     * @param image The image to be converted
     * @return The image in the correct format
     */
    private static BufferedImage convertTo3ByteBGRType(BufferedImage image) {
        BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(),
            BufferedImage.TYPE_3BYTE_BGR);
        convertedImage.getGraphics().drawImage(image, 0, 0, null);
        return convertedImage;
    }

    /**
     *  Draw a bounding box around each face.
     */
    private void drawRectangles() throws IOException {
        for (Rect rect : this.faceDetections.toArray()) {
            Imgproc.rectangle(this.originalImage, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
        this.imgStorage.setTemporaryImage(this.mat2BufferedImage(this.originalImage, this.imgStorage.getFileExtension()));
    }

    private ResponseEntity<Object> normalDetection(String cookie, OpenCVModel model) {
        try {
            this.initializeBasicSetup(cookie, model);
            this.drawRectangles();
            return ResponseEntity.status(HttpStatus.OK).contentType(contentType).body(this.bufferedImageToByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private ResponseEntity<Object> blackGrid(String cookie, OpenCVModel model) {
        return null;
    }

    private ResponseEntity<Object> whiteGrid(String cookie, OpenCVModel model) {
        return null;
    }

    private ResponseEntity<Object> hideHalf(String cookie, OpenCVModel model) {
        return null;
    }

    private ResponseEntity<Object> edgeDetection(String cookie, OpenCVModel model) {
        return null;
    }

    private ResponseEntity<Object> hsv(String cookie, OpenCVModel model) {
        return null;
    }

    private ResponseEntity<Object> bitwiseNot(String cookie, OpenCVModel model) {
        return null;
    }

    private ResponseEntity<Object> bitwiseNotGray(String cookie, OpenCVModel model) {
        return null;
    }

    private ResponseEntity<Object> cartoon(String cookie, OpenCVModel model) {
        return null;
    }

    private ResponseEntity<Object> colorHighlighting(String cookie, OpenCVModel model) {
        return null;
    }

    private ResponseEntity<Object> blueFilter(String cookie, OpenCVModel model) {
        return null;
    }
}
