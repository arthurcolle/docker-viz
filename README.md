# docker-viz
Web based visualization of Docker containers

## build

    mvn clean package
    docker build -t digitalsanctum/docker-viz .

## run

    docker run -d -p 4567:4567 --name dviz --add-host=docker-client:172.17.42.1 digitalsanctum/docker-viz /docker-viz
