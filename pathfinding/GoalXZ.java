package de.stylextv.bits.pathfinding;

public class GoalXZ implements IGoal {
	
	private int x,z;
	
	public GoalXZ(int x, int z) {
		this.x=x;
		this.z=z;
	}
	
	@Override
	public int calculateHeuristic(Node currentNode) {
		return ( Math.abs(x - currentNode.getRow()) + Math.abs(z - currentNode.getCol()))*20;
	}
	@Override
	public boolean isFinalNode(Node currentNode) {
		return currentNode.getRow()==x&&currentNode.getCol()==z;
	}
	
}
