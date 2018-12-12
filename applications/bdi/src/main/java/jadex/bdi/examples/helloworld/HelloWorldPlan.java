package jadex.bdi.examples.helloworld;

import jadex.bdiv3x.runtime.Plan;

/**
 *  The hello world plan prints out a short welcome message.
 */
public class HelloWorldPlan extends Plan
{
	//-------- methods --------

	/**
	 *  Handle the ping request.
	 */
	public void body()
	{
		System.out.println("\nHello world!");
		waitFor(2000);
		System.out.println("\n"+getBeliefbase().getBelief("msg").getFact());
		waitFor(2000);
		System.out.println("\nSee you. Bye! ");

		killAgent();
	}
}

