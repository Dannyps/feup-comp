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
            switch(type) {
                case "Class declaration" : {
                    createClass(child);
                } break;
            }
        }
    }

    public static void showChilds(Node node, Descriptor descriptor) {
        for(int i = 0 ; i < node.jjtGetNumChildren() ; i++) {
            Node child = node.jjtGetChild(i);
            String[] splited = child.toString().split(":");
            String type = splited[0];
            if(type.equals("Variable Declaration")) {
                String name = splited[1];
                createVariable(descriptor, child);
            } else if(type.equals("Method declaration")) {
                String name = splited[1];
                createMethod(child);
            } else if(type.equals("Parameter")) {
                String name = splited[1];
                System.out.println("Parameter");
                createVariable(descriptor, child);
            } else if(type.equals("MethodBody")) {
                createMethodBody(child, descriptor);
            } else if(type.equals("MethodReturn")) {
                checkReturn(child);
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
            ((ClassDeclaration) descriptor).addVariable(node, name);
        } else if(descriptor instanceof MethodDeclaration) {
            ((MethodDeclaration) descriptor).addVariable(node, name);
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
        Descriptor descriptor = classDeclaration.addMethod(node, name);
        showChilds(node, descriptor);
    }

    public static void createMethodBody(Node node, Descriptor descriptor) {
        showChilds(node, descriptor);
    }
}