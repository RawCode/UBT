package rc.ubt.hnde;


import net.minecraft.server.v1_7_R2.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import rc.ubt.Loader;

public class ForcedRespawn implements Listener, Runnable {
	
	public ForcedRespawn(){Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);}
	
	public CraftPlayer target; 
	public ForcedRespawn(CraftPlayer s)
	{
		this.target = s;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeathEvent (PlayerDeathEvent e){
		Bukkit.getScheduler().runTask(Loader.INSTANCE, new ForcedRespawn((CraftPlayer) e.getEntity()));
		e.setDeathMessage(null);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerJoinEvent (PlayerJoinEvent e){
		Bukkit.getScheduler().runTask(Loader.INSTANCE, new ForcedRespawn((CraftPlayer) e.getPlayer()));
		e.setJoinMessage(null);
	}
	
	public void run() {
		
		if (!MinecraftServer.getServer().getPlayerList().players.contains(target.getHandle()))
			return;//do not process uncontrolled players
		
		if (target.getHandle().getHealth() > 0.0)
			return;//do not process alive players
		
		MinecraftServer.getServer().getPlayerList().moveToWorld(target.getHandle(), 0, false);
		target.getHandle().addScore(1);
		target.sendMessage(ChatColor.RED + "Your maximum health decreased by " + target.getHandle().getScore());
	}
}