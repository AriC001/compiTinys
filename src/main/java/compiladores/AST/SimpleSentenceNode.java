package compiladores.AST;

import compiladores.AST.Statements.SentenceNode;
import compiladores.semantic.ASTVisitor;

public class SimpleSentenceNode extends SentenceNode {
    private ExpresionNode body;
    public SimpleSentenceNode(ExpresionNode body) {
        super(NodeType.SimpleSentenceNode, "simple");
        this.body = body;
    }

    public ExpresionNode getBody() {
        return body;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
