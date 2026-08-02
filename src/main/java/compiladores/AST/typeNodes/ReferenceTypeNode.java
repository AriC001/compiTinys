package compiladores.AST.typeNodes;

import compiladores.semantic.ASTVisitor;

public class ReferenceTypeNode extends TypeNode {
    private String name;

    public ReferenceTypeNode(String name) {
        super(name);
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
