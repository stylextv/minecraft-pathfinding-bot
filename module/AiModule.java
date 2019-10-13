package de.stylextv.bits.module;

import de.stylextv.bits.main.Bits;
import de.stylextv.bits.pathfinding.GoalBlock;
import de.stylextv.bits.pathfinding.GoalGetToBlock;
import de.stylextv.bits.pathfinding.GoalNear;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;

public class AiModule extends Module {
	
	public Entity targetEntity=null;
	public GoalNear goal;
	
	public AiModule() {
		goal=new GoalNear(0, 0, 0, 3);
	}
	
	@Override
	public void onEnable() {
	}
	@Override
	public void onDisable() {
	}
	
	@Override
	public void onCommand(String cmd, String[] args) {
		if(cmd.equalsIgnoreCase("ai")) {
			if(targetEntity==null) {
				for(Entity entity : mc.world.loadedEntityList) {
					if(entity instanceof EntityRabbit) {
						targetEntity=entity;
						break;
					}
				}
			} else targetEntity=null;
		} else if(cmd.equalsIgnoreCase("finddiamonds")) {
			Minecraft mc=Minecraft.getMinecraft();
			int chunkX=mc.player.chunkCoordX;
			int chunkZ=mc.player.chunkCoordZ;
			int goalX=0,goalY=0,goalZ=0;
			boolean hasGoal=false;
			for(int cx=-3; cx<=3; cx++) {
				for(int cz=-3; cz<=3; cz++) {
					Chunk chunk=mc.world.getChunkFromChunkCoords(chunkX+cx, chunkZ+cz);
					for(int x=0; x<16; x++) {
						for(int y=0; y<256; y++) {
							for(int z=0; z<16; z++) {
								if(chunk.getBlockState(x, y, z).getBlock().equals(Blocks.DIAMOND_ORE)) {
									if(!hasGoal||(y>goalY)) {
										hasGoal=true;
										goalX=chunk.xPosition*16+x;
										goalY=y;
										goalZ=chunk.zPosition*16+z;
									}
								}
							}
						}
					}
				}
			}
			if(hasGoal) Bits.MODULE_PATHFINDING.destGoal=new GoalGetToBlock(goalX, goalY, goalZ);
		}
	}
	
	@Override
	public void onKeyPress(int code) {
	}
	
	@Override
	public void onUpdate() {
		if(targetEntity!=null) {
			goal.x=PathFindingMod.coordToBlock(targetEntity.posX);
			goal.y=PathFindingMod.coordToBlock(targetEntity.posY);
			goal.z=PathFindingMod.coordToBlock(targetEntity.posZ);
			Bits.MODULE_PATHFINDING.destGoal=goal;
		}
	}
	
	@Override
	public void onRender(float partialTicks) {
	}
	@Override
	public void renderOverlay() {
	}
	
}
