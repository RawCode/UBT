package rc.ubt.genh;

import net.minecraft.server.v1_7_R3.EntityPlayer;

import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;

public class CraftPlayerImpl extends CraftPlayer
{

	public CraftPlayerImpl(CraftServer server, EntityPlayer entity) {
		super(server, entity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void finalize()
	{
		System.out.println(this.toString() + " is garbaged!");
	}
	
	@Override
	public void sendMessage(String s)
	{
		System.out.println("NOPE");
	}
}
