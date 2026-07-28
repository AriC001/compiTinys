package compiladores.AST;

public class ExpresionNode extends Node {
    private NodeType nodeType;
    private String value;

    public ExpresionNode(NodeType nodeType, String value) {
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
}
