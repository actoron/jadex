package jadex.bridge.service.types.factory;

import jadex.bridge.IComponentInterpreter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.cms.IComponentDescription;


/**
 *  Interface for platform specific component adapter factories.
 */
@Reference
public interface IComponentAdapterFactory
{
	/**
	 *  Create a component adapter for a component instance.
	 *  @param desc The component description.
	 *  @param model The component model.
	 *  @param instance The component instance.
	 *  @param parent The external access of the component's parent.
	 *  @return The component adapter.
	 */
	public IComponentAdapter createComponentAdapter(IComponentDescription desc, IModelInfo model, IComponentInterpreter instance, IExternalAccess parent);
	
	/**
	 *  Execute a step of the component via triggering the adapter.
	 *  @param adapter The component adapter.
	 *  @return true, if component wants to be executed again. 
	 */
	public boolean executeStep(IComponentAdapter adapter);

	/**
	 *  Perform the initial wake up of a component.
	 *  @param adapter	The component adapter.
	 */
	public void	initialWakeup(IComponentAdapter adapter);

}
