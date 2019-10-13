package de.stylextv.bits.pathfinding;

public class GoalNear implements IGoal {
	
	public int x,y,z;
	private float disSq;
	
	public GoalNear(int x, int y, int z, float radius) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.disSq=radius*radius;
	}
	
	@Override
	public int calculateHeuristic(Node currentNode) {
		return ( Math.abs(x - currentNode.getRow()) + Math.abs(z - currentNode.getCol()) + Math.abs(y - currentNode.getHeight()) )*20;
	}
	@Override
	public boolean isFinalNode(Node currentNode) {
		int cDisSq=(int) (Math.pow(x - currentNode.getRow(), 2) + Math.pow(y - currentNode.getHeight(), 2) + Math.pow(z - currentNode.getCol(), 2));
		return cDisSq<=disSq;
	}
	
}
