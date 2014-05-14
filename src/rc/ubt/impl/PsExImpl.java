package rc.ubt.impl;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.bukkit.entity.Player;

@SuppressWarnings("all")
public class PsExImpl {
	
	public PsExImpl()
	{
		try
		{
			Class c = Class.forName("ru.tehkode.permissions.bukkit.PermissionsEx");
			if (c == null) return;
			xhas = c.getDeclaredMethod("has", Player.class,String.class);
			xpex = c.getDeclaredMethod("getPlugin", null).invoke(null, null);
		}
		catch(Throwable t){
			LogManager.getLogger().error("PermissionsEx not found, using OP state instead");
		};
	}
	
	static Method xhas = null;
	static Object xpex = null;
	static public boolean has(Player player, String permission)
	{
		if (xhas != null)
		{
			try
			{
				return (boolean) xhas.invoke(xpex, player,permission);
			}catch (Exception e){e.printStackTrace(); return false;}
		}
		return player.isOp();
	}
}
