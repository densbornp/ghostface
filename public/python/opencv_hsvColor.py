'''
    BSD 3-Clause License

    Copyright (c) 2020, Philip Densborn
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions are met:

    1. Redistributions of source code must retain the above copyright notice, this
       list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright notice,
       this list of conditions and the following disclaimer in the documentation
       and/or other materials provided with the distribution.

    3. Neither the name of the copyright holder nor the names of its
       contributors may be used to endorse or promote products derived from
       this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
    IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
    DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
    DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
    SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
    CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
    OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
    OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
'''

# import the necessary packages
import numpy as np
import argparse
import cv2 as cv
import time
import os
import math


# Create the arguments
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
    ap.add_argument("-col", "--color",
                    help="color of grid")
    return vars(ap.parse_args())

def load_image():
    faceDiagonalList = []
    print("[INFO] loading model...")
    img = cv.imread(args["image"])

    # Load haar cascade
    faceCascade = cv.CascadeClassifier(args["model"])
    scaleFactor = args["scale"]
    minNeighbors = args["minNeighbors"]
    print("Settings: ScaleFactor=" +
          args["scale"] + ", MinNeighbors=" + args["minNeighbors"])

    # Detect faces in the image
    faces = faceCascade.detectMultiScale(
        img,
        scaleFactor=float(scaleFactor),
        minNeighbors=int(minNeighbors),
    )

    convertedImg = img

    path = os.path.abspath(os.path.join(
        os.path.dirname(__file__), os.pardir, "uploads"))

    if(len(faces) > 0):
        i = 0
        for (x, y, w, h) in faces:
            faceDiagonal = round(math.sqrt(
                ((x - (x+w))**2) + ((y - (y+h))**2)), 2)
            faceDiagonalList.insert(i, faceDiagonal)
            i += 1

        print("Found " + str(len(faces)) + " face(s) before convertion!")
        convertedImg = img

        # BGR2HSV
        convertedImg = cv.cvtColor(img, cv.COLOR_BGR2HSV)

        resitantFaces = faceCascade.detectMultiScale(
            convertedImg,
            scaleFactor=float(scaleFactor),
            minNeighbors=int(minNeighbors),
        )

        foundFaces = 0
        maxDeviationHigh = 1.3  # +30%
        maxDeviationLow = 0.7  # -30%

        # Save resistant image
        cv.imwrite(os.path.join(path, str(args["name"])), convertedImg)

        for (x, y, w, h) in resitantFaces:
            face = cv.rectangle(convertedImg, (x, y),
                                (x+w, y+h), (0, 0, 0), 2)
            convertedFaceDiagonal = round(math.sqrt(
                ((x - (x+w))**2) + ((y - (y+h))**2)), 2)

            # Print diagonal of found face after convertion
            print("ConvertedFaceDiagonal: " + str(convertedFaceDiagonal))

            for index in range(0, len(faceDiagonalList)):
                # Print diagonal info of found faces
                print(
                    "Min. diagonal " + str(maxDeviationLow) + ": " +
                    str(round(faceDiagonalList[index] * maxDeviationLow, 2)) +
                    ", Orig. diagonal: " +
                    str(faceDiagonalList[index]) + ", Max. diagonal " +
                    str(maxDeviationHigh) + ": "
                    + str(round(faceDiagonalList[index] * maxDeviationHigh, 2)))

                # Check if diagonal of face is in between threshold if true than it's the original face
                if((convertedFaceDiagonal <= faceDiagonalList[index] and convertedFaceDiagonal >= (faceDiagonalList[index] * maxDeviationLow))
                        or (convertedFaceDiagonal >= faceDiagonalList[index] and convertedFaceDiagonal <= (faceDiagonalList[index] * maxDeviationHigh))):
                    foundFaces += 1
                index += 1

        print("Found {0} face(s) after convertion!".format(foundFaces) + "\n")

    # Save resistant image with rectangle -> Only to show user
    # Downloaded file will have no rectangle
    cv.imwrite(os.path.join(path, str(args["tmp"])), convertedImg)
    return convertedImg


startTime = int(round(time.time() * 1000))
args = init_arguments()
img = load_image()
endTime = int(round(time.time() * 1000))
resultTime = (endTime - startTime) / 1000
print("Calculation time: " + str(resultTime) + " sec.")
print("Script finished")
