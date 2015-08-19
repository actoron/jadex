package jadex.bdiv3.runtime.impl;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Plan body implementation as a component.
 */
public class ComponentPlanBody implements IPlanBody
{
	//-------- attributes --------
	
	/** The component to create. */
	protected String	component;
	
	/** The internal access. */
	protected IInternalAccess	ia;
	
	/** The created component. */
	protected IComponentIdentifier	cid;
	
	//-------- constructors --------
	
	/**
	 *  Create a component plan body.
	 */
	public ComponentPlanBody(String component, IInternalAccess ia)
	{
		this.component	= component;
		this.ia	= ia;
	}
	
	//-------- IPlanBody interface --------
	
	/**
	 *  Get the plan body.
	 */
	public Object getBody()
	{
		return null;
	}
	
	/**
	 *  Execute the plan body.
	 */
	public IFuture<Void> executePlan()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		IComponentManagementService cms = SServiceProvider.getLocalService(ia, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		cms.createComponent(null, component, new CreationInfo(ia.getComponentIdentifier()))
			.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
		{
			@Override
			public void firstResultAvailable(IComponentIdentifier result)
			{
				cid	= result;
			}
			
			@Override
			public void secondResultAvailable(Map<String, Object> result)
			{
				ret.setResult(null);
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Issue abortion of the plan body, if currently running.
	 */
	public void abort()
	{
		if(cid!=null)
		{
			IComponentManagementService cms = SServiceProvider.getLocalService(ia, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			cms.destroyComponent(cid);
		}
	}
}
