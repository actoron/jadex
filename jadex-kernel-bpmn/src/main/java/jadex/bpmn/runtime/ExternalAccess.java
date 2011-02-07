package jadex.bpmn.runtime;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.IServiceProvider;

/**
 *  External access for bpmn components.
 */
public class ExternalAccess implements IExternalAccess
{
	//-------- attributes --------

	/** The agent. */
	protected BpmnInterpreter interpreter;

	/** The agent adapter. */
	protected IComponentAdapter adapter;

	/** The provider. */
	protected IServiceProvider provider;
	
	//-------- constructors --------

	/**
	 *	Create an external access.
	 */
	public ExternalAccess(BpmnInterpreter interpreter)
	{
		this.interpreter = interpreter;
		this.adapter = interpreter.getComponentAdapter();
		this.provider = interpreter.getServiceProvider();
	}

	//-------- methods --------
	
	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public IModelInfo	getModel()
	{
		return interpreter.getModel();
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
	public IComponentIdentifier getParent()
	{
		return interpreter.getParent().getComponentIdentifier();
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		return adapter.getChildrenIdentifiers();
	}

	/**
	 *  Get the application component.
	 */
	public IServiceProvider getServiceProvider()
	{
		return provider;
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture killComponent()
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					interpreter.killComponent().addResultListener(new DelegationResultListener(ret));
				}
			});
		}
		else
		{
			interpreter.killComponent().addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}

	/**
	 *  Get the interpreter.
	 *  @return the interpreter.
	 */
	public BpmnInterpreter getInterpreter()
	{
		return interpreter;
	}
	
	/**
	 *  Create a result listener that will be 
	 *  executed on the component thread.
	 *  @param listener The result listener.
	 *  @return A result listener that is called on component thread.
	 * /
	public IResultListener createResultListener(IResultListener listener)
	{
		return new ComponentResultListener(listener, adapter);
	}*/
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public IFuture scheduleStep(final IComponentStep step)
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			try
			{
				adapter.invokeLater(new Runnable() 
				{
					public void run() 
					{
						interpreter.scheduleStep(step).addResultListener(new DelegationResultListener(ret));
					}
				});
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			interpreter.scheduleStep(step).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 * /
	public String toString()
	{
		return "ExternalAccess(comp=" + tostring + ")";
	}*/
}
