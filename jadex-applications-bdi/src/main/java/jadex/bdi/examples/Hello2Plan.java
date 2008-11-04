package jadex.bdi.examples;

import jadex.bdi.runtime.Plan;


/**
 *  The hello world plan prints out a short welcome message.
 */
public class Hello2Plan extends Plan
{
	//-------- constructors ---------
	
	/**
	 *  Create a new helloworld plan.
	 */
	public Hello2Plan()
	{
		System.out.println("Created "+this);
	}
	
	//-------- methods --------

	/**
	 *  Handle the ping request.
	 */
	public void body()
	{
		String	msg	= (String)getParameter("msg").getValue();
		System.out.println(msg);
	}
}

