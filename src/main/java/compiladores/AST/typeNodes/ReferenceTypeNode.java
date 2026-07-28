package compiladores.AST.typeNodes;

public class ReferenceTypeNode extends TypeNode {
    private String name;

    public ReferenceTypeNode(String name) {
        super(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
