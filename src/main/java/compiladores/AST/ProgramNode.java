package compiladores.AST;

import compiladores.AST.Definitions.DefinitionNode;

import java.util.List;

public class ProgramNode extends Node {
    private Node body;

    public ProgramNode(List<DefinitionNode> clases, Node body) {
        this.body = body;
    }

    public Node getBody() {
        return body;
    }

    public void setBody(Node body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ProgramNode{" + "body=" + (body != null ? body.getClass().getSimpleName() : "null") + '}';
    }

}
