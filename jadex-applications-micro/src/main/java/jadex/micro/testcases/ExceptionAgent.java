package jadex.micro.testcases;

import jadex.micro.MicroAgent;


public class ExceptionAgent extends MicroAgent
{
	public void executeBody()
	{
		System.out.println("execute ExceptionTest ...");
		throw new RuntimeException("Exception in agent body");
//		System.out.println("... finished");
	}
}
