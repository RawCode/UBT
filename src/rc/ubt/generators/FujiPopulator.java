package rc.ubt.generators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;

import rc.ubt.implementations.SimplexImpl;

public class FujiPopulator extends BlockPopulator 
{
	//generic howto refactoring required.
	
	static final double SCALE = 0.0625d;
	static final Random RND   = new Random(0);
	
    public static int abs_int(int par0)
    {
        return par0 >= 0 ? par0 : -par0;
    }

	@SuppressWarnings("deprecation")
	public void SpawnShrub(World w, int X, int Z){
		if (w.getBiome(X, Z) != Biome.JUNGLE)
			return;
		
		for (int var8 = 64; var8 <= 67; ++var8)
		{
			int var9 = var8 - 64;
			int var10 = 2 - var9;

			for (int var11 = X - var10; var11 <= X + var10; ++var11)
			{
				int var12 = var11 - X;

				for (int var13 = Z - var10; var13 <= Z + var10; ++var13)
				{
					int var14 = var13 - Z;

					if ((Math.abs(var12) != var10 || Math.abs(var14) != var10 || RND.nextInt(2) != 0))
					{
						w.getBlockAt(var11, var8, var13).setTypeIdAndData(18,(byte) 3,true);
					}
				}
			}
		}
		w.getBlockAt(X, 64, Z).setTypeIdAndData(17,(byte) 3,true);
	}
	
	public static int fastfloor(double x) 
	{
		return x >= 0 ? (int) x : (int) x - 1;
	}
	
    public void SpawnOre(World w, int X, int Y,int Z,Material Source,Material Ore)
    {
    	//size is fixed, max chain size is 8 for now
    	
    	int cx = X;
    	int cy = Y;
    	int cz = Z;
    	
    	for (int i = 12 ; i > 0 ; i--)
    	{
    		if (cy < 0) cy = 1;
        	if (w.getBlockAt(cx, cy, cz).getType() == Source)
        	{
        		w.getBlockAt(cx, cy, cz).setType(Ore);
        	}
        	
        	switch (RND.nextInt(7))
        	{
        		case 0: cx++; break;
        		case 1: cy++; break;
        		case 2: cz++; break;
        		case 3: cx--; break;
        		case 4: cy--; break;
        		case 5: cz--; break;
        		case 6: cx = X; cy = Y; cz = Z; break;
        	}
    	}
    }
    
    public void populate(World w, Random random, Chunk source) {
    	int X = source.getX();
    	int Z = source.getZ();
    	
		RND.setSeed((long)(X)*341873128712L + (long)(Z)*132897987541L);
    	double R = SimplexImpl.noise(X*SCALE, Z*SCALE)+1.0d;
    	X *= 16;
    	Z *= 16;
    	
    	for (int p = 16 ; p > 0 ; p--)
    	{
    		SpawnShrub(w,X+RND.nextInt(16),Z+RND.nextInt(16));
    	}
    	X += 2;
    	Z += 2;
    	Material m = Material.STONE;
    	for (int p = (int) (16/R) ; p > 0 ; p--)
    	{
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(40),Z+RND.nextInt(16),m,Material.CLAY);
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(40),Z+RND.nextInt(16),m,Material.SAND);
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(40),Z+RND.nextInt(16),m,Material.GRAVEL);
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(40),Z+RND.nextInt(16),m,Material.DIRT);
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(40),Z+RND.nextInt(16),m,Material.COAL_ORE);
    		
    		SpawnOre(w,X+RND.nextInt(16),3+RND.nextInt(20),Z+RND.nextInt(16),Material.OBSIDIAN,Material.GLOWSTONE);
    		SpawnOre(w,X+RND.nextInt(16),3+RND.nextInt(20),Z+RND.nextInt(16),Material.NETHERRACK,Material.QUARTZ_ORE);
    	}

    	for (int p = (int) (8/R) ; p > 0 ; p--)
    	{
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(30),Z+RND.nextInt(16),m,Material.IRON_ORE);
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(20),Z+RND.nextInt(16),m,Material.GOLD_ORE);
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(20),Z+RND.nextInt(16),m,Material.LAPIS_ORE);

    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(15),Z+RND.nextInt(16),m,Material.REDSTONE_ORE);
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(10),Z+RND.nextInt(16),m,Material.DIAMOND_ORE);
    		SpawnOre(w,X+RND.nextInt(16),20+RND.nextInt(5 ),Z+RND.nextInt(16),m,Material.EMERALD_ORE);
    	}
    }
}