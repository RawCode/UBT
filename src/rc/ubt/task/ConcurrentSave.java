package rc.ubt.task;

public class ConcurrentSave
{
	//this is complex class that will unload save and GC chunk objects from server based on it's internal logic.
	//additional classes will be used to replace "vanilla" implementation of saving\loading and collecting.
	//also shoud collect other object types if too many spawned in single chunk
	//also shoud load \ unload chunks and keep age of each loaded chunk
	//there is no reason to waste time on chunks that not altered in any way by game
	
	//concurrent save will control all worlds, not just one world with custom rules set by world
	//this is not module (at least as long as custom classloader does not exists)
	
	
	//this about nolag, stoplag and all similar anilag features, antiAFK, nofarm and save
	//this module will require LONG work and deep integration with server to work properly
	
	//saving will be performed in background asyc based of snapshots made inside sync task
	//this will be original research
	//saving methods will be reversed if needed
}
