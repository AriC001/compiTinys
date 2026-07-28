package compiladores.AST.literals;

import compiladores.AST.NodeType;

public class BooleanLiteralNode extends LiteralNode{
    private boolean value;

    public BooleanLiteralNode(boolean value) {
        super(NodeType.BooleanLiteral, String.valueOf(value));
        this.value = value;
    }

    public boolean getBooleanValue() {
        return value;
    }
}
