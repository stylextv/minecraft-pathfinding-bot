package de.stylextv.bits.pathfinding;

public class GoalComposite implements IGoal {
	
	private IGoal[] goals;
	
	public GoalComposite(IGoal... goals) {
		this.goals=goals;
	}
	
	@Override
	public int calculateHeuristic(Node currentNode) {
		int min=Integer.MAX_VALUE;
		for(IGoal goal:goals) {
			int c=goal.calculateHeuristic(currentNode);
			if(c<min)min=c;
		}
		return min;
	}
	@Override
	public boolean isFinalNode(Node currentNode) {
		for(IGoal goal:goals) if(goal.isFinalNode(currentNode)) return true;
		return false;
	}
	
}
