package rc.ubt.aret;

public class RegionImplLong
{

	public static Long test()
	{
		return new Long(99);
	}
	
	public void main()
	{
		Object a = RegionImplLong.test();
		Object b = RegionImplInteger.test();
		a = b;
		b = a;
	}
}