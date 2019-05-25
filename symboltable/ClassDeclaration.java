package symboltable;

import java.util.HashMap;

import ast.Node;
import ast.ASTType;;

public class ClassDeclaration extends Descriptor {
    private boolean isStatic = false, isPublic = false;

    private HashMap<String, VariableDeclaration> allVariables;
    private HashMap<String, MethodDeclaration> allMethods;

    private String name;
    private Boolean writedConstructor;

    public ClassDeclaration(Node node, String name) {
        super(node, DescriptorType.CLASS_DECLARATION);

        this.name = name;
        this.writedConstructor = false;

        allVariables = new HashMap<>();
        allMethods = new HashMap<>();
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
        variableDeclaration.setIsClassVariable(true);
        allVariables.put(variableDeclaration.getName(), variableDeclaration);
    }

    public Boolean haveMethod(String name) {
        return allMethods.containsKey(name);
    }

    public Descriptor addMethod(Node node, String name) {
        MethodDeclaration methodDeclaration = new MethodDeclaration(node, name);
        allMethods.put(methodDeclaration.getName(), methodDeclaration);
        return methodDeclaration;
    }

    @Override
    public String getName() {
        return name;
    }

    public HashMap<String, MethodDeclaration> getAllMethods() {
        return allMethods;
    }

    public HashMap<String, VariableDeclaration> getAllVariables() {
        return allVariables;
    }

    /**
     * @return the writedConstructor
     */
    public Boolean getWritedConstructor() {
        return writedConstructor;
    }

    /**
     * @param writedConstructor the writedConstructor to set
     */
    public void setWritedConstructor(Boolean writedConstructor) {
        this.writedConstructor = writedConstructor;
    }    
}