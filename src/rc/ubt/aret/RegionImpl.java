package rc.ubt.aret;

import org.bukkit.Location;

public class RegionImpl
{

	//region in most cases can be defined by two location
	//if you want your arena to implement areas of arbitrary shape
	//or accept regions from world guard or world edit, you can use this class as bridge
	final Location A;
	final Location B;
	
	public RegionImpl(Location A,Location B)
	{
		this.A = A;
		this.B = B;
	}
	
	//there are method about shape and other stuff follows:
	
	
}
