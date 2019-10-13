package de.stylextv.bits.pathfinding;

public interface IGoal {
	
    public abstract int calculateHeuristic(Node currentNode);
    public abstract boolean isFinalNode(Node currentNode);
    
}
