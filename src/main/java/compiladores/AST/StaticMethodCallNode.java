package compiladores.AST;

import java.util.List;

public class StaticMethodCallNode extends ExpresionNode {

    private String className;

    private MethodCallNode  method;

    private List<ExpresionNode> arguments;

    public StaticMethodCallNode(String className, MethodCallNode method) {
        super(NodeType.StaticMethodCall, className + "." + method.getMethodName());
        this.className = className;
        this.method = method;
    }

    public String getClassName() {
        return className;
    }

    public MethodCallNode getMethod() {
        return method;
    }

    public List<ExpresionNode> getArguments() {
        return arguments;
    }
}
