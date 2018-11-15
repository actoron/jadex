package jadex.micro.examples.helloworld;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

/**
 *  The micro version of the hello world agent.
 */
@Agent
@Description("This agent prints out a hello message.")
@Arguments(@Argument(name="welcome text", description= "This parameter is the text printed by the agent.", 
	clazz=String.class, defaultvalue="\"Hello world, this is a Jadex micro agent.\""))
public class PojoHelloWorldAgent
{
	//-------- attributes --------
	
	/** The micro agent class. */
	@Agent
	protected IInternalAccess agent;
	
	/** The welcome text. */
	@AgentArgument("welcome text")
	protected String text;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	@AgentBody
	public void executeBody()
	{
//		System.out.println(agent.getComponentFeature(IArgumentsFeature.class).getArgument("welcome text"));
		
//		IClockService cl = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class, ServiceScope.PLATFORM));
//		System.out.println("clockservice: "+cl);
		
//		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(2000, new IComponentStep<Void>()
//		{			
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				System.out.println("Good bye world.");
//				agent.killComponent();
//				return IFuture.DONE;
//			}
//		});
		
		System.out.println(text);
		agent.getFeature(IExecutionFeature.class).waitForDelay(2000).get();
		System.out.println("Good bye world.");
		agent.killComponent();
	}
}
