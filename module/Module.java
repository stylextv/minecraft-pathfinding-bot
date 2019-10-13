package de.stylextv.bits.module;

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
import de.stylextv.bits.pathfinding.Node;
import de.stylextv.bits.pathfinding.PathEntity;
import de.stylextv.bits.util.MathUtil;
import de.stylextv.bits.util.RenderUtils;
import de.stylextv.bits.util.RotationUtils;
import de.stylextv.bits.world.ChunkCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class Module {
	
	public static Minecraft mc=Minecraft.getMinecraft();
	
	public abstract void onEnable();
	public abstract void onDisable();
	
	public abstract void onCommand(String cmd, String[] args);
	
	public abstract void onKeyPress(int code);
	
	public abstract void onUpdate();
	
	public abstract void onRender(float partialTicks);
	public abstract void renderOverlay();
	
}
