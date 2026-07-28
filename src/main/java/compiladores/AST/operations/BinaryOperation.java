package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;
import compiladores.lexicon.TokenType;

public class BinaryOperation extends ExpresionNode {
    private ExpresionNode left;
    private ExpresionNode right;
    private TokenType operator;

    public BinaryOperation(TokenType operator, ExpresionNode left, ExpresionNode right) {
        super(NodeType.BinaryNode, operator.toString());
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public TokenType getOperator() {
        return operator;
    }

    public ExpresionNode getLeft() {
        return left;
    }

    public ExpresionNode getRight() {
        return right;
    }
}
