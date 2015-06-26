package jadex.bridge;

import java.util.Map;

/**
 *  Marker interface for transferable component steps.
 */
public interface ITransferableStep<T>	extends IComponentStep<T> 
{
	/**
	 *  The object to be transferred as replacement for the component step.
	 *  May also be the step itself, if properly serializable to/from XML.
	 */
	public Map<String, String> getTransferableObject(); 
}
