
package symbolTable;

import java.util.HashMap;

import ast.*;

public class ClassDeclaration extends Descriptor {
    private boolean isStatic = false, isPublic = false;

    private HashMap<String, VariableDeclaration> allVariables;
    private HashMap<String, MethodDeclaration> allMethods;

    private String name;

    public ClassDeclaration(Node node, String name) {
        super(node, DescriptorType.CLASS_DECLARATION);

        this.name = name;

        allVariables = new HashMap<>();
        allMethods = new HashMap<>();
    }

    public Boolean haveVariable(String name) {
        return allVariables.containsKey(name);
    }

    public void addVariable(Node node, String name) {
        VariableDeclaration variableDescriptor = new VariableDeclaration(node, name);
        allVariables.put(name, variableDescriptor);
    }

    public Boolean haveMethod(String name) {
        return allMethods.containsKey(name);
    }

    public Descriptor addMethod(Node node, String name) {
        MethodDeclaration methodDeclaration = new MethodDeclaration(node, name);
        allMethods.put(name, methodDeclaration);
        return methodDeclaration;
    }

    @Override
    public String getName() {
        return name;
    }
}