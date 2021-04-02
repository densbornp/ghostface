# import the necessary packages
import numpy as np
import argparse
import cv2 as cv
import time
import os


# construct the argument parse and parse the arguments
def init_arguments():
    ap = argparse.ArgumentParser()
    ap.add_argument("-i", "--image", required=True,
                    help="path to input image")
    ap.add_argument("-m", "--model", required=True,
                    help="path pre-trained model")
    ap.add_argument("-t", "--tmp",
                    help="temporary image")
    ap.add_argument("-n", "--name",
                    help="name of the image")
    ap.add_argument("-s", "--scale", default=1.05,
                    help="scale factor of image processing")
    ap.add_argument("-mn", "--minNeighbors", default=3,
                    help="minNeighbors in image")
    return vars(ap.parse_args())


def load_image():
    print("[INFO] loading model...")
    
    img = cv.imread(args["image"])

    # Create the haar cascade
    faceCascade = cv.CascadeClassifier(args["model"])
    scaleFactor = args["scale"]
    minNeighbors = args["minNeighbors"]

    # Detect faces in the image
    faces = faceCascade.detectMultiScale(
        img,
        scaleFactor=float(scaleFactor),
        minNeighbors=int(minNeighbors),
        minSize=(30, 30)
    )

    foundFaces = len(faces)
    print("Found {0} face(s) detected!".format(foundFaces))
    path = os.path.abspath(os.path.join(
        os.path.dirname(__file__), os.pardir, "uploads"))

    cv.imwrite(os.path.join(path, str(args["name"])), img)

    # Draw a rectangle around the faces
    for (x, y, w, h) in faces:
        face = cv.rectangle(img, (x, y),
                            (x+w, y+h), (0, 0, 255), 2)

    # Save resistant image with rectangle -> Only to show user
    # Downloaded file will have no rectangle
    cv.imwrite(os.path.join(path, str(args["tmp"])), img)
    return img


startTime = int(round(time.time() * 1000))
args = init_arguments()
img = load_image()
endTime = int(round(time.time() * 1000))
resultTime = (endTime - startTime) / 1000
print("Calculation time: " + str(resultTime) + " sec.")
print("Script finished")
