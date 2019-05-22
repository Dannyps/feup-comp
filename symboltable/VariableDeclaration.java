package symboltable;

import ast.Node;

public class VariableDeclaration extends Descriptor {
    private String type;
    private String name;
    private Boolean isClassInstance;
    private Boolean initiated;
    private Boolean isArray;
    private Integer arrayLength;
    private Integer value;
    private Integer index;

    VariableDeclaration(Node node, String name, Boolean isArray, Integer index) {
        super(node, DescriptorType.VARIABLE_DECLARATION);
        String[] splitedName = name.split(" ");
        type = splitedName[0];
        this.name = splitedName[1];
        this.initiated = false;
        this.isArray = isArray;
        this.index = index;
        this.isClassInstance = false;
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

    public void setInitiated(Boolean initiated, Integer arrayLength) {
        this.initiated = initiated;
        this.arrayLength = arrayLength;
    }

    public Boolean getIsArray() {
        return isArray;
    }

    public Integer getArrayLength() {
        return arrayLength;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

    public Boolean getIsClassInstance() {
        return isClassInstance;
    }

    public void setIsClassInstance(Boolean isClassIntance) {
        this.isClassInstance = isClassIntance;
    }
}