package de.stylextv.bits.pathfinding;

public class GoalYLevel implements IGoal {
	
	private int y;
	
	public GoalYLevel(int y) {
		this.y=y;
	}
	
	@Override
	public int calculateHeuristic(Node currentNode) {
		return ( Math.abs(y - currentNode.getHeight()))*20;
	}
	@Override
	public boolean isFinalNode(Node currentNode) {
		return currentNode.getHeight()==y;
	}
	
}
