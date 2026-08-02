package compiladores.AST.Definitions;

import compiladores.AST.FormalParameterNode;
import compiladores.AST.Node;
import compiladores.AST.typeNodes.AttributeNode;
import compiladores.AST.typeNodes.ReferenceTypeNode;
import compiladores.AST.typeNodes.TypeNode;
import compiladores.semantic.ASTVisitor;

import java.util.List;

public class ClassNode extends DefinitionNode {
    private List<AttributeNode> parameters;
    private TypeNode type = null;

    /*
    public ClassNode(String name,List<AttributeNode> parameters) {
        super(name);
        this.parameters = parameters;
    }*/
    public ClassNode(String name,TypeNode type,List<AttributeNode> parameters) {
        super(name);
        this.parameters = parameters;
        this.type = type;
    }

    public List<AttributeNode> getParameters() {
        return parameters;
    }

    public void setParameters(List<AttributeNode> parameters) {
        this.parameters = parameters;
    }

    public String getParent() {
        if(type != null){
            return type.getName();
        }else{
            return "Object";
        }
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
