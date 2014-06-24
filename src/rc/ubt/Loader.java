package rc.ubt;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import rc.ubt.cmdh.Silencio;
import rc.ubt.genh.CustomLogin;
import rc.ubt.genh.ForcedPvP;
import rc.ubt.genh.ForcedRespawn;
import rc.ubt.genh.Respawn;
import rc.ubt.genh.Tester;
import rc.ubt.impl.PsExImpl;
import rc.ubt.impl.UnsafeImpl;
import rc.ubt.task.AutoSave;
import rc.ubt.wgen.FujiGenerator;

@SuppressWarnings("all")
public class Loader extends JavaPlugin implements Runnable
{
	public static JavaPlugin INSTANCE;
	
	{/**<init>*/
		INSTANCE = this;
	}
	
	/** for information only, there is no reason to store these states in one more place */
	private static boolean CONVERSION = false; // will perform singletron modifications
	private static boolean DEBUGGING  = false; // will output debug information to log
	private static boolean TESTING    = false; // will load additional handlers
	
	public boolean getOption(String _key, boolean _def)
	{
		if (this.getConfig().contains(_key))
			return this.getConfig().getBoolean(_key);
		this.getConfig().set(_key, _def);
		return _def;
	}
	
	public void onLoad()
	{
		CONVERSION = getOption("isConversion",false);
		DEBUGGING  = getOption("isDebugging" ,false);
		TESTING    = getOption("isTesting"   ,false);
		
		saveConfig(); //sync configuration file with class
		
		/** alter log4j logging level to DEBUG if this options selected */
		if (DEBUGGING) ((org.apache.logging.log4j.core.Logger) LogManager.getLogger()).setLevel(Level.DEBUG);

		/** disable unwanted <vanilla> commands */
		SimpleCommandMap scm = ((CraftServer) Bukkit.getServer()).getCommandMap();
		Map knownCommands = (Map) UnsafeImpl.getObject(scm, "knownCommands");
		knownCommands.remove("reload");
		knownCommands.remove("help");
		knownCommands.remove("?");
		knownCommands.remove("list");
		knownCommands.remove("seed");
		knownCommands.remove("me");
		knownCommands.remove("pl");
		knownCommands.remove("plugins");
		
		/** If server running in conversion mode - force world generator */
		if (CONVERSION)
		{
			LogManager.getLogger().debug("Forcing world generator");
			YamlConfiguration YC = (YamlConfiguration) UnsafeImpl.getObject(Bukkit.getServer(), "configuration");
			ConfigurationSection ss = YC.createSection("worlds");
			ss = ss.createSection("world");
			ss.set("generator", "UBT");
			
			LogManager.getLogger().debug("Forcing plugin loadorder");
			UnsafeImpl.putObject(this.getDescription(), PluginLoadOrder.STARTUP, "order");
		}
	}

	public void run() 
	{
		LogManager.getLogger().debug("TICKZERO");
		
		//new PsExImpl();
		//new AutoSave();
		//new CustomLogin();
		//new ForcedPvP();
		new Respawn();
		//new Silencio();

		/** TESTING section begin */
		//if (!TESTING) return;
		//new Tester();
	}
	
	
	//tick zero is OK but actually not
	//i shoud perform on worldinit
	
	//try setting plugin priority to postworld after initial start
	//this will allow to perform multiple "runs" from same code
	
	
	public void onEnable() 
	{
    	if (CONVERSION)
    		LogManager.getLogger().debug("STARTUP");
    	else
    		LogManager.getLogger().debug("POSTWORLD");
    	
    	Bukkit.getScheduler().runTask(this, this);
    }
    
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) 
    {
		LogManager.getLogger().debug("Dome Fuji Survival generator will be used for " + worldName);
		return new FujiGenerator();
    }
}