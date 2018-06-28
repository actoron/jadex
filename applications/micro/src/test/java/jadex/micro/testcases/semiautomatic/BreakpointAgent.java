package jadex.micro.testcases.semiautomatic;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.Breakpoint;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Breakpoints;
import jadex.micro.annotation.Description;

/**
 *  A simple agent showing how to use breakpoints in the micro kernel.
 */
@Description("A simple agent showing how to use breakpoints in the micro kernel.")
@Breakpoints(value={"hop", "step", "jump"})
@Agent
public class BreakpointAgent 
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Execute a series of steps.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
//		step	= "hop";	// first step
		
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
		{			
			@Breakpoint("hop")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("Current step: hop");
				
//				step	= "step";	// second step

				agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
				{		
					@Breakpoint("step")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						System.out.println("Current step: step");

//						step	= "jump";	// third step
						
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
						{			
							@Breakpoint("jump")
							public IFuture<Void> execute(IInternalAccess ia)
							{
								System.out.println("Current step: jump");

								agent.killComponent();
								
								return IFuture.DONE;
							}
						});
						
						return IFuture.DONE;
					}
				});
				
				return IFuture.DONE;
			}
		});
		
		return new Future<Void>(); // never kill?!
	}
	
//	/**
//	 *  Return true, when a breakpoint was hit.
//	 */
//	public boolean isAtBreakpoint(String[] breakpoints)
//	{
//		return new HashSet(Arrays.asList(breakpoints)).contains(step);
//	}
	
//	/**
//	 *  Add the 'testresults' marking this agent as a testcase. 
//	 */
//	public static Object getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("A simple agent showing how to use breakpoints in the micro kernel.", 
//			null, null, null, new String[]{"hop", "step", "jump"}, null);
//	}
}

