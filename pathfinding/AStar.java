package de.stylextv.bits.pathfinding;

import java.util.*;

import de.stylextv.bits.main.Bits;
import de.stylextv.bits.world.BetterBlockPos;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class AStar {
    private static int DEFAULT_HV_COST = 10; // Horizontal - Vertical Cost
    private static int DEFAULT_DIAGONAL_COST = 14;
    private static int DEFAULT_DIAGONAL3D_COST = 17;
    private int hvCost;
    private int diagonalCost;
    private int diagonalCost3d;
    private Long2ObjectOpenHashMap<Node> map;
    private PriorityQueue<Node> openList;
    private Set<Node> closedSet;
    private Node initialNode;
    private IGoal goal;
    public boolean paused=false;
    
    public AStar(Node initialNode, IGoal goal, int hvCost, int diagonalCost, int diagonalCost3d) {
        this.hvCost = hvCost;
        this.diagonalCost = diagonalCost;
        this.diagonalCost3d = diagonalCost3d;
        setInitialNode(initialNode);
        setGoal(goal);
        this.openList = new PriorityQueue<Node>(new Comparator<Node>() {
            @Override
            public int compare(Node node0, Node node1) {
                return Integer.compare(node0.getF(), node1.getF());
            }
        });
//        setNodes();
        this.closedSet = new HashSet<>();
        map=new Long2ObjectOpenHashMap<>(1024,0.75f);
    }
    
    public AStar(Node initialNode, IGoal goal) {
        this(initialNode, goal, DEFAULT_HV_COST, DEFAULT_DIAGONAL_COST, DEFAULT_DIAGONAL3D_COST);
    }
    
//    private void setNodes() {
//        for (int i = 0; i < searchArea.length; i++) {
//            for (int j = 0; j < searchArea[0].length; j++) {
//                for (int k = 0; k < searchArea[0][0].length; k++) {
//                    Node node = new Node(i, j, k);
//                    node.calculateHeuristic(getFinalNode());
//                    this.searchArea[i][j][k] = node;
//                }
//            }
//        }
//    }
    
//    public void setBlocks(int[][] blocksArray) {
//        for (int i = 0; i < blocksArray.length; i++) {
//            int row = blocksArray[i][0];
//            int col = blocksArray[i][1];
//            int height = blocksArray[i][2];
//            setBlock(row, col, height);
//        }
//    }
    
    public List<Node> findPath() {
        openList.add(initialNode);
        map.put(BetterBlockPos.longHash(initialNode.getRow(), initialNode.getHeight(), initialNode.getCol()),initialNode);
        while (!isEmpty(openList)) {
            Node currentNode = openList.poll();
            closedSet.add(currentNode);
            if (isFinalNode(currentNode)) {
                return getPath(currentNode);
            } else {
                if(addAdjacentNodes(currentNode)) {
                	paused=true;
                	return getPath(currentNode);
                }
            }
        }
        return new ArrayList<Node>();
    }
    
    private List<Node> getPath(Node currentNode) {
        List<Node> path = new ArrayList<Node>();
        path.add(currentNode);
        Node parent;
        while ((parent = currentNode.getParent()) != null) {
            path.add(0, parent);
            if(parent.getHeight()<currentNode.getHeight()&&parent.getBlock()!=2) parent.needsJump=true;
            else if(getNode(currentNode.getRow(),parent.getCol(),parent.getHeight()).getBlock()==1&&getNode(parent.getRow(),currentNode.getCol(),parent.getHeight()).getBlock()==1&&parent.getBlock()!=2) parent.needsJump=true;
            currentNode = parent;
        }
        return path;
    }
    
    private boolean addAdjacentNodes(Node currentNode) {
    	boolean b=false;
        if(currentNode.getHeight()+1<256) {
        	if(addAdjacentUpperRowHTOP(currentNode)) b=true;
        	if(addAdjacentMiddleRowHTOP(currentNode)) b=true;
        	if(addAdjacentLowerRowHTOP(currentNode)) b=true;
        }
        if(addAdjacentUpperRowHM(currentNode)) b=true;
        if(addAdjacentMiddleRowHM(currentNode)) b=true;
        if(addAdjacentLowerRowHM(currentNode)) b=true;
        if(currentNode.getHeight()-1>=0) {
        	if(addAdjacentUpperRowHBOT(currentNode)) b=true;
        	if(addAdjacentMiddleRowHBOT(currentNode)) b=true;
        	if(addAdjacentLowerRowHBOT(currentNode)) b=true;
        }
        return b;
    }
    
    private boolean addAdjacentLowerRowHTOP(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int height = currentNode.getHeight();
        int lowerRow = row + 1;
//        if (lowerRow < getSearchArea().length) {
//            if (col - 1 >= 0) {
                if(checkNode(currentNode, col - 1, lowerRow, height+1, getDiagonalCost3D())|| // Comment this line if diagonal movements are not allowed
//            }
//            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, lowerRow, height+1, getDiagonalCost3D())|| // Comment this line if diagonal movements are not allowed
//            }
            checkNode(currentNode, col, lowerRow, height+1, getDiagonalCost())) {return true;}
//        }
                return false;
    }

    private boolean addAdjacentMiddleRowHTOP(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int height = currentNode.getHeight();
        int middleRow = row;
//        if (col - 1 >= 0) {
            if(checkNode(currentNode, col - 1, middleRow, height+1, getDiagonalCost())||
//        }
//        if (col + 1 < getSearchArea()[0].length) {
            checkNode(currentNode, col + 1, middleRow, height+1, getDiagonalCost())) {return true;}
//        }
//        checkNode(currentNode, col, middleRow, height+1, getHvCost());
            return false;
    }

    private boolean addAdjacentUpperRowHTOP(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int height = currentNode.getHeight();
        int upperRow = row - 1;
//        if (upperRow >= 0) {
//            if (col - 1 >= 0) {
                if(checkNode(currentNode, col - 1, upperRow, height+1, getDiagonalCost3D())|| // Comment this if diagonal movements are not allowed
//            }
//            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, upperRow, height+1, getDiagonalCost3D())|| // Comment this if diagonal movements are not allowed
//            }
            checkNode(currentNode, col, upperRow, height+1, getDiagonalCost())) {return true;}
//        }
            return false;
    }
    
    private boolean addAdjacentLowerRowHM(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int height = currentNode.getHeight();
        int lowerRow = row + 1;
//        if (lowerRow < getSearchArea().length) {
//            if (col - 1 >= 0) {
                if(checkNode(currentNode, col - 1, lowerRow, height, getDiagonalCost())|| // Comment this line if diagonal movements are not allowed
//            }
//            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, lowerRow, height, getDiagonalCost())|| // Comment this line if diagonal movements are not allowed
//            }
            checkNode(currentNode, col, lowerRow, height, getHvCost())) {return true;}
//        }
                return false;
    }

    private boolean addAdjacentMiddleRowHM(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int height = currentNode.getHeight();
        int middleRow = row;
//        if (col - 1 >= 0) {
            if(checkNode(currentNode, col - 1, middleRow, height, getHvCost())||
//        }
//        if (col + 1 < getSearchArea()[0].length) {
            checkNode(currentNode, col + 1, middleRow, height, getHvCost())) {return true;}
//        }
            return false;
    }

    private boolean addAdjacentUpperRowHM(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int height = currentNode.getHeight();
        int upperRow = row - 1;
//        if (upperRow >= 0) {
//            if (col - 1 >= 0) {
                if(checkNode(currentNode, col - 1, upperRow, height, getDiagonalCost())|| // Comment this if diagonal movements are not allowed
//            }
//            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, upperRow, height, getDiagonalCost())|| // Comment this if diagonal movements are not allowed
//            }
            checkNode(currentNode, col, upperRow, height, getHvCost())) {return true;}
//        }
            return false;
    }
    
    private boolean addAdjacentLowerRowHBOT(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int height = currentNode.getHeight();
        int lowerRow = row + 1;
//        if (lowerRow < getSearchArea().length) {
//            if (col - 1 >= 0) {
                if(checkNode(currentNode, col - 1, lowerRow, height-1, getDiagonalCost3D())|| // Comment this line if diagonal movements are not allowed
//            }
//            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, lowerRow, height-1, getDiagonalCost3D())|| // Comment this line if diagonal movements are not allowed
//            }
            checkNode(currentNode, col, lowerRow, height-1, getDiagonalCost())) {return true;}
//        }
                return false;
    }
    
    private boolean addAdjacentMiddleRowHBOT(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int height = currentNode.getHeight();
        int middleRow = row;
//        if (col - 1 >= 0) {
            if(checkNode(currentNode, col - 1, middleRow, height-1, getDiagonalCost())||
//        }
//        if (col + 1 < getSearchArea()[0].length) {
            checkNode(currentNode, col + 1, middleRow, height-1, getDiagonalCost())) {return true;}
//        }
//        checkNode(currentNode, col, middleRow, height-1, getHvCost());
            return false;
    }

    private boolean addAdjacentUpperRowHBOT(Node currentNode) {
        int row = currentNode.getRow();
        int col = currentNode.getCol();
        int height = currentNode.getHeight();
        int upperRow = row - 1;
//        if (upperRow >= 0) {
//            if (col - 1 >= 0) {
                if(checkNode(currentNode, col - 1, upperRow, height-1, getDiagonalCost3D())|| // Comment this if diagonal movements are not allowed
//            }
//            if (col + 1 < getSearchArea()[0].length) {
                checkNode(currentNode, col + 1, upperRow, height-1, getDiagonalCost3D())|| // Comment this if diagonal movements are not allowed
//            }
            checkNode(currentNode, col, upperRow, height-1, getDiagonalCost())) {return true;}
//        }
                return false;
    }
    
    private boolean checkNode(Node currentNode, int col, int row, int height, int cost) {
//    	long before=System.nanoTime();
//        long after1=System.nanoTime();
    	for(int cx=-1; cx<=1; cx++) {
    		for(int cy=-1; cy<=1; cy++) {
    			for(int cz=-1; cz<=1; cz++) {
    		        if(Math.abs(cx)+Math.abs(cy)+Math.abs(cz)==1&&(getNode(row+cx,col+cz,height+cy).getBlock()==3)) return false;
            	}
        	}
    	}
        if(height>=currentNode.getHeight()&&(getNode(row,col,height-1).getBlock()==0)) return false;
        else if(height>currentNode.getHeight()) {
            if(getNode(currentNode.getRow(),currentNode.getCol(),currentNode.getHeight()+2).getBlock()==1) return false;
            else if(getNode(row,currentNode.getCol(),height+1).getBlock()==1||getNode(currentNode.getRow(),col,height+1).getBlock()==1) return false;
        }
        if(height<currentNode.getHeight()) {
        	if(Bits.MODULE_PATHFINDING.worldCache.getBlockState(row, height, col)==1||Bits.MODULE_PATHFINDING.worldCache.getBlockState(row, height+1, col)==1) return false;
        	boolean water=false;
        	while(height>=0) {
        		int state=Bits.MODULE_PATHFINDING.worldCache.getBlockState(row, height-1, col);
        		if(state==1) break;
        		else if(state==2) {
        			water=true;
        			break;
        		}
        		height--;
        	}
    		if(!water&&currentNode.getHeight()-height>3) return false;
    		if(water) height--;
        }
        if(Bits.MODULE_PATHFINDING.worldCache.getBlockState(row, height, col)==0&&Bits.MODULE_PATHFINDING.worldCache.getBlockState(row, height-1, col)==2)height--;
        Node adjacentNode = getNode(row, col, height);
        if(adjacentNode.getBlock()==-1) return true;
        if (!(adjacentNode.getBlock()==1) && !getClosedSet().contains(adjacentNode) && !(getNode(row,col,height+1).getBlock()==1) && !(getNode(row,col,currentNode.getHeight()+1).getBlock()==1)) {
        	boolean diagBlock1=(getNode(row,currentNode.getCol(),currentNode.getHeight()+1).getBlock()==1||getNode(row,currentNode.getCol(),currentNode.getHeight()).getBlock()==1);
        	boolean diagBlock2=(getNode(currentNode.getRow(),col,currentNode.getHeight()+1).getBlock()==1||getNode(currentNode.getRow(),col,currentNode.getHeight()).getBlock()==1);
            if(!(  (diagBlock1&&diagBlock2)||(currentNode.getRow()!=row&&currentNode.getCol()!=col&&(diagBlock1||diagBlock2))  )) {
            	if (!getOpenList().contains(adjacentNode)) {
                    adjacentNode.setNodeData(currentNode, cost);
                    getOpenList().add(adjacentNode);
                } else {
                    boolean changed = adjacentNode.checkBetterPath(currentNode, cost);
                    if (changed) {
                        // Remove and Add the changed node, so that the PriorityQueue can sort again its
                        // contents with the modified "finalCost" value of the modified node
                        getOpenList().remove(adjacentNode);
                        getOpenList().add(adjacentNode);
                    }
                }
            }
        }
//        long after2=System.nanoTime();
//        System.out.println("---");
//        System.out.println((after1-before)/1000000.0);
//        System.out.println((after2-after1)/1000000.0);
		return false;
    }
    public Node getNode(int row, int col, int height) {
//    	System.out.println("GETNODE");
    	long key=BetterBlockPos.longHash(row, height, col);
    	Node node=(Node) map.get(key);
        if(node==null) {
        	node=new Node(row, col, height);
            node.calculateHeuristic(goal);
            int state=Bits.MODULE_PATHFINDING.worldCache.getBlockState(row, height, col);
            node.setBlock(state);
        	map.put(key, node);
        }
        return node;
    }
    
    private boolean isFinalNode(Node currentNode) {
        return goal.isFinalNode(currentNode);
    }
    
    private boolean isEmpty(PriorityQueue<Node> openList) {
        return openList.size() == 0;
    }
    
//    public void setBlock(int row, int col, int height) {
//        this.searchArea[row][col][height].setBlock(true);
//    }
    
    public Node getInitialNode() {
        return initialNode;
    }
    
    public void setInitialNode(Node initialNode) {
        this.initialNode = initialNode;
    }

    public IGoal getGoal() {
        return goal;
    }

    public void setGoal(IGoal goal) {
        this.goal = goal;
    }

//    public Node[][][] getSearchArea() {
//        return searchArea;
//    }

//    public void setSearchArea(Node[][][] searchArea) {
//        this.searchArea = searchArea;
//    }

    public PriorityQueue<Node> getOpenList() {
        return openList;
    }

    public void setOpenList(PriorityQueue<Node> openList) {
        this.openList = openList;
    }

    public Set<Node> getClosedSet() {
        return closedSet;
    }

    public void setClosedSet(Set<Node> closedSet) {
        this.closedSet = closedSet;
    }

    public int getHvCost() {
        return hvCost;
    }

    public void setHvCost(int hvCost) {
        this.hvCost = hvCost;
    }

    private int getDiagonalCost() {
        return diagonalCost;
    }

    private void setDiagonalCost(int diagonalCost) {
        this.diagonalCost = diagonalCost;
    }
    
    private int getDiagonalCost3D() {
        return diagonalCost3d;
    }

    private void setDiagonalCost3D(int diagonalCost3d) {
        this.diagonalCost3d = diagonalCost3d;
    }
    
}
