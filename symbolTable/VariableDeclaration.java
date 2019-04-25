package symbolTable;

import ast.*;

public class VariableDeclaration extends Descriptor {
    private String type;
    private String name;

    VariableDeclaration(Node node, String name) {
        super(node, DescriptorType.VARIABLE_DECLARATION);
        String[] splitedName = name.split(" ");

        this.type = splitedName[0];
        this.name = splitedName[0];
    }

    @Override
    public String getName() {
        return name;
    }
}