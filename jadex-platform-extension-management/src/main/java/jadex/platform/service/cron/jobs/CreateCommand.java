package jadex.platform.service.cron.jobs;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.IResultCommand;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

import java.util.Collection;

/**
 *  The create command is used to create a component via the cms.
 */
public class CreateCommand implements IResultCommand<IFuture<IComponentIdentifier>, IInternalAccess>
{
	/** The name. */
	protected String name;
	
	/** The model. */
	protected String model;
	
	/** The creation info. */
	protected CreationInfo info;
	
	/** The result listener. */
	protected IResultListener<Collection<Tuple2<String, Object>>> resultlistener;
	
	/**
	 *  Create a new CreateCommand. 
	 */
	public CreateCommand(String name, String model, CreationInfo info,
		IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		this.name = name;
		this.model = model;
		this.info = info;
		this.resultlistener = resultlistener;
	}

	/**
	 *  Execute the command.
	 *  @param args The argument(s) for the call.
	 */
	public IFuture<IComponentIdentifier> execute(final IInternalAccess ia)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		
//		final IInternalAccess ia = args.getFirstEntity();
		SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(ia.createResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.createComponent(name, model, info, resultlistener).addResultListener(
					ia.createResultListener(new DelegationResultListener<IComponentIdentifier>(ret)));
//				{
//					public void resultAvailable(IComponentIdentifier cid)
//					{
//						System.out.println("created: "+cid);//+" at: "+args.getSecondEntity());
//						ret
//					}
//				}));
			}
		}));
		
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

	/**
	 *  Get the resultlistener.
	 *  @return The resultlistener.
	 */
	public IResultListener<Collection<Tuple2<String, Object>>> getResultlistener()
	{
		return resultlistener;
	}

	/**
	 *  Set the resultlistener.
	 *  @param resultlistener The resultlistener to set.
	 */
	public void setResultlistener(IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		this.resultlistener = resultlistener;
	}
}