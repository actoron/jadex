package jadex.bridge;

/**
 *  The interface for accessing components from the outside.
 *  To be specialized for concrete component types.
 */
public interface IExternalAccess
{
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public ILoadableComponentModel	getModel();
}
