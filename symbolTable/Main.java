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
                if(classDeclaration != null){
                    System.err.println("Error! Class '" + name + "' already exists!");
                    System.exit(-1);
                }
                createClass(child);
                classDeclaration = new ClassDeclaration(child, name);
            }
        }
    }

    public static void showChilds(Node node, Descriptor descriptor) {
        for(int i = 0 ; i < node.jjtGetNumChildren() ; i++) {
            Node child = node.jjtGetChild(i);
            String[] splited = child.toString().split(":");
            String type = splited[0];
            if(child instanceof ASTVarDeclaration) {
                String name = splited[1];
                createVariable(descriptor, child);
            } else if(child instanceof ASTMethodDeclaration) {
                String name = splited[1];
                createMethod(child);
            } else if(child instanceof ASTMethodParameter) {
                String name = splited[1];
                System.out.println("Parameter");
                createVariable(descriptor, child);
            } else if(child instanceof ASTMethodBody) {
                createMethodBody(child, descriptor);
            } else if(child instanceof ASTMethodReturn) {
                checkReturn(child);
            } else if(child instanceof ASTEqual) {
                initiateVariable(child);
            }
        }
    }

    public static void checkReturn(Node node) {
        System.out.println("checking return");
    }

    public static void createClass(Node node) {
        String[] splited = node.toString().split(":");
        String name = splited[1];
        System.out.println("found one class --> " + node);
        classDeclaration = new ClassDeclaration(node, name);
        showChilds(node, classDeclaration);
    }

    public static void createVariable(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String name = splited[1];        
        System.out.println("found one variable --> " + node);
        System.out.println("Add variable " + node + " at " + descriptor);
        
        if(descriptor instanceof ClassDeclaration) {
            if(!classDeclaration.haveVariable(name)) {
                ((ClassDeclaration) descriptor).addVariable(node, name);
            } else {
                String errorMessage = classDeclaration.getNode() + " already have variable " + node;
                showError(errorMessage);                
            }
        } else if(descriptor instanceof MethodDeclaration) {
            if(!classDeclaration.haveMethod(name)) {
            (   (MethodDeclaration) descriptor).addVariable(node, name);
            } else {
                String errorMessage = classDeclaration.getNode() + " already have method " + node;
                showError(errorMessage);
            }
        }
    }

    public static void createParameter(Descriptor descriptor, Node node) {
        String[] splited = node.toString().split(":");
        String name = splited[1];        
        System.out.println("found one parameter --> " + node);
        System.out.println("Add variable " + node + " at " + descriptor);
        
        if(descriptor instanceof MethodDeclaration) {
            ((MethodDeclaration) descriptor).addParameter(node, name);
        }
    }

    public static void createMethod(Node node) {
        String[] splited = node.toString().split(":");
        String name = splited[1]; 
        System.out.println("found one method --> " + node);
        System.out.println("Add method " + node + " at " + classDeclaration.getNode());
        Descriptor descriptor = classDeclaration.addMethod(node, name);
        showChilds(node, descriptor);
    }

    public static void createMethodBody(Node node, Descriptor descriptor) {
        System.out.println("Create method body at " + node.jjtGetParent());
        showChilds(node, descriptor);
    }

    public static void initiateVariable(Node node) {
        System.out.println("Assign one variable");

        for(Integer i = 0 ; i < node.jjtGetNumChildren() ; i++) {
            Node child = node.jjtGetChild(i);
            System.out.println(((SimpleNode)child.jjtGetChild(0)).str);
        }
    }

    public static void showError(String errorMessage) {
        System.out.println();
        System.out.println(errorMessage);
        System.out.println();
        System.exit(-1);
    }
}