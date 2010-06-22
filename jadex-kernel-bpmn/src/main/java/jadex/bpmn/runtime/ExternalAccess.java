package jadex.bpmn.runtime;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.service.BasicServiceProvider;

/**
 *  External access for bpmn components.
 */
public class ExternalAccess extends BasicServiceProvider implements IExternalAccess
{
	//-------- attributes --------

	/** The agent. */
	protected BpmnInterpreter bpmn;

	/** The agent adapter. */
	protected IComponentAdapter adapter;

	// -------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(BpmnInterpreter bpmn)
	{
		this.bpmn = bpmn;
		this.adapter = bpmn.getComponentAdapter();
	}

	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public ILoadableComponentModel	getModel()
	{
		return bpmn.getModel();
	}
	
	/**
	 *  Get the component identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return adapter.getComponentIdentifier();
	}
	
	/**
	 *  Get the parent.
	 */
	public IExternalAccess getParent()
	{
		return bpmn.getParent();
	}
}
