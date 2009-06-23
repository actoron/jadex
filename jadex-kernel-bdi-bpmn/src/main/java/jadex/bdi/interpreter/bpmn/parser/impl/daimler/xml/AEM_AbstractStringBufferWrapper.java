package jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml;


/**
 * <p>
 * This file is property of DaimlerChrysler.
 * </p> 
 * 
 * 
 * @author cwiech8
 *
 */
public abstract class AEM_AbstractStringBufferWrapper {
	StringBuffer theBuffer;

	public abstract String toString();

	public abstract void clear();

	public abstract void append(String apendix);

}
