package jadex.xml.test;

import java.util.Date;

import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;


/**
 * Testcases for writer and reader.
 */
public class Test extends jadex.commons.transformation.Test
{

	// -------- methods --------

	/**
	 * 
	 */
	public Object doWrite(Object wo)
	{
		return JavaWriter.objectToByteArray(wo, null);
	}

	/**
	 * 
	 */
	public Object doRead(Object ro)
	{
		return JavaReader.objectFromByteArray((byte[])ro, null);
	}

	/**
	 * Main for testing single methods.
	 */
	public static void main(String[] args)
	{
		Test t = new Test();
		t.performTests();
	}
}
