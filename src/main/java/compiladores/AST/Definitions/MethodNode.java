package compiladores.AST.Definitions;

import compiladores.AST.Statements.BlockNode;
import compiladores.AST.FormalParameterNode;
import compiladores.AST.typeNodes.TypeNode;

import java.util.List;

public class MethodNode extends DefinitionNode {
    private TypeNode returnType;
    private BlockNode body;
    private List<FormalParameterNode> parameters;
    private boolean isStatic;

    public MethodNode(String name, TypeNode returnType, List<FormalParameterNode> paramenters, BlockNode body, boolean isStatic) {
        super(name);
        this.returnType = returnType;
        this.body = body;
        this.parameters = paramenters;
        this.isStatic = isStatic;
    }

    public TypeNode getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeNode returnType) {
        this.returnType = returnType;
    }

    public List<FormalParameterNode> getParameters() {
        return parameters;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public BlockNode getBody() {
        return body;
    }

    public void setBody(BlockNode body) {
        this.body = body;
    }
}
