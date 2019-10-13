package de.stylextv.bits.world;

import java.util.BitSet;

import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLilyPad;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache {
	
	public int x,z;
	
	private BitSet bitSet=new BitSet(131072);//16*16*256*2
	
	public ChunkCache(int x, int z) {
		this.x=x;
		this.z=z;
		Chunk chunk=Minecraft.getMinecraft().world.getChunkFromChunkCoords(x, z);
		if(chunk.isLoaded()) load(chunk,false);
	}
	public ChunkCache(Chunk chunk) {
		this.x=chunk.xPosition;
		this.z=chunk.zPosition;
		if(chunk.isLoaded()) load(chunk,false);
	}
	public void load(Chunk chunk, boolean notEmpty) {
		int i=0;
		for(int cx=0; cx<16; cx++) {
			for(int cz=0; cz<16; cz++) {
				for(int cy=0; cy<256; cy++) {
					IBlockState state=chunk.getBlockState(cx, cy, cz);
					IBlockState bottom=chunk.getBlockState(cx, cy-1, cz);
					IBlockState top=chunk.getBlockState(cx, cy+1, cz);
					int id=1;
					if(top.getBlock() instanceof BlockLilyPad) {
						id=1;
					} else if(isAir(state)&&!(bottom.getBlock() instanceof BlockFence)) {
						id=0;
					} else if(isWater(state)) {
						id=2;
					} else if(isAvoid(state)) {
						id=3;
					}
					if(id!=0) {
						BitSet set=convert(id);
						bitSet.set(i*2,set.get(0));
						bitSet.set(i*2+1,set.get(1));
					} else if(notEmpty) {
						bitSet.set(i*2,false);
						bitSet.set(i*2+1,false);
					}
					i++;
				}
			}
		}
	}
	
	public void update() {
		Chunk chunk=Minecraft.getMinecraft().world.getChunkFromChunkCoords(x, z);
		if(chunk.isLoaded()) load(chunk,true);
	}
	
	public int getBlockState(int x, int y, int z) {
		int i=x*(16*256)+z*(256)+y;
		return (int) convert(bitSet.get(i*2, i*2+2));
	}
	
	public static BitSet convert(long value) {
	    BitSet bits = new BitSet();
	    int index = 0;
	    while (value != 0L) {
	      if (value % 2L != 0) {
	        bits.set(index);
	      }
	      ++index;
	      value = value >>> 1;
	    }
	    return bits;
	  }

	  public static long convert(BitSet bits) {
	    long value = 0L;
	    for (int i = 0; i < bits.length(); ++i) {
	      value += bits.get(i) ? (1L << i) : 0L;
	    }
	    return value;
	  }
	  
	public static boolean isAir(IBlockState state) {
		return state.getBlock() instanceof BlockAir||state.getBlock().equals(Blocks.FLOWING_WATER)||state.getBlock() instanceof BlockLilyPad||state.getBlock().equals(Blocks.SNOW_LAYER)||state.getBlock() instanceof BlockBush;
	}
	public static boolean isWater(IBlockState state) {
		return state.getBlock().equals(Blocks.WATER);
	}
	public static boolean isAvoid(IBlockState state) {
		return state.getBlock().equals(Blocks.FLOWING_LAVA)||state.getBlock().equals(Blocks.LAVA)||state.getBlock().equals(Blocks.FIRE)||state.getBlock().equals(Blocks.MAGMA)||state.getBlock().equals(Blocks.CACTUS);
	}
	
}
