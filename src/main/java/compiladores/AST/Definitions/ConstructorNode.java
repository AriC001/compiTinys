package compiladores.AST.Definitions;

import compiladores.AST.FormalParameterNode;
import compiladores.AST.Statements.BlockNode;

import java.util.List;

public class ConstructorNode extends DefinitionNode {
    private String className;
    private List<FormalParameterNode> parameters;
    private BlockNode body;

    public ConstructorNode(String name, List<FormalParameterNode> parameters, BlockNode body) {
        super(name);
        this.parameters = parameters;
        this.body = body;
        this.className = name;
    }
}
