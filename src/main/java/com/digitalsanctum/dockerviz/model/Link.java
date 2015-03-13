package com.digitalsanctum.dockerviz.model;

public class Link {

    private String source;
    private String target;
    private int value = 20;

    public Link(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
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
