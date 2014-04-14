package rc.ubt;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import rc.ubt.impl.UnsafeImpl;

public class RandomClassC implements Listener
{
	public RandomClassC()
	{
		Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void OnBlockPlaceEvent(BlockPlaceEvent e)
	{
		//this class a bit different, it have lower priority and wont attempt to cancell anything
		//instead it will alter class of event passed into it
		int REF = UnsafeImpl.unsafe.getInt(new BlockPlaceEventEx(null, null, null, null, null, false), 8l);
		System.out.println(REF);
		UnsafeImpl.unsafe.putInt(e, 8l, REF);
	}
}
