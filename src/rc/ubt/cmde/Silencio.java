package rc.ubt.cmde;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Silencio
{
	public Silencio(){};
	
	String Source;
	long   Stamp;
	String Reason;
	byte   Mode;
	
	static Map<String,Silencio> MAP = new HashMap<String,Silencio>();
	public Silencio(Player p, String Source,long Stamp,String Reason,byte Mode)
	{
		this.Source = Source;
		this.Stamp  = Stamp;
		this.Reason = Reason;
		this.Mode   = Mode;
		MAP.put(Player2Key(p), this);
	};
	
	static private String Player2Key(Player p)
	{
		if (p == null) return "null";
		return p.getName().toLowerCase();
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event)  {
		
		String Name = event.getPlayer().getName().toLowerCase();
		Silencio s = MAP.get(Name);
		
		if (s == null) return;
		
		if (w.Expiration < System.currentTimeMillis())
		{
			Registry.remove(Name);
			Bukkit.getPlayer(w.Admin).sendMessage(ChatColor.RED + "Мут игрока " + Name + " истёк");
			return;
		}
		
		if (w.Options == 1)
		{
			event.getRecipients().clear();
			event.getRecipients().add(event.getPlayer());
			return;
		}

		event.getPlayer().sendMessage(ChatColor.RED + "Вам запрещено использовать чат администратором " 
		+ w.Admin + " по причине " + w.Reason + " осталось " + (w.Expiration - System.currentTimeMillis()) / 1000 + " секунд");
		event.setCancelled(true); 
	}
}
