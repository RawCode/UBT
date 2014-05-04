package rc.ubt.nmsm;

import java.util.Random;

import net.minecraft.server.v1_7_R3.CreativeModeTab;
import net.minecraft.server.v1_7_R3.Material;


public class Block_Cobble extends Block_Base {

	public Block_Cobble() {
        super(Material.GRASS);
        a(true);
        a(CreativeModeTab.b);
    }

    public void a(net.minecraft.server.v1_7_R3.World world, int i, int j, int k, Random random)
    {
        if(!world.isStatic){}
    }
}