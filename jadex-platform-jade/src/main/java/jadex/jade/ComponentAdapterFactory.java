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
 *  JADE version of the adapter factory.
 */
public class ComponentAdapterFactory implements IComponentAdapterFactory
{
	//-------- constants --------
	
	/** The global factory. */
	// hack!!!
	protected static ComponentAdapterFactory	FACTORY;
	
	//-------- attributes --------
	
	/** The gateway agent. */
	protected AID gatewayagent;
	
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
		
		// Start Jade platform with gateway agent
		// This agent makes accessible the platform controller
		Boot.main(new String[]{"-gui", "jadexgateway:jadex.jade.PlatformGatewayAgent"});
		// Hack! Busy waiting for gateway agent init finished.
		while(gatewayagent==null)
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
		return new JadeComponentAdapter(desc, model, instance, parent, this);
	}
	
	/**
	 *  Execute a step of the component via triggering the adapter.
	 *  @param adapter The component adapter.
	 *  @return true, if component wants to be executed again. 
	 */
	public boolean executeStep(IComponentAdapter adapter)
	{
//		return ((JadeComponentAdapter)adapter).execute();
		// Automatically executed by JADE agent.
		return false;
	}
	
	//-------- methods --------
	
	/**
	 *  Set the platform controller.
	 *  @param controller The platform controller.
	 */
	protected void setPlatformController(PlatformController controller)
	{
		this.controller = controller;
	}
	
	/**
	 *  Get the platform controller.
	 */
	public PlatformController	getPlatformController()
	{
		return this.controller;
	}
	
	/**
	 *  Set the platform gateway agent.
	 */
	protected void	setGatewayAgent(AID gatewayagent)
	{
		this.gatewayagent	= gatewayagent;
	}
	
	/**
	 *  Get the gatewayagent.
	 *  @return the gatewayagent.
	 */
	public AID getGatewayAgent()
	{
		return gatewayagent;
	}
	
	/**
	 *  Get the global factory.
	 */
	public static ComponentAdapterFactory	getInstance()
	{
		return FACTORY;
	}
}
