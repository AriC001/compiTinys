package compiladores.AST.Definitions;

import compiladores.AST.FormalParameterNode;
import compiladores.AST.Node;
import compiladores.AST.typeNodes.AttributeNode;
import compiladores.AST.typeNodes.TypeNode;

import java.util.List;

public class ClassNode extends DefinitionNode {
    private List<AttributeNode> parameters;
    private Node body;
    private TypeNode type = null;

    public ClassNode(String name,List<AttributeNode> parameters) {
        super(name);
        this.parameters = parameters;
    }
    public ClassNode(String name,TypeNode type,List<AttributeNode> parameters) {
        super(name);
        this.parameters = parameters;
        this.type = type;
    }

    public Node getBody() {
        return body;
    }

    public void setBody(Node body) {
        this.body = body;
    }

    public List<AttributeNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<AttributeNode> parameters) {
        this.parameters = parameters;
    }
}
