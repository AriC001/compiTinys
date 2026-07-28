package compiladores.AST.Statements;

import compiladores.AST.ExpresionNode;
import compiladores.AST.NodeType;

public class WhileNode extends SentenceNode {
    private ExpresionNode condition;
    private SentenceNode body;

    public WhileNode(ExpresionNode condition, SentenceNode body) {
        super(NodeType.WhileNode, "while");
        this.condition = condition;
        this.body = body;
    }

    public ExpresionNode getCondition() {
        return condition;
    }

    public void setCondition(ExpresionNode condition) {
        this.condition = condition;
    }

    public SentenceNode getBody() {
        return body;
    }

    public void setBody(SentenceNode body) {
        this.body = body;
    }
}
