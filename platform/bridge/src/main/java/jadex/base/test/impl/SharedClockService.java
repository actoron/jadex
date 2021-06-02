package jadex.base.test.impl;

import java.util.TimerTask;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.IThreadPool;

/**
 *  Helper class to allow sharing a clock service across platforms in same VM.
 */
public class SharedClockService	extends SharedService<IClockService>	implements IClockService
{
	/**
	 *  Get the instance.
	 */
	public  SharedClockService(IComponentIdentifier provider, SharedServiceFactory<IClockService> factory)
	{
		super(provider, IClockService.class, factory);
	}
	
	//-------- IClockService interface --------
	
	@Override
	public long getTime()
	{
		return getInstance().getTime();
	}

	@Override
	public double getTick()
	{
		return getInstance().getTick();
	}

	@Override
	public long getStarttime()
	{
		return getInstance().getStarttime();
	}

	@Override
	public long getDelta()
	{
		return getInstance().getDelta();
	}

	@Override
	public String getState()
	{
		return getInstance().getState();
	}

	@Override
	public void setDelta(long delta)
	{
		getInstance().setDelta(delta);
	}

	@Override
	public double getDilation()
	{
		return getInstance().getDilation();
	}

	@Override
	public void setDilation(double dilation)
	{
		getInstance().setDilation(dilation);
	}

	@Override
	public void start()
	{
		getInstance().start();
	}

	@Override
	public void stop()
	{
		getInstance().stop();
	}

	@Override
	public ITimer createTimer(long time, ITimedObject to)
	{
		return getInstance().createTimer(time, to);
	}

	@Override
	public ITimer createTickTimer(ITimedObject to)
	{
		return getInstance().createTickTimer(to);
	}

	@Override
	public TimerTask createRealtimeTimer(long time, ITimedObject to)
	{
		return getInstance().createRealtimeTimer(time, to);
	}

	@Override
	public ITimer getNextTimer()
	{
		return getInstance().getNextTimer();
	}

	@Override
	public ITimer[] getTimers()
	{
		return getInstance().getTimers();
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

	@Override
	public String getClockType()
	{
		return getInstance().getClockType();
	}

	@Override
	public boolean advanceEvent()
	{
		return getInstance().advanceEvent();
	}

	@Override
	public void setClock(String type, IThreadPool tp)
	{
		getInstance().setClock(type, tp);
	}
}
