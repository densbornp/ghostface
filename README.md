![GhostFace Logo](frontend/src/assets/img/ghostface_logo.png)

GhostFace is a web application designed to safeguard your privacy by making facial detection more challenging for algorithms like Viola-Jones. This project empowers users to upload their facial images and apply techniques that resist detection by the Viola-Jones algorithm.

## Features
- **Image Upload**: Easily upload your facial image to the GhostFace platform.
- **Viola-Jones Resistance**: Apply filters and modifications to make your face detection-resistant against the Viola-Jones algorithm.
- **Privacy Enhancement**: Protect your identity and privacy by implementing alterations that disrupt common facial recognition techniques.
- **Simple Interface**: User-friendly design for effortless navigation and use.

[Screenshots](/docs/Screenshots.md)

#### How It Works
GhostFace utilizes image manipulation techniques to alter facial features. By making subtle changes, GhostFace aims to confuse the Viola-Jones algorithm, thereby making it more challenging for facial recognition systems to detect and identify faces accurately.
The website saves three versions of your uploaded image in the RAM and never writes them out to a disk. The original file, the converted image with the detections visualized and the converted image itself. After ~10 minutes of user inactivity, the website deletes all images assigned to a user.

## Getting Started
To use GhostFace locally, follow these steps:

### Run it manually: 
Download and install the newset [NodeJS](https://nodejs.org/) version
Also install a [Java JDK](https://jdk.java.net/) >= 11 + [Maven](https://maven.apache.org/download.cgi)

Clone the Repository: 
```
git clone https://github.com/densbornp/ghostface.git
```

Navigate to the Directory: cd ghostface

Run the Application: 
1. Switch the directory to ghostface/frontend and execute:
```
npm install -g @angular/cli && npm install && npm run build
```
2. Switch back to ghostface and execute: 
```
mvn clean install
```
3. Switch to ghostface/backend and start the application with `./mvnw quarkus:dev`

### Easy usage via Docker
Donwload and install [Docker](https://www.docker.com/products/docker-desktop/)
Use the docker-compose.yaml file in the GhostFace project to automatically build a new container: `
``` 
docker-compose up -d
```
The application should then be reachable on localhost:5250

## Contributing
Contributions are welcome! If you have suggestions, ideas for improvements, or want to report issues, please feel free to create an issue or submit a pull request. We appreciate your input and support in making GhostFace better.

## Disclaimer
**GhostFace aims to explore techniques to mitigate facial recognition algorithms. However, it's important to note that no system can guarantee complete anonymity or protection against all recognition algorithms. This project is for educational and experimental purposes and should not be relied upon as a foolproof method for privacy.**

# License
This project is licensed under the BSD 3-Clause License - see the [LICENSE](LICENSE) file for details.
