package com.digitalsanctum.dockerviz.model;

public class Link {

    private int source;
    private int target;
    private int value = 1;

    public Link(int source, int target) {
        this.source = source;
        this.target = target;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Link{");
        sb.append("source=").append(source);
        sb.append(", target=").append(target);
        sb.append(", value=").append(value);
        sb.append('}');
        return sb.toString();
    }
}
