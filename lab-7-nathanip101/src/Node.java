public class Node {
    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public Node getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(Node prevNode) {
        this.prevNode = prevNode;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    private int g;
    private int h;
    private int f; // == g + h
    private Node prevNode;
    private Point pos;

    public Node(int g, int h, Node prevNode, Point pos) {
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.prevNode = prevNode;
        this.pos = pos;
    }

    public boolean containsPoint(Point p) {
        return this.pos == p;
    }

    @Override
    public boolean equals(Object node) {
        return node instanceof Node && this.getPos().equals(((Node) node).getPos());
    }
}
