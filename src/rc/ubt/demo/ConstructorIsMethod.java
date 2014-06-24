package rc.ubt.demo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import rc.ubt.impl.UnsafeImpl;

public class ConstructorIsMethod
{
	UnsafeImpl(){System.out.println("CONSTRUCTOR INVOCATION");};
	public void codevessel(){System.out.println("METHOD INVOCATION");};
	
	static public void main(String[] args) throws Throwable {
		
		Method[] met = UnsafeImpl.class.getDeclaredMethods();
		Constructor[] con = UnsafeImpl.class.getDeclaredConstructors();
		
		for (Method m : met)
		{
			System.out.println(getInt(m,"slot"));
		}
		
		System.out.println("BREAK");
		
		for (Constructor c : con)
		{
			System.out.println(getInt(c,"slot"));
		}
		
		UnsafeImpl source = new UnsafeImpl();
		
		Method m = UnsafeImpl.class.getDeclaredMethod("codevessel", null);
		m.invoke(source, null);
		
		putInt(m,0,"slot");
		
		m.invoke(source, null);
}
