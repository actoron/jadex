package jadex.jade;

import jade.wrapper.ControllerException;
import jadex.base.AbstractComponentAdapter;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
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
			factory.getPlatformController().createNewAgent(desc.getName().getLocalName(), ComponentAgent.class.getName(), new Object[]{this});
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
	protected void	doWakeup()
	{
		// Todo: What if agent isn't yet available!? 
		if(agent!=null)
			agent.wakeup();
	}
	
	//-------- methods --------
	
	/**
	 *  Called from the JADE agent belonging to this adapter.
	 */
	protected void setJadeAgent(ComponentAgent agent)
	{
		System.out.println("Agent is: "+agent);
		this.agent	= agent;
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
