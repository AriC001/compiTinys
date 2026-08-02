package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;

public class OrNode extends ExpresionNode {
    ExpresionNode left;
    ExpresionNode right;

    public OrNode(ExpresionNode left, ExpresionNode right) {
        super(NodeType.OrNode, "||");
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
