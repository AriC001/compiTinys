package compiladores.AST.typeNodes;

import compiladores.AST.Node;
import compiladores.AST.NodeType;
import compiladores.AST.VisibilityNode;

public class AttributeNode extends Node {
    private VisibilityNode visibility;
    private TypeNode type;
    private String name;

    public AttributeNode(VisibilityNode visibility, TypeNode type, String name) {
        this.visibility = visibility;
        this.type = type;
        this.name = name;
    }
}
