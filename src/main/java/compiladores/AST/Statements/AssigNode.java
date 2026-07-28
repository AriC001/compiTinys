package compiladores.AST.Statements;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public class AssigNode extends SentenceNode {
    private ExpresionNode variable;
    private ExpresionNode expresion;

    public AssigNode(ExpresionNode variable, ExpresionNode expresion) {
        super(NodeType.AsigNode, "=");
        this.variable = variable;
        this.expresion = expresion;
    }

    public ExpresionNode getVariable() {
        return variable;
    }

    public void setVariable(ExpresionNode variable) {
        this.variable = variable;
    }

    public ExpresionNode getExpresion() {
        return expresion;
    }

    public void setExpresion(ExpresionNode expresion) {
        this.expresion = expresion;
    }
}
