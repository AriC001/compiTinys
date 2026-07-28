package compiladores.AST.literals;

import compiladores.AST.NodeType;

public class NilLiteralNode extends LiteralNode {
    public NilLiteralNode() {
        super(NodeType.NilLiteral, "nil");
    }
}
