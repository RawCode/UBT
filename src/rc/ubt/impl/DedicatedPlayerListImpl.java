package rc.ubt.impl;

import net.minecraft.server.v1_7_R3.DedicatedPlayerList;
import net.minecraft.server.v1_7_R3.DedicatedServer;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public class DedicatedPlayerListImpl extends DedicatedPlayerList
{
	private static DedicatedServer dont_do_this()
	{
		UnsafeImpl.unsafe.throwException(new Throwable("DONT DO THIS"));
		return null;
	}
	private DedicatedPlayerListImpl(DedicatedServer arg0) {
		super(dont_do_this());
	}

    public EntityPlayer processLogin(GameProfile gameprofile, EntityPlayer player) {
    	System.out.println("REPLACEMENT EVIDENCE");
    	return super.processLogin(gameprofile,player);
    }
	
}