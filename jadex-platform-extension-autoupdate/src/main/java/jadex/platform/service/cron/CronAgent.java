package jadex.platform.service.cron;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ICommand;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

/**
 *  The cron agent is an agent based implementation
 *  of unix cron. Allows for specifying time pattern
 *  and a job. The time pattern will be checked every
 *  minute and the job will be executed if the pattern 
 *  matches. The pattern corresponds to the way unix
 *  cron patterns are defined:
 *  
 *  *    *    *            *     *
 *  Min  Hour Day of Month Month Mo-Fr   
 */
@Arguments(
{
	@Argument(name="pattern", clazz=String.class, defaultvalue="\"* * * * *\""),
	@Argument(name="job", clazz=ICommand.class)
})
@Agent
public class CronAgent
{
	//-------- attributes --------
	
	/** The pattern. */
	@AgentArgument(convert="new TimePattern($value)")
	protected TimePattern pattern;
	
	/** The job. */
	@AgentArgument
	protected ICommand job;
	
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		IComponentStep<Void> check = new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				// check pattern
				long time = System.currentTimeMillis();
				if(pattern.match(time))
				{
					// schedule job on subagent?!
					if(job!=null)
					{
						job.execute(new Long(time));
					}
					else
					{
						System.out.println("no job to schedule");
					}
				}
//				else
//				{
//					System.out.println("no job");
//				}
				
				// wait
				long cur = System.currentTimeMillis();
				long min = ((cur/60000)+1)*60000; // next minute
				long sleep = (min - cur);
				if(sleep>0)
				{
					agent.waitFor(sleep, this);
				}
				else
				{
					agent.scheduleStep(this);
				}
				
				return IFuture.DONE;
			}
		};
		
		agent.scheduleStep(check);
	}
}
