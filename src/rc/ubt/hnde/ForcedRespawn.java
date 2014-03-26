package rc.ubt.hnde;
//event handler
import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.MinecraftServer;
import net.minecraft.server.v1_7_R1.PlayerConnection;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import rc.ubt.Loader;

public class ForcedRespawn implements Listener, Runnable {
	
	public ForcedRespawn()
	{
		Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);
	}
	
	public String target; 
	public ForcedRespawn(String e)
	{
		this.target = e;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeathEvent (PlayerDeathEvent e){
		BukkitScheduler scheduler = Bukkit.getScheduler();
		scheduler.runTask(Loader.INSTANCE, new ForcedRespawn(e.getEntity().getName()));
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerJoinEvent (PlayerJoinEvent e){
		if (!e.getPlayer().isDead())
			return;
		BukkitScheduler scheduler = Bukkit.getScheduler();
		scheduler.runTask(Loader.INSTANCE, new ForcedRespawn(e.getPlayer().getName()));
	}

	public void run() {
		CraftPlayer CP = (CraftPlayer) Bukkit.getPlayer(target);
		if (CP == null)
		{
			return;
		}
		EntityPlayer EP = CP.getHandle();
		if (EP.getHealth() > 0.)
			return;
		PlayerConnection PC = EP.playerConnection;
		PC.player = MinecraftServer.getServer().getPlayerList().moveToWorld(EP, 0, false);
	}
}