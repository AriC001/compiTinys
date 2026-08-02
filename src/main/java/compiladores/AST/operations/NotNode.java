package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;

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
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
