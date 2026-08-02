package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;

public class DivNode extends ExpresionNode{
    ExpresionNode left;
    ExpresionNode right;

    public DivNode(ExpresionNode left, ExpresionNode right) {
        super(NodeType.DivNode,"/");
        this.left = left;
        this.right = right;
    }

    public ExpresionNode getLeft() {
        return left;
    }

    public ExpresionNode getRight() {
        return right;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
