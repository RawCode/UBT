package rc.ubt.aret;

import org.bukkit.Location;

public class Team implements PublicAPI
{
	//team must register to specific arena, including drafting spawn\rest\whatever locations and other data from arena pool
	public Team(Arena A,int B,int C,int D)
	{}
	
	//area or location where team wait arena minigame start
	final Location REST_A;
	final Location REST_B;
	
	//area where team moved when game is started
	final Location SPAWN_A;
	final Location SPAWN_B;
	
	//area where team members move on defeat or other action
	final Location END_A;
	final Location END_B;
	
}
