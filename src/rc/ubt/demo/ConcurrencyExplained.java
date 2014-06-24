package rc.ubt.demo;

public class ConcurrencyExplained
{
	//Remote Fetch URL	https://github.com/RawCode/GPT.git
	public static long AsyncField = 0xBA1l;
	public static Object lock = new Object();
	
	static public void main(String[] args) throws Throwable {
		
		final Thread a = new Thread(){
			public void run(){
				while(true)
				{
					//synchronized(lock)
					//{
					AsyncField++;
					//}
					//concurrency works only if IO syncronized at every IO point
					//if you sync at random places, it wont make your object threadsafe, you must sync
					//everywhere or nowhere, there is no way to halfsync
				}
			}
		};
		
		final Thread b = new Thread(){
			public void run(){
				while(true)
				{
					synchronized(lock)
					{
						System.out.println(AsyncField);
						System.out.println(AsyncField);
					}
					
					try{Thread.sleep(1000);}catch(Exception e){};
				}
			}
		};
		
		a.start();
		b.start();
	}
}
