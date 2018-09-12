package jadex.bridge.service.types.simulation;

import java.lang.reflect.Field;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Helper class for simulation control.
 *
 */
public class SSimulation
{
	/**
	 *  Add the future as simulation blocker, if currently in simulation mode.
	 *  Simulation blocking means the clock will not advance until the future is done.
	 *  This allows synchronizing external threads (e.g. swing) with the simulation execution.
	 */
	public static void	addBlocker(IFuture<?> adblock)
	{
		IInternalAccess	ia	= ExecutionComponentFeature.LOCAL.get();
		if(isSimulating(ia))
		{
			try
			{
				ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISimulationService.class))
					.addAdvanceBlocker(adblock).get();
			}
			catch(ThreadDeath td)
			{
				// happens after successful get() wakeup in notifications caused by component died (endagenda.setResult()), grrr -> ignore so blocker gets removed.
			}
		}
		else if(isBisimulating(ia))
		{
			try
			{
				Field	f	= Class.forName("jadex.platform.service.simulation.SimulationAgent").getDeclaredField("bisimservice");
				f.setAccessible(true);
				ISimulationService	simserv	= (ISimulationService)f.get(null);
				try
				{
					simserv.addAdvanceBlocker(adblock).get();
				}
				catch(ThreadDeath td)
				{
					// happens after successful get() wakeup in notifications caused by component died (endagenda.setResult()), grrr -> ignore so blocker gets removed.
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
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
				ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISimulationService.class))
					.addAdvanceBlocker(adblock).get();
			}
			catch(ThreadDeath td)
			{
				// happens after successful get() wakeup in notifications caused by component died (endagenda.setResult()), grrr -> ignore so blocker gets removed.
			}
		}
		else if(isBisimulating(ia))
		{
			try
			{
				Field	f	= Class.forName("jadex.platform.service.simulation.SimulationAgent").getDeclaredField("bisimservice");
				f.setAccessible(true);
				ISimulationService	simserv	= (ISimulationService)f.get(null);  
				adblock	= new Future<>();
				try
				{
					simserv.addAdvanceBlocker(adblock).get();
				}
				catch(ThreadDeath td)
				{
					// happens after successful get() wakeup in notifications caused by component died (endagenda.setResult()), grrr -> ignore so blocker gets removed.
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return adblock;
	}

	/**
	 *  Check if running in bisimulation.
	 */
	public static boolean	isBisimulating(IInternalAccess ia)
	{
		return ia!=null && Boolean.TRUE.equals(Starter.getPlatformValue(ia.getId().getRoot(), IClockService.BISIMULATION_CLOCK_FLAG));
	}
	
	/**
	 *  Check if running in (single platform) simulation.
	 */
	public static boolean	isSimulating(IInternalAccess ia)
	{
		return ia!=null && Boolean.TRUE.equals(Starter.getPlatformValue(ia.getId().getRoot(), IClockService.SIMULATION_CLOCK_FLAG));
	}
}
