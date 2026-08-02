package compiladores.AST.typeNodes;

import compiladores.semantic.ASTVisitor;

public class PrimitiveTypeNode extends TypeNode {
    private String type;

    public PrimitiveTypeNode(String type) {
        super(type);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
