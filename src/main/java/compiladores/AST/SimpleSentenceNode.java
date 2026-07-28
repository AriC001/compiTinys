package compiladores.AST;

import compiladores.AST.Statements.SentenceNode;

public class SimpleSentenceNode extends SentenceNode {
    private ExpresionNode body;
    public SimpleSentenceNode(ExpresionNode body) {
        super(NodeType.SimpleSentenceNode, "simple");
        this.body = body;
    }

    public ExpresionNode getBody() {
        return body;
    }
}
