package rc.ubt.handlers;


import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.MinecraftServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import rc.ubt.Loader;

public class ForcedRespawn implements Listener, Runnable {
	
	public ForcedRespawn(){Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);}
	
	public EntityPlayer target; 
	public ForcedRespawn(EntityPlayer ep)
	{
		this.target = ep;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeathEvent (PlayerDeathEvent e){
		Bukkit.getScheduler().runTask(Loader.INSTANCE, new ForcedRespawn(((CraftPlayer) e.getEntity()).getHandle()));
		e.setDeathMessage(null);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerJoinEvent (PlayerJoinEvent e){
		Bukkit.getScheduler().runTask(Loader.INSTANCE, new ForcedRespawn(((CraftPlayer) e.getPlayer()).getHandle()));
		e.setJoinMessage(null);
	}
	
	public void run() {
		if (!MinecraftServer.getServer().getPlayerList().players.contains(target))
			return;//do not process uncontrolled players
		
		if (target.getHealth() > 0.0)
			return;//do not process alive players
		
		MinecraftServer.getServer().getPlayerList().moveToWorld(target, 0, false);
		
		if (!Loader.CONVERSION) return;
		//permanent death tracking
		
		target.addScore(1);
		target.getBukkitEntity().sendMessage(ChatColor.RED + "Your maximum health decreased by " + target.getScore());
	}
}