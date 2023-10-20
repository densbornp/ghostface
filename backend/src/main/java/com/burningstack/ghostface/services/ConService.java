package com.burningstack.ghostface.services;

import com.burningstack.ghostface.ParamHelper;
import com.burningstack.ghostface.model.OpenCVModel;
import com.burningstack.ghostface.storage.ImageStorage;
import com.burningstack.ghostface.storage.StorageHandler;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class ConService {

    @Inject
    private StorageHandler storageHandler;
    private ImageStorage imgStorage;
    private BufferedImage bufferedImage;
    private String contentType;
    private CascadeClassifier faceDetector;
    private double scaleFactor;
    private int minNeighbours;
    private int minFaceSize;
    private Mat originalImage;
    private MatOfRect faceDetections;

    public Response convert(String cookie, OpenCVModel model) {
        switch (model.getConversionType()) {
            case ParamHelper.C_TYPE_NONE:
                return normalDetection(cookie, model);
            case ParamHelper.C_TYPE_GRID_BLACK:
                return grid(cookie, model, ParamHelper.C_TYPE_GRID_BLACK);
            case ParamHelper.C_TYPE_GRID_WHITE:
                return grid(cookie, model, ParamHelper.C_TYPE_GRID_WHITE);
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
     * @param model  The OpenCVModel
     */
    private void initializeBasicSetup(String cookie, OpenCVModel model) {
        this.imgStorage = storageHandler.getImageStorage(cookie);
        this.bufferedImage = imgStorage.getImage();
        this.contentType = imgStorage.getContentType();
        this.faceDetector = new CascadeClassifier(model.getPreTrainedModelPath());
        this.scaleFactor = model.getImageScaleFactor();
        this.minNeighbours = model.getMinNeighbours();
        this.originalImage = bufferedImage2Mat(bufferedImage);
        this.minFaceSize = Math.round(originalImage.rows() * 0.1f);
        this.faceDetections = new MatOfRect();
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
     *
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
     * Draw a bounding box around each face.
     */
    private void drawRectangles(Mat image) throws IOException {
        for (Rect rect : this.faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }
        this.imgStorage.setTemporaryImage(this.mat2BufferedImage(image, this.imgStorage.getFileExtension()));
    }

    private Response normalDetection(String cookie, OpenCVModel model) {
        try {
            this.initializeBasicSetup(cookie, model);
            this.faceDetector.detectMultiScale(originalImage, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.drawRectangles(this.originalImage);
            this.imgStorage
                    .setConvertedImage(this.mat2BufferedImage(this.originalImage, this.imgStorage.getFileExtension()));
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response grid(String cookie, OpenCVModel model, String color) {
        try {
            double pixelValue = 0.0; // black
            if (color.equals(ParamHelper.C_TYPE_GRID_WHITE)) {
                pixelValue = 255.0; // white
            }
            this.initializeBasicSetup(cookie, model);
            Mat tempImage = this.bufferedImage2Mat(this.bufferedImage);
            int rows = tempImage.rows(); // Calculates number of rows
            int cols = tempImage.cols(); // Calculates number of columns
            int ch = tempImage.channels(); // Calculates number of channels (Grayscale: 1, RGB: 3)
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (i % 2 == 0 || j % 2 == 0) {
                        double[] data = tempImage.get(i, j);
                        for (int k = 0; k < ch; k++) // Run through channels
                        {
                            data[k] = pixelValue; // Change pixel to color
                        }
                        tempImage.put(i, j, data);
                    }
                }
            }
            this.faceDetector.detectMultiScale(tempImage, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.imgStorage.setConvertedImage(this.mat2BufferedImage(tempImage, this.imgStorage.getFileExtension()));
            this.drawRectangles(tempImage);
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response hideHalf(String cookie, OpenCVModel model) {
        try {
            this.initializeBasicSetup(cookie, model);
            Mat tempImage = this.bufferedImage2Mat(this.bufferedImage);
            Mat overlay = this.bufferedImage2Mat(this.bufferedImage);
            double alpha = 0.8;
            Imgproc.rectangle(overlay, new Point(0, (double) tempImage.height() / 2),
                    new Point(tempImage.width(), tempImage.height()), new Scalar(0, 0, 0), -1);
            Core.addWeighted(overlay, alpha, tempImage, 1 - alpha, 0, tempImage);
            this.faceDetector.detectMultiScale(tempImage, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.imgStorage.setConvertedImage(this.mat2BufferedImage(tempImage, this.imgStorage.getFileExtension()));
            this.drawRectangles(tempImage);
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response edgeDetection(String cookie, OpenCVModel model) {
        try {
            this.initializeBasicSetup(cookie, model);
            Mat tempImage = this.bufferedImage2Mat(this.bufferedImage);
            Mat gray = new Mat(tempImage.rows(), tempImage.cols(), tempImage.type());
            Mat edges = new Mat(tempImage.rows(), tempImage.cols(), tempImage.type());
            Mat dst = new Mat(tempImage.rows(), tempImage.cols(), tempImage.type(), new Scalar(0));
            // Converting the image to Gray
            Imgproc.cvtColor(tempImage, gray, Imgproc.COLOR_RGB2GRAY);
            // Blurring the image
            Imgproc.blur(gray, edges, new Size(3, 3));
            // Detecting the edges
            Imgproc.Canny(edges, edges, 10, 100, 3);
            tempImage.copyTo(dst, edges);
            this.faceDetector.detectMultiScale(dst, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.imgStorage.setConvertedImage(this.mat2BufferedImage(dst, this.imgStorage.getFileExtension()));
            this.drawRectangles(dst);
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response hsv(String cookie, OpenCVModel model) {
        try {
            this.initializeBasicSetup(cookie, model);
            Mat tempImage = this.bufferedImage2Mat(this.bufferedImage);
            Imgproc.cvtColor(this.originalImage, tempImage, Imgproc.COLOR_RGB2HSV);
            this.faceDetector.detectMultiScale(tempImage, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.imgStorage.setConvertedImage(this.mat2BufferedImage(tempImage, this.imgStorage.getFileExtension()));
            this.drawRectangles(tempImage);
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response bitwiseNot(String cookie, OpenCVModel model) {
        try {
            this.initializeBasicSetup(cookie, model);
            Mat tempImage = this.bufferedImage2Mat(this.bufferedImage);
            Core.bitwise_not(this.originalImage, tempImage);
            this.faceDetector.detectMultiScale(tempImage, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.imgStorage.setConvertedImage(this.mat2BufferedImage(tempImage, this.imgStorage.getFileExtension()));
            this.drawRectangles(tempImage);
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response bitwiseNotGray(String cookie, OpenCVModel model) {
        try {
            this.initializeBasicSetup(cookie, model);
            Mat tempImage = this.bufferedImage2Mat(this.bufferedImage);
            Imgproc.cvtColor(this.originalImage, tempImage, Imgproc.COLOR_BGR2GRAY);
            Core.bitwise_not(tempImage, tempImage);
            this.faceDetector.detectMultiScale(tempImage, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.imgStorage.setConvertedImage(this.mat2BufferedImage(tempImage, this.imgStorage.getFileExtension()));
            this.drawRectangles(tempImage);
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response cartoon(String cookie, OpenCVModel model) {
        try {
            // TODO Not working correctly
            this.initializeBasicSetup(cookie, model);
            Mat tempImage = this.bufferedImage2Mat(this.bufferedImage);
            Mat gray = new Mat(tempImage.rows(), tempImage.cols(), tempImage.type());
            Mat edges = new Mat(tempImage.rows(), tempImage.cols(), tempImage.type());
            Mat blur = new Mat(tempImage.rows(), tempImage.cols(), tempImage.type());
            Mat color = new Mat(tempImage.rows(), tempImage.cols(), tempImage.type());
            Imgproc.cvtColor(tempImage, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.medianBlur(gray, blur, 3);
            Imgproc.adaptiveThreshold(blur, edges, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 9, 2);
            Imgproc.bilateralFilter(tempImage, color, 9, 300, 300);
            Core.bitwise_and(color, color, tempImage, edges);
            this.faceDetector.detectMultiScale(tempImage, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.imgStorage.setConvertedImage(this.mat2BufferedImage(tempImage, this.imgStorage.getFileExtension()));
            this.drawRectangles(tempImage);
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response colorHighlighting(String cookie, OpenCVModel model) {
        try {
            // TODO Not working correctly
            this.initializeBasicSetup(cookie, model);
            Mat tempImage = this.bufferedImage2Mat(this.bufferedImage);
            Mat gray = new Mat(tempImage.rows(), tempImage.cols(), tempImage.type());
            Imgproc.cvtColor(tempImage, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(gray, tempImage, 150, 255, Imgproc.THRESH_BINARY);
            this.faceDetector.detectMultiScale(tempImage, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.imgStorage.setConvertedImage(this.mat2BufferedImage(tempImage, this.imgStorage.getFileExtension()));
            this.drawRectangles(tempImage);
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    private Response blueFilter(String cookie, OpenCVModel model) {
        try {
            // TODO Not working correctly
            this.initializeBasicSetup(cookie, model);
            Mat tempImage = this.bufferedImage2Mat(this.bufferedImage);
            List<Mat> channels = new ArrayList<>();
            Core.split(tempImage, channels);
            Mat blueChannel = channels.get(0);
            this.faceDetector.detectMultiScale(blueChannel, faceDetections, scaleFactor, minNeighbours, 0,
                    new Size(minFaceSize, minFaceSize), new Size());
            this.imgStorage.setConvertedImage(this.mat2BufferedImage(blueChannel, this.imgStorage.getFileExtension()));
            this.drawRectangles(blueChannel);
            return Response.status(Response.Status.OK).entity(this.bufferedImageToByteArray()).type(contentType)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
