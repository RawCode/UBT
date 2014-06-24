package rc.ubt.demo;

import rc.ubt.impl.UnsafeImpl;

public class FinalDoNothing
{
	
	public UnsafeImpl()
	{
		testnotfinal = this;
		testfinal = 50;
	}
		new Thread()
{
	public void run(){
		while(true)
		{
		if (testnotfinal != null)
			if (testnotfinal.testfinal == 0)
				System.out.println("GOT ZERO FINAL FIELD");
		}
	}
}.start();

new Thread()
{
	public void run(){
		while(true)
		{
			new UnsafeImpl();
		}
	}
}.start();
}
