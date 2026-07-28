package compiladores.AST.literals;

import compiladores.AST.NodeType;

public class StringLiteralNode extends LiteralNode {
    public StringLiteralNode(String value) {
        super(NodeType.StringLiteral, value);
    }
}
