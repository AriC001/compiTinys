package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public class AddNode extends ExpresionNode {
    private ExpresionNode left;
    private ExpresionNode right;

    public AddNode(String value, ExpresionNode left, ExpresionNode right) {
        super(NodeType.AddNode, value);
        this.left = left;
        this.right = right;
    }

    public ExpresionNode getLeft() {
        return left;
    }

    public void setLeft(ExpresionNode left) {
        this.left = left;
    }

    public ExpresionNode getRight() {
        return right;
    }

    public void setRight(ExpresionNode right) {
        this.right = right;
    }

    // a validar con el manual
    public Boolean validate(){
        if(left.getNodeType() == NodeType.IntegerLiteral && right.getNodeType() == NodeType.IntegerLiteral){
            this.setNodeType(NodeType.IntegerLiteral);
            return true;
        } else if(left.getNodeType() == NodeType.FloatLiteral && right.getNodeType() == NodeType.FloatLiteral){
            this.setNodeType(NodeType.FloatLiteral);
            return true;
        } else if((left.getNodeType() == NodeType.IntegerLiteral && right.getNodeType() == NodeType.FloatLiteral) || (left.getNodeType() == NodeType.FloatLiteral && right.getNodeType() == NodeType.IntegerLiteral)){
            this.setNodeType(NodeType.FloatLiteral);
            return true;
        } else {
            return false;
        }
    }
}
