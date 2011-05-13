package jadex.base;

import jadex.bridge.IInternalAccess;
import jadex.bridge.IMultiKernelListener;
import jadex.bridge.IMultiKernelNotifierService;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MultiKernelNotifierService extends BasicService implements IMultiKernelNotifierService
{
	/** Internal access */
	protected IInternalAccess ia;
	
	/** The listeners. */
	protected List listeners;
	
	public MultiKernelNotifierService(IInternalAccess ia)
	{
		super(ia.getServiceContainer().getId(), IMultiKernelNotifierService.class, null);
		this.ia = ia;
		listeners = new ArrayList();
	}
	
	/**
	 *  Adds a kernel listener.
	 *  @param listener The listener.
	 *  @return Null, when done.
	 */
	public IFuture addKernelListener(IMultiKernelListener listener)
	{
		listeners.add(listener);
		return IFuture.DONE;
	}
	
	/**
	 *  Removes a kernel listener.
	 *  @param listener The listener.
	 *  @return Null, when done.
	 */
	public IFuture removeKernelListener(IMultiKernelListener listener)
	{
		listeners.remove(listeners);
		return IFuture.DONE;
	}
	
	// TODO: Temporary, until service references become available.
	public IFuture fireTypesAdded(String[] types)
	{
		final Future ret = new Future();
		IMultiKernelListener[] ls = (IMultiKernelListener[]) listeners.toArray(new IMultiKernelListener[listeners.size()]);
		IResultListener counter = ia.createResultListener(new CounterResultListener(ls.length, true, ia.createResultListener(new DelegationResultListener(ret))));
		for (int i = 0; i < ls.length; ++i)
			ls[i].componentTypesAdded(types).addResultListener(counter);
		return ret;
	}
	public IFuture fireTypesRemoved(String[] types)
	{
		final Future ret = new Future();
		IMultiKernelListener[] ls = (IMultiKernelListener[]) listeners.toArray(new IMultiKernelListener[listeners.size()]);
		IResultListener counter = ia.createResultListener(new CounterResultListener(ls.length, true, ia.createResultListener(new DelegationResultListener(ret))));
		for (int i = 0; i < ls.length; ++i)
			ls[i].componentTypesAdded(types).addResultListener(counter);
		return ret;
	}
}
