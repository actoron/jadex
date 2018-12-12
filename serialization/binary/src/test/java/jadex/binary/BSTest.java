package jadex.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.UndeclaredThrowableException;

import jadex.commons.transformation.A;
import jadex.commons.transformation.B;
import jadex.commons.transformation.traverser.IErrorReporter;


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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SBinarySerializer.writeObjectToStream(baos, wo, null);
		return baos.toByteArray();
	}
	
	/**
	 * 
	 */
	public Object doRead(Object ro) 
	{
		ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) ro);
		return SBinarySerializer.readObjectFromStream(bais, null, null, null, null, null);
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
		
//		byte[] serialized = SBinarySerializer2.objectToByteArray(oute, null, null, null);
		byte[] serialized = (byte[]) doWrite(oute);
		
		UndeclaredThrowableException out = (UndeclaredThrowableException) doRead(serialized);
		
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
		
		byte[] serialized = (byte[]) doWrite(obj);
		
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
		
		ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
		Object out = SBinarySerializer.readObjectFromStream(bais, null, null, dcl, new IErrorReporter()
		{
			public void exceptionOccurred(Exception e)
			{
				System.out.println("Decoder reports error: " + e.getMessage() + ", skipping...");
			}
		}, null);
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
