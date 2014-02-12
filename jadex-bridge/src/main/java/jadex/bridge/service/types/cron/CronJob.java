package jadex.bridge.service.types.cron;

import jadex.bridge.IInternalAccess;
import jadex.commons.IFilter;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  The cron job consists of:
 *  - a unique id
 *  - a time pattern filter
 *  - a command
 */
public class CronJob<T>
{
	//-------- attributes --------
	
	/** The id. */
	protected String id;
	
	/** The time pattern. */
	protected String pattern;
	
	/** The filter. */
	protected IFilter<Long> filter;
	
	/** The command. */
	protected IResultCommand<ISubscriptionIntermediateFuture<T>, Tuple2<IInternalAccess, Long>> command;
	 
	//-------- constructors --------
	
	/**
	 *  Create a new cronjob. 
	 */
	public CronJob()
	{
	}

	/**
	 *  Create a new cron job.
	 */
	public CronJob(String pattern, IFilter<Long> filter, IResultCommand<ISubscriptionIntermediateFuture<T>, Tuple2<IInternalAccess, Long>> command)
	{
		this.id = SUtil.createUniqueId("cronjob");
		this.pattern = pattern;
		this.filter = filter;
		this.command = command;
	}

	//-------- methods --------
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
	/**
	 *  Get the pattern.
	 *  @return The pattern.
	 */
	public String getPattern()
	{
		return pattern;
	}

	/**
	 *  Set the pattern.
	 *  @param pattern The pattern to set.
	 */
	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	/**
	 *  Get the filter.
	 *  @return The filter.
	 */
	public IFilter<Long> getFilter()
	{
		return filter;
	}

	/**
	 *  Set the filter.
	 *  @param filter The filter to set.
	 */
	public void setFilter(IFilter<Long> filter)
	{
		this.filter = filter;
	}

	/**
	 *  Get the command.
	 *  @return The command.
	 */
	public IResultCommand<ISubscriptionIntermediateFuture<T>, Tuple2<IInternalAccess, Long>> getCommand()
	{
		return command;
	}

	/**
	 *  Set the command.
	 *  @param command The command to set.
	 */
	public void setCommand(IResultCommand<ISubscriptionIntermediateFuture<T>, Tuple2<IInternalAccess, Long>> command)
	{
		this.command = command;
	}

//	/** 
//	 *  Compute the hashcode.
//	 */
//	public int hashCode()
//	{
//		return id.hashCode()*31;
//	}
//
//	/** 
//	 *  Test for equality.
//	 */
//	public boolean equals(Object obj)
//	{
//		boolean ret = false;
//		if(obj instanceof CronJob)
//		{
//			ret = ((CronJob)obj).getId().equals(getId());
//		}
//		return ret;
//	}
	
}
