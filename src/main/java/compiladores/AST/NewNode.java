package compiladores.AST;

import java.util.List;

public class NewNode extends ExpresionNode {
    private String className;
    private List<ExpresionNode> arguments;

    public NewNode(String className, List<ExpresionNode> arguments) {
        super(NodeType.NewNode, className);
        this.className = className;
        this.arguments = arguments;
    }

    public String getClassName() {
        return className;
    }

    public List<ExpresionNode> getArguments() {
        return arguments;
    }
}
