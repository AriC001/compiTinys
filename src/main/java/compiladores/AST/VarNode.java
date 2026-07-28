package compiladores.AST;

public class VarNode extends ExpresionNode {
    private String name;

    public VarNode(String name) {
        super(NodeType.VarNode, name);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}