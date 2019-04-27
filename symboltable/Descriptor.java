package symboltable;

import ast.Node;

public abstract class Descriptor {
    private DescriptorType descriptorType;
    private Node node;

    Descriptor(Node node, DescriptorType descriptorType) {
        this.node = node;
        this.descriptorType = descriptorType;
    }

    public Node getNode() {
        return node;
    }

    public DescriptorType getDescriptorType() {
        return descriptorType;
    }

    @Override
    public String toString() {
        return node.toString();
    }

    public abstract String getName();
}