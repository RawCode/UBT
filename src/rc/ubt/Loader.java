package rc.ubt;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Map;

import net.minecraft.server.v1_7_R1.WorldServer;
import net.minecraft.server.v1_7_R1.WorldType;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R1.CraftServer;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import rc.ubt.hnde.CustomLogin;
import rc.ubt.hnde.ForcedPvP;
import rc.ubt.hnde.ForcedRespawn;
import rc.ubt.impl.PsExImpl;
import rc.ubt.impl.UnsafeImpl;
import rc.ubt.task.AutoSave;
import rc.ubt.wgen.Generator_DFS;

@SuppressWarnings("all")
public class Loader extends JavaPlugin
{
	static String VER = Bukkit.getServer().getClass().getName().split("\\.")[3];
	public static JavaPlugin INSTANCE;
	{
		INSTANCE = this;
	}
	
	public void onLoad()
	{
		FileConfiguration config = this.getConfig();
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
		
		if (ischanged)
			saveConfig();
		
		SimpleCommandMap scm = ((CraftServer)Bukkit.getServer()).getCommandMap();
		Map knownCommands = (Map) UnsafeImpl.getObject(scm, "knownCommands");
		knownCommands.remove("reload");
		
		if (!config.getBoolean("isMainServer")){
			LogManager.getLogger().info("Forcing world generator");
			YamlConfiguration YC = (YamlConfiguration) UnsafeImpl.getObject(Bukkit.getServer(), "configuration");
			ConfigurationSection ss = YC.createSection("worlds");
			ss = ss.createSection("world");
			ss.set("generator", "UBT");
	    	Bukkit.getPluginManager().registerEvents(new Tester(), this);
		}
		
		Bukkit.getPluginManager().enablePlugin(this);
		//anyway i need method to properly handle postworld and preworld
		//most effective way to leave plugin startup mode and perform stuff on worldinit event, this is best move
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
	
    public void onEnable()
    {
    	//this shoud execute on worldinit or after worldinit or on first server tick
    	new PsExImpl();
    	new AutoSave();
    	new CustomLogin();
    	new ForcedPvP();
    	new ForcedRespawn();
    }
    
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
    	LogManager.getLogger().info("Dome Fuji Survival generator will be used for " + worldName);
        return new Generator_DFS();
    }
}
