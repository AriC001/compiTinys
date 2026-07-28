package compiladores.AST.Statements;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public class IfNode extends SentenceNode {
    private ExpresionNode condition;
    private SentenceNode thenBranch;
    private SentenceNode elseBranch;

    public IfNode(ExpresionNode condition, SentenceNode thenBranch, SentenceNode elseBranch) {
        super(NodeType.IfNode, null);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public ExpresionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpresionNode condition) {
        this.condition = condition;
    }

    public SentenceNode getThenBranch() {
        return thenBranch;
    }

    public void setThenBranch(SentenceNode thenBranch) {
        this.thenBranch = thenBranch;
    }

    public SentenceNode getElseBranch() {
        return elseBranch;
    }

    public void setElseBranch(SentenceNode elseBranch) {
        this.elseBranch = elseBranch;
    }
}
