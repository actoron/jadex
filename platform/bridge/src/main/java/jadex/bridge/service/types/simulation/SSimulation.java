package jadex.bridge.service.types.simulation;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Helper class for simulation control.
 *
 */
public class SSimulation
{
	public static final boolean	DEBUG_BLOCKERS	= false;
	
	/**
	 *  Add the future as simulation blocker, if currently in simulation mode.
	 *  Simulation blocking means the clock will not advance until the future is done.
	 *  This allows synchronizing external threads (e.g. swing) with the simulation execution.
	 */
	public static boolean	addBlocker(IFuture<?> adblock)
	{
		boolean	blocked	= false;
		
		IInternalAccess	ia	= ExecutionComponentFeature.LOCAL.get();
		if(isSimulating(ia))
		{
			try
			{
				debugBlocker();
				ia.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>(ISimulationService.class))
					.addAdvanceBlocker(adblock).get();
			}
			catch(ThreadDeath td)
			{
				// happens after successful get() wakeup in notifications caused by component died (endagenda.setResult()), grrr -> ignore so blocker gets removed.
			}
			blocked	= true;
		}
		
		return blocked;
	}

	/**
	 *  Create future as simulation blocker, if currently in simulation mode.
	 *  Simulation blocking means the clock will not advance until the future is done.
	 *  This allows synchronizing external threads (e.g. swing) with the simulation execution.
	 *  @return A future that has to be set to null for simulation to continue or null if not running in simulation mode.
	 */
	public static Future<Void>	block()
	{
		Future<Void>	adblock	= null;
		IInternalAccess	ia	= ExecutionComponentFeature.LOCAL.get();
		if(isSimulating(ia))
		{
			adblock	= new Future<>();
			try
			{
				debugBlocker();
				ia.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>(ISimulationService.class))
					.addAdvanceBlocker(adblock).get();
			}
			catch(ThreadDeath td)
			{
				// happens after successful get() wakeup in notifications caused by component died (endagenda.setResult()), grrr -> ignore so blocker gets removed.
			}
		}
		
		return adblock;
	}

	/**
	 *  Check if running in (single platform) simulation.
	 */
	public static boolean	isSimulating(IInternalAccess ia)
	{
		return ia!=null && Boolean.TRUE.equals(Starter.getPlatformValue(ia.getId().getRoot(), IClockService.SIMULATION_CLOCK_FLAG));
	}
	
	/**
	 *  Add caller stack to service call on debug.
	 */
	public static ServiceCall	debugBlocker()
	{
		ServiceCall	sc	= null;
		if(DEBUG_BLOCKERS)
		{
			sc	= ServiceCall.getOrCreateNextInvocation();
			sc.setProperty("adblockerstack", SUtil.getExceptionStacktrace(new RuntimeException()));
		}
		return sc;
	}
}
