package jadex.simulation.remote;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IEAGoal;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.BasicService;

/**
 *  The shop for buying goods at the shop.
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
	public IFuture executeExperiment(String item) {
		System.out.println("Called Service at Remote Service with parameter: " + item);
		final Future ret = new Future();
		
		if(!isValid())
		{
			ret.setException(new RuntimeException("Service unavailable."));
		}
		else
		{
			ret.setResult(comp.getBeliefbase().getBelief("myName").getFact());
		}
		
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
