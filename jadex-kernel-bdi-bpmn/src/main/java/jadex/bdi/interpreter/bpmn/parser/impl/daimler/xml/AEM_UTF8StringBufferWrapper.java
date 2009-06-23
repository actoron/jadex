package jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml;


/**
 * <p>
 * This file is property of DaimlerChrysler.
 * </p>
 *  
 *  The AEM_UTF8StringBufferWrapper applies the {@link jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml.AEM_UTF8Tools AEM_UTF8Tools} to the
 *  enclosed StringBuffer. 
 *  
 * @author cwiech8
 *
 */

public class AEM_UTF8StringBufferWrapper extends AEM_AbstractStringBufferWrapper {
	
	
	/**
	 * 
	 * Creates a new instance of AEM_UTF8StringBufferWrapper
	 * 
	 */
	public AEM_UTF8StringBufferWrapper() 
	{
		theBuffer = new StringBuffer();
	}

	/**
	 * @return the decoded Stringcontent of the encapsulated StringBuffer
	 */
	public String toString() {
		return AEM_UTF8Tools.decodeUTF8(theBuffer.toString());
	}

	/** 
	 * clears the content of the encapsulated StringBuffer
	 */
	public void clear() {
		theBuffer = new StringBuffer();
	}

	/** 
	 * appends <code>apendix</code> to the encapsulated StringBuffer
	 * 
	 * @param apendix
	 * 					The String to append
	 */
	public void append(String apendix) {
		theBuffer.append(apendix);
	}

}
