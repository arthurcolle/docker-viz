package com.digitalsanctum.dockerviz;

import com.digitalsanctum.dockerviz.model.Cluster;
import com.digitalsanctum.dockerviz.model.Link;
import com.digitalsanctum.dockerviz.model.Node;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerInfo;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

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


        get("/containers/:id/log", (request, response) -> {
            LogStream logStream = docker.logs(request.params(":id"), DockerClient.LogsParameter.STDOUT);
            String log = logStream.readFully();
            logStream.close();
            return "<pre>" + log + "</pre>";
        });


        post("/containers/:id/exec", (request, response) -> {
            String[] cmd = request.queryParams("cmd").split(" ");

            // todo handle quoted cmd args like runuser -l jive -c "jive status -v"

            String execId = docker.execCreate(request.params(":id"), cmd,
                    DockerClient.ExecParameter.STDOUT, DockerClient.ExecParameter.STDERR);
            String result = "";
            try (LogStream stream = docker.execStart(execId)) {
                result = stream.readFully();
            }

            return "<pre>" + result + "</pre>";
        });


        get("/containers/:id/delete", (request, response) -> {
            docker.removeContainer(request.params(":id"));
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
                c.addNode(new Node(container));



                if (container.names().size() > 1) {
                    String sourceName = container.names().get(0).substring(1);

                    List<String> names = container.names();
                    for (int i1 = 0; i1 < names.size(); i1++) {
                        if (i1 == 0) {
                            continue;
                        }
                        String name = names.get(i1);
                        String targetName = name.substring(0, name.indexOf('/', 1)).substring(1);
                        c.addLink(new Link(sourceName, targetName));
                    }
                }
            }
            return c;

        }, new JsonTransformer());
    }
}
