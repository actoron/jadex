package jadex.bridge;

import jadex.commons.concurrent.IResultListener;

import java.util.Map;

/**
 *  General interface for elements that the container can execute.
 */
public interface IElementExecutionService
{
	
	/**
	 *  Test if the execution service can handle the element (or model).
	 *  @param element The element (or its filename).
	 */
	public boolean isResponsible(Object element);
	
	/**
	 *  Create a new element on the platform.
	 *  The element will not run before the {@link startElement()}
	 *  method is called.
	 *  Ensures (in non error case) that the aid of
	 *  the new element is added to the AMS when call returns.
	 *  @param name The element name (null for auto creation)
	 *  @param model The model name.
	 *  @param config The configuration.
	 *  @param args The arguments map (name->value).
	 *  @param listener The result listener (if any).
	 *  @param creator The creator (if any).
	 */
	public void	createElement(String name, String model, String config, Map args, IResultListener listener, Object creator);
	
	/**
	 *  Start a previously created element on the platform.
	 *  @param elementid The id of the previously created element.
	 */
	public void	startElement(Object elementid, IResultListener listener);
	
	/**
	 *  Destroy (forcefully terminate) an element on the platform.
	 *  @param elementid	The element to destroy.
	 */
	public void destroyElement(Object elementid, IResultListener listener);

	/**
	 *  Suspend the execution of an element.
	 *  @param elementid The element identifier.
	 */
	public void suspendElement(Object elementid, IResultListener listener);
	
	/**
	 *  Resume the execution of an element.
	 *  @param elementid The element identifier.
	 */
	public void resumeElement(Object elementid, IResultListener listener);
	
	//-------- listener methods --------
	
	/**
     *  Add an element listener.
     *  The listener is registered for element changes.
     *  @param listener  The listener to be added.
     */
    public void addElementListener(IElementListener listener);
    
    /**
     *  Remove an ams listener.
     *  @param listener  The listener to be removed.
     */
    public void removeElementListener(IElementListener listener);

}
