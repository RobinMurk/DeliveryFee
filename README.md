# Delivery Fee API

This service acts as a Web API, that has multiple open endpoints for use.

> **Note:** This is a work in progress.

## How to Use Locally:

1. Fork the repository.
2. Build the project.
3. Run the `MainApplication`. It will run the Tomcat web server on port 8080.
4. The H2 database gui is available at `/h2-gui`

## To Build the Docker Image:

> **Note:** Docker must be installed on the machine.

1. Run the command `./gradlew clean bootJar`.
2. Run the command `docker build -t <image name here> .`.
3. You can build your own image or pull the image from DockerHub: [DockerHub Repository](https://hub.docker.com/repository/docker/widyrob/servicefee/general).

To run the image, run `docker run -dit -p 8080:8080 --name <container name here> <image name here>`.
