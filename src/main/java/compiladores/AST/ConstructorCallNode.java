package compiladores.AST;

import java.util.List;

public class ConstructorCallNode extends ExpresionNode {
    private String className;
    private List<ExpresionNode> arguments;

    public ConstructorCallNode(String className, List<ExpresionNode> arguments) {

        super(NodeType.ConstructorCallNode, className);

        this.className = className;
        this.arguments = arguments;
    }

}