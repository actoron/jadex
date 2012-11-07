package jadex.platform.service.cron.jobs;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cron.CronJob;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.future.IResultListener;

import java.util.Collection;

/**
 *  This job can be used to create a component.
 *  In order to create it at a remote platform the parent
 *  cid must be set in the creation info.
 */
public class CreateComponentJob extends CronJob<IComponentIdentifier>
{
	/**
	 *  Create a new CreateComponentJob. 
	 */
	public CreateComponentJob()
	{
	}

	/**
	 *  Create a new CreateComponentJob. 
	 */
	public CreateComponentJob(IFilter<Long> filter, String model)
	{
		super(filter, new CronCreateCommand(null, model, null, null));
	}
	
	/**
	 *  Create a new CreateComponentJob. 
	 */
	public CreateComponentJob(IFilter<Long> filter, String name, String model)
	{
		super(filter, new CronCreateCommand(name, model, null, null));
	}
	
	/**
	 *  Create a new CreateComponentJob. 
	 */
	public CreateComponentJob(IFilter<Long> filter, String name, String model, CreationInfo ci)
	{
		super(filter, new CronCreateCommand(name, model, ci, null));
	}
	
	/**
	 *  Create a new CreateComponentJob. 
	 */
	public CreateComponentJob(IFilter<Long> filter, String name, String model, CreationInfo ci,
		IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		super(filter, new CronCreateCommand(name, model, ci, resultlistener));
	}
}

