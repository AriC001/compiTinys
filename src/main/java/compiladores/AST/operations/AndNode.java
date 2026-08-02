package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;

public class AndNode extends ExpresionNode {
    ExpresionNode left;
    ExpresionNode right;

    public AndNode(ExpresionNode left, ExpresionNode right) {
        super(NodeType.AndNode, "&&");
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
