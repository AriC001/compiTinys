package compiladores.AST.typeNodes;

import compiladores.AST.Node;
import compiladores.AST.NodeType;
import compiladores.AST.VisibilityNode;
import compiladores.semantic.ASTVisitor;
import compiladores.semantic.SemanticAnalyzer;

public class AttributeNode extends Node {
    private VisibilityNode visibility;
    private TypeNode type;
    private String name;

    public AttributeNode(VisibilityNode visibility, TypeNode type, String name) {
        this.visibility = visibility;
        this.type = type;
        this.name = name;
    }

    public VisibilityNode getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityNode visibility) {
        this.visibility = visibility;
    }

    public TypeNode getType() {
        return type;
    }

    public void setType(TypeNode type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
