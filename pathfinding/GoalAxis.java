package de.stylextv.bits.pathfinding;

public class GoalAxis implements IGoal {
	
	private int x,z;
	
	public GoalAxis(int x, int z) {
		this.x=x;
		this.z=z;
	}
	
	@Override
	public int calculateHeuristic(Node currentNode) {
		if(x!=0&&z==0) return ( Math.abs(currentNode.getRow()) )*20;
		else if(x==0&&z!=0) return ( Math.abs(currentNode.getCol()) )*20;
		else if(x==z) {
			int disX=Math.abs(currentNode.getRow());
			int disZ=Math.abs(currentNode.getCol());
			int min=Math.min(disX, disZ);
			return ( disX-min+disZ-min )*20;
		} else if(x!=z) {
			int cx=currentNode.getRow();
			int cz=currentNode.getCol();
			cx=-cx;
			int min=Math.min(cx, cz);
			cx-=min;
			cz-=min;
			return ( cx+cz )*20;
		}
		return 0;
	}
	@Override
	public boolean isFinalNode(Node currentNode) {
		if(x!=0&&z==0) return currentNode.getRow()==0;
		else if(x==0&&z!=0) return currentNode.getCol()==0;
		else if(x==z) return currentNode.getRow()==currentNode.getCol();
		else if(x!=z) return -currentNode.getRow()==currentNode.getCol();
		return true;
	}
	
}
