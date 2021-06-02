package jadex.base.test.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.IChangeListener;
import jadex.commons.future.IFuture;

/**
 *  Helper class to allow sharing a simulation service across platforms in same VM.
 */
public class SharedSimulationService	extends SharedService<ISimulationService>	implements ISimulationService
{
	/**
	 *  Get the instance.
	 */
	public  SharedSimulationService(IComponentIdentifier provider, SharedServiceFactory<ISimulationService> factory)
	{
		super(provider, ISimulationService.class, factory);
	}
	
	//-------- ISimulationService interface --------
	
	@Override
	public IFuture<Void> pause()
	{
		return getInstance().pause();
	}

	@Override
	public IFuture<Void> start()
	{
		return getInstance().start();
	}

	@Override
	public IFuture<Void> stepEvent()
	{
		return getInstance().stepEvent();
	}

	@Override
	public IFuture<Void> stepTime()
	{
		return getInstance().stepTime();
	}

	@Override
	public IFuture<Void> setClockType(String type)
	{
		return getInstance().setClockType(type);
	}

	@Override
	public IFuture<String> getMode()
	{
		return getInstance().getMode();
	}

	@Override
	public IFuture<Boolean> isExecuting()
	{
		return getInstance().isExecuting();
	}

	@Override
	public IClockService getClockService()
	{
		return getInstance().getClockService();
	}

	@Override
	public IExecutionService getExecutorService()
	{
		return getInstance().getExecutorService();
	}

	@Override
	public IFuture<Void> addAdvanceBlocker(IFuture<?> blocker)
	{
		return getInstance().addAdvanceBlocker(blocker);
	}

	@Override
	public void addChangeListener(IChangeListener listener)
	{
		getInstance().addChangeListener(listener);
	}

	@Override
	public void removeChangeListener(IChangeListener listener)
	{
		getInstance().removeChangeListener(listener);
	}
}
