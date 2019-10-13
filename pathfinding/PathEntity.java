package de.stylextv.bits.pathfinding;

import java.util.List;

public class PathEntity {
	
	private List<Node> path;
	public int currentIndex=0;
	public int jumpsLeft=0;
	public boolean shortCut=false;
	
	public PathEntity(List<Node> path) {
		this.path=path;
	}
	
	public Node getCurrentNode() {
		return getNode(currentIndex);
	}
	public Node getNode(int index) {
		return path.get(index);
	}
	public int getPathLength() {
		return path.size();
	}
	
	public List<Node> getNodes() {
		return path;
	}
	
	public boolean isEmpty() {
		return path.isEmpty();
	}
	
}
