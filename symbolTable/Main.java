package symbolTable;

public class Main {
    public static void createSymbolTable(SimpleNode root) {

        System.out.println("here");

        for(int i = 0 ; i < root.jjtGetNumChildren() ; i++) {
            System.out.println(root.jjtGetNumChildren());
        }
    }
}