package com.digitalsanctum.dockerviz.model;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private List<Node> nodes = new ArrayList<>();
    private List<Link> links = new ArrayList<>();

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public Cluster addNode(Node n) {
        this.nodes.add(n);
        return this;
    }

    public Cluster addLink(Link link) {
        this.links.add(link);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cluster{");
        sb.append("links=").append(links);
        sb.append(", nodes=").append(nodes);
        sb.append('}');
        return sb.toString();
    }
}
