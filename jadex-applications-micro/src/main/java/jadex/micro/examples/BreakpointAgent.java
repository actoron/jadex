package jadex.micro.examples;

import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

import java.util.Arrays;
import java.util.HashSet;

/**
 *  A simple agent showing how to use breakpoints in the micro kernel.
 */
public class BreakpointAgent extends MicroAgent
{
	/** The current step. */
	protected String	step;
	
	/**
	 *  Execute a series of steps.
	 */
	public void executeBody()
	{
		step	= "hop";	// first step
		
		waitFor(1000, new Runnable()
		{			
			public void run()
			{
				System.out.println("Current step: "+step);
				
				step	= "step";	// second step

				waitFor(1000, new Runnable()
				{			
					public void run()
					{
						System.out.println("Current step: "+step);

						step	= "jump";	// third step
						
						waitFor(1000, new Runnable()
						{			
							public void run()
							{
								System.out.println("Current step: "+step);

								killAgent();
							}
						});
					}
				});
			}
		});
	}
	
	/**
	 *  Return true, when a breakpoint was hit.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		return new HashSet(Arrays.asList(breakpoints)).contains(step);
	}
	
	/**
	 *  Add the 'testresults' marking this agent as a testcase. 
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("A simple agent showing how to use breakpoints in the micro kernel.", 
			null, null, null, new String[]{"hop", "step", "jump"});
	}
}
