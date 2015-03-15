package com.digitalsanctum.dockerviz;

import com.digitalsanctum.dockerviz.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.SparkBase.staticFileLocation;

public class Main {

    static final DockerClient docker = DefaultDockerClient.builder()
            .uri(URI.create("http://docker-client:2376"))
            .build();

    public static void main(String[] args) throws Exception {


        staticFileLocation("/public");

        post("/containers", (request, response) -> {
            ContainerConfig config = ContainerConfig.builder()
                    .image("jive/data")
                    .build();
            ContainerCreation creation = docker.createContainer(config);
            String id = creation.id();
            docker.startContainer(id);
            return id;
        });

        get("/containers/:id/logs", (request, response) -> {
            LogStream logStream = null;
            try {
                logStream = docker.logs(request.params(":id"), DockerClient.LogsParameter.STDOUT);
            } catch (DockerException | InterruptedException e) {
                e.printStackTrace();
            }
            String log = null;
            if (logStream != null) {
                log = logStream.readFully();
                logStream.close();
            }

            return new ModelAndView(new LogResponse(log), "logs.mustache");
        }, new MustacheTemplateEngine());


        post("/containers/:id/exec", "application/json", (request, response) -> {

            Gson gson = new Gson();
            CommandRequest commandRequest = gson.fromJson(request.body(), CommandRequest.class);
            // todo handle quoted cmd args like runuser -l jive -c "jive status -v"

            String execId = null;
            try {
                execId = docker.execCreate(request.params(":id"), new String[]{commandRequest.getCmd()},
                        DockerClient.ExecParameter.STDOUT, DockerClient.ExecParameter.STDERR);
            } catch (DockerException | InterruptedException e) {
                e.printStackTrace();
            }

            String result = "";
            try {
                LogStream stream = docker.execStart(execId);
                result = stream.readFully();
            } catch (InterruptedException | DockerException e) {
                e.printStackTrace();
            }

            return new ModelAndView(new LogResponse(result), "logs.mustache");
        }, new MustacheTemplateEngine());


        get("/containers/:id/delete", (request, response) -> {
            String id = request.params(":id");
            docker.killContainer(id);
            docker.removeContainer(id);
            return "ok";
        });


        get("/containers/:id", (request, response) -> {
            ContainerInfo info = null;
            try {
                info = docker.inspectContainer(request.params(":id"));
            } catch (DockerException | InterruptedException e) {
                e.printStackTrace();
            }
            return new ModelAndView(info, "containerInfo.mustache");

        }, new MustacheTemplateEngine());


        get("/cluster", (request, response) -> {

            List<Container> containers = docker.listContainers();

            Cluster c = new Cluster();
            for (Container container : containers) {
                Node node = new Node(container);
                if (container.names().size() > 1) {
                    List<String> names = container.names();
                    for (int i1 = 0; i1 < names.size(); i1++) {
                        if (i1 == 0) {
                            continue;
                        }
                        String sourceName = container.names().get(0).substring(1);
                        String name = names.get(i1);
                        String targetName = name.substring(0, name.indexOf('/', 1)).substring(1);
                        c.addLink(new Link(sourceName, targetName));
                    }
                }
                c.addNode(node);
            }
            return c;

        }, new JsonTransformer());
    }
}
