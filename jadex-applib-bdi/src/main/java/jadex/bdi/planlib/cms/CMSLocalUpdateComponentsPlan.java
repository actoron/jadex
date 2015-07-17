package jadex.bdi.planlib.cms;

import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Update the belief set containing the local components.
 */
public class CMSLocalUpdateComponentsPlan extends Plan
{ 
	//-------- attributes --------
	
	/** The listener. */
	protected ICMSComponentListener	listener;
	
	//-------- methods --------
	
	/**
	 *  The body method.
	 */
	public void body()
	{
		final IComponentManagementService	ces	= (IComponentManagementService)getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get();
		this.listener	= new ICMSComponentListener()
		{
			public IFuture componentAdded(final IComponentDescription desc)
			{
				try
				{
					getExternalAccess().scheduleStep(new IComponentStep<Void>()
					{
						@Classname("addFact")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							ia.getComponentFeature(IBDIXAgentFeature.class).getBeliefbase().getBeliefSet("components").addFact(desc);
							return IFuture.DONE;
						}
					});
				}
				catch(ComponentTerminatedException ate)
				{
					ces.removeComponentListener(null, this);
				}
				return IFuture.DONE;
			}
					
			public IFuture componentRemoved(final IComponentDescription desc, java.util.Map results)
			{
				try
				{
					getExternalAccess().scheduleStep(new IComponentStep<Void>()
					{
						@Classname("removeFact")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							ia.getComponentFeature(IBDIXAgentFeature.class).getBeliefbase().getBeliefSet("components").removeFact(desc);
							return IFuture.DONE;
						}
					});
				}
				catch(ComponentTerminatedException ate)
				{
					ces.removeComponentListener(null, this);
				}
				return IFuture.DONE;
			}

			public IFuture componentChanged(IComponentDescription desc)
			{
				return IFuture.DONE;
			}
		};
		
		ces.addComponentListener(null, listener);
		
		IFuture fut = ces.getComponentDescriptions();
		IComponentDescription[] descs = (IComponentDescription[])fut.get();
		getBeliefbase().getBeliefSet("components").addFacts(descs);
		
//		getScope().addComponentListener(new TerminationAdapter()
//		{	
//			public void componentTerminated()
//			{
//				ces.removeComponentListener(null, listener);
//			}
//		});
		
		getScope().subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
			.addResultListener(new IntermediateDefaultResultListener<IMonitoringEvent>()
		{
			public void intermediateResultAvailable(IMonitoringEvent result)
			{
				ces.removeComponentListener(null, listener);
			}
		});
	}
}
