package jadex.platform.service.cron.jobs;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.commons.IResultCommand;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

/**
 *  The create command is used to create a component via the cms.
 */
public class CreateCommand implements IResultCommand<IIntermediateFuture<CMSStatusEvent>, IInternalAccess>
{
	/** The name. */
	protected String name;
	
	/** The model. */
	protected String model;
	
	/** The creation info. */
	protected CreationInfo info;
	
//	/** The result listener. */
//	protected IResultListener<Collection<Tuple2<String, Object>>> resultlistener;
	
	/**
	 *  Create a new CreateCommand. 
	 */
	public CreateCommand(String name, String model, CreationInfo info)
//		IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		this.name = name;
		this.model = model;
		this.info = info;
//		this.resultlistener = resultlistener;
	}

	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> execute(final IInternalAccess ia)
	{
		final SubscriptionIntermediateDelegationFuture<CMSStatusEvent> ret = (SubscriptionIntermediateDelegationFuture<CMSStatusEvent>)
			SFuture.getNoTimeoutFuture(SubscriptionIntermediateDelegationFuture.class, ia);
		
		info.setName(name);
		info.setFilename(model);
		ISubscriptionIntermediateFuture<CMSStatusEvent> fut = ia.getFeature(ISubcomponentsFeature.class).createComponentWithResults(null, info);
		TerminableIntermediateDelegationResultListener<CMSStatusEvent> lis = new TerminableIntermediateDelegationResultListener<CMSStatusEvent>(ret, fut);
		fut.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(lis));
				
		return ret;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the model.
	 *  @return The model.
	 */
	public String getModel()
	{
		return model;
	}

	/**
	 *  Set the model.
	 *  @param model The model to set.
	 */
	public void setModel(String model)
	{
		this.model = model;
	}

	/**
	 *  Get the info.
	 *  @return The info.
	 */
	public CreationInfo getInfo()
	{
		return info;
	}

	/**
	 *  Set the info.
	 *  @param info The info to set.
	 */
	public void setInfo(CreationInfo info)
	{
		this.info = info;
	}

//	/**
//	 *  Get the resultlistener.
//	 *  @return The resultlistener.
//	 */
//	public IResultListener<Collection<Tuple2<String, Object>>> getResultlistener()
//	{
//		return resultlistener;
//	}
//
//	/**
//	 *  Set the resultlistener.
//	 *  @param resultlistener The resultlistener to set.
//	 */
//	public void setResultlistener(IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
//	{
//		this.resultlistener = resultlistener;
//	}
}