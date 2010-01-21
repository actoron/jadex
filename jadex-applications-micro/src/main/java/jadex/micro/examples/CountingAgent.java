package jadex.micro.examples;

import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *  An agent that is counting to infinity.
 *  Used to demonstrate "semantic" breakpoints.
 */
public class CountingAgent extends MicroAgent
{
	/** The counter. */
	protected int	cnt;
	
	/**
	 *  Execute a series of steps.
	 */
	public void executeBody()
	{
		cnt	= 1;

		Runnable	step	= new Runnable()
		{
			public void run()
			{
				System.out.println("Step: "+cnt);
				
				cnt++;

				scheduleStep(this);
			}
		};
		
		scheduleStep(step);
	}
	
	/**
	 *  Return true, when a breakpoint was hit.
	 */
	public boolean isAtBreakpoint(String[] breakpoints)
	{
		boolean	isatbreakpoint	= false;
		Set	bps	= new HashSet(Arrays.asList(breakpoints));
		
		isatbreakpoint	= isatbreakpoint || bps.contains(Integer.toString(cnt));
		isatbreakpoint	= isatbreakpoint || cnt%2!=0 && bps.contains("odd");
		isatbreakpoint	= isatbreakpoint || cnt%2==0 && bps.contains("even");
		isatbreakpoint	= isatbreakpoint || cnt%10==0 && bps.contains("every tenth");
		
		return isatbreakpoint;
	}
	
	/**
	 *  Add the 'testresults' marking this agent as a testcase. 
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("A simple agent showing how to use breakpoints in the micro kernel.", 
			null, null, null, new String[]{"2", "5", "odd", "even", "every tenth"});
	}
}
