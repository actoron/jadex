package jadex.bridge.service.types.threadpool;

import jadex.bridge.service.annotation.Service;
import jadex.commons.concurrent.IThreadPool;

/**
 *  Interface for threadpool service.
 */
@Service(system=true)
public interface IThreadPoolService extends IThreadPool
{
}
