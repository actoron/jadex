package jadex.bpmn.runtime;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
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
	public IFuture getModel()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(bpmn.getModel());
				}
			});
		}
		else
		{
			ret.setResult(bpmn.getModel());
		}
		
		return ret;
	}
	
	/**
	 *  Get the component identifier.
	 */
	public IFuture getComponentIdentifier()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(adapter.getComponentIdentifier());
				}
			});
		}
		else
		{
			ret.setResult(adapter.getComponentIdentifier());
		}
		
		return ret;
	}
	
	/**
	 *  Get the parent.
	 */
	public IFuture getParent()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(bpmn.getParent());
				}
			});
		}
		else
		{
			ret.setResult(bpmn.getParent());
		}
		
		return ret;
	}
}
