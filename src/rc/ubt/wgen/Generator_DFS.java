package rc.ubt.wgen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import rc.ubt.impl.SimplexImpl;

public class Generator_DFS extends ChunkGenerator {
	
	//Dome Fuji Survival
	//Hardcoded defaults
	
	//minor refactoring to get rid off too hardcoded stuff is required
	//shrom caves are required
	//world seed is required
	//or distribution must be altered
	
	//there is no answer about how minecraft generate ores and other stuff without invoking populators recursively
	//some method must allow to change world ignoring chunk borders
	//and not falling into stackoverflow
	
	//probably it's flaw inside standart bukkit implementation of custom generators
	//to invoke populator over chunk not having borders available
	//or to populate chunk and load objects if some required to chunk is set
	//in any case must study carefully how chunk generated and populated and what may cause chunk to load and generate
	
	static final double SCALE = 0.00390625d; //DONT
	static final double BASE  = 0.67d; //minimal noise for dome
	static final double STEP  = 0.05d; //noise step for dome
	
	static final short BLOCK_DOME 			= 174; 	//block used for dome walls
	static final short SURFACE_OUTSIDE 		= 174; 	//block used for surface
	static final short SURFACE_INSIDE_TOP 	= 2; 	//block used for dome internal surface
	static final short SURFACE_INSIDE 		= 3; 	//block used for dome internal fill
	
	public void AddLayer(short[] E,int Start,int End,int ID)
	{
		while (Start <= End)
		{
			for (int i = 0; i < 256 ; i++)
			{
				E[i+256*Start] = (short) ID;
			}
			Start++;
		}
	}
	
	//section with minimal noise factor also shoud be used
	//it will generate similar underground cave at level ~~20
	//with top height about level ~~40
	//with glowstone and shrooms and mycel blocks
	//due to light rules, caves shoud have no more then 30 height if blocks of glowstone will grow both ground and celling
	//in case of only celling - 20 height shoud be top
	//form of caves probably shoud not be completely flat...
	
	public short[][] generateExtBlockSections(World world, Random random, int x, int z, BiomeGrid biomes) {
		short[][]  output    = new short [16][  ];
		double[][] heightmap = new double[16][16];
		
		output[0] = new short[4096];
		output[1] = new short[4096];
		output[2] = new short[4096];
		output[3] = new short[4096];
		output[4] = new short[4096];
		//DONT
		for (int lx = 0 ; lx < 16 ; lx++){
			for (int lz = 0 ; lz < 16 ; lz++){
				heightmap[lx][lz] = SimplexImpl.noise((x*16+lx)*SCALE, (z*16+lz)*SCALE);
				if (heightmap[lx][lz] >= BASE+STEP*4){
					biomes.setBiome(lx, lz, Biome.JUNGLE);
					continue;
				}
				if (heightmap[lx][lz] >= BASE+STEP*2){
					biomes.setBiome(lx, lz, Biome.JUNGLE_HILLS);
					continue;
				}
				if (heightmap[lx][lz] >= BASE+STEP*1){
					biomes.setBiome(lx, lz, Biome.BIRCH_FOREST);
					continue;
				}
				if (heightmap[lx][lz] >= BASE+STEP*0){
					biomes.setBiome(lx, lz, Biome.TAIGA);
					continue;
				}
				biomes.setBiome(lx, lz, Biome.ICE_PLAINS_SPIKES); //default hardcoded biome for world
			}
		}
		
		//sector 0
		AddLayer(output[0],0 ,0 ,7 ); //bedrock filler
		AddLayer(output[0],1 ,3 ,49); //3 tick obsidian
		AddLayer(output[0],4 ,12,87); //8 tick netherack
		AddLayer(output[0],13,15,49); //3 tick obsidian
		
		//sector 1
		AddLayer(output[1],0,2 ,7);
		AddLayer(output[1],3,15,1); //this will be shroom cave level
		
		//sector 2
		AddLayer(output[2],0,15,1); //whis will be shroom cave level
		
		//shroom caves is secon step generation
		//i will remove stone and add mycelium based of noise value
		//after generation shape finished it will be optimized by merging with initial filling
		
		//sector 3
		AddLayer(output[3],0,11,1);
		
		for (int i = 4096-1024; i < 4096 ; i++)
		{
			
			int ordinates = i % 256;
			output[3][i] = SURFACE_OUTSIDE;
			if (biomes.getBiome(ordinates % 16, ordinates / 16) == Biome.JUNGLE_HILLS)
				output[3][i] = (i / 256 == 15) ? SURFACE_INSIDE_TOP : SURFACE_INSIDE ;
			if (biomes.getBiome(ordinates % 16, ordinates / 16) == Biome.JUNGLE)
				output[3][i] = (i / 256 == 15) ? SURFACE_INSIDE_TOP : SURFACE_INSIDE ;
			if (biomes.getBiome(ordinates % 16, ordinates / 16) == Biome.BIRCH_FOREST)
				output[3][i] = 10;
			if (biomes.getBiome(ordinates % 16, ordinates / 16) == Biome.TAIGA)
				output[3][i] = 49;
			
		}

		//this generates closed domes over jungles
		//jungle vegetation handled inside populator
		for (int i = 0 ; i < 4096 ; i++)
		{
			int ordinates = i % 256;
			if (heightmap[ordinates % 16][ordinates / 16] < BASE) continue;
			if (heightmap[ordinates % 16][ordinates / 16] > BASE+(STEP*4)) continue;
			
			int top = (int)((heightmap[ordinates % 16][ordinates / 16] - BASE) * 60);
			
			if ( i / 256 > top  ) continue;
			if ( i / 256 < top-2) continue;
			
			output[4][i] = BLOCK_DOME; //packed ice
		}
		
        return output;
    }

    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList((BlockPopulator)new Populator_DFS());
    }
    
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }
    
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 14,66,88);
    }
}