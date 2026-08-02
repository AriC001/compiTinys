package compiladores.AST.Statements;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;

public class RetNode extends SentenceNode {
    private ExpresionNode expresion;

    public RetNode(NodeType nodeType, String value, ExpresionNode expresion) {
        super(nodeType, value);
        this.expresion = expresion;
    }

    public ExpresionNode getExpresion() {
        return expresion;
    }

    public void setExpresion(ExpresionNode expresion) {
        this.expresion = expresion;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
