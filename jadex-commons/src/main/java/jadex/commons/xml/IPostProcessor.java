package jadex.commons.xml;

/**
 *  Post-processes objects after an XML has been loaded.
 */
public interface IPostProcessor
{
	/**
	 *  Post-process an OAV object after an XML has been loaded.
	 *  @param state	The OAV state.
	 *  @param object	The object to post process.
	 *  @param root	The root object in the state.
	 */
	public void postProcess(Object context, Object object, Object root, ClassLoader classloader);
	
	/**
	 *  Test if this post processor can be executed in first pass.
	 *  @return True if can be executed on first pass.
	 */
	public boolean isFirstPass();
}
