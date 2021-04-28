package jadex.base.test.impl;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.IChangeListener;

/**
 *  Helper class to allow sharing a thread pool service across platforms in same VM.
 */
public class SharedThreadPoolService	extends SharedService<IThreadPoolService>	implements IThreadPoolService
{
	/**
	 *  Get the instance.
	 */
	public  SharedThreadPoolService(IComponentIdentifier provider, SharedServiceFactory<IThreadPoolService> factory)
	{
		super(provider, IThreadPoolService.class, factory);
	}
	
	//-------- IThreadPool interface --------
		
	@Override
	public void execute(Runnable task)
	{
		getInstance().execute(task);
	}

	@Override
	public void executeForever(Runnable task)
	{
		getInstance().executeForever(task);
	}

	@Override
	public void dispose()
	{
		// IThreadPool method -> Should not be called on service
		throw new UnsupportedOperationException("Service shut be terminated via shutdown!");
	}

	@Override
	public boolean isRunning()
	{
		return getInstance().isRunning();
	}

	@Override
	public void addFinishListener(IChangeListener<Void> listener)
	{
		getInstance().addFinishListener(listener);
	}
}
