package com.digitalsanctum.dockerviz.model;

import com.spotify.docker.client.messages.Container;

public class Node {

    private String name;
    private String image;
    private int group = 1;

    private Container container;

    public Node(Container container) {
        this.container = container;
        this.name = container.names().get(0).substring(1);
        this.image = imageFromContainer(container);
    }

    public Node(String name) {
        this.name = name;
    }

    public Node(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public Node(int group, String name) {
        this.group = group;
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    private String imageFromContainer(Container container) {
        String image = container.image();
        if (image.startsWith("jive/webapp")) {
            return "jive.png";
        }
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Node{");
        sb.append("group=").append(group);
        sb.append(", name='").append(name).append('\'');
        sb.append(", image='").append(image).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
