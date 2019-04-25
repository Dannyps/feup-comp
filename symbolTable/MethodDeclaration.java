
package symbolTable;

import java.util.HashMap;

import ast.*;

public class MethodDeclaration extends Descriptor {
    private boolean isStatic = false, isPublic = false;
    private HashMap<String, VariableDeclaration> allVariables;
    private HashMap<String, VariableDeclaration> allParameters;

    private String name;


    public MethodDeclaration(Node node, String name) {
        super(node, DescriptorType.METHOD);

        this.name = name;

        allVariables = new HashMap<>();
        allParameters = new HashMap<>();
    }

    public Boolean havaVariable(String name) {
        return allVariables.containsKey(name);
    }

    public void addVariable(Node node, String name) {
        VariableDeclaration variableDescriptor = new VariableDeclaration(node, name);
        allVariables.put(name, variableDescriptor);
    }

    public Boolean havaParameter(String name) {
        return allParameters.containsKey(name);
    }

    public void addParameter(Node node, String name) {
        VariableDeclaration variableDescriptor = new VariableDeclaration(node, name);
        allVariables.put(name, variableDescriptor);
    }

    @Override
    public String getName() {
        return name;
    }
}