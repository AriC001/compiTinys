package compiladores.AST.literals;

import compiladores.AST.ExpresionNode;

import static compiladores.AST.NodeType.IntegerLiteral;

public class IntegerLiteralNode extends LiteralNode{
    private int value;

    public IntegerLiteralNode(int value) {
        super(IntegerLiteral, String.valueOf(value));
        this.value = value;
    }

    public int getIntValue() {
        return value;
    }
}
