
package symboltable;

import java.util.ArrayList;
import java.util.HashMap;

import ast.ASTType;
import ast.Node;
import ast.SimpleNode;

public class MethodDeclaration extends Descriptor {
    private boolean isStatic = false, isPublic = false;
    private HashMap<String, VariableDeclaration> allVariables;
    private ArrayList<VariableDeclaration> allParameters;

    private String type;
    private String name;

    public MethodDeclaration(Node node, String name) {
        super(node, DescriptorType.METHOD);
        String[] splitedName = name.split(" ");
        type = splitedName[0];
        this.name = splitedName[1];
        allVariables = new HashMap<>();
        allParameters = new ArrayList<>();
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
        for(VariableDeclaration variableDeclaration : allParameters) {
            if(variableDeclaration.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addParameter(Node node, String name) {
        VariableDeclaration variableDeclaration = null;
        if(node.jjtGetChild(0) instanceof ASTType) {
            variableDeclaration = new VariableDeclaration(node, name, ((ASTType)node.jjtGetChild(0)).isArray);
        } else {
            variableDeclaration = new VariableDeclaration(node, name, false);
        }
        allParameters.add(variableDeclaration);
    }

    public ArrayList<VariableDeclaration> getAllParameters() {
        return allParameters;
    }

    public VariableDeclaration getParameter(String name) {
        for(VariableDeclaration variableDeclaration : allParameters) {
            if(variableDeclaration.getName().equals(name)) {
                return variableDeclaration;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}