import java.util.ArrayList;

public class Node {

    private ArrayList<Car> data;
    private String label;
    private boolean isLeaf;
    private Node [] children;
    private String attribute;

    public Node(ArrayList<Car> data, String label, boolean isLeaf, Node[] children, String attribute) {
        this.data = data;
        this.label = label;
        this.isLeaf = isLeaf;
        this.children = children;
        this.attribute = attribute;
    }

    //-----------------Getters-----------------//
    public ArrayList<Car> getData() {
        return data;
    }

    public String getLabel() {
        return label;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public Node[] getChildren() {
        return children;
    }

    public String getAttribute() {
        return attribute;
    }
}
