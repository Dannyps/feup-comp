package symbolTable;
public abstract class Descriptor {
    private descriptorType type;

    Descriptor(descriptorType t) {
        type = t;
    }
}