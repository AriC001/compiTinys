package compiladores.AST.literals;

import compiladores.AST.ExpresionNode;
import compiladores.semantic.ASTVisitor;

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

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
