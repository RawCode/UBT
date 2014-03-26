package rc.ubt;

import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.NBTTagEnd;
import net.minecraft.server.v1_7_R1.NBTTagList;
import net.minecraft.server.v1_7_R1.NBTTagString;

import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import rc.ubt.impl.UnsafeImpl;

public class Tester implements Listener
{
	//this is test class, loader class will be static and everything esle will be modular
	
	//when loader class code finished, it wont change on updates, i will dump classfiles (or ever plain source texts)
	//into defined folder and system will load given classes
	//Tester class unlike all other classes can be defined on it's own
	//in my case i will load directly from workspace
	@EventHandler()
	public void OnChunkLoadEvent(ChunkLoadEvent e)
	{
		System.out.println("LOAD " + e.getChunk() + "--" + e.isNewChunk());
		//i want to know who loaded this chunk and who supports it
	}
	
	@EventHandler()
	public void OnChunkPopulateEvent(ChunkPopulateEvent e)
	{
		System.out.println("POPULATE " + e.getChunk());
		//i want to know who caused chunk population
	}
	
	@EventHandler()
	public void OnChunkUnloadEvent(ChunkUnloadEvent e)
	{
		System.out.println("UNLOAD " + e.getChunk());
		//i want to know who was last player in unloaded chunk
	}
}

