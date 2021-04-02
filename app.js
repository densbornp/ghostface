const express = require("express");
const app = express();
const fs = require("fs");
const port = 9000;
var path = require("path");
var rmdir = require('rimraf');
const fileUpload = require("express-fileupload");
var public = path.join(__dirname, "/public");
var cookieParser = require("cookie-parser");
const { Session } = require("inspector");
const { nextTick } = require("process");
const spawn = require("child_process").spawn;

// cookieParser to work with cookies
app.use(cookieParser());

app.use(express.static(public));
app.use(fileUpload());
// Hide field for security puposes
app.disable("x-powered-by");

/**
 * Deletes all files from the "uploads" folder
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
        if (now > endTime) {
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

      pythonProcess.stdout.on("data", function (data) {
        dataString = data.toString();
        console.log(dataString);
        if (dataString.includes("Script finished")) {
          res.status(200).contentType("text/plain").end("File uploaded!");
          pythonProcess.kill();
          console.log("Execution finished\n");
        }
      });

      pythonProcess.stderr.on("data", (data) => {
        console.log("Error: " + data);
      });
    }
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        console.log("Status 200 send");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

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

    pythonProcess.stdout.on("data", function (data) {
      dataString = data.toString();
      console.log(dataString);
      if (dataString.includes("Script finished")) {
        res.status(200).contentType("text/plain").end("File uploaded!");
        pythonProcess.kill();
        console.log("Execution finished\n");
      }
    });

    pythonProcess.stderr.on("data", (data) => {
      console.log("Error: " + data);
    });
  } catch (err) {
    console.log(err);
    res.status(500).send(err);
  }
}

app.post("/cookie", (req, res) => {
  // check if client sent cookie
  var cookie = req.cookies.user_session;
  if (cookie === undefined) {
    // no: set a new cookie
    var randomNumber = Math.random().toString(16).substring(2);
    var cookieValue = Buffer.from(randomNumber).toString("base64");
    res.cookie("user_session", cookieValue, {
      
    });
    console.log("cookie created successfully");
    res.send("Cookie set");
  } else {
    console.log("cookie already set");
    res.send("Cookie already set");
  }
});

app.post("/upload", async (req, res) => {
  uploadEntryPoint(req, res);
});

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

app.get("/download", (req, res) => {
  findFile(req, res);
});

app.listen(port, '0.0.0.0', (req, res) => {
  console.log(`Server listening on port ${port}...`);
});
