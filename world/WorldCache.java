package de.stylextv.bits.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import de.stylextv.bits.main.Bits;
import de.stylextv.bits.module.PathFindingMod;
import de.stylextv.bits.pathfinding.Node;
import de.stylextv.bits.util.MathUtil;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class WorldCache {
	
    public Long2ObjectOpenHashMap<ChunkCache> chunks;
    public int loadedChunks=0;
    private int updateTimer=0;
	private ArrayList<Chunk> chunkProvider=new ArrayList<Chunk>();
	
	public WorldCache() {
		chunks=new Long2ObjectOpenHashMap<>(512,0.75f);
	}
	
	public ChunkCache getCache(int chunkX, int chunkZ) {
		return chunks.get(longHash(chunkX, chunkZ));
	}
	public void unloadChunk(int chunkX, int chunkZ) {
		chunks.remove(longHash(chunkX, chunkZ));
	}
	public void unloadAllChunks() {
		chunks.clear();
	}
	public ChunkCache loadChunk(int chunkX, int chunkZ) {
		ChunkCache cache=new ChunkCache(chunkX, chunkZ);
		chunks.put(longHash(cache.x, cache.z),cache);
		return cache;
	}
	
	public int getBlockState(int x, int y, int z) {
		ChunkCache chunk=getCache(PathFindingMod.coordToChunk(x), PathFindingMod.coordToChunk(z));
		if(chunk==null) return -1;
		return chunk.getBlockState(x-chunk.x*16, y, z-chunk.z*16);
	}
	
	public void addToChunkProvider(Chunk chunk) {
		chunkProvider.add(chunk);
	}
	public void checkChunkProvider(EntityPlayer player) {
		while(!chunkProvider.isEmpty()) {
			Chunk chunk=chunkProvider.remove(0);
			ChunkCache cache=new ChunkCache(chunk);
			chunks.put(longHash(cache.x, cache.z),cache);
		}
		updateTimer++;
		boolean update=false;
		if(updateTimer==3) {
			update=true;
			updateTimer=0;
		}
	    Iterator it = chunks.entrySet().iterator();
	    Map.Entry pair;
	    while (
	    		it.hasNext()
	    		&&(pair= (Map.Entry)it.next())!=null) {
			ChunkCache chunk=(ChunkCache) pair.getValue();
			if(Math.pow((player.posX/16 - (chunk.x+0.5)), 2) + Math.pow((player.posZ/16 - (chunk.z+0.5)), 2)>15*15) {
//				keysToRemove.add(longHash(chunk.x, chunk.z));
		        it.remove();
			} else if(update) {
				chunk.update();
			}
//	        it.remove();
	    }
	    loadedChunks=chunks.size();
	}
	
	private static long longHash(int x, int z) {
        // TODO use the same thing as BlockPos.fromLong();
        // invertibility would be incredibly useful
        /*
         *   This is the hashcode implementation of Vec3i (the superclass of the class which I shall not name)
         *
         *   public int hashCode() {
         *       return (this.getY() + this.getZ() * 31) * 31 + this.getX();
         *   }
         *
         *   That is terrible and has tons of collisions and makes the HashMap terribly inefficient.
         *
         *   That's why we grab out the X, Y, Z and calculate our own hashcode
         */
        long hash = 3241;
        hash = 3457689L * hash + x;
        hash = 8734625L * hash + z;
        return hash;
    }
	
}
