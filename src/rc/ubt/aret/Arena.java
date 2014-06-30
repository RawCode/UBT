package rc.ubt.aret;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Location;

public class Arena implements PublicAPI
{
	private Arena(){};
	
	//Definition of arena is simple, this is limited by some bounds area of world
	//Its unlikely for someone with posivite pressure on internal skull surface to build multiple arenas one above other
	
	 String REF_WORLD; //we shoud not change arena world after creation, this is instance constant
	
	 int MIN_X; // i just dont see any reason to bake areana location with something
	 int MIN_Z; // just physical bounds, no more no less
	 int MAX_X;
	 int MAX_Z;
	//also can be implemented with vector or pair of locations
	
	 String NAME; //each arena shoud have unique name
	
	//since names are unique we must ensure this by keeping list of all known arenas
	static  Collection<String> ARENA_LOOKUP = new ArrayList<String>();
	
	
	//arena must have entry points, it may have as many entry points as you want
	//both collection and enum is possible
	//when teams are added, spawns distributed between them
	 Collection<Location> ARENA_SPAWNS = new ArrayList<Location>();
	
	//teams stored as collection, as many teams as needed may be added
	//when team is constructed it registered to specific arena
	 Collection<Team> ARENA_TEAMS = new ArrayList<Team>();
	
	enum LOCATIONS
	{
		//not valid code but generic concept is enchanced ENUM that stores some location by its name
		//this can be both spawn\leave\die\wait\spectator areas - two locations can define arbitrary rect 
		//unlike collection that can feature same thing - enum is hardcoded and cannot be altered at runtime, collection can
		{LOCATIONA,LOCATIONB,LOCATIONC}
		
		LOCATIONS(Location A,Location B)
		{
			this.Point = A;
			this.Point = B;
		}
		
		public Location PointA = null;
		public Location PointB = null;
	}
	

	
	
	
	
	
}
