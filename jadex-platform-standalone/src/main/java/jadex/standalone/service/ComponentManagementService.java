package jadex.standalone.service;

import jadex.bridge.IComponentInstance;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.bridge.service.types.factory.IComponentAdapterFactory;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.kernelbase.IBootstrapFactory;
import jadex.standalone.ComponentAdapterFactory;
import jadex.standalone.StandaloneComponentAdapter;

/**
 *  Standalone implementation of component execution service.
 */
public class ComponentManagementService extends jadex.base.service.cms.ComponentManagementService
{
	//-------- attributes --------
	
	/** The adapter factory. */
	protected ComponentAdapterFactory adapterfactory = new ComponentAdapterFactory();
	
	//-------- constructors --------

	/**
	 *  Create a new component execution service.
	 *  @param exta	The service provider.
	 */
	public ComponentManagementService(IInternalAccess exta)
	{
		super(exta);
	}
	
	/**
	 *  Create a new component execution service.
	 *  @param exta	The service provider.
	 */
	public ComponentManagementService(IInternalAccess exta, IComponentAdapter root)
	{
		super(exta, root);
	}
	
	/**
	 *  Create a new component execution service.
	 *  @param exta	The service provider.
	 */
	public ComponentManagementService(IInternalAccess exta, IComponentAdapter root, IBootstrapFactory factory, boolean copy)
	{
		super(exta, root, factory, copy);
	}
	
	/**
	 *  Get the component instance from an adapter.
	 */
	public IComponentInstance getComponentInstance(IComponentAdapter adapter)
	{
		return ((StandaloneComponentAdapter)adapter).getComponentInstance();
	}

	/**
	 *  Get the component adapter factory.
	 */
	public IComponentAdapterFactory getComponentAdapterFactory()
	{
		return adapterfactory;
	}
	
	/**
	 *  Invoke kill on adapter.
	 */
	public IFuture<Void> killComponent(IComponentAdapter adapter)
	{
		Future<Void> ret = new Future<Void>();
		((StandaloneComponentAdapter)adapter).killComponent()
			.addResultListener(new DelegationResultListener<Void>(ret));
		return ret;
	}
	
	/**
	 *  Cancel the execution.
	 */
	public IFuture<Void> cancel(IComponentAdapter adapter)
	{
		Future<Void> ret = new Future<Void>();
		getExecutionService().cancel((StandaloneComponentAdapter)adapter)
			.addResultListener(new DelegationResultListener<Void>(ret));
		return ret;
	}

	/**
	 *  Do a step.
	 */
	public IFuture doStep(IComponentAdapter adapter)
	{
		Future ret = new Future();
		((StandaloneComponentAdapter)adapter).doStep()
			.addResultListener(new DelegationResultListener(ret));
		return ret;
	}
}
