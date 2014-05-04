package rc.ubt.nmsm;
//base class modification
import net.minecraft.server.v1_7_R3.Block;
import net.minecraft.server.v1_7_R3.Material;

public class Block_Base extends Block {
	
	//must update names and methods to follow latest bukkit r04
	
	
	
	public Block_Base(Material material) {
        super(material);
    }
	
	public Block_Base b(float f) {
        this.durability = f * 3.0F;
        return this;
    }
	
	public Block_Base c(float f) {
        this.strength = f;
        if (this.durability < f * 5.0F) {
            this.durability = f * 5.0F;
        }
        return this;
    }
}