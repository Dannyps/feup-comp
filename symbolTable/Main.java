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
        System.out.println("Checking return");
        System.out.println(parent);
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
        String name = splited[1];        
        System.out.println("Found one variable --> " + node);
        System.out.println("Add variable " + node + " at " + descriptor);
        
        if(descriptor instanceof ClassDeclaration) {
            ClassDeclaration classDeclaration = (ClassDeclaration) descriptor;
            if(!classDeclaration.haveVariable(name)) {
                classDeclaration.addVariable(node, name);
            } else {
                String errorMessage = classDeclaration.getNode() + " already have variable " + node;
                showError(errorMessage);                
            }
        } else if(descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if(!methodDeclaration.haveVariable(name)) {
                methodDeclaration.addVariable(node, name);
            } else {
                String errorMessage = methodDeclaration.getNode() + " already have variable " + node;
                showError(errorMessage);
            }
        }
    }

    public static void createParameter(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String name = splited[1];        
        System.out.println("Found one parameter --> " + node);
        System.out.println("Add variable " + node + " at " + descriptor);
        if(descriptor instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) descriptor;
            if(!methodDeclaration.haveParameter(name)) {
                methodDeclaration.addParameter(node, name);
            } else {
                String errorMessage = methodDeclaration.getNode() + " already have parameter " + node;
                showError(errorMessage);
            }
        }
    }

    public static void createMethod(Node node) {
        String[] splited = node.toString().split(":");
        String name = splited[1]; 
        System.out.println("Found one method --> " + node);
        System.out.println("Add method " + node + " at " + classDeclaration.getNode());
        
        if(!classDeclaration.haveMethod(name)) {
            Descriptor descriptor = classDeclaration.addMethod(node, name);
            showChilds(node, descriptor);
        } else {
            String errorMessage = classDeclaration.getNode() + " already have method " + node;
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

        System.out.println("1 - " + simpleNodes[0].str);
        System.out.println("2 - " + simpleNodes[1].str);

        Integer value = null;
        String name = null;
        String name1 = null;
        String name2 = null;

        try {
            value = Integer.parseInt(simpleNodes[0].str);
        } catch (NumberFormatException e1) {
            try {
                value = Integer.parseInt(simpleNodes[1].str);
            } catch(NumberFormatException e2) {
                name2 = simpleNodes[0].str;
            }
            name1 = simpleNodes[0].str;
        }

        if(name1 != null) {
            name = name1;
        } else if(name2 != null) {
            name = name2;
        } else {
            String errorMessage = "Error on equal";
            showError(errorMessage);
        }

        if(name != null && value != null) {
            VariableDeclaration variableDeclaration = null;
            
            if(methodDeclaration.haveParameter(name)) {
                variableDeclaration = methodDeclaration.getAllParameters().get(name);
            } else if(methodDeclaration.haveVariable(name)) {
                variableDeclaration = methodDeclaration.getAllVariables().get(name);
            } else if(classDeclaration.haveVariable(name)) {
                variableDeclaration = classDeclaration.getAllVariables().get(name);
            } else {
                String errorMessage = "Variable " + name + " dont exist on method " + methodDeclaration.getNode() + " and not in class " + classDeclaration.getNode();
                showError(errorMessage);
            }

            if(variableDeclaration.getType().equals("int")) {
                variableDeclaration.setInitiated(true);
            }
        }
    }

    public static void showError(String errorMessage) {
        System.out.println();
        System.out.println(errorMessage);
        System.out.println();
        System.exit(-1);
    }
}