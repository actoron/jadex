package jadex.json.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.commons.transformation.A;
import jadex.commons.transformation.B;
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
		t.performTests(1);
		t.testExcludes();
	}
	
	/**
	 *  Test if excludes work.
	 */
	public void testExcludes()
	{
		A a = new A(3, "s", new B(), new B[0]);
		Map<Class<?>, Set<String>> ex = new HashMap<Class<?>, Set<String>>();
		Set<String> exs = new HashSet<String>();
		exs.add("b");
		ex.put(A.class, exs);
		String ret = JsonTraverser.objectToString(a, null, false, ex);
		System.out.println("ret: " + ret);
		
		assertTrue(ret.contains("\"s\""));
		assertTrue(!ret.contains("\"b\":"));
	}


	public void testException() {
		String nullString = null;
		NullPointerException npe = null;
		try {
			nullString.length();
		} catch (NullPointerException e) {
			npe = e;
		}
		String s = JsonTraverser.objectToString(npe, null);
		NullPointerException fromString = JsonTraverser.objectFromString(s, null, NullPointerException.class);
		assertEquals(fromString.getMessage(), npe.getMessage());
		assertEquals(fromString.getCause(), npe.getCause());
		assertTrue(Arrays.equals(fromString.getStackTrace(), npe.getStackTrace()));
	}
}
