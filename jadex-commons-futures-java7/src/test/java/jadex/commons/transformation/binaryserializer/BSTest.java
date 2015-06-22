package jadex.commons.transformation.binaryserializer;

import java.lang.reflect.UndeclaredThrowableException;

import jadex.commons.transformation.A;
import jadex.commons.transformation.B;


/**
 *  Testcases for writer and reader.
 */
public class BSTest extends jadex.commons.transformation.Test
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
			testUndeclaredThrowableException();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void testUndeclaredThrowableException() throws Exception
	{
		System.out.println("Reversed constructor for throwables encoding/decoding test.");
		
		String msg = "I'm the message.";
		String causemsg = "I'm the throwable used as cause.";
		UndeclaredThrowableException oute = new UndeclaredThrowableException(new RuntimeException(causemsg), msg);
		
		byte[] serialized = BinarySerializer.objectToByteArray(oute, null, null, null);
		
		UndeclaredThrowableException out = (UndeclaredThrowableException) BinarySerializer.objectFromByteArray(serialized, null, null, null, null);
		
		if (!msg.equals(out.getMessage()))
		{
			throw new RuntimeException("Messages do not match, expected: " + msg + " got: " + out.getMessage());
		}
		
		if (!causemsg.equals(out.getCause().getMessage()))
		{
			throw new RuntimeException("Cause does not match, expected: " + causemsg + " got: " + out.getCause().getMessage());
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
		BSTest t = new BSTest();
		t.performTests();
	}
}
