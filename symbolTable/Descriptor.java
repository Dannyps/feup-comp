public enum descriptorType {METHOD, VARIABLE_DECLARATION, PARAMETER, CLASS_DECLARATION};

public abstract class Descriptor {
    private descriptorType type;

    Descriptor(descriptorType t) {
        type = t;
    }
}