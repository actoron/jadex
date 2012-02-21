package jadex.micro.testcases.semiautomatic;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Breakpoints;
import jadex.micro.annotation.Description;

import java.util.Arrays;
import java.util.HashSet;

/**
 *  A simple agent showing how to use breakpoints in the micro kernel.
 */
@Description("A simple agent showing how to use breakpoints in the micro kernel.")
@Breakpoints(value={"hop", "step", "jump"})
public class BreakpointAgent extends MicroAgent
{
	/** The current step. */
	protected String	step;
	
	/**
	 *  Execute a series of steps.
	 */
	public IFuture<Void> executeBody()
	{
		step	= "hop";	// first step
		
		waitFor(1000, new IComponentStep<Void>()
		{			
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("Current step: "+step);
				
				step	= "step";	// second step

				waitFor(1000, new IComponentStep<Void>()
				{			
					public IFuture<Void> execute(IInternalAccess ia)
					{
						System.out.println("Current step: "+step);

						step	= "jump";	// third step
						
						waitFor(1000, new IComponentStep<Void>()
						{			
							public IFuture<Void> execute(IInternalAccess ia)
							{
								System.out.println("Current step: "+step);

								killAgent();
								
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
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		return new HashSet(Arrays.asList(breakpoints)).contains(step);
	}
	
//	/**
//	 *  Add the 'testresults' marking this agent as a testcase. 
//	 */
//	public static Object getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("A simple agent showing how to use breakpoints in the micro kernel.", 
//			null, null, null, new String[]{"hop", "step", "jump"}, null);
//	}
}
