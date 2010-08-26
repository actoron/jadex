package jadex.jade;

import jade.Boot;
import jade.core.AID;
import jade.wrapper.PlatformController;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;

/**
 *  Standalone version of the adapter factory.
 */
public class ComponentAdapterFactory implements IComponentAdapterFactory
{
	//-------- constants --------
	
	/** The global factory. */
	// hack!!!
	protected static ComponentAdapterFactory	FACTORY;
	
	//-------- attributes --------
	
	/** The platform agent. */
	protected AID platformagent;
	
	/** The container controller. */
	protected PlatformController controller;

	//-------- constructors --------
	
	public ComponentAdapterFactory()
	{
		synchronized(ComponentAdapterFactory.class)
		{
			if(FACTORY!=null)
			{
				throw new RuntimeException("Currently only one JADE instance per VM supported.");
			}
			FACTORY	= this;
		}
		
		// Start Jade platform with platform agent
		// This agent make accessible the platform controller
		new Boot(new String[]{"-gui", "platform:jadex.jade.PlatformAgent"});
		// Hack! Busy waiting for platform agent init finished.
		while(platformagent==null)
		{
			System.out.print(".");
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e)
			{
			}
		}
	}
	
	//-------- IComponentAdapterFactory interface --------
	
	/**
	 *  Create a component adapter for a component instance.
	 *  @param desc The component description.
	 *  @param model The component model.
	 *  @param instance The component instance.
	 *  @param parent The external access of the component's parent.
	 *  @return The component adapter.
	 */
	public IComponentAdapter createComponentAdapter(IComponentDescription desc, IModelInfo model, IComponentInstance instance, IExternalAccess parent)
	{
		return new JadeAgentAdapter(desc, model, instance, parent);
	}
	
	/**
	 *  Execute a step of the component via triggering the adapter.
	 *  @param adapter The component adapter.
	 *  @return true, if component wants to be executed again. 
	 */
	public boolean executeStep(IComponentAdapter adapter)
	{
		return ((JadeAgentAdapter)adapter).execute();
	}
	
	//-------- helper methods --------
	
	/**
	 *  Set the platform agent.
	 */
	protected void	setPlatformAgent(AID platformagent)
	{
		this.platformagent	= platformagent;
	}
	
	/**
	 *  Set the container.
	 *  @param container The container.
	 */
	protected void setPlatformController(PlatformController controller)
	{
		this.controller = controller;
	}

	
	/**
	 *  Get the global factory.
	 */
	protected static ComponentAdapterFactory	getInstance()
	{
		return FACTORY;
	}
}
