package compiladores.AST;

public class SelfNode extends ExpresionNode {

    public SelfNode() {
        super(NodeType.SelfNode, "self");
    }
}
