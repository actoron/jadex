package jadex.jade;

import jade.Boot;
import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import jadex.base.fipa.CMSComponentDescription;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.jade.service.message.JadexMessageTransportProtocol;
import jadex.kernelbase.AbstractInterpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	
	/** The Jadex root component. */
	protected IComponentInstance root;
	
	/** Flag for init step. */
	protected boolean	inited;

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
		// Start JADE platform on first component creation.
		if(!inited)
		{
			inited	= true;
			this.root	= instance;
			assert	desc.getName().getParent()==null : "First component must be root component.";
			
			
			Map	args	= null;
			if(instance instanceof AbstractInterpreter)
			{
				args	= ((AbstractInterpreter)instance).getArguments();
			}
			Object	rma	= args!=null ? args.get("rma") : null;
			boolean	gui	= rma!=null && rma instanceof Boolean && ((Boolean)rma).booleanValue();
			Object	port	= args!=null ? args.get("port") : null;
			Object	jadextransport	= args!=null ? args.get("jadextransport") : null;
			
			List	jadeargs	= new ArrayList();
			if(jadextransport!=null)
			{
				jadeargs.add("-mtp");
				jadeargs.add(JadexMessageTransportProtocol.class.getName());
				jadeargs.add("-jadextransport");
				jadeargs.add(jadextransport);
			}
			if(port!=null)
			{
				jadeargs.add("-local-port");
				jadeargs.add(""+port);
			}
			if(gui)
				jadeargs.add("-gui");
			else
				jadeargs.add("-agents");
			
			// Start Jade platform with gateway agent
			// This agent makes accessible the platform controller
			jadeargs.add("jadexgateway:jadex.jade.PlatformGatewayAgent");
			Boot.main((String[])jadeargs.toArray(new String[jadeargs.size()]));
			
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

			// Change name of root component to match JADE platform name (hack!!!).
			((CMSComponentDescription)desc).setName(new ComponentIdentifier(gatewayagent.getHap()));
		}
		
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
	
	/**
	 *  Perform the initial wake up of a component.
	 *  @param adapter	The component adapter.
	 */
	public void	initialWakeup(IComponentAdapter adapter)
	{
		// Automatically executed by JADE agent.
		adapter.wakeup();
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
	 *  Get the platform gateway controller.
	 */
	public AgentController	getGatewayController()	throws ControllerException
	{
		return controller.getAgent(gatewayagent.getLocalName());
	}
	
	/**
	 *  Get the Jadex root platform component.
	 */
	public IComponentInstance	getRootComponent()
	{
		return root;
	}
	
	/**
	 *  Get the global factory.
	 */
	public static ComponentAdapterFactory	getInstance()
	{
		return FACTORY;
	}
}
