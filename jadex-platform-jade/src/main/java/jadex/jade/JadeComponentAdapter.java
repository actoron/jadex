package jadex.jade;

import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jadex.base.AbstractComponentAdapter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IExecutable;

import java.io.Serializable;

/**
 *  Adapter for running Jadex components on top of JADE.
 */
public class JadeComponentAdapter	extends AbstractComponentAdapter	implements IComponentAdapter, IExecutable, Serializable
{
	//-------- attributes --------
	
	/** The JADE agent. */
	protected ComponentAgent	agent;
	
	/** The wakeup flag. */
	protected boolean	wakeup; 
	
	//-------- constructors --------

	/**
	 *  Create a new component adapter.
	 *  Uses the thread pool for executing the component.
	 */
	public JadeComponentAdapter(IComponentDescription desc, IModelInfo model, IComponentInstance component, IExternalAccess parent, ComponentAdapterFactory factory)
	{
		super(desc, model, component, parent);
		
		try
		{
			AgentController	ac	= factory.getPlatformController().createNewAgent(desc.getName().getLocalName(), ComponentAgent.class.getName(), new Object[]{this});
			ac.start();
		}
		catch(ControllerException e)
		{
			e.printStackTrace();
		}
	}
	
	//-------- AbstractComponentAdapter methods --------

	/**
	 *  Wake up this component.
	 */
	public void	doWakeup()
	{
		boolean	dowakeup;
		synchronized(this)
		{
//			System.out.println("doWakeup "+this+", "+agent);
			if(agent!=null)
			{
				dowakeup	= true;
			}
			else
			{
				dowakeup	= false;
				wakeup	= true;
			}
		}
		
		if(dowakeup)
		{
			agent.wakeup();
		}
	}
	
	/**
	 *  Gracefully terminate the component.
	 *  This method is called from ams and delegated to the reasoning engine,
	 *  which might perform arbitrary cleanup actions, goals, etc.
	 *  @return A future top indicate, when cleanup of the component is finished.
	 */
	public IFuture killComponent()
	{
		// Overridden to kill JADE agent, when component has terminated.
		Future	ret	= new Future();
		super.killComponent().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				agent.doDelete();
				super.customResultAvailable(result);
			}
		});
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Called from the JADE agent belonging to this adapter.
	 */
	protected void setJadeAgent(ComponentAgent agent)
	{
		boolean	dowakeup;
		synchronized(this)
		{
//			System.out.println("setJadeAgent "+this+", "+wakeup);
			dowakeup	= wakeup;
			this.agent	= agent;
		}

		if(dowakeup)
		{
			agent.wakeup();
		}
}

	/**
	 *  Get the agent.
	 *  @return the agent.
	 */
	public ComponentAgent getJadeAgent()
	{
		return agent;
	}
}
