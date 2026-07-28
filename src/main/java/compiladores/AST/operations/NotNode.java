package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public class NotNode extends ExpresionNode{
    private ExpresionNode expresion;

    public NotNode(ExpresionNode expresion) {
        super(NodeType.NotEqualNode, "!");
        this.expresion = expresion;
    }

    public ExpresionNode getExpresion() {
        return expresion;
    }

    public void setExpresion(ExpresionNode expresion) {
        this.expresion = expresion;
    }
}
