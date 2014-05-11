package rc.ubt.cmde;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import rc.ubt.impl.PsExImpl;

public class Silencio
{
	public Silencio(){};
	
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
	static String   PER   = "UBT.Bypass";
	
	static int TYPE_INFO = 0x01; //Non punishment information
	static int TYPE_GEN  = 0x02; //Generic punishment
	static int TYPE_HEL  = 0x04; //Silent punishment
	static int TYPE_PERM = 0x08; //Persistent punishment
	
	String Target;
	String Source;
	long   Stamp;
	String Reason;
	int    Flags;
	
	
	static HashMap<String,Silencio> MAP = new HashMap<String,Silencio>();

	public Silencio(String Target, String Source, long Stamp, String Reason, int Flags)
	{
		this.Target = Target;
		this.Source = Source;
		this.Stamp  = Stamp;
		this.Reason = Reason;
		this.Flags  = Flags;
		MAP.put(Target, this);
	};
	
	
	public String toString()
	{
		String B = " : ";
		if ((Flags & TYPE_HEL) == 0)
		{
			return "H#" + Target + " by " + Source + " Expire in " + 
		return "";
	}
	
	public static long ProcessDelay(String Input)
	{
		byte[] RawData = Input.getBytes();
		
		int buffer   = 0;
		int brate    = 1;
		int multiply = 0;
		int result   = 0;
		int temp     = 0;
		int step     = RawData.length-1;
		
		
		for(;;)
		{
			temp = Key2Value[RawData[step]];
			
			if (temp == 1488) return -1;
			
			if (temp > 10)
			{
				if (multiply != 0)
				{
					result+= buffer * multiply;
				}
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
			if (!PsExImpl.has(event.getPlayer(), PER)) return;
			
			if (MAP.isEmpty())
			{
				event.getPlayer().sendMessage("Игроков с банчатом нет. Быть может, пора исправить положение?");
				return;
			}
			
			event.getPlayer().sendMessage("Список банов чата:");
			
			Iterator<Silencio> i = MAP.values().iterator();
			
			while(i.hasNext())
			{
				Silencio s = i.next();
				event.getPlayer().sendMessage(s.toString());
			}
			event.setCancelled(true);
			return;
		}

		if (Order.equals("mute") || Order.equals("fmute"))
		{
			hellish = Order.equals("fmute");
			if (!PsExImpl.has(event.getPlayer(), PER)) return;
			if (Data.length < 3)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "необходимо указывать ник цели, срок мута и комментарий");
				return;
			}
			
			long Delay = ProcessDelay(Data[2]);
			if (Delay < 0)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "срок указывается числом, допустимы указатели m, h, d");
				return;
			}
			String Cause = event.getMessage().substring(event.getMessage().indexOf(Data[2]));
			
			if (hellish)
				Bukkit.broadcastMessage(event.getPlayer().getName() + ChatColor.RED + " умолчал " + Data[1] + " на " + Delay + " секунд.");
			else
			{
				event.getPlayer().sendMessage(ChatColor.RED + Data[1] + " толсто заткнут на " + Delay + " секунд.");
				event.getPlayer().sendMessage(ChatColor.RED + "используйте с умом цель не знает, что она в муте");
			}
			
			Delay*= 1000;
			Delay+= System.currentTimeMillis();
			
			new Silencio(Data[1],event.getPlayer().getName(),Delay,Cause,hellish ? TYPE_GEN : TYPE_HEL);
			
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
	public void AsyncPlayerChatEvent(AsyncPlayerChatEvent event)  {
		
		String Name = event.getPlayer().getName().toLowerCase();
		Silencio s = MAP.get(Name);
		
		if (s == null) return;
		
		if (w.Expiration < System.currentTimeMillis())
		{
			Registry.remove(Name);
			Bukkit.getPlayer(w.Admin).sendMessage(ChatColor.RED + "��� ������ " + Name + " ����");
			return;
		}
		
		if (w.Options == 1)
		{
			event.getRecipients().clear();
			event.getRecipients().add(event.getPlayer());
			return;
		}

		event.getPlayer().sendMessage(ChatColor.RED + "��� ��������� ������������ ��� ��������������� " 
		+ w.Admin + " �� ������� " + w.Reason + " �������� " + (w.Expiration - System.currentTimeMillis()) / 1000 + " ������");
		event.setCancelled(true); 
	}
}
