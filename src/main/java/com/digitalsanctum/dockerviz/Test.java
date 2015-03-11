package com.digitalsanctum.dockerviz;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;

public class Test {


    public static void main(String[] args) throws Exception {
        final DockerClient docker = DefaultDockerClient
                .fromEnv()
//                .builder()
//                .uri(URI.create("http://docker-client:2376"))
                .build();

//        List<Image> images = docker.listImages();
//        System.out.println(images);

        LogStream logStream = docker.logs("34bd8e22f63fc4c257e5e4b477dcce591e0a6b525fc9a0a151fbb5fae6344a43", DockerClient.LogsParameter.STDOUT);
        String log = logStream.readFully();

        System.out.println(log);



    }
}
