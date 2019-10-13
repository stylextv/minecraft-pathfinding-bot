package de.stylextv.bits.pathfinding;

public class GoalTwoBlocks implements IGoal {
	
	private int x,y,z;
	
	public GoalTwoBlocks(int x, int y, int z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	@Override
	public int calculateHeuristic(Node currentNode) {
		return ( Math.abs(x - currentNode.getRow()) + Math.abs(z - currentNode.getCol()) + Math.min(Math.abs(y - currentNode.getHeight()), Math.abs(y - (currentNode.getHeight()+1))) )*20;
//      this.h = ( Math.abs(finalNode.getRow() - getRow()) + Math.abs(finalNode.getCol() - getCol()) )*10;
//  	this.h=(int)( Math.sqrt(Math.pow(getRow() - finalNode.getRow(), 2) + Math.pow(getCol() - finalNode.getCol(), 2) + Math.pow(finalNode.getHeight() - finalNode.getHeight(), 2)) )*10;
	}
	@Override
	public boolean isFinalNode(Node currentNode) {
		return (currentNode.getRow()==x&&currentNode.getCol()==z&&(currentNode.getHeight()==y||currentNode.getHeight()+1==y));
	}
	
}
