package symbolTable;

import ast.*;

public class Main {
    public Main() {

    }

    public static void createSymbolTable(SimpleNode root) {
        System.out.println("\n\n----------Starting Symbol Table ----------\n");


        for(int i = 0 ; i < root.jjtGetNumChildren() ; i++) {
            Node child = root.jjtGetChild(i);

            System.out.println(child);
            showChilds(child);
        }
    }


    public static void showChilds(Node node) {
        for(int i = 0 ; i < node.jjtGetNumChildren() ; i++) {
            Node child = node.jjtGetChild(i);
            System.out.println(child);
            showChilds(child);
        }
    }
}