package rc.ubt;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Map;

import net.minecraft.server.v1_7_R2.WorldServer;
import net.minecraft.server.v1_7_R2.WorldType;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R2.CraftServer;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;

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
	static String VER = Bukkit.getServer().getClass().getName().split("\\.")[3];
	public static JavaPlugin INSTANCE;
	{
		INSTANCE = this;
	}
	
	
	//public void dynamicKey()
	
	public void initSetting()
	{
		FileConfiguration c = this.getConfig();
		//c.
	}
	
	public void onLoad()
	{
		
		FileConfiguration config = this.getConfig();
		System.out.println(config.getInt("TEST"));
		config.addDefault("TEST", 1488);
		System.out.println(config.getInt("TEST"));
		
		config.set("TEST",666);
		System.out.println(config.getInt("TEST"));
		
		config.addDefault("TEST", 1488);
		System.out.println(config.getInt("TEST"));
		
		saveConfig();
		
		//[00:06:22] [Server thread/INFO]: [UBT] Loading UBT v0
		//[00:06:22] [Server thread/INFO]: 0
		//[00:06:22] [Server thread/INFO]: 1488
		//[00:06:22] [Server thread/INFO]: 666
		//[00:06:22] [Server thread/INFO]: 666
		
		
		if (true) return;
		
		boolean ischanged = false;
		
		if (!config.contains("isMainServer"))
		{
			config.set("isMainServer", true);
			ischanged = true;
		}
		
		if (!config.contains("PathToClasses"))
		{
			config.set("PathToClasses", "NULL");
			ischanged = true;
		}
		if (!config.contains("isDebugging"))
		{
			config.set("isDebugging", false);
			ischanged = true;
		}
		if (!config.contains("isTesting"))
		{
			config.set("isTesting", false);
			ischanged = true;
		}
		
		if (ischanged)
			saveConfig();
		
		if (config.getBoolean("isDebugging"))
			((org.apache.logging.log4j.core.Logger) LogManager.getLogger()).setLevel(Level.DEBUG);
		
		SimpleCommandMap scm = ((CraftServer)Bukkit.getServer()).getCommandMap();
		Map knownCommands = (Map) UnsafeImpl.getObject(scm, "knownCommands");
		knownCommands.remove("reload");
		
		if (!config.getBoolean("isMainServer")){
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
	//ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
	//getStackTrace
	@Override
	public void run() 
	{
    	LogManager.getLogger().debug("Postworld initialization");
		//POSTWORLD SECTION
    	new PsExImpl();
    	new AutoSave();
    	new CustomLogin();
    	new ForcedPvP();
    	new ForcedRespawn();
	}
	
    public void onEnable()
    {
    	new RandomClassA();
    	new RandomClassB();
    	new Tester();
    	
    	//PREWORLD SECTION
    	LogManager.getLogger().debug("Preworld initialization");
		//if (this.getConfig().getBoolean("isTesting"))
	    	//new Tester();
		
		Bukkit.getScheduler().runTask(this, this);
    }
    
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    	LogManager.getLogger().debug("Dome Fuji Survival generator will be used for " + worldName);
        return new Generator_DFS();
    }
}
