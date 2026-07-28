package compiladores.AST;

public class VisibilityNode extends Node {
    private String visibility;

    public VisibilityNode(String visibility) {
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
