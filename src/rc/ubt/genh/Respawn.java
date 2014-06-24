package rc.ubt.genh;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import rc.ubt.Loader;
import rc.ubt.impl.UnsafeImpl;

public class Respawn implements Listener {
	
	public Respawn(){Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void _EntityDamageEvent(PlayerJoinEvent e)
	{
		CraftPlayer ep = (CraftPlayer) e.getPlayer();
		
		System.out.println(UnsafeImpl.Object2Trace(ep));
		try
		{
			ep = (CraftPlayer) UnsafeImpl.unsafe.allocateInstance(CraftPlayerImpl.class);
		} catch (InstantiationException e1)
		{
			e1.printStackTrace();
		}
		System.out.println(UnsafeImpl.Object2Trace(ep));
		
		int newclassid = UnsafeImpl.unsafe.getInt(ep, 8L);
		
		UnsafeImpl.unsafe.putInt(e.getPlayer(), 8L,newclassid);
		
		ep.sendMessage("TESTING");
		
		t.put(e.getPlayer().getName(), new HashMapAnchor());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void _EntityDamageEvent(PlayerQuitEvent e)
	{
		
		t.remove(e.getPlayer().getName());
	}
	
	
	
	static HashMap t = new HashMap();
}