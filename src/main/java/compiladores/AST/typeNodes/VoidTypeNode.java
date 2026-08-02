package compiladores.AST.typeNodes;

import compiladores.semantic.ASTVisitor;

public class VoidTypeNode extends TypeNode {
    public VoidTypeNode() {
        super("void");
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
