package jadex.json.test;

import jadex.transformation.jsonserializer.JsonTraverser;

/**
 * Testcases for writer and reader.
 */
public class JsonTest extends jadex.commons.transformation.Test
{
	//-------- methods --------

	/**
	 * 
	 */
	public Object doWrite(Object wo)
	{
		return JsonTraverser.objectToByteArray(wo, null, /*StandardCharsets.UTF_8.name()*/"UTF-8"); 
	}

	/**
	 * 
	 */
	public Object doRead(Object ro)
	{
		return JsonTraverser.objectFromByteArray((byte[])ro, null, null, /*StandardCharsets.UTF_8.name()*/"UTF-8", null);
	}

	/**
	 * Main for testing single methods.
	 */
	public static void main(String[] args)
	{
		JsonTest t = new JsonTest();
		t.performTests();
	}
	
//	/**
//	 *  Test if excludes work.
//	 */
//	public void testExcludes()
//	{
//	}
}
