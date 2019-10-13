package de.stylextv.bits.module;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL11;

import de.stylextv.bits.main.Bits;
import de.stylextv.bits.pathfinding.AStar;
import de.stylextv.bits.pathfinding.GoalAxis;
import de.stylextv.bits.pathfinding.GoalBlock;
import de.stylextv.bits.pathfinding.GoalGetToBlock;
import de.stylextv.bits.pathfinding.GoalNear;
import de.stylextv.bits.pathfinding.GoalTwoBlocks;
import de.stylextv.bits.pathfinding.GoalXZ;
import de.stylextv.bits.pathfinding.GoalYLevel;
import de.stylextv.bits.pathfinding.IGoal;
import de.stylextv.bits.pathfinding.Node;
import de.stylextv.bits.pathfinding.PathEntity;
import de.stylextv.bits.util.MathUtil;
import de.stylextv.bits.util.RenderUtils;
import de.stylextv.bits.util.RotationUtils;
import de.stylextv.bits.world.ChunkCache;
import de.stylextv.bits.world.WorldCache;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;

public class PathFindingMod extends Module {
	
	private int playerBox;
	
	public CopyOnWriteArrayList<PathEntity> paths=new CopyOnWriteArrayList<PathEntity>();
	public Node lastTarget=null,lastTargetedNode=null;
	public int ticksSinceLastJump=0,ticksToNode=0;
	
	public WorldCache worldCache=new WorldCache();
	private AStar aStar;
	
	private Thread mainThread=null;
	private boolean running=false,calculating;
	public IGoal destGoal;
	
	@Override
	public void onEnable() {
		playerBox = GL11.glGenLists(1);
		GL11.glNewList(playerBox, GL11.GL_COMPILE);
		AxisAlignedBB bb = new AxisAlignedBB(-0.5, 0, -0.5, 0.5, 1, 0.5);
		RenderUtils.drawOutlinedBox(bb);
		GL11.glEndList();
		
		running=true;
		mainThread=new Thread(new Runnable() {
			@Override
			public void run() {
				while(running) {
					try {
						if(mc.player!=null) {
							if(paths.size()<3) checkDestInput();
							worldCache.checkChunkProvider(mc.player);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {ex.printStackTrace();}
				}
			}
		});
		mainThread.start();
	}
	private void checkDestInput() {
		if(destGoal!=null||(aStar!=null&&aStar.paused)) {
			calculating=true;
//			path=null;
			long after1=System.nanoTime();
//			worldCache.unloadAllChunks();
//			int startChunkX=coordToChunk(startX);
//			int startChunkZ=coordToChunk(startZ);
//			int endChunkX=coordToChunk(endX);
//			int endChunkZ=coordToChunk(endZ);
//			int originChunkX=Math.min(endChunkX, startChunkX)-1;
//			int originChunkZ=Math.min(endChunkZ, startChunkZ)-1;
//			int opOriginChunkX=Math.max(endChunkX, startChunkX)+1;
//			int opOriginChunkZ=Math.max(endChunkZ, startChunkZ)+1;
//	        pathWorldX=originChunkX*16;
//	        pathWorldZ=originChunkZ*16;
			boolean con=false;
			if(aStar!=null&&aStar.paused) {
				con=true;
				PathEntity path=paths.get(paths.size()-1);
				Node end=path.getNode(path.getPathLength()-1);
		        aStar = new AStar(new Node(end.getRow(), end.getCol(), end.getHeight()), aStar.getGoal());
			} else {
				paths.clear();
				int startX=coordToBlock(mc.player.posX);
				int startY=coordToBlock(mc.player.posY);
				int startZ=coordToBlock(mc.player.posZ);
				Node initialNode = new Node(startX, startZ, startY);
		        aStar = new AStar(initialNode, destGoal);
			}
			long after2=System.nanoTime();
//	        for(int chunkX=originChunkX; chunkX<=opOriginChunkX; chunkX++) {
//		        for(int chunkZ=originChunkZ; chunkZ<=opOriginChunkZ; chunkZ++) {
//			        ChunkCache cache=worldCache.getCache(chunkX, chunkZ);
//			        if(cache!=null) for(int cx=0; cx<16; cx++) {
//						for(int cz=0; cz<16; cz++) {
//							for(int cy=0; cy<256; cy++) {
//								if(cache.getBlockState(cx, cy, cz)==1) {
//									aStar.setBlock(cx+chunkX*16-pathWorldX, cz+chunkZ*16-pathWorldZ, cy);
//								}
//							}
//						}
//			        }
//		        }
//	        }
	        PathEntity path = new PathEntity(aStar.findPath());
	        if(!con) {
	        	if(!path.isEmpty()) paths.add(path);
	        } else if(path.getPathLength()>16||!aStar.paused) {
	        	paths.add(path);
	        }
	        long after4=System.nanoTime();
	        Bits.sendMsgToUser("TOOK1 "+((after2-after1)/1000000.0)+"ms");
	        Bits.sendMsgToUser("TOOK2 "+((after4-after2)/1000000.0)+"ms");
//	        sendMsgToUser("TOOK3 "+((after4-after3)/1000000.0)+"ms");
	        Bits.sendMsgToUser("TOOKALL "+((after4-after1)/1000000.0)+"ms");
	        Bits.sendMsgToUser("LENGTH: "+path.getPathLength());
	        destGoal=null;
	        if(!aStar.paused) aStar=null;
//	        System.gc();
			calculating=false;
		}
	}
	@Override
	public void onDisable() {
		GL11.glDeleteLists(playerBox, 1);
		playerBox = 0;
		
		running=false;
	}
	
	@Override
	public void onCommand(String cmd, String[] args) {
		if(cmd.equalsIgnoreCase("setdest")) {
			if(args.length==0) {
				paths.clear();
				aStar=null;
				ticksToNode=0;
			} else {
				try {
					if(args[0].equalsIgnoreCase("block")&&args.length==4) {
						destGoal=new GoalBlock(stringToBlock(args[1],0),stringToBlock(args[2],1),stringToBlock(args[3],2));
					} else if(args[0].equalsIgnoreCase("near")&&args.length==5) {
						float f=Float.parseFloat(args[4]);
						destGoal=new GoalNear(stringToBlock(args[1],0),stringToBlock(args[2],1),stringToBlock(args[3],2),f);
					} else if(args[0].equalsIgnoreCase("twoblocks")&&args.length==4) {
						destGoal=new GoalTwoBlocks(stringToBlock(args[1],0),stringToBlock(args[2],1),stringToBlock(args[3],2));
					} else if(args[0].equalsIgnoreCase("ylevel")&&args.length==2) {
						destGoal=new GoalYLevel(stringToBlock(args[1],1));
					} else if(args[0].equalsIgnoreCase("xz")&&args.length==3) {
						destGoal=new GoalXZ(stringToBlock(args[1],0),stringToBlock(args[2],2));
					} else if(args[0].equalsIgnoreCase("gettoblock")&&args.length==4) {
						destGoal=new GoalGetToBlock(stringToBlock(args[1],0),stringToBlock(args[2],1),stringToBlock(args[3],2));
					} else if(args[0].equalsIgnoreCase("axis")&&args.length==3) {
						double x=Double.parseDouble(args[1]);
						double z=Double.parseDouble(args[2]);
						destGoal=new GoalAxis(coordToBlock(x),coordToBlock(z));
					}
				} catch(NumberFormatException ex) {}
			}
		} else if(cmd.equalsIgnoreCase("clearcache")) {
			worldCache.unloadAllChunks();
		}
	}
	
	@Override
	public void onKeyPress(int code) {
		
	}
	
	public static int coordToChunk(int coord) {
		boolean b=coord<0;
		boolean b1=coord/16.0==(int)coord/16;
		int chunk=Math.abs(coord)/16;
		if(b) {
			if(!b1)chunk++;
			chunk=-(chunk);
		}
		return chunk;
	}
	public static int coordToBlock(double coord) {
		boolean b=coord<0;
		boolean b1=coord==(int)coord;
		int chunk=Math.abs((int)coord);
		if(b) {
			if(!b1)chunk++;
			chunk=-(chunk);
		}
		return chunk;
	}
	public static int stringToBlock(String s, int axis) throws NumberFormatException {
		boolean off=s.startsWith("~");
		if(off) {
			double d=0;
			s=s.replaceFirst("~", "");
			if(!s.isEmpty()) d=Double.parseDouble(s);
			switch(axis) {
			case 0: d+=mc.player.posX;
            break;
			case 1: d+=mc.player.posY;
            break;
			case 2: d+=mc.player.posZ;
            break;
			}
			return coordToBlock(d);
		} else {
			return coordToBlock(Double.parseDouble(s));
		}
	}
	
	@Override
	public void onUpdate() {
		EntityPlayerSP player=mc.player;
		
		if(!paths.isEmpty()) {
			PathEntity path=paths.get(0);
			if(path.currentIndex<path.getPathLength()) {
				Node target=path.getCurrentNode();
				double targetX=target.getRow()+0.5;
				double targetZ=target.getCol()+0.5;
				double targetY=target.getHeight();
				
				double speed=0.215;
				if(true) speed=0.28;//SPRINT
				double disX=Math.abs(player.posX-targetX);
				double disZ=Math.abs(player.posZ-targetZ);
				
				double disXZ=MathUtil.getDistance(targetX,targetZ,player.posX,player.posZ);
				double disSq=Math.pow(targetX - player.posX, 2) + Math.pow((targetY - player.posY)/4, 2) + Math.pow(targetZ - player.posZ, 2);
				
				double speedX = disX * (speed / disXZ);
				double speedZ = disZ * (speed / disXZ);
				
				if(disX>0.01) if(player.posX<targetX) {
					if(player.posX+speedX<targetX) player.motionX=speedX;
					else player.motionX=(targetX-player.posX)/2;
				} else if(player.posX>targetX) {
					if(player.posX-speedX>targetX) player.motionX=-speedX;
					else player.motionX=(targetX-player.posX)/2;
				}
				
				if(disZ>0.01) if(player.posZ<targetZ) {
					if(player.posZ+speedZ<targetZ) player.motionZ=speedZ;
					else player.motionZ=(targetZ-player.posZ)/2;
				} else if(player.posZ>targetZ) {
					if(player.posZ-speedZ>targetZ) player.motionZ=-speedZ;
					else player.motionZ=(targetZ-player.posZ)/2;
				}
				
				if(disSq<=0.75*0.75&&target.needsJump&&lastTarget!=target) {
					lastTarget=target;
					path.jumpsLeft=1;
				}
				
				if(player.onGround&&ticksSinceLastJump<=5&&ticksSinceLastJump>0) {
					MovementInputFromOptions.forceJump=true;
					ticksSinceLastJump=0;
				} else if(player.onGround&&ticksToNode>20) {
					MovementInputFromOptions.forceJump=true;
				}
				
				if(path!=null&&player.onGround&&ChunkCache.isAir(mc.world.getBlockState(new BlockPos(player).add(0, 2, 0)))&&path.jumpsLeft>0) {
					boolean b=true;
					if(path.currentIndex>0) {
						Node before=path.getNode(path.currentIndex-1);
						if(!ChunkCache.isAir(mc.world.getBlockState(new BlockPos(before.getRow(), before.getHeight()+2, before.getCol())))) b=false;
					}
					if(b) {
						MovementInputFromOptions.forceJump=true;
						ticksSinceLastJump=1;
						path.jumpsLeft--;
					}
				}
				if(player.isInWater()&&(ChunkCache.isWater(mc.world.getBlockState(new BlockPos(player).add(0, -1, 0)))||targetY>player.posY)&&(ChunkCache.isAir(mc.world.getBlockState(new BlockPos(targetX,targetY+2,targetZ)))||targetY>player.posY)) {
					MovementInputFromOptions.forceJump=true;
				}
				
				if(disSq<=0.1*0.1||((path.currentIndex<path.getPathLength()-1||paths.size()>1)&&disSq<=0.5*0.5)) {
					int slope=0;
					if(path.currentIndex>0) {
						slope=path.getNode(path.currentIndex-1).getHeight()-target.getHeight();
					} else if(lastTargetedNode!=null) {
						slope=lastTargetedNode.getHeight()-target.getHeight();
					}
					int nextSlope=0;
					if(path.currentIndex+1<path.getPathLength()) {
						nextSlope=target.getHeight()-path.getNode(path.currentIndex+1).getHeight();
					} else if(paths.size()>1) {
						nextSlope=target.getHeight()-paths.get(1).getNode(1).getHeight();
					}
					if((slope<=1&&nextSlope<=1)||player.onGround||player.isInWater()) {
						path.currentIndex++;
						lastTargetedNode=target;
						ticksToNode=-1;
						if(path.currentIndex==path.getPathLength()) {
							paths.remove(0);
							if(paths.isEmpty()) {
								player.motionX=0;
								player.motionZ=0;
								ticksToNode=0;
							}
						} else if(paths.size()>1) {
//							PathEntity nextSeg=paths.get(1);
//							if(!nextSeg.shortCut&&!path.shortCut) {
//								GoalBlock goal=new GoalBlock(0,0,0);
//								PathEntity best=null;
//								int saves=0;
//								int max=Math.max(path.getPathLength(), nextSeg.getPathLength());
//								int bestI=0;
//								for(int i=max; i>0; i--) {
//									if(path.getPathLength()-i>0&&nextSeg.getPathLength()>i) {
//										Node to=nextSeg.getNode(i);
//										goal.x=to.getRow();
//										goal.z=to.getCol();
//										goal.y=to.getHeight();
//										Node from=path.getNode(path.getPathLength()-i);
//								        PathEntity shortCut = new PathEntity(new AStar(new Node(from.getRow(), from.getCol(), from.getHeight()), goal).findPath());
//								        int segSaves=(i*2+1)-shortCut.getPathLength();
//								        if(segSaves>0&&segSaves>saves) {
//								        	best=shortCut;
//								        	saves=segSaves;
//								        	bestI=i;
//								        }
//									}
//								}
//								
//								if(best!=null&&!best.isEmpty()&&best.getPathLength()>0) {
//									best.shortCut=true;
//									paths.add(1, best);
//									for(int i=0; i<bestI+1; i++) {
//										path.getNodes().remove(path.getPathLength()-1);
//										nextSeg.getNodes().remove(0);
//									}
//									Bits.sendMsgToUser("SHORTCUT FOUND");
//								} else nextSeg.shortCut=true;
//							}
						}
					}
				}
				
				if(ticksSinceLastJump>0)ticksSinceLastJump++;
				ticksToNode++;
			} else paths.remove(0);
			player.capabilities.isFlying = false;
		} else lastTargetedNode=null;
	}
	
	public static void renderText(String str, double x, double y, double z, int maxDistance)
    {
		Entity e=mc.getRenderManager().renderViewEntity;
		if(e!=null) {
			double d0=Math.sqrt(Math.pow(x - e.posX, 2) + Math.pow(y - e.posY, 2) + Math.pow(z - e.posZ, 2));
	        
	        if (d0 <= maxDistance)
	        {
	        float f = mc.getRenderManager().playerViewY;
	        float f1 = mc.getRenderManager().playerViewX;
	        boolean flag1 = mc.getRenderManager().options.thirdPersonView == 2;
	        EntityRenderer.drawNameplate(mc.fontRendererObj, str, (float)x, (float)y, (float)z, 0, f, f1, flag1, false);
	        }
		}
    }
	
	@Override
	public void onRender(float partialTicks) {
		// GL settings
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glEnable(GL11.GL_LINE_SMOOTH);
				GL11.glLineWidth(2);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				
				GL11.glPushMatrix();
				GL11.glTranslated(-mc.getRenderManager().renderPosX,
					-mc.getRenderManager().renderPosY,
					-mc.getRenderManager().renderPosZ);
				
				if(!paths.isEmpty()) {
					for(PathEntity path:paths) renderLine(path,partialTicks);
					if(false) if(aStar!=null&&!calculating) {
						for(Node node: aStar.getOpenList()) {
//							float red=node.getF()/10f;
//							if(red>1)red=1;
//							GL11.glColor4f(0F, 0F, 1F, red);
//							renderBox(partialTicks, node.getRow()+0.5, node.getHeight(), node.getCol()+0.5, 1, 1, 1);
							renderText(""+node.getF(), node.getRow()+0.5, node.getHeight()+0.5, node.getCol()+0.5, 10);
						}
						for(Node node: aStar.getClosedSet()) {
//							float red=node.getF()/10f;
//							if(red>1)red=1;
//							GL11.glColor4f(0F, 1F, 0F, red);
//							renderBox(partialTicks, node.getRow()+0.5, node.getHeight(), node.getCol()+0.5, 1, 1, 1);
							renderText(""+node.getF(), node.getRow()+0.5, node.getHeight()+0.5, node.getCol()+0.5, 10);
						}
					}
					PathEntity path=paths.get(0);
				}
				if(calculating) renderLine2(partialTicks);
				
//				GL11.glColor4f(1F, 1F, 0F, 0.5F);
//				for(Entry<Long, ChunkCache> entry : worldCache.chunks.entrySet()) {
//					ChunkCache cache=entry.getValue();
//					if(cache!=null) {
//						GL11.glBegin(GL11.GL_LINES);
//						
//						int x=cache.x*16+8;
//						int z=cache.z*16+8;
//						GL11.glVertex3d(x,0,z);
//						GL11.glVertex3d(x,256,z);
//						
//						GL11.glEnd();
//					}
//				}
				
//				if(tracers.isChecked())
//					renderTracers(partialTicks);
//				EntityPlayer e=mc.player;
//				renderBox(partialTicks, e.prevPosX + (e.posX - e.prevPosX) * partialTicks,
//						e.prevPosY + (e.posY - e.prevPosY) * partialTicks,
//						e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks, 1, 1, 1);
//				renderBox(partialTicks, -2+0.5, 63, 0+0.5, 1, 1, 1);
				
				GL11.glPopMatrix();
				
				// GL resets
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_LINE_SMOOTH);
	}
	@Override
	public void renderOverlay() {
        mc.fontRendererObj.drawString("Chunks: "+worldCache.loadedChunks+" (+0)", 1, 1, -1, false);
	}
	
	private void renderBox(double partialTicks, double x, double y, double z, double scaleX, double scaleY, double scaleZ)
	{
		// set position
		GL11.glPushMatrix();
//		GL11.glTranslated(e.prevPosX + (e.posX - e.prevPosX) * partialTicks,
//			e.prevPosY + (e.posY - e.prevPosY) * partialTicks,
//			e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks);
//		GL11.glScaled(e.width + 0.1, e.height + 0.1, e.width + 0.1);
		GL11.glTranslated(x,
				y,
				z);
			GL11.glScaled(scaleX,scaleY,scaleZ);
		
		
		// set color
		
		// draw box
//        GL11.glBegin(2);
//        GL11.glVertex3d(x, y, z);
//        GL11.glVertex3d(x, y+1, z);
//        GL11.glEnd();
		GL11.glCallList(playerBox);
		
		GL11.glPopMatrix();
	}
	private void renderTracers(float partialTicks)
	{
		Vec3d start = RotationUtils.getClientLookVec()
			.addVector(0, mc.player.getEyeHeight(), 0)
//			.addVector(mc.getRenderManager().renderPosX,
//				mc.getRenderManager().renderPosY,
//				mc.getRenderManager().renderPosZ);
			.addVector(mc.getRenderManager().renderPosX,
					mc.getRenderManager().renderPosY,
					mc.getRenderManager().renderPosZ);
		
		GL11.glBegin(GL11.GL_LINES);
		for(Entity e : mc.world.loadedEntityList)
		{
			if(e!=mc.player) {
				Vec3d end = e.getEntityBoundingBox().getCenter()
						.subtract(new Vec3d(e.posX, e.posY, e.posZ)
							.subtract(e.prevPosX, e.prevPosY, e.prevPosZ)
							.scale(1.0 - partialTicks));
					
					float f = mc.player.getDistanceToEntity(e) / 20F;
					GL11.glColor4f(2 - f, f, 0, 0.5F);
					
					GL11.glVertex3d(start.xCoord, start.yCoord, start.zCoord);
					GL11.glVertex3d(end.xCoord, end.yCoord, end.zCoord);
			}
		}
		GL11.glEnd();
	}
	private void renderLine(PathEntity path, float partialTicks)
	{
		if(path!=null&&!path.isEmpty()) {
			GL11.glColor4f(0.867F, 0.063F, 0.361F, 1F);
			Node previous=path.getNode(0);
			for(int i=1; i<path.getPathLength(); i++) {
				Node node=path.getNode(i);
				GL11.glBegin(GL11.GL_LINES);
				
				GL11.glVertex3d(previous.getRow()+0.5,previous.getHeight()+0.5,previous.getCol()+0.5);
				GL11.glVertex3d(node.getRow()+0.5,node.getHeight()+0.5,node.getCol()+0.5);
				
				GL11.glEnd();
				previous=node;
			}
			if(path.currentIndex<path.getPathLength()) {
				Node current=path.getCurrentNode();
				GL11.glColor4f(0.192F, 0.8F, 0.6118F, 1F);
				GL11.glBegin(GL11.GL_LINES);
				
				GL11.glVertex3d(current.getRow()+0.5,current.getHeight(),current.getCol()+0.5);
				GL11.glVertex3d(current.getRow()+0.5,current.getHeight()+1,current.getCol()+0.5);
				
				GL11.glEnd();
			}
		}
	}
	private void renderLine2(float partialTicks)
	{
		if(aStar!=null&&!aStar.getOpenList().isEmpty()) {
			GL11.glColor4f(0.3451F, 0.1608F, 0.898F, 1F);
			Node top=aStar.getOpenList().peek();
			Node parent;
			if(top!=null) while((parent=top.getParent())!=null) {
				GL11.glBegin(GL11.GL_LINES);
				
				GL11.glVertex3d(parent.getRow()+0.5,parent.getHeight()+0.5,parent.getCol()+0.5);
				GL11.glVertex3d(top.getRow()+0.5,top.getHeight()+0.5,top.getCol()+0.5);
				
				GL11.glEnd();
				top=parent;
			}
		}
	}
	
}
