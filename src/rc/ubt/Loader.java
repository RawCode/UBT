package rc.ubt;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Map;

import net.minecraft.server.v1_7_R3.WorldServer;
import net.minecraft.server.v1_7_R3.WorldType;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;

import rc.ubt.cmde.Silencio;
import rc.ubt.hnde.CustomLogin;
import rc.ubt.hnde.ForcedPvP;
import rc.ubt.hnde.ForcedRespawn;
import rc.ubt.impl.PsExImpl;
import rc.ubt.impl.UnsafeImpl;
import rc.ubt.task.AutoSave;
import rc.ubt.wgen.Generator_DFS;

@SuppressWarnings("all")
public class Loader extends JavaPlugin implements Runnable
{
	/** It will be nice to see list of modules with desired loading order
	 * name.class,CINIT,INIT,LOAD,PREWORLD,POSTWORLD,FIRSTTICK,DELAYED.AFTER PLUGIN X
	 */
	public static JavaPlugin INSTANCE;
	/** <init> section */{
		INSTANCE = this;
	}
	
	public static boolean CONVERSION = false;
	public static boolean DEBUGGING  = false;
	public static boolean TESTING    = false;
	
	public boolean ConfigBoolean(String Key, boolean Default)
	{
		if (this.getConfig().contains(Key))
			return this.getConfig().getBoolean(Key);
		this.getConfig().set(Key, Default);
		return Default;
	}
	
	public void onLoad()
	{
		CONVERSION = ConfigBoolean("isConversion",false);
		DEBUGGING  = ConfigBoolean("isDebugging" ,false);
		TESTING    = ConfigBoolean("isTesting"   ,false);
		
		saveConfig();
		
		if (DEBUGGING)
			((org.apache.logging.log4j.core.Logger) LogManager.getLogger()).setLevel(Level.DEBUG);

		/** Unwanted <vanilla> commands */
		SimpleCommandMap scm = ((CraftServer)Bukkit.getServer()).getCommandMap();
		Map knownCommands = (Map) UnsafeImpl.getObject(scm, "knownCommands");
		knownCommands.remove("reload");
		knownCommands.remove("help");
		knownCommands.remove("list");
		knownCommands.remove("seed");
		knownCommands.remove("me");
		
		/** If server running in conversion mode - replace world generator */
		if (CONVERSION){
			LogManager.getLogger().debug("Forcing world generator");
			YamlConfiguration YC = (YamlConfiguration) UnsafeImpl.getObject(Bukkit.getServer(), "configuration");
			ConfigurationSection ss = YC.createSection("worlds");
			ss = ss.createSection("world");
			ss.set("generator", "UBT");
			
			LogManager.getLogger().debug("Forcing plugin loadorder");
			UnsafeImpl.putObject(this.getDescription(), PluginLoadOrder.STARTUP, "order");
		}
	}
	
	static void Load(File Target)
	{
		//this is low level classloader, it will read classfile and construct class from it.
		//when class constructed control will be passed into Load(Class)
	}
	
	static void Load(Class Target)
	{
		//module loading dont need any checks
		
		//probably i just need to register plugin two times of ever two separate classes
		//this will allow to handle preworld and postworld correctly without issues
		
		//secondary instance with same class by special flag and other startup order...
		
		
	}
	public void run() 
	{
    	LogManager.getLogger().debug("TICKZERO");
    	
    	new PsExImpl();
    	new AutoSave();
    	new CustomLogin();
    	new ForcedPvP();
    	new ForcedRespawn();
    	new Silencio();

    	/**  TESTING section begin*/
    	if (!TESTING) return;
    	
    	new Tester();
	}
	
    public void onEnable()
    {
    	if (CONVERSION)
    		LogManager.getLogger().debug("STARTUP");
    	else
    		LogManager.getLogger().debug("POSTWORLD");
    	
		Bukkit.getScheduler().runTask(this, this);
    }
    
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    	LogManager.getLogger().debug("Dome Fuji Survival generator will be used for " + worldName);
        return new Generator_DFS();
    }
}
