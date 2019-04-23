package symbolTable;

import java.util.HashMap;
import java.util.HashSet;

import ast.*;

public class Main {

    static HashMap<String, ClassDeclaration> allClasses = new HashMap<>();

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
                    System.out.println("found one class");
                    String name = splited[1];
                    if(allClasses.get(name)!=null){
                        System.err.println("Error! Class '" + name + "' already exists!");
                        System.exit(-1);
                    }
                    ClassDeclaration classDeclaration = new ClassDeclaration(child);
                    allClasses.put(name, classDeclaration);
                } break;
            }

            System.out.println(child);
            showChilds(child);
        }
    }


    public static void showChilds(Node node) {
        for(int i = 0 ; i < node.jjtGetNumChildren() ; i++) {
            Node child = node.jjtGetChild(i);

            String[] splited = child.toString().split(":");

            String type = splited[0];

            switch(type) {
                case "Variable Declaration" : {
                    System.out.println("found one class");
                    String name = splited[1];
                    ClassDeclaration classDeclaration = new ClassDeclaration(child);
                    allClasses.put(name, classDeclaration);
                } break;
            }

            System.out.println(child);
            showChilds(child);
        }
    }
}