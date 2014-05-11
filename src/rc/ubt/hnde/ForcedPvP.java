package rc.ubt.hnde;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import rc.ubt.Loader;

public class ForcedPvP implements Listener {
	
	public ForcedPvP(){Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);}
	
	static String                   PER   = "UBT.Bypass";
	static long 					DELAY = 1000 * 20;
	static HashMap<String,ForcedPvP>MAP   = new HashMap<String, ForcedPvP>();
	static boolean                  SYNC  = false;

	//we need "no more valid entries event"
	//this event will time to time and just GC entire map
	//it will trigger if VALIDKEYS is ZERO and HashMap size NOT zero

	// no way to use any type of counter
	// just shoud check latest allocation on each deallocation
	// if latest allocation was "long ago" just wipe map

	long 	TimeStamp;
	String 	Source;
	static private String Player2Key(Player p){return p.getName().toLowerCase();}
	ForcedPvP(Player Target,String Owner,long Offset)
	{
		this.TimeStamp = Offset;
		this.Source    = Owner;
		MAP.put(Player2Key(Target), this);
	}

	@EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
	public void onPlayerKickEvent(PlayerKickEvent event){
		MAP.remove(Player2Key(event.getPlayer()));
		//dont kill players from kicks
	}

	@EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
	public void onPlayerDeathEvent(PlayerDeathEvent event){
		if (SYNC) return;
		MAP.remove(event.getEntity().getName().toLowerCase());
	}

	@EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		String Key = Player2Key(event.getPlayer());
		ForcedPvP Container = MAP.get(Key);
		if (Container == null) return;

		long TIME = System.currentTimeMillis();

		if (TIME - Container.TimeStamp <= DELAY){
			SYNC = true;
			event.getPlayer().damage(19d);
			SYNC = false;
			Bukkit.broadcastMessage(ChatColor.RED + event.getPlayer().getDisplayName() + 
					" покинул игру во время боя с " + Container.Source + " и был наказан!");
			return;
		}
		MAP.remove(Key);
	}

	@EventHandler(priority = EventPriority.HIGHEST,ignoreCancelled = true)
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		String Key = Player2Key(event.getPlayer());
		ForcedPvP Container = MAP.get(Key);
		if (Container == null) return;

		event.setJoinMessage(ChatColor.RED + event.getPlayer().getDisplayName() + 
				" зашел в игру мёртвым, так как ранее вышел из боя с " + Container.Source);
		MAP.remove(Key);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event)  
	{

		String Key = Player2Key(event.getPlayer());
		ForcedPvP Container = MAP.get(Key);
		if (Container == null) return;

		long TIME = System.currentTimeMillis();

		if (TIME - Container.TimeStamp <= DELAY) 
		{
			event.getPlayer().sendMessage(ChatColor.RED + 
					"Нельзя использовать команды во время боя с " + Container.Source);
			event.setCancelled(true);
			return;
		}
		MAP.remove(Key);
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void EntityDamageEvent(EntityDamageByEntityEvent event)
	{
		if (!(event.getEntity() instanceof Player))
			return;

		Entity source = event.getDamager();

		if (source instanceof Projectile)
			source = ((Projectile) source).getShooter();

		if (!(source instanceof Player))
			return;
		new ForcedPvP((Player) event.getEntity(),((Player) source).getDisplayName(),System.currentTimeMillis());
	}
}