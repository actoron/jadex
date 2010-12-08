package jadex.simulation.remote;

import jadex.bdi.runtime.ICapability;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.ThreadSuspendable;
import jadex.commons.service.BasicService;
import jadex.commons.service.SServiceProvider;
import jadex.simulation.helper.FileHandler;

import java.util.Map;

/**
 *  Implementation of a remote execution service for (single) experiments.
 */
public class RemoteSimulationExecutionService extends BasicService implements IRemoteSimulationExecutionService
{
	//-------- attributes --------
	
	/** The component. */
	protected ICapability comp;
	
	/** The name of the platform. */
	protected String name;
	
	//-------- constructors --------
	
	/**
	 *  Create a new shop service.
	 *  @param comp The active component.
	 */
	public RemoteSimulationExecutionService(ICapability comp, String name)
	{
		super(comp.getServiceProvider().getId(), IRemoteSimulationExecutionService.class, null);

//		System.out.println("created: "+name);
		this.comp = comp;
		this.name = name;
	}

	//-------- methods --------
	
	/**
	 *  Get the name of the platform. 
	 *  @return The name.
	 */
	public String getPlatformName()
	{
		return name;
	}
	
	
	
	/**
	 *  Simulate an experiment defined as application.xml
	 *  @param item The item.
	 */
	public IFuture executeExperiment(String appName, String applicationDescription, String configName, Map args) {
		System.out.println("Called Service at Remote Service.");
		final Future ret = new Future();
		
		try {
			//persist application description			
			FileHandler.writeToFile(System.getProperty("user.dir")+"\\ApplicationDescription.application.xml",applicationDescription);
			
			IComponentManagementService executionService = (IComponentManagementService)SServiceProvider.getService(comp.getServiceProvider(), IComponentManagementService.class).get(new ThreadSuspendable());
			             

			// create application in order to add additional components to application
			IFuture fut = executionService.createComponent(appName, System.getProperty("user.dir")+"\\ApplicationDescription.application.xml", new CreationInfo(configName, args, null, false, false), null);
//			IComponentIdentifier comp = (IComponentIdentifier) fut.get(this);
			
			System.out.println("Helo\n" + System.getProperty("user.dir"));
//			FileHandler.writeToFile("C:\\file.xml", res1);

			// add data consumer and provider
//			addDataConsumerAndProvider(comp, executionService, (SimulationConfiguration) ((Map) args.get(Constants.SIMULATION_FACTS_FOR_CLIENT)).get(Constants.SIMULATION_FACTS_FOR_CLIENT));

		} catch (Exception e) {
			// JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this),
			// "Could not start application: "+e,
			// "Application Problem", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("Could not start application...." + e);
		}
		
		//SIM result
//		ret.setResult(result);
		
		return ret;
	}


	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return name;
	}	
}
