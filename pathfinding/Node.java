package de.stylextv.bits.pathfinding;

public class Node {

    private int g;
    private int f;
    private int h;
    private int row;
    private int col;
    private int height;
    private int block;
    private Node parent;
    
    public boolean needsJump=false;
    
    public Node(int row, int col, int height) {
        super();
        this.row = row;
        this.col = col;
        this.height = height;
    }
    
    public void calculateHeuristic(IGoal goal) {
        this.h = goal.calculateHeuristic(this);
    }
    
    public void setNodeData(Node currentNode, int cost) {
        int gCost = currentNode.getG() + cost;
        setParent(currentNode);
        setG(gCost);
        calculateFinalCost();
    }

    public boolean checkBetterPath(Node currentNode, int cost) {
        int gCost = currentNode.getG() + cost;
        if (gCost < getG()) {
            setNodeData(currentNode, cost);
            return true;
        }
        return false;
    }

    private void calculateFinalCost() {
        int finalCost = getG() + getH();
        setF(finalCost);
    }
    
    @Override
    public boolean equals(Object arg0) {
        Node other = (Node) arg0;
        return this.getRow() == other.getRow() && this.getCol() == other.getCol() && this.getHeight() == other.getHeight();
    }

    @Override
    public String toString() {
        return "Node [row=" + row + ", col=" + col + ", height=" + height + "]";
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    public int getBlock() {
        return block;
    }
    
    public void setBlock(int id) {
        this.block = id;
    }
    
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
    
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}