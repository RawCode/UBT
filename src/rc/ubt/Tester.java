package rc.ubt;

import net.minecraft.server.v1_7_R2.NBTTagCompound;
import net.minecraft.server.v1_7_R2.NBTTagEnd;
import net.minecraft.server.v1_7_R2.NBTTagList;
import net.minecraft.server.v1_7_R2.NBTTagString;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R2.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import rc.ubt.impl.UnsafeImpl;

public class Tester implements Listener
{
	public Tester()
	{
		Bukkit.getPluginManager().registerEvents(this, Loader.INSTANCE);
	}
	//this is test class, loader class will be static and everything esle will be modular
	
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
	public void OnChunkPopulateEvent(ChunkPopulateEvent e)
	{
		//System.out.println("POPULATE " + e.getChunk());
		//i want to know who caused chunk population
	}
	
	@EventHandler()
	public void OnChunkUnloadEvent(ChunkUnloadEvent e)
	{
		//System.out.println("UNLOAD " + e.getChunk());
		//i want to know who was last player in unloaded chunk
	}
	
	@EventHandler()
	public void OnChat(AsyncPlayerChatEvent tt)
	{
		//we get item in hands
		CraftItemStack tz = (CraftItemStack) tt.getPlayer().getItemInHand();
		if (tz == null)return;
		//get internal handle of item
		net.minecraft.server.v1_7_R2.ItemStack handle = (net.minecraft.server.v1_7_R2.ItemStack) UnsafeImpl.getObject(tz, "handle");
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