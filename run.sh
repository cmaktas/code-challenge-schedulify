#!/bin/bash

# Build the Docker image
docker build -t schedulify:latest .

# Run the Docker container
docker run -d -p 8080:8080 --name schedulify_container schedulify:latest
