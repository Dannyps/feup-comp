package symbolTable;

import ast.*;

public class VariableDeclaration extends Descriptor {
    private String type;
    private String name;
    private Boolean initiated;

    VariableDeclaration(Node node, String name) {
        super(node, DescriptorType.VARIABLE_DECLARATION);
        String[] splitedName = name.split(" ");
        type = splitedName[0];
        this.name = splitedName[1];
    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Boolean getInitiated() {
        return initiated;
    }

    public void setInitiated(Boolean initiated) {
        this.initiated = initiated;
    }
}