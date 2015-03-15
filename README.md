# docker-viz
Web based visualization of Docker containers

## Build

    mvn clean package
    docker build -t digitalsanctum/docker-viz .

## Run

    docker run -d -p 4567:4567 --name dviz --add-host=docker-client:172.17.42.1 digitalsanctum/docker-viz /docker-viz


Open browser to http://docker-client:4567 to see the containers running on your docker host.


## Notable Dependencies

- d3.js (https://github.com/mbostock/d3) JavaScript visualization library
- docker-client (https://github.com/spotify/docker-client) Docker client for Java
- sparkjava (https://github.com/perwendel/spark) lightweight Java framework inspired by Sinatra