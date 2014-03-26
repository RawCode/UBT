package rc.ubt.smpl;

import rc.ubt.impl.UnsafeImpl;

public class TrueObjectSizeExplained
{
	boolean testbool1 = true;
	boolean testbool2 = false;
	boolean testbool3 = true;
	boolean testbool4 = true;
	
	byte testbyte1 = (byte) 0xFF;
	byte testbyte2 = (byte) 0xF0;
	byte testbyte3 = (byte) 0xAA;
	byte testbyte4 = (byte) 0xA0;
	
	static public void main(String[] args) throws Throwable {
		
		TrueObjectSizeExplained t = new TrueObjectSizeExplained();
		
		UnsafeImpl.putInt(t,0xFF,"testbool1");
		
		UnsafeImpl.forObject_Dump(t);
		System.out.println(t.testbool1);
		
		//UnsafeImpl.putInt(t,0xFF,"testbool1");
		//System.out.println(UnsafeImpl.getInt(t,"testbool1"));
	}
}