package jadex.bridge;

/**
 *  Marker interface for transferable component steps.
 */
public interface ITransferableStep	extends IComponentStep 
{
	/**
	 *  The object to be transferred as replacement for the component step.
	 *  May also be the step itself, if properly serializable to/from XML.
	 */
	public Object	getTransferableObject(); 
}
