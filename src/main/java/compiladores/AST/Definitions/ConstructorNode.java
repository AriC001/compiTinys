package compiladores.AST.Definitions;

import compiladores.AST.FormalParameterNode;
import compiladores.AST.Statements.BlockNode;
import compiladores.semantic.ASTVisitor;

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

    public String getClassName() {
        return className;
    }

    public BlockNode getBody() {
        return body;
    }

    public List<FormalParameterNode> getParameters() {
        return parameters;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
