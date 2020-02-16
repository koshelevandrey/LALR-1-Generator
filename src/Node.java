import java.util.ArrayList;

// Класс вершин дерева разбора
public class Node {
    // Родитель для вершины
    private Node parent;
    // Потомки вершины
    public ArrayList<Node> children;
    // Пометка на вершине (нетерминал грамматики)
    private String marker;
    // Токен для вершины
    private Token token;

    public Node(Node parent, ArrayList<Node> children, String marker, Token token) {
        this.parent = parent;
        if (children == null) {
            this.children = new ArrayList<>();
        } else {
            this.children = new ArrayList<>(children);
        }
        this.marker = marker;
        this.token = token;
    }

    public void addChild(Node child) {
        child.parent = this;
        children.add(child);
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public String getMarker() {
        return marker;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String toString() {
        if (marker != null) return marker;
        else {
            return "(" + token.getTag() + ", " + token.getAttr() + ")";
        }
    }
}
