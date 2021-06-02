package jadex.micro.examples.mandelbrot_new;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Calculate agent allows calculating the colors of an area using a calculate service.
 */
@Description("Agent offering a calculate service.")
@ProvidedServices(@ProvidedService(type=ICalculateService.class, scope=ServiceScope.GLOBAL, implementation=@Implementation(CalculateService.class)))
@Agent(synchronous=Boolean3.FALSE)
public class CalculateAgent
{
	//-------- attributes --------

	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** Id of the current job. */
	protected Object taskid;
	
	//-------- methods --------
	
	/*@OnEnd
	public void terminate()
	{
		System.out.println("killed: "+agent.getId());
	}*/
	
	/**
	 *  Get the current task id.
	 */
	public Object	getTaskId()
	{
		return taskid;
	}
	
	/**
	 *  Set the current task id.
	 */
	public void	setTaskId(Object taskid)
	{
		this.taskid	= taskid;
	}
}
