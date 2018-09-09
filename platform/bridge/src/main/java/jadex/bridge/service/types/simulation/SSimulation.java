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
		if(ia!=null && Boolean.TRUE.equals(Starter.getPlatformValue(ia.getId().getRoot(), IClockService.SIMULATION_CLOCK_FLAG)))
		{
			ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISimulationService.class))
				.addAdvanceBlocker(adblock).get();
		}
		else if(ia!=null && ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IExecutionService.class).setRequiredProxyType(ServiceQuery.PROXYTYPE_RAW)).toString().startsWith("Bisim"))
		{
			try
			{
				Field	f	= Class.forName("jadex.platform.service.simulation.SimulationAgent").getDeclaredField("bisimservice");
				f.setAccessible(true);
				ISimulationService	simserv	= (ISimulationService)f.get(null);  
				simserv.addAdvanceBlocker(adblock).get();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

//	protected static final Map<Future<Void>, String>	openfuts	= Collections.synchronizedMap(new LinkedHashMap<>());
	
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
		if(ia!=null && Boolean.TRUE.equals(Starter.getPlatformValue(ia.getId().getRoot(), IClockService.SIMULATION_CLOCK_FLAG)))
		{
			adblock	= new Future<>();
			ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISimulationService.class))
				.addAdvanceBlocker(adblock).get();
		}
		else if(ia!=null && ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IExecutionService.class).setRequiredProxyType(ServiceQuery.PROXYTYPE_RAW)).toString().startsWith("Bisim"))
		{
			try
			{
				Field	f	= Class.forName("jadex.platform.service.simulation.SimulationAgent").getDeclaredField("bisimservice");
				f.setAccessible(true);
				ISimulationService	simserv	= (ISimulationService)f.get(null);  
				adblock	= new Future<>();
				simserv.addAdvanceBlocker(adblock).get();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		// For debugging when simulation hangs due to leftover adblocker.
//		if(adblock!=null)
//		{
//			openfuts.put(adblock, SUtil.getExceptionStacktrace(new RuntimeException("Stacktrace")));
//			Future<Void>	fadblock	= adblock;
//			adblock.addResultListener(result -> {openfuts.remove(fadblock);});
//			System.out.println("adblocks: "+openfuts);
//		}
		
		return adblock;
	}

}
