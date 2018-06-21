package jadex.platform.service.cron.jobs;

import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.bridge.service.types.cron.CronJob;
import jadex.platform.service.cron.TimePatternFilter;

/**
 *  This job can be used to create a component.
 *  In order to create it at a remote platform the parent
 *  cid must be set in the creation info.
 */
public class CreateComponentJob extends CronJob<CMSStatusEvent>
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
	public CreateComponentJob(String pattern, String model)
	{
		super(pattern, new TimePatternFilter(pattern), new CronCreateCommand(null, model, null));
	}
	
	/**
	 *  Create a new CreateComponentJob. 
	 */
	public CreateComponentJob(String pattern, String name, String model)
	{
		super(pattern, new TimePatternFilter(pattern), new CronCreateCommand(name, model, null));
	}
	
	/**
	 *  Create a new CreateComponentJob. 
	 */
	public CreateComponentJob(String pattern, String name, String model, CreationInfo ci)
	{
		super(pattern, new TimePatternFilter(pattern), new CronCreateCommand(name, model, ci));
	}
	
//	/**
//	 *  Create a new CreateComponentJob. 
//	 */
//	public CreateComponentJob(String pattern, IFilter<Long> filter, String name, String model, CreationInfo ci,
//		IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
//	{
//		super(pattern, filter, new CronCreateCommand(name, model, ci, resultlistener));
//	}
}

