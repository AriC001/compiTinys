package compiladores.AST;

import compiladores.semantic.ASTVisitor;

import java.util.List;

public class StaticMethodCallNode extends ExpresionNode {

    private String className;

    private String methodName;

    private List<ExpresionNode> arguments;

    public StaticMethodCallNode(String className, String methodName, List<ExpresionNode> arguments) {
        super(NodeType.StaticMethodCall, className + "." + methodName);
        this.className = className;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public String getClassName() {
        return className;
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
