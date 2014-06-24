package rc.ubt.demo;

public class fulltrace
{
	static public void FullTracePrint()
	{
		ThreadGroup List = Thread.currentThread().getThreadGroup();
		
		for(;;)
		{
			if (List.getParent() == null)
				break;
			List = List.getParent();
				
		}
		
		Thread[] data = new Thread[List.activeCount()];
		List.enumerate(data);
		
		for (Thread t : data)
		{
			System.out.println(t);
			for (StackTraceElement e : t.getStackTrace())
			{
				System.out.println(e);
			}
			System.out.println();
		}
	}
}
