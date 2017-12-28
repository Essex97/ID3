import java.util.ArrayList;

public class Node {

    private ArrayList<Car> data;
    private String label;
    private boolean isLeaf;
    private ArrayList<Node> children = new ArrayList<Node>();
    private String attribute;
    private String valueOfAtrr;

    public Node(ArrayList<Car> data, String label, boolean isLeaf, String attribute, String valueOfAtrr) {
        this.data = data;
        this.label = label;
        this.isLeaf = isLeaf;
        this.attribute = attribute;
        this.valueOfAtrr = valueOfAtrr;
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

    public ArrayList<Node> getChildren() {
        return children;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getValueOfAtrr() {
        return valueOfAtrr;
    }

    //-----------------------------------------//

    //-----------------Setters-----------------//

    public void setData(ArrayList<Car> data) {
        this.data = data;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLeaf(boolean leaf) {
        isLeaf = leaf;
    }

    public void setChildren(Node children) {
        this.children.add(children);
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setValueOfAtrr(String valueOfAtrr) {
        this.valueOfAtrr = valueOfAtrr;
    }

    //-----------------------------------------//
}
