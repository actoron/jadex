package jadex.standalone.service;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentInstance;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
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
	public ComponentManagementService(IExternalAccess exta)
	{
		super(exta);
	}
	
	/**
	 *  Create a new component execution service.
	 *  @param exta	The service provider.
	 */
	public ComponentManagementService(IExternalAccess exta, IComponentAdapter root)
	{
		super(exta, root);
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
	public IFuture killComponent(IComponentAdapter adapter)
	{
		Future ret = new Future();
		((StandaloneComponentAdapter)adapter).killComponent()
			.addResultListener(new DelegationResultListener(ret));
		return ret;
	}
	
	/**
	 *  Cancel the execution.
	 */
	public IFuture cancel(IComponentAdapter adapter)
	{
		Future ret = new Future();
		getExecutionService().cancel((StandaloneComponentAdapter)adapter)
			.addResultListener(new DelegationResultListener(ret));
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
	
	/**
	 *  Get the component description.
	 */
	public IComponentDescription getDescription(IComponentAdapter adapter)
	{
		return ((StandaloneComponentAdapter)adapter).getDescription();
	}
}
