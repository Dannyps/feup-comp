package symbolTable;

import java.util.HashMap;
import java.util.HashSet;

import ast.*;

public class Main {

    static ClassDeclaration classDeclaration;

    public Main() {

    }

    public static void createSymbolTable(SimpleNode root) {
        System.out.println("\n\n----------Starting Symbol Table ----------\n");
        for(int i = 0 ; i < root.jjtGetNumChildren() ; i++) {
            Node child = root.jjtGetChild(i);
            String[] splited = child.toString().split(":");
            String type = splited[0];

            if(type.equals("Class declaration")) {
                String name = splited[1];
                createClass(child);
            }
        }
    }

    public static void showChilds(Node node, Descriptor descriptor) {
        for(int i = 0 ; i < node.jjtGetNumChildren() ; i++) {
            Node child = node.jjtGetChild(i);
            if(child instanceof ASTVarDeclaration) {
                createVariable(descriptor, child);
            } else if(child instanceof ASTMethodDeclaration) {
                createMethod(child);
            } else if(child instanceof ASTMethodParameter) {
                System.out.println("Parameter");
                createParameter(descriptor, child);
            } else if(child instanceof ASTMethodBody) {
                createMethodBody(child, descriptor);
            } else if(child instanceof ASTMethodReturn) {
                checkReturn(child, node);
            } else if(child instanceof ASTEqual) {
                initiateVariable(child, descriptor);
            }
        }
    }

    public static void checkReturn(Node node, Node parent) {
        System.out.println("Checking return in method " + parent);
    }

    public static void createClass(Node node) {
        String[] splited = node.toString().split(":");
        String name = splited[1];
        System.out.println("found one class --> " + node);
        if(classDeclaration != null){
            System.err.println("Error! Class '" + name + "' already exists!");
            System.exit(-1);
        }
        classDeclaration = new ClassDeclaration(node, name);
        showChilds(node, classDeclaration);
    }

    public static void createVariable(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];
        System.out.println("Found one variable --> " + node);
        System.out.println("Add variable " + node + " at " + descriptor);
        
        if(descriptor instanceof ClassDeclaration) {
            ClassDeclaration classDeclaration = (ClassDeclaration) descriptor;
            if(!classDeclaration.haveVariable(name)) {
                classDeclaration.addVariable(node, typeAndName);
            } else {
                String errorMessage = classDeclaration.getName() + " already have variable with name " + name;
                showError(errorMessage);                
            }
        } else if(descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if(!methodDeclaration.haveVariable(name) && !methodDeclaration.haveParameter(name)) {
                methodDeclaration.addVariable(node, typeAndName);
            } else {
                String errorMessage = methodDeclaration.getName() + " already have variable with name " + name;
                showError(errorMessage);
            }
        }
    }

    public static void createParameter(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];     
        System.out.println("Found one parameter --> " + node);
        System.out.println("Add variable " + node + " at " + descriptor);
        if(descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if(!methodDeclaration.haveParameter(name)) {
                methodDeclaration.addParameter(node, typeAndName);
            } else {
                String errorMessage = methodDeclaration.getName() + " already have parameter with name " + name;
                showError(errorMessage);
            }
        }
    }

    public static void createMethod(Node node) {
        String[] splited = node.toString().split(":");
        String typeAndName = splited[1];
        String name = typeAndName.split(" ")[1];
        System.out.println("Found one method --> " + node);
        System.out.println("Add method " + node + " at " + classDeclaration.getNode());
        
        if(!classDeclaration.haveMethod(name)) {
            Descriptor descriptor = classDeclaration.addMethod(node, typeAndName);
            showChilds(node, descriptor);
        } else {
            String errorMessage = "Class " + classDeclaration.getName() + " already have method with name " + name;
            showError(errorMessage);
        }
    }

    public static void createMethodBody(Node node, Descriptor descriptor) {
        System.out.println("Create method body at " + node.jjtGetParent());
        showChilds(node, descriptor);
    }

    public static void initiateVariable(Node node, Descriptor descriptor) {
        System.out.println("Initiate one variable");

        MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
        
        Node child = node.jjtGetChild(0);
        SimpleNode[] simpleNodes = new SimpleNode[2];
        simpleNodes[0] = (SimpleNode)node.jjtGetChild(0).jjtGetChild(0);
        simpleNodes[1] = (SimpleNode)node.jjtGetChild(1).jjtGetChild(0);

        String leftSide = simpleNodes[0].str;
        Integer rightSideNumber = null;
        String rightSideString = null;

        try {
            rightSideNumber = Integer.parseInt(simpleNodes[1].str);
        } catch (NumberFormatException e1) {
            rightSideString = simpleNodes[1].str;
        }

        VariableDeclaration variableDeclaration = getLeftSideVariable(methodDeclaration, leftSide);

        if(variableDeclaration.getType().equals("int")) {
            initiateVariableInt(methodDeclaration, variableDeclaration, rightSideString, rightSideNumber, simpleNodes);
        } else if(variableDeclaration.getType().equals("boolean")) {
            initiateVariableBoolean(methodDeclaration, variableDeclaration, rightSideString, simpleNodes);
        }
    }

    private static VariableDeclaration getLeftSideVariable(MethodDeclaration methodDeclaration, String leftSide) {
        VariableDeclaration variableDeclaration = null;
        if(leftSide != null) {
            if(methodDeclaration.haveParameter(leftSide)) {
                variableDeclaration = methodDeclaration.getAllParameters().get(leftSide);
            } else if(methodDeclaration.haveVariable(leftSide)) {
                variableDeclaration = methodDeclaration.getAllVariables().get(leftSide);
            } else if(classDeclaration.haveVariable(leftSide)) {
                variableDeclaration = classDeclaration.getAllVariables().get(leftSide);
            } else {
                String errorMessage = "Variable " + leftSide + " dont exist on method " + methodDeclaration.getNode() + " and not in class " + classDeclaration.getNode();
                showError(errorMessage);
            }
        }
        return variableDeclaration;
    }

    private static void initiateVariableInt(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, String rightSideString, Integer rightSideNumber, SimpleNode[] simpleNodes) {
        if(rightSideNumber != null) {
            variableDeclaration.setInitiated(true);
            return;
        } else if(rightSideString != null) {
            checkIfExistVariableAndYourType(methodDeclaration, variableDeclaration, rightSideString, "int");
        } else if(!simpleNodes[1].op.equals(0)) {
            System.out.println("Found sub operation " + MyConstants.ops[simpleNodes[1].op - 1]);
            initiateVariableIntRecursively(methodDeclaration, variableDeclaration, simpleNodes[1]);
        } else {
            String errorMessage = "In right side need to have a type of int";
            showError(errorMessage);
        }
    }

    private static void initiateVariableBoolean(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, String rightSideString, SimpleNode[] simpleNodes) {
        if(rightSideString != null) {
            if(rightSideString.equals("true") || rightSideString.equals("false")) {
                variableDeclaration.setInitiated(true);
                return;
            } else if(MyConstants.ops[simpleNodes[1].op - 1].equals("&&")) {
                initiateVariableBooleanRecursiverly(methodDeclaration, variableDeclaration, simpleNodes[1]);
            }
            checkIfExistVariableAndYourType(methodDeclaration, variableDeclaration, rightSideString, "boolean");
        } else {
            String errorMessage = "In right dont have a valid argument";
            showError(errorMessage);
        }
    }

    private static void checkIfExistVariableAndYourType(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, String rightSideString, String type) {
        if(classDeclaration.haveMethod(rightSideString)) {
            if(classDeclaration.getAllMethods().get(rightSideString).getType().equals(type)) {
                variableDeclaration.setInitiated(true);
            } else {
                String errorMessage = "Type of " + rightSideString + "is not " + type;
                showError(errorMessage);
            }
        } else if(classDeclaration.haveVariable(rightSideString)) {
            if(classDeclaration.getAllVariables().get(rightSideString).getType().equals(type)) {
                if(classDeclaration.getAllVariables().get(rightSideString).getInitiated()) {
                    variableDeclaration.setInitiated(true);
                } else {
                    String errorMessage = "Variable " + rightSideString + " from class " + classDeclaration.getNode() + "is not initialized";
                    showError(errorMessage);
                }
            } else {
                String errorMessage = "Type of " + rightSideString + "is not " + type;
                showError(errorMessage);
            }
        } else if(methodDeclaration.haveVariable(rightSideString)) {
            if(methodDeclaration.getAllVariables().get(rightSideString).getType().equals(type)) {
                if(methodDeclaration.getAllVariables().get(rightSideString).getInitiated()) {
                    variableDeclaration.setInitiated(true);
                } else {
                    String errorMessage = "Variable " + rightSideString + " from method " + methodDeclaration.getNode() + " is not initialized";
                    showError(errorMessage);
                }
            } else {
                String errorMessage = "Type of " + rightSideString + " is not " + type;
                showError(errorMessage);
            }
        }
    }

    private static void initiateVariableIntRecursively(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode simpleNode) {
        SimpleNode[] childs = new SimpleNode[2];
        childs[0] = (SimpleNode) simpleNode.jjtGetChild(0);
        childs[1] = (SimpleNode) simpleNode.jjtGetChild(1);
        
        String leftSideString = childs[0].str;
        String rightSideString = childs[1].str;

        try {
            Integer leftSideNumber = Integer.parseInt(leftSideString);
            try {
                //dois inteiros
                Integer rightSideNumber = Integer.parseInt(rightSideString);
                variableDeclaration.setInitiated(true);
                return;
            } catch (NumberFormatException e1) {
                //string do lado direito
                if(!childs[1].op.equals(0)) {
                    initiateVariableIntRecursively(methodDeclaration, variableDeclaration, childs[1]);
                } else {
                    checkIfExistVariableAndYourType(methodDeclaration, variableDeclaration, rightSideString, "int");
                }
            }
        } catch (NumberFormatException e1) {
            try {
                //string do lado esquerdo
                Integer rightSideNumber = Integer.parseInt(rightSideString);
                if(!childs[1].op.equals(0)) {
                    initiateVariableIntRecursively(methodDeclaration, variableDeclaration, childs[0]);
                } else {
                    checkIfExistVariableAndYourType(methodDeclaration, variableDeclaration, rightSideString, "int");
                }
            } catch (NumberFormatException e2) {
                //duas strings
                checkIfExistVariableAndYourType(methodDeclaration, variableDeclaration, leftSideString, "int");
                checkIfExistVariableAndYourType(methodDeclaration, variableDeclaration, rightSideString, "int");
            }
        }
    }

    private static void initiateVariableBooleanRecursiverly(MethodDeclaration methodDeclaration, VariableDeclaration variableDeclaration, SimpleNode simpleNode) {
        SimpleNode[] childs = new SimpleNode[2];
        childs[0] = (SimpleNode) simpleNode.jjtGetChild(0);
        childs[1] = (SimpleNode) simpleNode.jjtGetChild(1);
        
        String leftSideString = childs[0].str;
        String rightSideString = childs[1].str;

        if(leftSideString.equals("true") || leftSideString.equals("false")) {
            if(rightSideString.equals("true") || rightSideString.equals("false")) {
                //dois booleanos
                variableDeclaration.setInitiated(true);
            } else if (MyConstants.ops[childs[1].op - 1].equals("&&")) {
                //recursivo a direita
                initiateVariableBooleanRecursiverly(methodDeclaration, variableDeclaration, childs[1]);
            } else {
                //variavel ou metodo a direita
            }
        } else if(rightSideString.equals("true") || rightSideString.equals("false")) {
            if(MyConstants.ops[childs[0].op - 1].equals("&&")) {
                //recursivo a esquerda
            } else {
                //variavel ou metodo a esquerda
                checkIfExistVariableAndYourType(methodDeclaration, variableDeclaration, leftSideString, "boolean");
            }
        } else {
            //variavel ou metodo a esquerda e direita
            checkIfExistVariableAndYourType(methodDeclaration, variableDeclaration, leftSideString, "boolean");
            checkIfExistVariableAndYourType(methodDeclaration, variableDeclaration, rightSideString, "boolean");
        }
    }

    public static void showError(String errorMessage) {
        System.out.println();
        System.out.println(errorMessage);
        System.out.println();
        System.exit(-1);
    }
}