package de.stylextv.bits.main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import de.stylextv.bits.module.AiModule;
import de.stylextv.bits.module.Module;
import de.stylextv.bits.module.PathFindingMod;
import de.stylextv.bits.pathfinding.AStar;
import de.stylextv.bits.pathfinding.GoalAxis;
import de.stylextv.bits.pathfinding.GoalBlock;
import de.stylextv.bits.pathfinding.GoalComposite;
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
import de.stylextv.bits.world.Location;
import de.stylextv.bits.world.WorldCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;

public class Bits {
	
	private static ArrayList<Module> modules = new ArrayList<Module>();
	
	public static PathFindingMod MODULE_PATHFINDING=new PathFindingMod();
	public static AiModule MODULE_AI=new AiModule();
	
	public static void onEnable() {
		registerModule(MODULE_PATHFINDING);
		registerModule(MODULE_AI);
	}
	public static void onDisable() {
		for(Module m:modules) m.onDisable();
	}
	
	public static void registerModule(Module m) {
		modules.add(m);
		m.onEnable();
	}
	
	public static void onUserCmd(String msg) {
		String[] split=msg.split(" ");
		String cmd=split[0].replaceFirst("#", "");
		String argS=msg.replaceFirst("#"+cmd, "").replaceFirst(" ", "");
		String[] args=new String[]{};
		if(!argS.replace(" ", "").isEmpty()) args=argS.split(" ");
		
		for(Module m:modules) m.onCommand(cmd, args);
	}
	public static void sendMsgToUser(String msg) {
		Minecraft.getMinecraft().player.addChatComponentMessage(new TextComponentString(msg), false);
	}
	
	public static void onKeyPress(int code) {
		for(Module m:modules) m.onKeyPress(code);
	}
	
	public static void onUpdate() {
		for(Module m:modules) m.onUpdate();
	}
	
	public static void onRender(float partialTicks) {
		for(Module m:modules) m.onRender(partialTicks);
	}
	public static void renderOverlay() {
		for(Module m:modules) m.renderOverlay();
	}
	
}
