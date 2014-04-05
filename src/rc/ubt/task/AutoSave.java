package rc.ubt.task;

import net.minecraft.server.v1_7_R2.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitScheduler;

import rc.ubt.Loader;
import rc.ubt.impl.PsExImpl;

public class AutoSave implements Runnable, Listener
{
	static String  PER        = "UBT.Bypass";
	static long    LAST_SAVE  = System.currentTimeMillis();
	static int 	   FRAME_SIZE = 1000*60*15;
	static boolean PROCESS    = false;
	static CraftChunk[] TOSAVE= null;
	static int     STEP       = 0;
	static boolean DISABLED   = false;
	static long    SAVECOUNT  = 0;
	static int     SKIP       = 0;
	static WorldServer WSH    = null;
	
	public AutoSave()
	{
		BukkitScheduler scheduler = Bukkit.getScheduler();
		scheduler.runTaskTimer(Loader.INSTANCE, new AutoSave(null),0,1L);
	}
	public AutoSave(Object o){}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event)  
	{
		String[] Data   = event.getMessage().toLowerCase().split("\\s+");
		if (Data.length == 0) return;
		String Order = Data[0].substring(1);
		
		if (Order.equals("cstoggle") && PsExImpl.has(event.getPlayer(),PER))
		{
			if (DISABLED)
			{
				//save currently disabled, it will be enabled
				//we set last save to now, in other case it will start save instantly
				LAST_SAVE = System.currentTimeMillis();
			}else
			{
				//save enabled, will stop it
				PROCESS = false;
				TOSAVE = null;
			}
			DISABLED = !DISABLED;
			event.getPlayer().sendMessage("IS SAVING DISABLED " + DISABLED);
			event.setCancelled(true);
		}
		if (Order.equals("csforce") && PsExImpl.has(event.getPlayer(),PER))
		{
			//restart saving if needed.
			DISABLED = false;
			PROCESS = false;
			LAST_SAVE = 0;
			event.getPlayer().sendMessage("FORCED SAVING PROCEDURE");
			event.setCancelled(true);
		}
	}
	/*
	String S1 = new String("проверка".getBytes("CP866"),"CP866"); //valid for logfile
	String S2 = new String("проверка".getBytes(),"CP866"); //not valid completely
	String S3 = new String("проверка".getBytes("CP866")); //valid for console
	String S4 = new String("проверка"); //valid for logfile*/
	
	//String S1 = new String("проверка".getBytes("CP1252"));
	//String S2 = new String("проверка".getBytes("CP1251")); //this is logfile encoding
	//String S3 = new String("проверка".getBytes("CP866")); //this is console encoding
	//String S4 = new String("проверка".getBytes("UTF-8"));
	//System.out.println(S1);
	//System.out.println(S2);
	//System.out.println(S3);
	//System.out.println(S4);
	//this check indicate that console window encoding is CP866 and internal encoding NOT cp866
	
	public void NOP()
	{
		if (PROCESS) return;
		long Offset = System.currentTimeMillis() - LAST_SAVE;
		if (Offset >= FRAME_SIZE)
		{
			PROCESS = true;
			TOSAVE = (CraftChunk[]) Bukkit.getWorld("world").getLoadedChunks();
			STEP = 0;
			SKIP = 0;
			SAVECOUNT = System.currentTimeMillis();
			Bukkit.broadcastMessage("EXPECTED " + TOSAVE.length + " CHUNKS");
			Bukkit.broadcastMessage("EXPECTED TIME " + TOSAVE.length/20 + " SECONDS");
		}
	}
	
	public boolean SaveIfPossible()
	{
		for(;STEP < TOSAVE.length;)
		{
			if (TOSAVE[STEP].isLoaded())
			{
				WSH.chunkProviderServer.saveChunk(TOSAVE[STEP].getHandle());
				//System.out.println("SAVED " + TOSAVE[STEP]);
				STEP++;
				return false;
			}
			//System.out.println("SKIPPED " + TOSAVE[STEP]);
			TOSAVE[STEP] = null;
			STEP++;
			SKIP++;
		}
		return true;
	}

	public void run()
	{
		if (WSH == null) WSH = ((CraftWorld)Bukkit.getWorld("world")).getHandle();
		if (DISABLED) return;

		NOP();

		if (!PROCESS) return;

		if (SaveIfPossible())
		{
			PROCESS = false;
			LAST_SAVE = System.currentTimeMillis();
			Bukkit.savePlayers();
			TOSAVE = null;
			Bukkit.broadcastMessage("SAVED " + (STEP-SKIP) + " SKIPPED " + SKIP);
			Bukkit.broadcastMessage("PASSED " + (System.currentTimeMillis() - SAVECOUNT)/1000 + " SECONDS");
		}
	}
}