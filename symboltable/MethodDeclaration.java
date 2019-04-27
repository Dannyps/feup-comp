
package symboltable;

import java.util.HashMap;

import ast.ASTType;
import ast.Node;
import ast.SimpleNode;

public class MethodDeclaration extends Descriptor {
    private boolean isStatic = false, isPublic = false;
    private HashMap<String, VariableDeclaration> allVariables;
    private HashMap<String, VariableDeclaration> allParameters;

    private String type;
    private String name;

    public MethodDeclaration(Node node, String name) {
        super(node, DescriptorType.METHOD);
        String[] splitedName = name.split(" ");
        type = splitedName[0];
        this.name = splitedName[1];
        allVariables = new HashMap<>();
        allParameters = new HashMap<>();
    }

    public Boolean haveVariable(String name) {
        return allVariables.containsKey(name);
    }

    public void addVariable(Node node, String name) {
        VariableDeclaration variableDeclaration = null;
        if(node.jjtGetChild(0) instanceof ASTType) {
            variableDeclaration = new VariableDeclaration(node, name, ((ASTType)node.jjtGetChild(0)).isArray);
        } else {
            variableDeclaration = new VariableDeclaration(node, name, false);
        }
        allVariables.put(variableDeclaration.getName(), variableDeclaration);
    }

    public HashMap<String, VariableDeclaration> getAllVariables() {
        return allVariables;
    }

    public Boolean haveParameter(String name) {
        return allParameters.containsKey(name);
    }

    public void addParameter(Node node, String name) {
        VariableDeclaration variableDeclaration = null;
        if(node.jjtGetChild(0) instanceof ASTType) {
            variableDeclaration = new VariableDeclaration(node, name, ((ASTType)node.jjtGetChild(0)).isArray);
        } else {
            variableDeclaration = new VariableDeclaration(node, name, false);
        }
        allParameters.put(variableDeclaration.getName(), variableDeclaration);
    }

    public HashMap<String, VariableDeclaration> getAllParameters() {
        return allParameters;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}