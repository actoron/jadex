package jadex.micro.testcases;

import jadex.micro.MicroAgent;


public class ExceptionAgent extends MicroAgent
{
	public void executeBody()
	{
		System.out.println("execute NullPointerTest ...");
		String s = null;
		if(s.equals(""))
			System.out.println("Empty String");
		System.out.println("... finished");
	}
}
