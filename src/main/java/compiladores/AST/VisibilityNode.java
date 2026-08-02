package compiladores.AST;

import compiladores.semantic.ASTVisitor;
import compiladores.semantic.SemanticAnalyzer;

public class VisibilityNode extends Node {
    private String visibility;

    public VisibilityNode(String visibility) {
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
