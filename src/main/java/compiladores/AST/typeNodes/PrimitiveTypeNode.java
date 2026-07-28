package compiladores.AST.typeNodes;

public class PrimitiveTypeNode extends TypeNode {
    private String type;

    public PrimitiveTypeNode(String type) {
        super(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
