package jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml;


/**
 * <p>
 * This file is property of DaimlerChrysler.
 * </p>
 * 
 * The AEM_XmlHandler provides a StringBufferWrapper to decode input strings.
 * 
 * @author cwiech8
 *
 */
public class AEM_XmlHandler
{
	/**
	 * the StringBuffer<BR>
	 * By default an {@link jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml.AEM_UTF8StringBufferWrapper AEM_UTF8StringBufferWrapper} is used to instanciate characterBuffer
	 */
	AEM_AbstractStringBufferWrapper characterBuffer = new AEM_UTF8StringBufferWrapper(); 
	
	/**
	 * Getter for the characterBuffer
	 * @return AEM_AbstractStringBufferWrapper
	 * 
	 */
	public AEM_AbstractStringBufferWrapper getCharacterBuffer() {
		return characterBuffer;
	}

	/**
	 * Setter for the characterBuffer
	 * @param characterBuffer
	 * @return void
	 */
	public void setCharacterBuffer(AEM_AbstractStringBufferWrapper characterBuffer) {
		this.characterBuffer = characterBuffer;
	}
}
