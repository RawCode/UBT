package rc.ubt.hnde;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;

import rc.ubt.Loader;
import rc.ubt.impl.PsExImpl;

public class CustomLogin implements Listener {
	public CustomLogin(){Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);}
	
	static String                      PER   = "UBT.Bypass";
	static public HashMap<String,Long> MAP   = new HashMap<String,Long>();
	static long 					   DELAY = 1000 * 10;
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void CountdownQuit(PlayerQuitEvent event){
		
		if (PsExImpl.has(event.getPlayer(), PER)) return;
		
		MAP.put(event.getPlayer().getName().toLowerCase(), new Long(System.currentTimeMillis()));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void CountdownLogin(PlayerLoginEvent event) {
		
		Long offset = MAP.get(event.getPlayer().getName().toLowerCase());
		if (offset == null) return;
		
		long passed = System.currentTimeMillis() - offset;
		if (passed <= DELAY){
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "ÎÆÈÄÀÉÒÅ " + (DELAY - passed)/1000 + " ÑÅÊÓÍÄ");
			return;
		}
    }
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void WhitelistLogin(PlayerLoginEvent event) {
		if (event.getResult() == Result.KICK_WHITELIST)
		{
			if (PsExImpl.has(event.getPlayer(), PER))
			{
				event.allow();
				return;
			}
			event.setKickMessage(ChatColor.RED+"ÒÅÕÐÀÁÎÒÛ");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void WhitelistPing(ServerListPingEvent event)  {
		
		if (Bukkit.getServer().hasWhitelist())
		{
			event.setMotd("ÒÅÕÐÀÁÎÒÛ");
			event.setMaxPlayers(0);
			return;
		}
	}
}