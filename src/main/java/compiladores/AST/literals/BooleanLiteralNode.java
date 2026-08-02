package compiladores.AST.literals;

import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;

public class BooleanLiteralNode extends LiteralNode{
    private boolean value;

    public BooleanLiteralNode(boolean value) {
        super(NodeType.BooleanLiteral, String.valueOf(value));
        this.value = value;
    }

    public boolean getBooleanValue() {
        return value;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
