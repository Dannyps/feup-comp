
package symboltable;

import java.util.ArrayList;
import java.util.HashMap;

import ast.ASTMethodDeclaration;
import ast.ASTType;
import ast.Node;
import ast.ASTMainDeclaration;

public class MethodDeclaration extends Descriptor {
    private boolean isStatic = false, isPublic = false;
    private HashMap<String, VariableDeclaration> allVariables;
    private ArrayList<VariableDeclaration> allParameters;

    private String type;
    private String name;

    private String returnType;

    private Boolean writedLocals;

    public MethodDeclaration(Node node, String name) {
        super(node, DescriptorType.METHOD);
        String[] splitedName = name.split(" ");
        type = splitedName[0];
        if (name.contains("Main declaration")) {
            this.name = "main()";
        } else {
            this.name = splitedName[1];
        }
        allVariables = new HashMap<>();
        allParameters = new ArrayList<>();
        if (node instanceof ASTMainDeclaration) { // main always returns void
            this.returnType = "void";
        } else {
            this.returnType = ((ASTMethodDeclaration) node).getReturnType();
        }
        this.writedLocals = true;
    }

    public Boolean haveVariable(String name) {
        return allVariables.containsKey(name);
    }

    public void addVariable(Node node, String name) {
        VariableDeclaration variableDeclaration = null;
        if (node.jjtGetChild(0) instanceof ASTType) {
            variableDeclaration = new VariableDeclaration(node, name, ((ASTType) node.jjtGetChild(0)).isArray, allVariables.size());
        } else {
            variableDeclaration = new VariableDeclaration(node, name, false, allVariables.size());
        }
        allVariables.put(variableDeclaration.getName(), variableDeclaration);
    }

    public HashMap<String, VariableDeclaration> getAllVariables() {
        return allVariables;
    }

    public Boolean haveParameter(String name) {
        for (VariableDeclaration variableDeclaration : allParameters) {
            if (variableDeclaration.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addParameter(Node node, String name) {
        VariableDeclaration variableDeclaration = null;
        if (node.jjtGetChild(0) instanceof ASTType) {
            variableDeclaration = new VariableDeclaration(node, name, ((ASTType) node.jjtGetChild(0)).isArray, allParameters.size());
        } else {
            variableDeclaration = new VariableDeclaration(node, name, false, allParameters.size());
        }
        allParameters.add(variableDeclaration);
    }

    public ArrayList<VariableDeclaration> getAllParameters() {
        return allParameters;
    }

    public VariableDeclaration getParameter(String name) {
        for (VariableDeclaration variableDeclaration : allParameters) {
            if (variableDeclaration.getName().equals(name)) {
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

    /**
     * @return the returnType
     */
    public String getReturnType() {
        return returnType;
    }

    public ArrayList<VariableDeclaration> getAllParametersAndVariables() {
        ArrayList<VariableDeclaration> temp = new ArrayList<>();
        temp.addAll(allParameters);
        temp.addAll(allVariables.values());

        return temp;
    }

    public Boolean getWritedLocals() {
        return writedLocals;
    }

    public void setWritedLocals(Boolean writedLocals) {
        this.writedLocals = writedLocals;
    }
}