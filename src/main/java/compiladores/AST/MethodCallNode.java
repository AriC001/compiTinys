package compiladores.AST;

import compiladores.semantic.ASTVisitor;

import java.util.List;

public class MethodCallNode extends ExpresionNode {
    private ExpresionNode parent;
    private String methodName;
    private List<ExpresionNode> arguments;

    public MethodCallNode(ExpresionNode parent, String methodName, List<ExpresionNode> arguments) {
        super(NodeType.MethodCallNode, methodName);
        this.parent = parent;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public ExpresionNode getParent() {
        return parent;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<ExpresionNode> getArguments() {
        return arguments;
    }
    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
