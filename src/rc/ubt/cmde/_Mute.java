package rc.ubt.cmde;
//command executor
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class _Mute implements Listener {
	
	//modular design require improving and refactoring
	//probably mute will be merged with something and form something like Censor or similar plugin
	//with large array of features
	
	//i already can see list of forbidden words and infractions on users
	

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event)  {
		String[] Data   = event.getMessage().toLowerCase().split("\\s+");
		if (Data.length == 0) return;
		String Order = Data[0].substring(1);

		/**
		 * 0 сама команда
		 * 1 ник игрока
		 * 2 время
		 * 3 причина
		 * 4-999 причина
		 * 
		 * если указан только ник - навсегда
		 * если указано только время - без причины
		 */
		event.setCancelled(true);
		if (Order.equals("mute"))
		{
			if (Data.length < 3)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "имя игрока срок мута и комментарий");
				return;
			}
			PermissionUser user = PermissionsEx.getUser(event.getPlayer());
			if (!user.has("RawDev.Mute")){
				event.getPlayer().sendMessage(ChatColor.RED + "необходим узел RawDev.Mute");
				return;
			}

			long Time = 60000;
			try{Time = Integer.parseInt(Data[2])*1000;} catch (Exception e)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "намутили со сроком, должно быть число написанное обычными цифрами");
				return;
			}
			
	        StringBuilder message = new StringBuilder();
	        byte c = 0;
	        for (String arg : Data) {
	        	c++;
	        	if (c <= 3)continue;
	            message.append(arg);
	            message.append(" ");
	        }
			
			Bukkit.broadcastMessage(event.getPlayer().getName() + ChatColor.RED + " умолчал " + Data[1] + " на " + Time/1000 + " секунд.");
			
	        Time += System.currentTimeMillis();
	        
	        Wrapper w = new Wrapper(event.getPlayer().getName(),Time,message.toString().trim(),0);
			Registry.put(Data[1], w);
			return;
		}
		
		
		if (Order.equals("fmute"))
		{
			if (Data.length < 2)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "имя игрока и срок толстого мута");
				return;
			}
			PermissionUser user = PermissionsEx.getUser(event.getPlayer());
			if (!user.has("RawDev.fMute")){
				event.getPlayer().sendMessage(ChatColor.RED + "необходим узел RawDev.fMute");
				return;
			}

			long Time = 60000;
			try{Time = Integer.parseInt(Data[2])*1000;} catch (Exception e)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "намутили со сроком, должно быть число");
				return;
			}
			
			event.getPlayer().sendMessage(ChatColor.RED + Data[1] + " толсто заткнут на " + Time/1000 + " секунд.");
			event.getPlayer().sendMessage(ChatColor.RED + "используйте с умом цель не знает, что она в муте");
			
	        Time += System.currentTimeMillis();
	        
	        Wrapper w = new Wrapper(event.getPlayer().getName(),Time,"",1);
			Registry.put(Data[1], w);
			return;
		}
		
		if (Order.equals("mlist"))
		{
			PermissionUser user = PermissionsEx.getUser(event.getPlayer());
			if (!user.has("RawDev.mList")){
				event.getPlayer().sendMessage(ChatColor.RED + "необходим узел RawDev.mList");
				return;
			}
			
			if (Registry.isEmpty())
			{
				event.getPlayer().sendMessage("Игроков с банчатом нет. Быть может, пора исправить положение?");
				return;
			}
			
			event.getPlayer().sendMessage("Список банов чата:");
			for (Map.Entry<String, Wrapper> entry : Registry.entrySet())
			{
				StringBuilder sb = new StringBuilder();
				if (entry.getValue().Options == 1 && user.has("RawDev.mList.fat"))
				{
					sb.append(ChatColor.BOLD + "(FAT) ");
				}
				sb.append(ChatColor.RED + entry.getKey() + " - " + (entry.getValue().Expiration - System.currentTimeMillis()) / 1000 + " сек. осталось. Выдал "
				+ entry.getValue().Admin + ": " + entry.getValue().Reason);
				event.getPlayer().sendMessage(sb.toString());
			}
			return;
		}
		event.setCancelled(false);
	}
	
}