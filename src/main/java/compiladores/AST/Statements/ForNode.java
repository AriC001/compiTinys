package compiladores.AST.Statements;

import compiladores.AST.*;

public class ForNode extends SentenceNode {
    private VariableDeclarationNode variable;
    private VarNode iterable;
    private SentenceNode body;

    public ForNode(VariableDeclarationNode variable, VarNode iterable, SentenceNode body) {
        super(NodeType.ForNode, "for");
        this.variable = variable;
        this.iterable = iterable;
        this.body = body;
    }

    public VariableDeclarationNode getVariable() {
        return variable;
    }

    public void setVariable(VariableDeclarationNode variable) {
        this.variable = variable;
    }

    public VarNode getIterable() {
        return iterable;
    }

    public void setIterable(VarNode iterable) {
        this.iterable = iterable;
    }

    public SentenceNode getBody() {
        return body;
    }

    public void setBody(SentenceNode body) {
        this.body = body;
    }
}
