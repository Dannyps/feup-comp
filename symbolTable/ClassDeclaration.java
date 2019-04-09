
public class ClassDeclaration extends Descriptor {
    private boolean isStatic = false, isPublic = false;
    private String name;
    
    ClassDeclaration(SimpleNode c){
        super(descriptorType.CLASS_DECLARATION);

    }
}