package rc.ubt;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import sun.reflect.Reflection;

public class BlockPlaceEventEx extends BlockPlaceEvent
{
	public BlockPlaceEventEx(Block placedBlock, BlockState replacedBlockState, Block placedAgainst,
			ItemStack itemInHand, Player thePlayer, boolean canBuild) {
		super(placedBlock, replacedBlockState, placedAgainst, itemInHand, thePlayer, canBuild);
	}
	
	@Override
	public void setCancelled(boolean b)
	{
		System.out.println(Reflection.getCallerClass());
	}
}