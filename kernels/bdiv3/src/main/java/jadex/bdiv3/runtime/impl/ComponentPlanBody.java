package jadex.bdiv3.runtime.impl;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
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
	
	/** The plan element. */
	protected RPlan	rplan;
	
	/** The created component. */
	protected IComponentIdentifier	cid;
	
	/** True, when the plan was aborted. */
	protected boolean	aborted;
	
	//-------- constructors --------
	
	/**
	 *  Create a component plan body.
	 */
	public ComponentPlanBody(String component, IInternalAccess ia, RPlan rplan)
	{
		this.component	= component;
		this.ia	= ia;
		this.rplan	= rplan;
	}
	
	//-------- IPlanBody interface --------
	
	/**
	 *  Get the plan body.
	 */
	public Object getBody()
	{
		return cid;
	}
	
	/**
	 *  Execute the plan body.
	 */
	public IFuture<Void> executePlan()
	{
		final Future<Void>	ret	= new Future<Void>();

		rplan.setLifecycleState(RPlan.PlanLifecycleState.BODY);
		// Todo: should also set processing state and RPLANS thread local?
		
		IComponentManagementService cms = ia.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
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
				rplan.setLifecycleState(aborted ? RPlan.PlanLifecycleState.ABORTED : RPlan.PlanLifecycleState.PASSED);
				ret.setResult(null);
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				rplan.setLifecycleState(RPlan.PlanLifecycleState.FAILED);
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
			// todo: fix synchronous subcomponents!? may be called from inner or outer component.
			IComponentManagementService cms = ia.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IComponentManagementService.class));
			cms.destroyComponent(cid);
		}
	}
}
