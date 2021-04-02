# import the necessary packages
import numpy as np
import argparse
import cv2 as cv
import time
import os
from matplotlib import pyplot as plt

# Cascade frontalface_default
numberOfDetectedFacesCascade = 0
defaultFaceList = []
# Cascade frontalface_alt
numberOfDetectedFacesCascade2 = 0
altFaceList = []
# Cascade frontalface_alt2
numberOfDetectedFacesCascade3 = 0
alt2FaceList = []
# Cascade frontalface_alt_tree
numberOfDetectedFacesCascade4 = 0
altTreeList = []


# Create the arguments
def init_arguments():
    ap = argparse.ArgumentParser()
    ap.add_argument("-i", "--images", required=True,
                    help="path to image directory")
    ap.add_argument("-s", "--scale", default=1.01,
                    help="scale factor of image processing")
    ap.add_argument("-mn", "--minNeighbors", default=3,
                    help="minNeighbors in image")
    ap.add_argument("-l", "--length", default=0,
                    help="how many images should be read")
    ap.add_argument("-si", "--show", default=0,
                    help="shows images with detected faces after convertion")
    return vars(ap.parse_args())


def load_image():
    print("[START] Loading images...")
    scaleFactor = args["scale"]
    minNeighbors = args["minNeighbors"]
    print("Settings: ScaleFactor=" +
          str(args["scale"]) + ", MinNeighbors=" + str(args["minNeighbors"]) + "\n")

    # Run through all images in directory
    fileIndex = 0
    for file in sorted(os.listdir(args["images"])):
        # Check fileIndex attribute and possibly break for-loop
        if(fileIndex == int(args["length"])):
            break

        # Create the haar cascade
        path = "public/python/"
        cascade = "haarcascade_frontalface_default.xml"
        print("######## Processed image " + file + " ########")
        print("Loading " + cascade)
        faceCascade = cv.CascadeClassifier(path + cascade)

        img = cv.imread(args["images"] + "/" + file)

        # Detect faces in the image
        faces = faceCascade.detectMultiScale(
            img,
            scaleFactor=float(scaleFactor),
            minNeighbors=int(minNeighbors),
        )

        convertedImg = img.copy()

        if(len(faces) > 0):
            for (x, y, w, h) in faces:
                convertedImg = cv.rectangle(convertedImg, (x, y),
                                            (x+w, y+h), (0, 0, 255), 1)
                global numberOfDetectedFacesCascade
                numberOfDetectedFacesCascade += 1
            # Add Image with rectangle
            defaultFaceList.append(convertedImg)
            print("Found " + str(len(faces)) + " face(s)!")

        print("\n")

        ####### haarcascade_frontal_alt.xml ########
        cascade = "haarcascade_frontalface_alt.xml"
        print("Loading " + cascade)
        # Create the haar cascade
        faceCascade = cv.CascadeClassifier(path + cascade)

        # Detect faces in the image
        faces = faceCascade.detectMultiScale(
            img,
            scaleFactor=float(scaleFactor),
            minNeighbors=int(minNeighbors),
        )

        convertedImg = img.copy()

        if(len(faces) > 0):
            for (x, y, w, h) in faces:
                convertedImg = cv.rectangle(convertedImg, (x, y),
                                            (x+w, y+h), (0, 0, 255), 1)
                global numberOfDetectedFacesCascade2
                numberOfDetectedFacesCascade2 += 1
            # Add Image with rectangle
            altFaceList.append(convertedImg)
            print("Found " + str(len(faces)) + " face(s)!")

        print("\n")

        ####### haarcascade_frontal_alt2.xml ########
        cascade = "haarcascade_frontalface_alt2.xml"
        print("Loading " + cascade)
        # Create the haar cascade
        faceCascade = cv.CascadeClassifier(path + cascade)

        # Detect faces in the image
        faces = faceCascade.detectMultiScale(
            img,
            scaleFactor=float(scaleFactor),
            minNeighbors=int(minNeighbors),
        )

        convertedImg = img.copy()

        if(len(faces) > 0):
            for (x, y, w, h) in faces:
                convertedImg = cv.rectangle(convertedImg, (x, y),
                                            (x+w, y+h), (0, 0, 255), 1)
                global numberOfDetectedFacesCascade3
                numberOfDetectedFacesCascade3 += 1
            # Add Image with rectangle
            alt2FaceList.append(convertedImg)
            print("Found " + str(len(faces)) + " face(s)!")

        print("\n")

        ####### haarcascade_frontalface_alt_tree.xml ########
        cascade = "haarcascade_frontalface_alt_tree.xml"
        print("Loading " + cascade)
        # Create the haar cascade
        faceCascade = cv.CascadeClassifier(path + cascade)

        # Detect faces in the image
        faces = faceCascade.detectMultiScale(
            img,
            scaleFactor=float(scaleFactor),
            minNeighbors=int(minNeighbors),
        )

        convertedImg = img.copy()

        if(len(faces) > 0):
            for (x, y, w, h) in faces:
                convertedImg = cv.rectangle(convertedImg, (x, y),
                                            (x+w, y+h), (0, 0, 255), 1)
                global numberOfDetectedFacesCascade4
                numberOfDetectedFacesCascade4 += 1
            # Add Image with rectangle
            altTreeList.append(convertedImg)
            print("Found " + str(len(faces)) + " face(s)!")

        print("\n")
        fileIndex += 1

    print("HaarCascade: Default")
    print(f"{numberOfDetectedFacesCascade} faces found in images.\n")
    print("HaarCascade: Alt")
    print(f"{numberOfDetectedFacesCascade2} faces found in images.\n")
    print("HaarCascade: Alt2")
    print(f"{numberOfDetectedFacesCascade3} faces found in images.\n")
    print("HaarCascade: Alt-Tree")
    print(f"{numberOfDetectedFacesCascade4} faces found in images.\n")


startTime = int(round(time.time() * 1000))
args = init_arguments()
img = load_image()
endTime = int(round(time.time() * 1000))
resultTime = (endTime - startTime) / 1000 / 60
print("Calculation time: " + str(resultTime) + " min.")
print("Script finished")

# Show images or statistic
if(str(args["show"]) == "1"):

    if(len(defaultFaceList) > 0):
        cv.namedWindow("Haarcascade_Frontalface_Default")
        numpy_horizontal = np.hstack(defaultFaceList)
        cv.imshow("Haarcascade_Frontalface_Default",
                  numpy_horizontal)

    if(len(altFaceList) > 0):
        cv.namedWindow("Haarcascade_Frontalface_Alt")
        numpy_horizontal = np.hstack(altFaceList)
        cv.imshow("Haarcascade_Frontalface_Alt",
                  numpy_horizontal)

    if(len(alt2FaceList) > 0):
        cv.namedWindow("Haarcascade_Frontalface_Alt2")
        numpy_horizontal = np.hstack(alt2FaceList)
        cv.imshow("Haarcascade_Frontalface_Alt2",
                  numpy_horizontal)

    if(len(altTreeList) > 0):
        cv.namedWindow("Haarcascade_Frontalface_Alt_Tree")
        numpy_horizontal = np.hstack(altTreeList)
        cv.imshow("Haarcascade_Frontalface_Alt_Tree",
                  numpy_horizontal)

    res = cv.waitKey(0)

    if res & 0xFF == ord('q'):
        cv.destroyAllWindows()
else:
    # Show statistic
    fig, ((ax1, ax2), (ax3, ax4)) = plt.subplots(2, 2, figsize=(10, 8))

    plt.suptitle(
        "Number of detected faces\n" +
        "Number of images: " + args["length"] +
        ",  Calculation time: " + str(round(resultTime, 2)) + " min.\n" + "Sensitivity: " + str(args["minNeighbors"]) + ", Scale-Factor: " + str(args["scale"]))

    minValue = 0
    maxValue = 2500

    # Ax1
    namesCascade = ("Faces found (" + str(numberOfDetectedFacesCascade) + ")")

    ax1.bar(namesCascade, [numberOfDetectedFacesCascade])

    ax1.set_ylim(minValue, maxValue)

    ax1.set_title("haarcascade_frontalface_default.xml")

    # Ax2
    namesCascade = ("Faces found (" + str(numberOfDetectedFacesCascade2) + ")")

    ax2.bar(namesCascade, [numberOfDetectedFacesCascade2])

    ax2.set_ylim(minValue, maxValue)

    ax2.set_title("haarcascade_frontalface_alt.xml")

    # Ax3
    namesCascade = ("Faces found (" + str(numberOfDetectedFacesCascade3) + ")")

    ax3.bar(namesCascade, [numberOfDetectedFacesCascade3])

    ax3.set_ylim(minValue, maxValue)

    ax3.set_title("haarcascade_frontalface_alt2.xml")

    # Ax4
    namesCascade = ("Faces found (" + str(numberOfDetectedFacesCascade4) + ")")

    ax4.bar(namesCascade, [numberOfDetectedFacesCascade4])

    ax4.set_ylim(minValue, maxValue)

    ax4.set_title("haarcascade_frontalface_alt_tree.xml")
    plt.show()
