package jadex.xml.test;

import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;


/**
 * Testcases for writer and reader.
 */
public class XMLTest extends jadex.commons.transformation.Test
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
		return JavaReader.objectFromByteArray((byte[])ro, null, null);
	}

	/**
	 * Main for testing single methods.
	 */
	public static void main(String[] args)
	{
		XMLTest t = new XMLTest();
		t.performTests();
	}

	/** dont test XML with optionals, because XML is obsolete / different in jadex 4.0 **/
	public void testOptionalsPrimitive() throws Exception {
	}
	/** dont test XML with optionals, because XML is obsolete / different in jadex 4.0 **/
	public void testOptionalsComplex() throws Exception {
	}
	/** dont test XML with optionals, because XML is obsolete / different in jadex 4.0 **/
	public void testOptionalsCollection() throws Exception {
	}
}
