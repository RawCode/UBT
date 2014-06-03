package rc.ubt.handlers;

import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.minecraft.server.v1_7_R3.NBTTagCompound;
import net.minecraft.server.v1_7_R3.NBTTagEnd;
import net.minecraft.server.v1_7_R3.NBTTagList;
import net.minecraft.server.v1_7_R3.NBTTagString;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import rc.ubt.Loader;
import rc.ubt.implementations.UnsafeImpl;

public class Tester implements Listener
{
	public Tester(){Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);}
	/** After loader implementation, other classes will load from arbitrary locations
	 * but this class will stay embedded
	 */
	
	//when loader class code finished, it wont change on updates, i will dump classfiles (or ever plain source texts)
	//into defined folder and system will load given classes
	//Tester class unlike all other classes can be defined on it's own
	//in my case i will load directly from workspace
	
	@EventHandler()
	public void OnChunkLoadEvent(ChunkLoadEvent e)
	{
		//System.out.println("LOAD " + e.getChunk() + "--" + e.isNewChunk());
		//i want to know who loaded this chunk and who supports it
	}
	
	@EventHandler()
	public void OnDeath(PlayerRespawnEvent e)
	{
		new Throwable().printStackTrace();
		//System.out.println("LOAD " + e.getChunk() + "--" + e.isNewChunk());
		//i want to know who loaded this chunk and who supports it
	}
	
	@EventHandler()
	public void OnChunkPopulateEvent(ChunkPopulateEvent e)
	{
		//System.out.println("POPULATE " + e.getChunk());
		//i want to know who caused chunk population
	}
	
	
	int pX;
	int pZ;
	
	boolean firstrun = true;
	boolean traceshow = true;
	
	//@EventHandler()
	public void OnChunkUnloadEvent(ChunkUnloadEvent e)
	{
		if (firstrun)
		{
			pX = e.getChunk().getX();
			pZ = e.getChunk().getZ();
			firstrun = false;
		}
		
		if (e.getChunk().getX() == pX & e.getChunk().getZ() == pZ)
		{
			System.out.println("UNLOAD " + e.getChunk());
			e.setCancelled(true);
			
			if (traceshow)
			{
				new Throwable().printStackTrace();
				traceshow = false;
			}
		}
	}
	
	@EventHandler()
	public void OnPlayerExpChangeEvent(PlayerExpChangeEvent e)
	{
		e.setAmount(0);
		System.out.println("XP EVENT");
	}
	
	@EventHandler()
	public void OnChat(AsyncPlayerChatEvent tt)
	{
		
		
		//tt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 10));
		
		if (true) return;
		
		FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
		
		PrintStream test = new PrintStream(new BufferedOutputStream(fdOut, 128), true);
		
		test.println("Elusive string");
		
		
		if (true) return;
		
		//we get item in hands
		CraftItemStack tz = (CraftItemStack) tt.getPlayer().getItemInHand();
		if (tz == null)return;
		//get internal handle of item
		net.minecraft.server.v1_7_R3.ItemStack handle = (net.minecraft.server.v1_7_R3.ItemStack) UnsafeImpl.getObject(tz, "handle");
		//forceset nbt tag of item to proper type
		handle.setTag(new NBTTagCompound());
		//setting random key wont work, client process data in predefined way
		//setting null list wont work, client will reset item on inventory open
		NBTTagList ttz = new NBTTagList();
		NBTTagCompound zz = new NBTTagCompound();
		zz.setShort("id", (short) 1);
		ttz.add(zz);
		handle.tag.set("ench", ttz);
		
		System.out.println("passed");
	}
}