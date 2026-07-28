package compiladores.AST.literals;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public abstract class LiteralNode extends ExpresionNode {
    private Object value;

    public LiteralNode(NodeType nodeType, String value) {
        super(nodeType, value);
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
