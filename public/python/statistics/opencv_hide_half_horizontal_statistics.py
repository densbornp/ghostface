# import the necessary packages
import numpy as np
import argparse
import cv2 as cv
import time
import os
import math
from matplotlib import pyplot as plt

# Cascade frontalface_default
numberOfDetectedConvertedFacesCascade = 0
defaultFaceList = []
# Cascade frontalface_alt
numberOfDetectedConvertedFacesCascade2 = 0
altFaceList = []
# Cascade frontalface_alt2
numberOfDetectedConvertedFacesCascade3 = 0
alt2FaceList = []
# Cascade frontalface_alt_tree
numberOfDetectedConvertedFacesCascade4 = 0
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

        # Set thresholds for diagonal differences
        maxDeviationHigh = 1.3  # +30% (longer)
        maxDeviationLow = 0.7  # -30% (shorter)

        # Detect faces in the image
        faces = faceCascade.detectMultiScale(
            img,
            scaleFactor=float(scaleFactor),
            minNeighbors=int(minNeighbors)
        )
        print("Found " + str(len(faces)) + " face(s) before convertion!")

        faceFoundAfter = False

        if(len(faces) > 0):
            faceDiagonalList = []
            faceIndex = 0
            for (x, y, w, h) in faces:
                faceDiagonal = round(math.sqrt(
                    ((x - (x+w))**2) + ((y - (y+h))**2)), 2)  # Calculate diagonal
                faceDiagonalList.insert(faceIndex, faceDiagonal)
                faceIndex += 1

            # Get biggest detection diagonal
            # We estimate that this is the face
            maxFace = max(faceDiagonalList)

            convertedImg = img.copy()
            overlay = img.copy()

            # rows, columns, color channels
            height, width, channels = img.shape

            # Lay black window over half of the image
            cv.rectangle(overlay, (0, int(height/2)),
                         (width, height), (0, 0, 0), -1)
            alpha = 0.8
            convertedImg = cv.addWeighted(
                overlay, alpha, convertedImg, 1 - alpha, 0)

            # Detect faces in manipulated image
            resitantFaces = faceCascade.detectMultiScale(
                convertedImg,
                scaleFactor=float(scaleFactor),
                minNeighbors=int(minNeighbors)
            )

            for (x, y, w, h) in resitantFaces:
                convertedImg = cv.rectangle(convertedImg, (x, y),
                                            (x+w, y+h), (0, 0, 255), 1)
                convertedFaceDiagonal = round(math.sqrt(
                    ((x - (x+w))**2) + ((y - (y+h))**2)), 2)  # Calculate diagonal of face

                # Print diagonal of found face after convertion
                print("ConvertedFaceDiagonal: " +
                      str(convertedFaceDiagonal))

                # Print diagonal info of found faces
                print(
                    "Min. diagonal " + str(maxDeviationLow) + ": " +
                    str(round(maxFace * maxDeviationLow, 2)) +
                    ", Orig. diagonal: " +
                    str(maxFace) + ", Max. diagonal " +
                    str(maxDeviationHigh) + ": "
                    + str(round(maxFace * maxDeviationHigh, 2)))

                # Check if diagonal of face is in between threshold if true than it's the original face
                if((convertedFaceDiagonal <= maxFace and convertedFaceDiagonal >= (maxFace * maxDeviationLow))
                        or (convertedFaceDiagonal >= maxFace and convertedFaceDiagonal <= (maxFace * maxDeviationHigh))):
                    global numberOfDetectedConvertedFacesCascade
                    numberOfDetectedConvertedFacesCascade += 1
                    faceFoundAfter = True
                    break
            # Add Image with rectangle
            defaultFaceList.append(convertedImg)
        print("Face found after convertion: " + str(faceFoundAfter) + "\n")

        ####### haarcascade_frontal_alt.xml ########
        cascade = "haarcascade_frontalface_alt.xml"
        print("Loading " + cascade)
        # Create the haar cascade
        faceCascade = cv.CascadeClassifier(path + cascade)

        # Detect faces in the image
        faces = faceCascade.detectMultiScale(
            img,
            scaleFactor=float(scaleFactor),
            minNeighbors=int(minNeighbors)
        )
        print("Found " + str(len(faces)) + " face(s) before convertion!")

        faceFoundAfter = False

        if(len(faces) > 0):
            faceDiagonalList = []
            faceIndex = 0
            for (x, y, w, h) in faces:
                faceDiagonal = round(math.sqrt(
                    ((x - (x+w))**2) + ((y - (y+h))**2)), 2)  # Calculate diagonal
                faceDiagonalList.insert(faceIndex, faceDiagonal)
                faceIndex += 1

            # Get biggest detection diagonal
            # We estimate that this is the face
            maxFace = max(faceDiagonalList)

            convertedImg = img.copy()
            overlay = img.copy()

            # rows, columns, color channels
            width, height, channels = img.shape

            # Lay black window over half of the image
            cv.rectangle(overlay, (0, int(height/2)),
                         (width, height), (0, 0, 0), -1)
            alpha = 0.8
            convertedImg = cv.addWeighted(
                overlay, alpha, convertedImg, 1 - alpha, 0)

            # Detect faces in manipulated image
            resitantFaces = faceCascade.detectMultiScale(
                convertedImg,
                scaleFactor=float(scaleFactor),
                minNeighbors=int(minNeighbors)
            )

            for (x, y, w, h) in resitantFaces:
                convertedImg = cv.rectangle(convertedImg, (x, y),
                                            (x+w, y+h), (0, 0, 255), 1)
                convertedFaceDiagonal = round(math.sqrt(
                    ((x - (x+w))**2) + ((y - (y+h))**2)), 2)  # Calculate diagonal of face

                # Print diagonal of found face after convertion
                print("ConvertedFaceDiagonal: " +
                      str(convertedFaceDiagonal))

                # Print diagonal info of found faces
                print(
                    "Min. diagonal " + str(maxDeviationLow) + ": " +
                    str(round(maxFace * maxDeviationLow, 2)) +
                    ", Orig. diagonal: " +
                    str(maxFace) + ", Max. diagonal " +
                    str(maxDeviationHigh) + ": "
                    + str(round(maxFace * maxDeviationHigh, 2)))

                # Check if diagonal of face is in between threshold if true than it's the original face
                if((convertedFaceDiagonal <= maxFace and convertedFaceDiagonal >= (maxFace * maxDeviationLow))
                        or (convertedFaceDiagonal >= maxFace and convertedFaceDiagonal <= (maxFace * maxDeviationHigh))):
                    global numberOfDetectedConvertedFacesCascade2
                    numberOfDetectedConvertedFacesCascade2 += 1
                    faceFoundAfter = True
                    break
            # Add Image with rectangle
            altFaceList.append(convertedImg)
        print("Face found after convertion: " + str(faceFoundAfter) + "\n")

        ####### haarcascade_frontal_alt2.xml ########
        cascade = "haarcascade_frontalface_alt2.xml"
        print("Loading " + cascade)
        # Create the haar cascade
        faceCascade = cv.CascadeClassifier(path + cascade)

        # Detect faces in the image
        faces = faceCascade.detectMultiScale(
            img,
            scaleFactor=float(scaleFactor),
            minNeighbors=int(minNeighbors)
        )
        print("Found " + str(len(faces)) + " face(s) before convertion!")

        faceFoundAfter = False

        if(len(faces) > 0):
            faceDiagonalList = []
            faceIndex = 0
            for (x, y, w, h) in faces:
                faceDiagonal = round(math.sqrt(
                    ((x - (x+w))**2) + ((y - (y+h))**2)), 2)  # Calculate diagonal
                faceDiagonalList.insert(faceIndex, faceDiagonal)
                faceIndex += 1

            # Get biggest detection diagonal
            # We estimate that this is the face
            maxFace = max(faceDiagonalList)

            convertedImg = img.copy()
            overlay = img.copy()

            # rows, columns, color channels
            width, height, channels = img.shape

            # Lay black window over half of the image
            cv.rectangle(overlay, (0, int(height/2)),
                         (width, height), (0, 0, 0), -1)
            alpha = 0.8
            convertedImg = cv.addWeighted(
                overlay, alpha, convertedImg, 1 - alpha, 0)

            # Detect faces in manipulated image
            resitantFaces = faceCascade.detectMultiScale(
                convertedImg,
                scaleFactor=float(scaleFactor),
                minNeighbors=int(minNeighbors)
            )

            for (x, y, w, h) in resitantFaces:
                convertedImg = cv.rectangle(convertedImg, (x, y),
                                            (x+w, y+h), (0, 0, 255), 1)
                convertedFaceDiagonal = round(math.sqrt(
                    ((x - (x+w))**2) + ((y - (y+h))**2)), 2)  # Calculate diagonal of face

                # Print diagonal of found face after convertion
                print("ConvertedFaceDiagonal: " +
                      str(convertedFaceDiagonal))

                # Print diagonal info of found faces
                print(
                    "Min. diagonal " + str(maxDeviationLow) + ": " +
                    str(round(maxFace * maxDeviationLow, 2)) +
                    ", Orig. diagonal: " +
                    str(maxFace) + ", Max. diagonal " +
                    str(maxDeviationHigh) + ": "
                    + str(round(maxFace * maxDeviationHigh, 2)))

                # Check if diagonal of face is in between threshold if true than it's the original face
                if((convertedFaceDiagonal <= maxFace and convertedFaceDiagonal >= (maxFace * maxDeviationLow))
                        or (convertedFaceDiagonal >= maxFace and convertedFaceDiagonal <= (maxFace * maxDeviationHigh))):
                    global numberOfDetectedConvertedFacesCascade3
                    numberOfDetectedConvertedFacesCascade3 += 1
                    faceFoundAfter = True
                    break
            # Add Image with rectangle
            alt2FaceList.append(convertedImg)
        print("Face found after convertion " + str(faceFoundAfter) + "\n")

        ####### haarcascade_frontalface_alt_tree.xml ########
        cascade = "haarcascade_frontalface_alt_tree.xml"
        print("Loading " + cascade)
        # Create the haar cascade
        faceCascade = cv.CascadeClassifier(path + cascade)

        # Detect faces in the image
        faces = faceCascade.detectMultiScale(
            img,
            scaleFactor=float(scaleFactor),
            minNeighbors=int(minNeighbors)
        )
        print("Found " + str(len(faces)) + " face(s) before convertion!")

        faceFoundAfter = False

        if(len(faces) > 0):
            faceDiagonalList = []
            faceIndex = 0
            for (x, y, w, h) in faces:
                faceDiagonal = round(math.sqrt(
                    ((x - (x+w))**2) + ((y - (y+h))**2)), 2)  # Calculate diagonal
                faceDiagonalList.insert(faceIndex, faceDiagonal)
                faceIndex += 1

            # Get biggest detection diagonal
            # We estimate that this is the face
            maxFace = max(faceDiagonalList)

            convertedImg = img.copy()
            overlay = img.copy()

            # rows, columns, color channels
            width, height, channels = img.shape

            # Lay black window over half of the image
            cv.rectangle(overlay, (0, int(height/2)),
                         (width, height), (0, 0, 0), -1)
            alpha = 0.8
            convertedImg = cv.addWeighted(
                overlay, alpha, convertedImg, 1 - alpha, 0)

            # Detect faces in manipulated image
            resitantFaces = faceCascade.detectMultiScale(
                convertedImg,
                scaleFactor=float(scaleFactor),
                minNeighbors=int(minNeighbors)
            )

            for (x, y, w, h) in resitantFaces:
                convertedImg = cv.rectangle(convertedImg, (x, y),
                                            (x+w, y+h), (0, 0, 255), 1)
                convertedFaceDiagonal = round(math.sqrt(
                    ((x - (x+w))**2) + ((y - (y+h))**2)), 2)  # Calculate diagonal of face

                # Print diagonal of found face after convertion
                print("ConvertedFaceDiagonal: " +
                      str(convertedFaceDiagonal))

                # Print diagonal info of found faces
                print(
                    "Min. diagonal " + str(maxDeviationLow) + ": " +
                    str(round(maxFace * maxDeviationLow, 2)) +
                    ", Orig. diagonal: " +
                    str(maxFace) + ", Max. diagonal " +
                    str(maxDeviationHigh) + ": "
                    + str(round(maxFace * maxDeviationHigh, 2)))

                # Check if diagonal of face is in between threshold if true than it's the original face
                if((convertedFaceDiagonal <= maxFace and convertedFaceDiagonal >= (maxFace * maxDeviationLow))
                        or (convertedFaceDiagonal >= maxFace and convertedFaceDiagonal <= (maxFace * maxDeviationHigh))):
                    global numberOfDetectedConvertedFacesCascade4
                    numberOfDetectedConvertedFacesCascade4 += 1
                    faceFoundAfter = True
                    break
            # Add Image with rectangle
            altTreeList.append(convertedImg)
        print("Face found after convertion: " + str(faceFoundAfter) + "\n")
        fileIndex += 1

    print("HaarCascade: Default")
    print(f"{numberOfDetectedConvertedFacesCascade} faces found in converted images.\n")
    print("HaarCascade: Alt")
    print(f"{numberOfDetectedConvertedFacesCascade2} faces found in converted images.\n")
    print("HaarCascade: Alt2")
    print(f"{numberOfDetectedConvertedFacesCascade3} faces found in converted images.\n")
    print("HaarCascade: Alt-Tree")
    print(f"{numberOfDetectedConvertedFacesCascade4} faces found in converted images.\n")


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
        "Number of detected faces (Method: Hide half horizontal) --> Less is better\n" +
        "Number of images: " + str(args["length"]) +
        ",  Calculation time: " + str(round(resultTime, 2)) + " min.\n" + "Sensitivity: " + str(args["minNeighbors"]) + ", Scale-Factor: " + str(args["scale"]))

    minValue = 0
    maxValue = 2500

    # Ax1
    namesCascade = ("Faces found (" + str(numberOfDetectedConvertedFacesCascade) + ")")

    ax1.bar(namesCascade, [numberOfDetectedConvertedFacesCascade])

    ax1.set_ylim(minValue, maxValue)

    ax1.set_title("haarcascade_frontalface_default.xml")

    # Ax2
    namesCascade = ("Faces found (" + str(numberOfDetectedConvertedFacesCascade2) + ")")

    ax2.bar(namesCascade, [numberOfDetectedConvertedFacesCascade2])

    ax2.set_ylim(minValue, maxValue)

    ax2.set_title("haarcascade_frontalface_alt.xml")

    # Ax3
    namesCascade = ("Faces found (" + str(numberOfDetectedConvertedFacesCascade3) + ")")

    ax3.bar(namesCascade, [numberOfDetectedConvertedFacesCascade3])

    ax3.set_ylim(minValue, maxValue)

    ax3.set_title("haarcascade_frontalface_alt2.xml")

    # Ax4
    namesCascade = ("Faces found (" + str(numberOfDetectedConvertedFacesCascade4) + ")")

    ax4.bar(namesCascade, [numberOfDetectedConvertedFacesCascade4])

    ax4.set_ylim(minValue, maxValue)

    ax4.set_title("haarcascade_frontalface_alt_tree.xml")
    plt.show()