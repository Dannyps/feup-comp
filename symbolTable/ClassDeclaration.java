
package symbolTable;

public class ClassDeclaration extends Descriptor {
    private boolean isStatic = false, isPublic = false;
    private String name;

    ClassDeclaration(ast.Node c){
        super(descriptorType.CLASS_DECLARATION);
    }
}