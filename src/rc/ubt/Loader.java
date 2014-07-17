package rc.ubt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import net.minecraft.server.v1_7_R3.FoodMetaData;
import net.minecraft.server.v1_7_R3.MinecraftServer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;

import rc.ubt.impl.DedicatedPlayerListImpl;
import rc.ubt.impl.FoodMetaDataForged;
import rc.ubt.impl.UnsafeImpl;
import sun.misc.IOUtils;
import sun.misc.SharedSecrets;
import sun.reflect.ConstantPool;

@SuppressWarnings("all")
public class Loader extends JavaPlugin implements Runnable,Listener
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
	
	int ref = -1;
	String[] demo = 
		{
			"one","small","user","spoonfeeded","much","on","forum","everyone die","end"
		};
	String xmod = null;
	
	//@EventHandler
	public void ReactOnChat(AsyncPlayerChatEvent e)
	{
		ref++;
		Bukkit.broadcastMessage("WorldGuard string is changed");
		if (xmod != null)
		{
			UnsafeImpl.putObject(xmod, demo[ref].toCharArray(), "value");
			return;
		}
		try
		{
			Class wgce = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlayerListener");
			ConstantPool cpx = SharedSecrets.getJavaLangAccess().getConstantPool(wgce);
			xmod = cpx.getStringAt(883);
			UnsafeImpl.putObject(xmod, demo[ref].toCharArray(), "value");
		} catch (Throwable e1)
		{e1.printStackTrace();}
	}

	public void run() 
	{
		LogManager.getLogger().debug("TICKZERO");
		
		String className = FoodMetaDataForged.class.getName();
		String classAsPath = className.replace('.', '/') + ".class";
		InputStream stream = FoodMetaDataForged.class.getClassLoader().getResourceAsStream(classAsPath);
		
		byte[] data = null;
		try
		{
			data = IOUtils.readFully(stream, -1, true);
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		UnsafeImpl.unsafe.defineClass(className, data, 0, data.length, FoodMetaData.class.getClassLoader(), null);
		
		try
		{
			Class c = FoodMetaData.class.getClassLoader().loadClass("rc.ubt.impl.FoodMetaDataForged");
			UnsafeImpl.forgeMethodTable(FoodMetaData.class,c);
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//will crash jvm cos different classloader
		//UnsafeImpl.forgeMethodTable(FoodMetaData.class,FoodMetaDataForged.class);
		
		
		
		
		/**
		//dont ever need magic for this
		Object o = MinecraftServer.getServer().getPlayerList();
		Object x = null;
		try
		{
			x = UnsafeImpl.unsafe.allocateInstance(DedicatedPlayerListImpl.class);
		} catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		UnsafeImpl.unsafe.putInt(o, 8l, UnsafeImpl.unsafe.getInt(x, 8l));
		
		//new PsExImpl();
		//new AutoSave();
		//new CustomLogin();
		//new ForcedPvP();
		//new Respawn();
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
    	Bukkit.getPluginManager().registerEvents(this, this);
    }
    
	//public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) 
    //{
	//	LogManager.getLogger().debug("Dome Fuji Survival generator will be used for " + worldName);
	//	return new FujiGenerator();
    //}
}