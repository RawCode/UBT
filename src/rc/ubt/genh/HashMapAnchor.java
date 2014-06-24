package rc.ubt.genh;

public class HashMapAnchor
{

	public HashMapAnchor()
	{
		payload = new long[999];
	}
	
	long[] payload = null;
	
	@Override
	public void finalize()
	{
		System.out.println("HashMapAnchor is garbaged!");
		new Throwable().printStackTrace();
	}
}
