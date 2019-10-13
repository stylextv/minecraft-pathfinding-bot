package de.stylextv.bits.pathfinding;

public class GoalGetToBlock implements IGoal {
	
	private int x,y,z;
	
	public GoalGetToBlock(int x, int y, int z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	@Override
	public int calculateHeuristic(Node currentNode) {
		return ( Math.abs(x - currentNode.getRow()) + Math.abs(z - currentNode.getCol()) + Math.abs(y - currentNode.getHeight()) )*20;
//      this.h = ( Math.abs(finalNode.getRow() - getRow()) + Math.abs(finalNode.getCol() - getCol()) )*10;
//  	this.h=(int)( Math.sqrt(Math.pow(getRow() - finalNode.getRow(), 2) + Math.pow(getCol() - finalNode.getCol(), 2) + Math.pow(finalNode.getHeight() - finalNode.getHeight(), 2)) )*10;
	}
	@Override
	public boolean isFinalNode(Node currentNode) {
		return (Math.abs(currentNode.getRow()-x)+Math.abs(currentNode.getCol()-z)+Math.abs(currentNode.getHeight()-y)<=2);
	}
	
}
