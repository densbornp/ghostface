
/*
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
 */
const express = require("express");
const app = express();
const https = require("https");
const http = require("http");
const fs = require("fs");
const port = 3000;
var path = require("path");
var rmdir = require('rimraf');
const fileUpload = require("express-fileupload");
var public = path.join(__dirname, "/public");
var cookieParser = require("cookie-parser");
const spawn = require("child_process").spawn;

// cookieParser to work with cookies
app.use(cookieParser());

// Use static HTML files
app.use(express.static(public));

app.use(fileUpload());
// Hide field for security puposes
app.disable("x-powered-by");

/**
 * Deletes all folders from the "uploads" folder
 * periodically after 1/2 hour
 */
function deleteContent() {
    const directoryUploads = public + "/uploads/";

    fs.readdir(directoryUploads, function (err, files) {
        files.forEach(function (file, index) {
            fs.stat(path.join(directoryUploads, file), function (err, stat) {
                var endTime, now;
                if (err) {
                    return console.error(err);
                }
                now = new Date().getTime();
                endTime = new Date(stat.ctime).getTime() + 1800000; // 30 min.
                if (now > endTime) { // Check if folder is older than 30 min and delete it
                    rmdir(path.join(directoryUploads, file), (err) => {
                        if (err) {
                            console.error(err);
                        } else {
                            console.log(path.basename(file) + " deleted\n");
                        }
                    });
                }
            });
        });
    });
}

// Searches the correct folder with the image files
async function findFile(req, res) {
    try {
        cookie = req.cookies["user_session"];
        const directoryUploads = public + "/uploads/" + cookie + "/";
        image = req.query.image;
        filetype = path.extname(image);

        var data;

        fs.readdirSync(directoryUploads).forEach((file) => {
            if (file.startsWith("res") && file.endsWith(filetype)) {
                data = directoryUploads + file;
            }
        });

        res.download(data, function (error) {
            console.log("Download error: " + error);
        });
    } catch (err) {
        console.log(err);
    }
}

// Delete uploads after certain time
setInterval(function () {
    let date_ob = new Date();

    // current date
    // adjust 0 before single digit date
    let date = ("0" + date_ob.getDate()).slice(-2);
    // current month
    let month = ("0" + (date_ob.getMonth() + 1)).slice(-2);
    // current year
    let year = date_ob.getFullYear();
    // current hours
    let hours = date_ob.getHours();
    // current minutes
    let minutes = ("0" + date_ob.getMinutes()).slice(-2);
    // current seconds
    let seconds = ("0" + date_ob.getSeconds()).slice(-2);

    console.log(
        "Searching for files that can be deleted...    (Date: " +
        year +
        "/" +
        month +
        "/" +
        date +
        ", Time: " +
        hours +
        ":" +
        minutes +
        ":" +
        seconds +
        ")" +
        "\n"
    );
    deleteContent();
}, 300000); // 5min.

// Saves the uploaded file
async function uploadEntryPoint(req, res) {
    try {
        if (!req.files) {
            console.log({
                status: false,
                message: "No image uploaded",
            });
        } else {
            image = req.files.imageFile;
            filetype = path.extname(image.name);
            if(filetype !== '.png' || filetype !== '.PNG' || filetype !== '.jpg' || filetype !== '.JPG'
                || filetype !== '.jpeg' || filetype !== '.JPEG' || filetype !== '.gif' || filetype !== '.GIF') {
                res.status(500).send("The uploaded file is not an image!");
            }

            var basePath = "public/uploads/";

            try {
                fs.mkdirSync(basePath + req.cookies["user_session"]);
            } catch (err) {
                // Folder already exists
            }

            var imagePath =
                basePath +
                req.cookies["user_session"] + "/" +
                "original" +
                filetype;

            image.mv(imagePath);

            console.log(
                "Executing python script (opencv_face_detection.py) with method: haar..."
            );

            // Call python process
            const pythonProcess = spawn("python3", [
                "public/python/opencv_face_detection.py",
                "-m",
                "public/python/haarcascade_frontalface_default.xml",
                "-i",
                imagePath,
                "-t",
                req.cookies["user_session"] + "/tmp" + filetype,
                "-n",
                req.cookies["user_session"] + "/resistant" + filetype,
            ]);

            // Listen to python script output
            pythonProcess.stdout.on("data", function (data) {
                dataString = data.toString();
                console.log(dataString);
                if (dataString.includes("Script finished")) { // Script successfully finished
                    res.status(200).contentType("text/plain").end("File uploaded!");
                    pythonProcess.kill();
                    console.log("Execution finished\n");
                }
            });

            // Listen on python script error output
            pythonProcess.stderr.on("data", (data) => {
                console.log("Error: " + data);
            });
        }
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Adds a black or white grid to the image
async function convertWithGrid(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_grid.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_grid.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
            "-col",
            req.body.options,
        ]);

        // Listen to python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Only shows the edges on the image
async function convertEdges(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_edges.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_edges.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
        ]);

        // Listen to python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Shows the detected faces of a normal, not modified image
async function normalDetection(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_face_detection.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_face_detection.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
        ]);

        // Listen on python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Converts the BGR color space of the image to HSV
async function convertHSV(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_hsvColor.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_hsvColor.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
        ]);

        // Listen on python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Inverts the colors of an image
async function convertBitwiseNot(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_bitwise_not.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_bitwise_not.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
        ]);

        // Listen on python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Inverts the colors of a grayscale image
async function convertBitwiseNotGrayscale(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_bitwise_not_gray.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_bitwise_not_gray.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
        ]);

        // Listen on python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Cartoonizes an image
async function convertCartoon(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_cartoon.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_cartoon.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
        ]);

        // Listen on python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Hides the lower half of an image with a bit of transparency
async function convertHideHalf(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_hide_half.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_hide_half.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
        ]);

        // Listen on python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                console.log("Status 200 send");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Converts white pixels to black pixels and highlights them
async function convertColorHighlighting(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_color_highlighting.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_color_highlighting.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
        ]);

        // Listen on python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

// Removes the red and green channels of an image
async function convertBlueFilter(req, res) {
    try {
        imageName = req.body.imageName;
        filetype = path.extname(imageName);

        var imagePath =
            "public/uploads/" + req.cookies["user_session"] + "/original" + filetype;

        // Set cascade option
        var cascadeOption = null;

        if (req.body.cascade_options == "haar") {
            cascadeOption = "public/python/haarcascade_frontalface_default.xml";
        } else if (req.body.cascade_options == "haar-alt") {
            cascadeOption = "public/python/haarcascade_frontalface_alt.xml";
        } else if (req.body.cascade_options == "haar-alt2") {
            cascadeOption = "public/python/haarcascade_frontalface_alt2.xml";
        } else if (req.body.cascade_options == "haar-alt-tree") {
            cascadeOption = "public/python/haarcascade_frontalface_alt_tree.xml";
        }

        console.log(
            "Executing python script (opencv_blue_filter.py) with method: " +
            req.body.cascade_options +
            "..."
        );

        // Call python process
        const pythonProcess = spawn("python3", [
            "public/python/opencv_blue_filter.py",
            "-m",
            cascadeOption,
            "-i",
            imagePath,
            "-t",
            req.cookies["user_session"] + "/tmp" + filetype,
            "-n",
            req.cookies["user_session"] + "/resistant" + filetype,
            "-mn",
            req.body.minNeighbors,
            "-s",
            req.body.scaleFactor,
        ]);

        // Listen on python script output
        pythonProcess.stdout.on("data", function (data) {
            dataString = data.toString();
            console.log(dataString);
            if (dataString.includes("Script finished")) { // Script successfully finished
                res.status(200).contentType("text/plain").end("File uploaded!");
                pythonProcess.kill();
                console.log("Execution finished\n");
            }
        });

        // Listen on python script error output
        pythonProcess.stderr.on("data", (data) => {
            console.log("Error: " + data);
        });
    } catch (err) {
        console.log(err);
        res.status(500).send(err);
    }
}

/**
 * Handles the cookie requests
 */
app.post("/cookie", (req, res) => {
    // check if client sent cookie
    var cookie = req.cookies.user_session;
    if (cookie === undefined) {
        // no: set a new cookie
        var randomNumber = Math.random().toString(16).substring(2);
        var cookieValue = Buffer.from(randomNumber).toString("base64");
        res.cookie("user_session", cookieValue, {
            secure: true
        });
        console.log("cookie created successfully");
        res.send("Cookie set");
    } else {
        console.log("cookie already set");
        res.send("Cookie already set");
    }
});

/**
 * Handles the upload request
 */
app.post("/upload", async (req, res) => {
    uploadEntryPoint(req, res);
});

/**
 * Handles the conversion requests
 */
app.post("/convert", async (req, res) => {
    var option = req.body.options;

    if (option == "none") {
        normalDetection(req, res);
    } else if (option == "blackgrid" || option == "whitegrid") {
        convertWithGrid(req, res);
    } else if (option == "hide-half") {
        convertHideHalf(req, res);
    } else if (option == "edges") {
        convertEdges(req, res);
    } else if (option == "hsv") {
        convertHSV(req, res);
    } else if (option == "bitwise-not") {
        convertBitwiseNot(req, res);
    } else if (option == "bitwise-not-gray") {
        convertBitwiseNotGrayscale(req, res);
    } else if (option == "cartoon") {
        convertCartoon(req, res);
    } else if (option == "color-highlighting") {
        convertColorHighlighting(req, res);
    } else if (option == "blue-filter") {
        convertBlueFilter(req, res);
    }
});

/**
 * Handles the download requests
 */
app.get("/download", (req, res) => {
    findFile(req, res);
});

// HTTP server
app.listen(port, (req, res) => {
    console.log(`Server listening on port: ${port}...`);
});
