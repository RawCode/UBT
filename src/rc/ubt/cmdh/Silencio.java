package rc.ubt.cmdh;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import rc.ubt.Loader;
import rc.ubt.impl.PsExImpl;

public class Silencio implements Listener
{
	public Silencio(){Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);}
	
	static public int[] Key2Value = new int[256];
	/** <cinit> section */ static
	{
		Arrays.fill(Key2Value, 1488);
		Key2Value['0'] = 0;
		Key2Value['1'] = 1;
		Key2Value['2'] = 2;
		Key2Value['3'] = 3;
		Key2Value['4'] = 4;
		Key2Value['5'] = 5;
		Key2Value['6'] = 6;
		Key2Value['7'] = 7;
		Key2Value['8'] = 8;
		Key2Value['9'] = 9;
		Key2Value['m'] = 60;
		Key2Value['h'] = 3600;
		Key2Value['d'] = 86400;
	}
	
	//flags used for punishment tracking
	//kicking player and banning for short time (not persistent)
	//also can be stored here
	static String PER_GENERIC	= "UBT.Mute";
	static String PER_SUPER		= "UBT.sMute";
	
	//flags added by += operator and extracted by & operator
	static int TYPE_INFO = 0x01; //Non punishment information
	static int TYPE_GEN  = 0x02; //Generic punishment
	static int TYPE_HEL  = 0x04; //Silent punishment
	static int TYPE_PERM = 0x08; //Persistent punishment
	
	/** container section start*/
	static HashMap<String,Silencio> MAP = new HashMap<String,Silencio>();
	String Target;
	String Source;
	long   Stamp;
	String Reason;
	int    Flags;
	
	public Silencio(String Target, String Source, long Stamp, String Reason, int Flags)
	{
		this.Target = Target;
		this.Source = Source;
		this.Stamp  = Stamp;
		this.Reason = Reason;
		this.Flags  = Flags;
		MAP.put(Target, this);
	};
	/** container section end*/
	
	public String toString()
	{
		return Target + " by " + Source + " expire in " + ReverseDelay(Stamp) + " cause " + Reason;
	}
		
	public static String ReverseDelay(long Input)
	{
		long now = System.currentTimeMillis();
		long tmp = ( Input - now ) / 1000;
		
		if (tmp < 1)
			return "expired";
		
		long d = tmp / 86400;
		if (d > 0)
			tmp-= d * 86400;
		
		long h = tmp / 3600;
		if (h > 0)
			tmp-= h * 3600;
		
		long m = tmp / 60;
		
		if (m <= 0 & h <= 0 & d <= 0)
			return "soon";
		
		return m+"m"+(h > 0 ? h+"h" : "")+(d > 0 ? d + "d" : "");
	}
	
	public static long ProcessDelay(String Input)
	{
		byte[] RawData = Input.getBytes();
		
		int buffer   = 0;
		int brate    = 1;
		int multiply = 1;
		int result   = 0;
		int temp     = 0;
		int step     = RawData.length-1;
		
		
		for(;;)
		{
			temp = Key2Value[RawData[step]];
			
			if (temp == 1488) return -1;
			
			if (temp > 10)
			{
				result+= buffer * multiply;
				multiply = temp;
				buffer = 0;
				brate = 1;
			}
			else
			{
				buffer+= temp * brate;
				brate*= 10;
			}
			
			step--;
			
			if (step == -1)
			{
				result+= buffer * multiply;
				return result;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void InvokeCommand(PlayerCommandPreprocessEvent event)  {
		String[] Data   = event.getMessage().toLowerCase().split("\\s+");
		if (Data.length == 0) return;
		String Order = Data[0].substring(1);
		boolean hellish = false;
		
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
		
		if (Order.equals("mlist"))
		{
			if (!PsExImpl.has(event.getPlayer(), PER_SUPER)) return;
			
			if (MAP.isEmpty())
			{
				event.getPlayer().sendMessage("�?гроков с банчатом нет. Быть может, пора исправить положение?");
				return;
			}
			
			event.getPlayer().sendMessage("Список банов чата:");
			
			Iterator<Silencio> i = MAP.values().iterator();
			boolean b = false;
			while(i.hasNext())
			{
				Silencio s = i.next();
				b = !b;
				event.getPlayer().sendMessage((b ? ChatColor.GRAY : ChatColor.DARK_GRAY) + s.toString());
			}
			event.setCancelled(true);
			return;
		}

		if (Order.equals("mute") || Order.equals("fmute"))
		{
			if (!PsExImpl.has(event.getPlayer(), PER_GENERIC)) return;
			hellish = Order.equals("fmute") && PsExImpl.has(event.getPlayer(), PER_SUPER);
			//Hellbanning allowed only for users with special permission
			if (Data.length < 4)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "Необходимо указывать ник цели, срок мута и комментарий");
				event.setCancelled(true);
				return;
			}
			
			long Delay = ProcessDelay(Data[2]);
			if (Delay < 0)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "срок указывается числом, допустимы степени m, h, d");
				return;
			}
			String Cause = event.getMessage().substring(event.getMessage().indexOf(Data[2]));
			
			if (hellish)
			{
				event.getPlayer().sendMessage(ChatColor.YELLOW + Data[1] + " толсто заткнут на " + Delay + " секунд.");
				event.getPlayer().sendMessage(ChatColor.RED + "�?спользуйте с умом цель не знает, что она в муте");
			}
			else
			{
				Bukkit.broadcastMessage(event.getPlayer().getName() + ChatColor.RED + " умолчал " + Data[1] + " на " + Delay + " секунд.");
			}
			
			Delay*= 1000;
			Delay+= System.currentTimeMillis();
			
			new Silencio(Data[1],event.getPlayer().getName(),Delay,Cause,hellish ? TYPE_HEL : TYPE_GEN);
			
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event)  {
		
		String Name = event.getPlayer().getName().toLowerCase();
		Silencio s = MAP.get(Name);

		//there is no such player in map
		if (s == null) return;
		
		if (s.Stamp < System.currentTimeMillis())
		{
			MAP.remove(Name);
			@SuppressWarnings("deprecation")
			Player source = Bukkit.getPlayer(s.Source);
			if (source != null)
				source.sendMessage(ChatColor.RED + "Наказание игрока "+ ChatColor.YELLOW + Name + " истекло!");
			return;
		}
		
		if ((s.Flags & TYPE_HEL) != 0)
		{
			event.getRecipients().clear();
			event.getRecipients().add(event.getPlayer());
			return;
		}

		event.getPlayer().sendMessage(ChatColor.RED + "Чат запрещен администратором " 
		+ s.Source + " причина " + s.Reason + " осталось " + ReverseDelay(s.Stamp));
		event.setCancelled(true); 
	}
}