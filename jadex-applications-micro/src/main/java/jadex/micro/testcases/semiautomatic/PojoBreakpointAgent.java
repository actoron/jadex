package jadex.micro.testcases.semiautomatic;

import java.util.Arrays;
import java.util.HashSet;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentBreakpoint;
import jadex.micro.annotation.Breakpoints;
import jadex.micro.annotation.Description;


/**
 *  A simple agent showing how to use breakpoints in the micro kernel.
 */
@Description("A simple agent showing how to use breakpoints in the micro kernel.")
@Breakpoints(value={"hop", "step", "jump"})
@Agent
public class PojoBreakpointAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The current step. */
	protected String	step;
	
	/**
	 *  Execute a series of steps.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		step	= "hop";	// first step
		
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
		{			
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("Current step: "+step);
				
				step	= "step";	// second step

				agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
				{			
					public IFuture<Void> execute(IInternalAccess ia)
					{
						System.out.println("Current step: "+step);

						step	= "jump";	// third step
						
						agent.getComponentFeature(IExecutionFeature.class).waitForDelay(1000, new IComponentStep<Void>()
						{			
							public IFuture<Void> execute(IInternalAccess ia)
							{
								System.out.println("Current step: "+step);

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
	
	/**
	 *  Return true, when a breakpoint was hit.
	 */
	@AgentBreakpoint
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		return new HashSet(Arrays.asList(breakpoints)).contains(step);
	}
}
