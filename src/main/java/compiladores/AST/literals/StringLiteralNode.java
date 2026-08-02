package compiladores.AST.literals;

import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;

public class StringLiteralNode extends LiteralNode {
    public StringLiteralNode(String value) {
        super(NodeType.StringLiteral, value);
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
