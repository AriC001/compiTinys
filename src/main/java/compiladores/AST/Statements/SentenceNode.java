package compiladores.AST.Statements;

import compiladores.AST.Node;
import compiladores.AST.NodeType;
import compiladores.semantic.ASTVisitor;
import compiladores.semantic.SemanticAnalyzer;

public class SentenceNode extends Node {
    private NodeType nodeType;
    private String value;

    public SentenceNode(NodeType nodeType, String value) {
        this.nodeType = nodeType;
        this.value = value;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
