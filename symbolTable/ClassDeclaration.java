
package symbolTable;

import ast.*;

public class ClassDeclaration extends Descriptor {
    private boolean isStatic = false, isPublic = false;
    private String name;
    private Node node;

    public ClassDeclaration(Node node) {
        super(descriptorType.CLASS_DECLARATION);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}