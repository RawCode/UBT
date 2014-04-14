package rc.ubt;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class RandomClassB implements Listener
{
	public RandomClassB()
	{
		Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);
	}
	
	@EventHandler()
	public void OnBlockPlaceEvent(BlockPlaceEvent e)
	{
		if (Math.random() > 0.5)
		{
			e.getPlayer().sendMessage("Cancelled by class B");
			e.setCancelled(true);
		}
	}
}
