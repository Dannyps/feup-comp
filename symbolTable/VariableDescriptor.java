public enum dataType {
    INTEGER, BOOLEAN
};

public class VariableDescriptor extends Descriptor {

    VariableDescriptor(descriptorType type) {
        super(type);
    }

    private dataType dt;
}