package compiladores.AST.operations;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;
import compiladores.lexicon.TokenType;
import compiladores.semantic.ASTVisitor;

public class UnaryOperation extends ExpresionNode{
    private ExpresionNode operand;
    private TokenType operator;

    public UnaryOperation(TokenType operator, ExpresionNode operand) {
        super(NodeType.UnaryNode, operator.toString());
        this.operator = operator;
        this.operand = operand;
    }

    public TokenType getOperator() {
        return operator;
    }

    public ExpresionNode getOperand() {
        return operand;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
