package jadex.simulation.remote;

import java.util.Map;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.service.IService;
import jadex.commons.service.SServiceProvider;

/**
 *  The simulation execution interface for executing (single) experiments.
 */
public interface IRemoteSimulationExecutionService	extends IService
{
	/**
	 *  Get the name of the platform. 
	 *  @return The name of the platform.
	 */
	public String getPlatformName();
	
	/**
	 *  Simulate an experiment defined as application.xml
	 *  @param item The item.
	 */
	public IFuture executeExperiment(String item);
	
	
//	private void startApplication(String appName, String fileName, String configName, Map args) {
//
//		try {
//			IComponentManagementService executionService = (IComponentManagementService)SServiceProvider.getService(getScope().getServiceProvider(), IComponentManagementService.class).get(this);
//			             
//
//			// create application in order to add additional components to
//			// application
//			IFuture fut = executionService.createComponent(appName, fileName, new CreationInfo(configName, args, null, false, false), null);
//			IComponentIdentifier comp = (IComponentIdentifier) fut.get(this);
//			IApplicationExternalAccess iap= (IApplicationExternalAccess) executionService.getExternalAccess(comp).get(this);
//			iap.getSpace("my2dspace").getClass();
////			comp.
//			// add data consumer and provider
////			addDataConsumerAndProvider(comp, executionService, (SimulationConfiguration) ((Map) args.get(Constants.SIMULATION_FACTS_FOR_CLIENT)).get(Constants.SIMULATION_FACTS_FOR_CLIENT));
//
//		} catch (Exception e) {
//			// JOptionPane.showMessageDialog(SGUI.getWindowParent(StarterPanel.this),
//			// "Could not start application: "+e,
//			// "Application Problem", JOptionPane.INFORMATION_MESSAGE);
//			System.out.println("Could not start application...." + e);
//		}
//	}
}
