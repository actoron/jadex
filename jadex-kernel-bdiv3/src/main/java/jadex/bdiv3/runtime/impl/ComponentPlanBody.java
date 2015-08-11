package jadex.bdiv3.runtime.impl;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
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
//		ia.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//		{
//			public void customResultAvailable(IComponentManagementService cms)
//			{
				cms.createComponent(null, component, new CreationInfo(ia.getComponentIdentifier()),
					new ExceptionDelegationResultListener<Collection<Tuple2<String,Object>>, Void>(ret)
				{
					public void customResultAvailable(Collection<Tuple2<String,Object>> result)
					{
						ret.setResult(null);
					}
				})
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{
					public void customResultAvailable(IComponentIdentifier result)
					{
					}
				});
//			}
//		});
		
		return ret;
	}
}
