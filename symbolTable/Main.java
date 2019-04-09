public class Main {
    public static void main(SimpleNode root) {

        System.out.println("here");

        for(int i = 0 ; i < root.jjtGetNumChildren() ; i++) {
            System.out.println(root.jjtGetNumChildren());
        }
    }
}