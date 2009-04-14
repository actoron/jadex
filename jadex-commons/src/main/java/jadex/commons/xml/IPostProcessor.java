package jadex.commons.xml;

/**
 *  Post-processes OAV objects after an XML has been loaded.
 */
public interface IPostProcessor
{
	/**
	 *  Post-process an OAV object after an XML has been loaded.
	 *  @param state	The OAV state.
	 *  @param object	The object to post process.
	 *  @param root	The root object in the state.
	 */
	public Object postProcess(Object context, Object object, Object root);
}
