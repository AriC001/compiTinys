package compiladores.AST;

import compiladores.AST.Definitions.DefinitionNode;
import compiladores.semantic.ASTVisitor;
import compiladores.semantic.SemanticAnalyzer;

import java.util.List;

public class ProgramNode extends Node {
    private StartNode body;
    private List<DefinitionNode> clases;

    public ProgramNode(List<DefinitionNode> clases, StartNode body) {
        this.body = body;
        this.clases = clases;
    }

    public List<DefinitionNode> getClases() {
        return clases;
    }

    public void setClases(List<DefinitionNode> clases) {
        this.clases = clases;
    }

    public StartNode getBody() {
        return body;
    }

    public void setBody(StartNode body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "ProgramNode{" + "body=" + (body != null ? body.getClass().getSimpleName() : "null") + '}';
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }

}
