package jadex.commons.transformation.binaryserializer;


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
	 *  Main for testing single methods.
	 */
	public static void main(String[] args)
	{
		Test t = new Test();
		t.performTests();
	}
}
