package rc.ubt.impl;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.locks.LockSupport;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import sun.misc.Unsafe;
import sun.reflect.ConstantPool;
import sun.reflect.Reflection;

  /**
  * sandbox key for obfuscated stuff -Djava.security.manager
  * this key can be used inside "run setup" in order to run malware code without any risk of infection
  * 
  * 
  * 
  * Generic update to allow more actions on fields and objects without lots of calls
  * Benchmark testing for hashmap caching and unchached version to get proper results about speed
  * If cached version faster - move everything to cache.
  */

@SuppressWarnings("all") /** in other case you will see zillion warnings */
public /**static*/ class UnsafeImpl /**import class as static inside your classes*/
{
	private UnsafeImpl(){/**unsafe.throwException(new Throwable("dont"));*/};
	
	public @interface PlatformVersionDependant{String value();}
	
	/**
	 * Java process IO
	 * This class allows to read and write java process memory directly via unsafe natives
	 * Later valid bytecode compilation will be available
	 * Same stuff already implemented in jillegal but i dont evedrop on it's sources to prevent spoilers
	 */
	 /* if you dont want to spoil fun of original research dont read this class */
	 /** 
	 * Unsafe internally used by lang.reflect, basically this is reflections with all security and safety removed
	 * also some optimizations are removed, this shoud be slower then vanilla reflection actually
	 * Java objects is *API* over internal OOPs implemented by C++
	 */
	
	static public	Unsafe unsafe;
	static private	Object anchor;
	static private	long   offset;
	
	@PlatformVersionDependant("64bit Compressed OOPs default")
	static private long REF = 4L;
	@PlatformVersionDependant("64bit Compressed OOPs default")
	static private long PAD = 8L;
	
	static /*<cinit> executed on class declaration, undefined if multiple classloaders are present*/
	{
		try
		{
			@PlatformVersionDependant("ID may vary from JVM to JVM")
			Field f = Unsafe.class.getDeclaredFields()[0];
			f.setAccessible(true);
			unsafe = (Unsafe) f.get(null);
			offset = unsafe.staticFieldOffset(fetchField(UnsafeImpl.class,"anchor"));
			
			Object[] sourcearray = {new Object(),new Object()};
			
			/** not implemented by underlying methods*/
			if (unsafe.getInt(sourcearray, 0x4L) != 0)
				REF = 8L;
			if (unsafe.getInt(sourcearray, 0x8L) == 2)
				PAD = 4L;
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			Runtime.getRuntime().halt(42);
		}
	}
	
	/**
	 * Perform deep multiname field lookup
	 * Multiname required for forge SRG names to work both test and runtime build
	 * @param Source mostly instance.getClass()
	 * @param Names array of possible names
	 * @return null of requested field
	 * 
	 * Type and name pair version dropped, but may be required in some cases
	 */
	
	static private Field fetchField(Class Source,String... Names)
	{
		for(;Source != Object.class;Source = Source.getSuperclass())
		{
			for (Field f : Source.getDeclaredFields())
				for (String s : Names)
					if (f.getName().equals(s))
						return f;
		}
		return null;
	}
	
	/**
	 * Subject to bytecode runtime or postruntime compilation
	 * via accessor generator
	 * slow and unsafe
	 */
	
	@PlatformVersionDependant("change without notice on GC events dont store output and use syncblocks")
	static public long Object2ID(Object O){
		anchor = O;
		return (long)(unsafe.getInt(UnsafeImpl.class, offset)) & 0xfFfFfFfFl;
	}
	
	@PlatformVersionDependant("change without notice on GC events dont store output and use syncblocks")
	static public Object ID2Object(long ID){
		unsafe.putInt(UnsafeImpl.class, offset, (int) ID);
		return anchor;
	}
	
	static public String Object2Trace(Object O,int L){
		StringBuilder sb = new StringBuilder();
		sb.append("Layout of @0x" + Long.toHexString(Object2ID(O)*8));
		sb.append("\n");
		String s;
        for (long i = 0; i < L; i++){
        	s = (i % 4 == 3) ? "%2$02X": "%2$02X-";
        	s = (i % 4 == 0) ? "%1$03d %2$02X-": s;
        	sb.append(String.format(s, i,unsafe.getByte(O,i)));
            	if (i % 4 == 3 && i != L-1)
            		sb.append("\n");
        }
        return sb.toString();
	}
	
	/** this method allows to load classes ignoring most rules */
	static private Class RawBytes2Class(byte[] Data)
	{
		if (Data.length < 32) return null; //obviously invalid input
		
		//offsets 14 and 15 used to store lenght of subsequent classname section
		int NameLenght = Data[14]*255+Data[15];
		byte[] NameRaw = new byte[NameLenght];
		System.arraycopy(Data, 16, NameRaw, 0, NameLenght);
		return unsafe.defineClass(new String(NameRaw), Data, 0, Data.length);
	}
	
	static public void putObject(Object Owner, Object Value, String... Names)
	{
		if (Owner instanceof Class)
		{
		unsafe.putObject(Owner,unsafe.staticFieldOffset(fetchField((Class) Owner   ,Names)),Value);
		return;
		}
		unsafe.putObject(Owner,unsafe.objectFieldOffset(fetchField(Owner.getClass(),Names)),Value);
	}
	static public Object getObject(Object Owner, String... Names)
	{
		if (Owner instanceof Class)
		return unsafe.getObject(Owner,unsafe.staticFieldOffset(fetchField((Class) Owner   ,Names)));
		return unsafe.getObject(Owner,unsafe.objectFieldOffset(fetchField(Owner.getClass(),Names)));
	}
	
	
	static public void putInt(Object Owner, int Value, String... Names)
	{
		if (Owner instanceof Class)
		{
		unsafe.putInt(Owner,unsafe.staticFieldOffset(fetchField((Class) Owner   ,Names)),Value);
		return;
		}
		unsafe.putInt(Owner,unsafe.objectFieldOffset(fetchField(Owner.getClass(),Names)),Value);
	}
	static public int getInt(Object Owner, String... Names)
	{
		if (Owner instanceof Class)
		return unsafe.getInt(Owner,unsafe.staticFieldOffset(fetchField((Class) Owner   ,Names)));
		return unsafe.getInt(Owner,unsafe.objectFieldOffset(fetchField(Owner.getClass(),Names)));
	}
	
	
	static public void setClass(Object Target, Class Type) throws InstantiationException
	{
		unsafe.ensureClassInitialized(Type);
		unsafe.putInt(Target,2*REF,unsafe.getInt(Type, 21*REF));
	}
	
	static public void main(String[] args) throws Throwable {
		
		String s = new String("");
		setClass(s,Integer.class);
		System.out.println(s.getClass());
		
		//System.out.println("TESTING");
		//setClass(Thread.currentThread().getContextClassLoader(),ArcaneClassLoader.class);
		
		//Class.forName("BADDAD");
		
		//System.out.println(Object2Trace(unsafe.allocateInstance(ArcaneClassLoader.class),60));
		
		//Thread.currentThread().suspend();
		
		//0x7aaf21a90
		//System.out.println(Long.toHexString(refx));
		
		
		//System.out.println(Long.toHexString(unsafe.getInt(refx)));
		
		//reinterpret(test,String.class);
		
		
		
		
		
		
		
		String s1 = Object2Trace(new Integer(66),16);
		String s2 = Object2Trace(Integer.class,256);
		
		int l1 = unsafe.getInt(new Integer(66), 2*REF);
		int l2 = unsafe.getInt(Integer.class, 21*REF);
		//22*4
		
		Object oz = new String("TEST");
		
		String s3 = Object2Trace(oz,16);
		//reinterpret(oz,new String("DATA"));
		String s4 = Object2Trace(oz,16);
		
		System.out.println(oz);
		
		System.out.println(s3);
		System.out.println("BREAK");
		System.out.println(s4);
		
		//System.out.println(Integer.toHexString(l1));
		//System.out.println(Integer.toHexString(l2));
		
		//char[] RawDataNotInterned = {'t','e','s','t'};
		//String InternedString = "test";
		
		//System.out.println(Long.toHexString(Object2ID(InternedString)));
		//System.out.println(Long.toHexString(Object2ID(new String(RawDataNotInterned))));
		//System.out.println(Long.toHexString(Object2ID(new String(RawDataNotInterned))));
		//System.out.println(Long.toHexString(Object2ID(new String(RawDataNotInterned).intern())));
		
		//putObject(InternedString,new char[5],"value");
		
		//System.out.println(Long.toHexString(Object2ID(new String(InternedString).intern())));
		//System.out.println(Long.toHexString(Object2ID(new String(new char[5]).intern())));
		
		//if (new String(InternedString).intern() == new String(new char[5]).intern())
			//System.out.println("valid");
		
		//System.out.println(Long.toHexString(Object2ID(new String(InternedString))));
		//System.out.println(Long.toHexString(Object2ID(new String(InternedString))));
		
		//System.out.println(Long.toHexString(Object2ID(new String(InternedString).intern())));
		
		//String test  = "CHOOSEN BY FAIR DICE ROLL ";
		//String test2 = "CHOOSEN BY UNFAIR ROLL____";
		//char[] vessel = (char[]) getObject(test2,"value");
		//displayrandomstring();
		//putObject(test,vessel,"value");
		//displayrandomstring();
		//System.out.println(test.intern());
		
		//ConstantPool cp = SharedSecrets.getJavaLangAccess().getConstantPool(UnsafeImpl.class);
		
		//System.out.println(cp.getStringAt(222));
		
		
		
		
		//ThreadRoot().enumerate(a);
        
		//for (Thread e : a)
		//{
		//	System.out.println(e);
		//}
		
		
		
		//working offheap object sample
		
		//must check rules of memory copy, since for some reason it does not copy
		//from object to object and this is not good
		
		//TEST testA = new TEST();
		
	//	testA.A = 666;
	//	testA.B = 666;
	//	testA.C = new Integer(888);
		
		//int A = unsafe.getInt(test, 4l);
		//int B = unsafe.getInt(test, 8l);
		//int C = unsafe.getInt(test, 12l);
		
		//System.out.println(A);
		//System.out.println(B);
		//System.out.println(C);
		
		//long TRY = unsafe.allocateMemory(32);
		
		//Object FabA = ID2Object(TRY/8);
		//Object FabB = ID2Object((TRY/8)+1);
		
		//System.out.println(TRY);
		
		//unsafe.copyMemory(testA, 0, null, TRY, 32);
		//unsafe.copyMemory(testA, 8, null, TRY+16, 4);
		
		//((TEST)FabA).C = null;
		
		//System.out.println(((TEST)FabA).C);
		
		
	//	Object2Trace(FabA);
		
		//Object2Trace(testA);
		//((TEST)FabA).D = null;
		
		//Object2Trace(FabB);
		
		//System.out.println(unsafe.getInt(TRY+12));
		
		//System.out.println(o);
		
		//System.out.println(FabA.getClass());
		//System.out.println(FabB.getClass());
		
		
		//Object2Trace(o);
		
		//System.out.println(unsafe.getInt(TRY+19));
	
		
		/*
		System.out.println("THIS IS TESTSTRING TO CONSOLE");
		
		FileOutputStream fis = new FileOutputStream(FileDescriptor.out);
		
		System.out.println("BEFORE TEST");
		try
		{
			fis.write('a');
			fis.write(new byte[999]);
			System.out.println("TEST");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("ERR");
		}
		System.out.println("OUT");
		
		

		
		System.out.println("THIS IS TESTSTRING TO CONSOLE");
		
		
		
		/*
		int[] t = {1,2,3,4};
		int step = 0;
		for(;;)
		{
			if (t[step] != 3)
				System.out.println(t[step]);
			
			if (step++ == t.length-1)break;
		}
		/*
		
		
		/*
		Object S1 = new Integer(0xAAAAAAAA);
		Object S2 = new String("");
		
		//forObject_Dump(S1);
		//forObject_Dump(S2);
		
		int C1 = unsafe.getInt(S1, 8);
		int C2 = unsafe.getInt(S2, 8);
		
		forObject_Dump(forOOP_Object(C1));
		forObject_Dump(forOOP_Object(C2));
		
		int X1 = unsafe.getInt(forOOP_Object(C1), 24); //this is layout helper OOP instance
		int X2 = unsafe.getInt(forOOP_Object(C2), 24);
		
		System.out.println(X1);
		System.out.println(X2);
		
		//forObject_Dump(forOOP_Object(X1));
		//forObject_Dump(forOOP_Object(X2));
		
		//int tt = unsafe.getInt(subject, 8);
		//System.out.println(Long.toHexString(I2L(tt)));
		//forObject_Dump(forOOP_Object(tt));
		//forObject_Dump(forObject_OOP(tt));
		/*
		Path path = Paths.get("d://1.class");
		byte[] data = Files.readAllBytes(path);
		
		for (byte b : data)
			System.out.println(b);
		
	    Path pathd = Paths.get("d://bt.txt");
	    Files.write(pathd, data); //creates, overwrites
	    
	    //string representation of byte
	    //casting byte to it's char will be implemented via replacement table
	    /*
	    challenge eh?
	    		looks like you accept entries about "let i merge everything into single line", i will provide ultimate entry with this "feature".

	    		1) I will pick very first entry of this thread - class "Give"
	    		2) I will precompile it
	    		3) With little suplementary class i will assemble byte array storage from given class
		
		/*
		B test = new B();
		
		Field f = forName_Field(B.class,"A");
		
		System.out.println(f);
		*/
		
		/*
		long before = System.nanoTime();
		for (int i = 0 ; i < 1000000 ; i++)
		{
			forName_Field(Reflection.class,"zz");
		}
		System.out.println(System.nanoTime()-before);
		
		before = System.nanoTime();
		for (int i = 0 ; i < 1000000 ; i++)
		{
			testaz(Reflection.class,"zz");
		}
		System.out.println(System.nanoTime()-before);
		//426209254
		//424380599
		//198173277
		//198173277
		//198173277
		//367406745
		//197619324
		//364876120
		
		
		/*
		short[][] data = new short[16][];
		
		short[][] test = new short[16][];
		
		System.out.println(6 | 10);
		
		
		/*
		
		URLClassLoader urs = (URLClassLoader) ClassLoader.getSystemClassLoader();
		
		Method m = URLClassLoader.class.getDeclaredMethod("addURL",new Class[]{URL.class});
		m.setAccessible(true);
		
		URL u = new File("D:\\1").toURL();
		
		//m.invoke(urs, u);
		
		Class testa = Class.forName("ru.rawcode.dev.A");
		/*
		File f = new File("D:\\1");
		File[] ff = f.listFiles();
		
		File USE = ff[0];
		
	    FileInputStream stream = new FileInputStream(USE);
	    
	    byte[] data;
	    
	    stream.read(data = new byte[stream.available()]);
	    
	    int esize = data[14]*255+data[15];
	    byte[] ename = new byte[esize];
	    
	    System.arraycopy(data, 16, ename, 0, esize);
	    
	    System.out.println(new String(ename));
	    
		Class az = unsafe.defineClass(new String(ename), data, 0, data.length);
		
		ClassLoader zz = az.getClassLoader();
		
		havemain test = (havemain) unsafe.allocateInstance(az);
		test.main();
		
		zz.loadClass(new String(ename));
		
		
		Class testa = Class.forName(new String(ename));
		System.out.println(az.getClassLoader());
		
		/*
		
		byte[] test = testLocation();
		
		//printLocation(test);
		
		long testl = Loc2Long(test);
		
		System.out.println(Long.toHexString(testl));

		for (byte b : test) {
			   System.out.format("0x%x ", b);
			}
		
		byte[] bbs = Long2Loc(testl);
		System.out.println();
		for (byte b : bbs) {
			   System.out.format("0x%x ", b);
			}
		
		//int OUT = bb[3] + (bb[2]<<8) + (bb[1]<<16) + (bb[0]<<24);
		
		//System.out.println(Integer.toHexString(OUT));
		
		/*
		Random r = new Random();
		
		byte[] setA = new byte[]{'h','e','l','l','o'};
		byte[] setB = new byte[]{'w','o','r','l','d'};
		
		//(0-19)+100
		//100-119
		/*
		long l = 0;
		int step = 0;
		
		byte[] az = new byte[5];
		
		for (;;)
		{
			r.setSeed(l);
			
			r.nextBytes(az);
			
			if (az[0] == setA[0])
				if (az[1] == setA[1])
					if (az[2] == setA[2])
						if (az[3] == setA[3])
							if (az[4] == setA[4])
								System.out.println(l);
			l++;
		}
	//	*/
		
		/*
		byte[] message = new byte[10];
		int i = 0;
		Random rnd = new Random();
		for(;;)
		{
			if (i == 0)
				rnd.setSeed(12051153);
			if (i == 5)
				rnd.setSeed(10782634);
			if (i == 10)
				break;
			message[i] = (byte) (rnd.nextInt(20)+100);
			i++;
		}
		
		System.out.println(new String(message));
		//seed A = 12051153
		//seed B = 10782634
		
		

		A testa = new A();
		B testb = new B();
		
		int aa = unsafe.getInt(testa, 4);
		int bb = unsafe.getInt(testb, 4);
		
		Object oa = forOOP_Object(aa);
		Object ob = forOOP_Object(bb);
		
		//forObject_Dump(oa);
		System.out.println();
		//forObject_Dump(ob);
		
		int offa = 82; //second key for interface
		unsafe.putInt(oa, offa*4, unsafe.getInt(ob, offa*4));
		offa = 7; //first key for interface
		unsafe.putInt(oa, offa*4, unsafe.getInt(ob, offa*4));
		
		havemain2 testing = (havemain2) new A();
		testing.have();
		
		/*
		for(;;){
		offa++;
		unsafe.putInt(oa, offa*4, unsafe.getInt(ob, offa*4));
		Thread.sleep(1);
		try
		{
			havemain2 testing = (havemain2) new A();
			testing.main();
			System.out.println("WORK WITH" + offa);
		} catch (Throwable e)
		{
		}
		}
		
		//forObject_Dump(oa);
		

		
		/*
		System.out.println(Integer.toHexString(z));
		forObject_Dump(forOOP_Object(z));
		if(true)return;
		System.out.println();
		System.out.println();
		System.out.println("FFFFFF");
		if(true)return;
		int b = unsafe.getInt(testb, 4);
		System.out.println(Integer.toHexString(b));
		
		forObject_Dump(forOOP_Object(b));
		
		
		//Class.forName("TEST");
		
		
		//UnsafeImpl test = new UnsafeImpl();
		//System.out.println(test.ss);
		
		//Field f = test.getClass().getDeclaredField("ss");
		//f.setAccessible(true);
		//f.set(test, new String("REPLACED"));
		
		//System.out.println(test.ss);
		
		
		/*
		long A = 0xffffffff;
		long[] b = new long[0xff];
		System.out.println(Integer.toHexString(forObject_OOP(b)));
		String f = "ZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZZ";
		
		forObject_Dump(Thread.currentThread());
		
		for(;;){}
		
		
		//System.out.println(SimplexNoise.noise(0, 08));
		/*
		Field f = forName_Field(ConstantPool.class,"constantPoolOop");
		
		long l = forField_offset(f);
		
		ConstantPool cp = sun.misc.SharedSecrets.getJavaLangAccess().getConstantPool(UnsafeImpl.class);
		ConstantPool cp2 = sun.misc.SharedSecrets.getJavaLangAccess().getConstantPool(Object.class);
		
		Object o = unsafe.getObject(cp, l);
		Object o2 = unsafe.getObject(cp2, l);
		
		long o22 = unsafe.getLong(cp2, l);
		int o33 = unsafe.getInt(cp2,l);
		
		System.out.println(Long.toHexString(o22*8));
		System.out.println(Long.toHexString(normalize(o33)*8));
		
		//System.out.println(Long.toHexString(o22*8));
		
		
		//System.out.println(Integer.toHexString(forObject_OOP(o)));
		//System.out.println(Integer.toHexString(forObject_OOP(o2)*8));
		
		
		
		//for(;;){}
		/*
		if (true)return;

		if (checked){
			checked = false;
			tz = Thread.currentThread();

			new Thread(){
				public void run() {

					for(;;)
					{
						if (!tz.isAlive()){
							System.out.println("DEAD");
							new Thread(){
								public void run(){
									try
									{
										tz = Thread.currentThread();
										UnsafeImpl.main(null);
									} catch (Throwable e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}.start();
						}
						try
						{
							Thread.sleep(100l);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}

			}.start();
		}

		System.out.println(System.currentTimeMillis());

		for(;;)
		{
			if (new Random().nextInt(10) == 5)unsafe.throwException(new Exception("MAIN INTERRUPT"));
			System.out.println(System.currentTimeMillis());
			Thread.sleep(100l);
		}
		
		
		
		
		/*
	        PrintWriter writer = new PrintWriter(System.out);
	        System.setOut(null);
	        writer.println("Method 2");
	        writer.flush();
	        writer.close();
	        System.out.println("test");
		/*
	    ItemStack chestRed = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
	    LeatherArmorMeta metaRed = (LeatherArmorMeta) chestRed.getItemMeta();
	    metaRed.setColor(Color.RED);
		
		//long a = 0x7aaf14d90l;
		long b = 0x6fba00000l;
		
		//long c = a-b;
		
		long tt = forObject_OOP(new UnsafeImpl());
		
		long addr = tt*32*-1+b;
		//tt = tt << 3;
		
		System.out.println(Long.toHexString(addr));
		
		//System.out.println(Long.toHexString(c));
		
		//af514d90
		//55e29b0
		
		
		/*
		URL input = new URL("http://www.minecraftforum.net/topic/1003192-prominecrafthostcom-us-and-europe-locations-save-25-off-with-promo-code-thanks13/");
		URLConnection uc = input.openConnection();
		InputStream raw = uc.getInputStream();
		InputStream in = new BufferedInputStream(raw);
		int[] data = new int[9999];
		int i = 0;
		
		while(in.available() != 0)
		{
			i = in.read();
			if (i == 118)System.out.println();
			System.out.print(String.valueOf((char)i));
		}
		*/
		
		//forObject_Dump(new UnsafeImpl());
		
		//for(;;);
		//System.out.println(8<<3);
		//0xfc2fff8 //max accessable adress
		//f55e2aea
		//System.out.println(0x7aaf15758L);
		//0x6fba00000 //heap base adress
		
		//long adr = 0x6fba00000L - 0x7aaf15758L;
		//System.out.println(Long.toHexString(adr));
		//int a = 0;
		//long ttz = unsafe.getLong(0x7aaf15758L+7);

		//for(;;)
		//	{
		//	System.out.println(Long.toHexString(unsafe.getLong(0x7aaf15758L+a)));
		//	a++;
		//	if (a == 10)return;
		//	}
		//System.out.println(Long.toHexString(ttz));
		
		//System.out.println(Long.toHexString(ttz));
		
		//Object test = forInt_OOP(tt);
		//Object tests = forInt_OOP(ttz);
		
		//System.out.println(ttz);
		
		//System.out.println(Integer.toHexString(unsafe.getInt(anchor, 4L)));
		
		//System.out.println(unsafe.getAddress(unsafe.getInt(anchor, 4L)));
        
        //long [] test = new long[99999999];
        
        //unsafe.getAddress(-1);
		//for(;;);
		
		/*
		long azz = forOOP_Int(anchor);
		
		System.out.println(Long.toHexString(azz));
		System.out.println(Long.toHexString(forOOP_Intnp(anchor) << 32 >>> 32));
		
		//unsafe.getAddress(azz);
		
		//int atz = unsafe.getInt(anchor, 8);
		//System.out.println(atz);
		
		
		Object zz = forInt_OOP((int) azz);
		System.out.println(zz.getClass());
		
		Object zzz = forInt_OOP((int) forOOP_Intnp(anchor));
		System.out.println(zzz.getClass());
		
		unsafe.getInt(zz, 0);
		
		//System.out.println(zz.getClass());
		
		//System.out.println(Long.toHexString(addressOf(UnsafeImpl.class)));
		//System.out.println(Integer.toHexString(forOOP_Int(UnsafeImpl.class)));
		
		
		//long a1 = unsafe.getByte(o, 8l);
	//	long a2 = unsafe.getByte(o, 9l)  << 8  ;
	//	long a3 = unsafe.getByte(o, 10l) << 16 ;
	//	long a4 = unsafe.getByte(o, 11l) << 24 ;
		
	//	long azx = normalizez(a1+a2+a3+a4);
		
		
		
		
		//System.out.println(String.format("%02X ", tz));
		
	//	System.out.println(Long.toHexString(azx));
	//	System.out.println(azx);
		
		//long tt = unsafe.getAddress(azx);
		
		//System.out.println(Long.toHexString(tt));
		
		//System.out.println(unsafe.getObject(null,0x7aaeb72f0L).getClass());
		
		//for(;;);
		*/
	}
}
