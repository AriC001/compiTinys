package compiladores.AST.literals;

import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;

public class NilLiteralNode extends LiteralNode {
    public NilLiteralNode() {
        super(NodeType.NilLiteral, "nil");
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
