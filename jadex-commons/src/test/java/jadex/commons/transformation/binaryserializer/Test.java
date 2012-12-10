package jadex.commons.transformation.binaryserializer;

import jadex.commons.transformation.A;
import jadex.commons.transformation.B;


/**
 *  Testcases for writer and reader.
 */
public class Test extends jadex.commons.transformation.Test
{
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public Object doWrite(Object wo)
	{
		return BinarySerializer.objectToByteArray(wo, null, null, null);
	}
	
	/**
	 * 
	 */
	public Object doRead(Object ro) 
	{
		return BinarySerializer.objectFromByteArray((byte[])ro, null, null, null, null);
	}
	
	/**
	 * 
	 */
	public void performTests()
	{
		super.performTests();
		try
		{
			testUnknownSubobjectClass();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void testUnknownSubobjectClass() throws Exception
	{
		System.out.println("Unknown sub-object class test.");
		A obj = new A(3, "Abc", new B("xyz"), null);
		
		byte[] serialized = BinarySerializer.objectToByteArray(obj, null, null, null);
		
		ClassLoader dcl = new ClassLoader()
		{
			public Class<?> loadClass(String name) throws ClassNotFoundException
			{
				if (name.endsWith("jadex.commons.transformation.B"))
				{
					throw new ClassNotFoundException("B filtered.");
				}
				return super.loadClass(name);
			}
		};
		
		Object out = BinarySerializer.objectFromByteArray(serialized, null, null, dcl, new IErrorReporter()
		{
			public void exceptionOccurred(Exception e)
			{
				System.out.println("Decoder reports error: " + e.getMessage() + ", skipping...");
			}
		});
	}
	
	/**
	 *  Main for testing single methods.
	 */
	public static void main(String[] args)
	{
		Test t = new Test();
		t.performTests();
	}
}
