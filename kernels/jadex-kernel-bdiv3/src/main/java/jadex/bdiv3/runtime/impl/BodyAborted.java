package jadex.bdiv3.runtime.impl;

/**
 *  An error thrown to abort the execution of the plan body.
 */
public class BodyAborted	extends	ThreadDeath 
{
//	public BodyAborted()
//	{
//		System.err.print(this+": ");
//		Thread.dumpStack();
//	}
//	
//	public String toString()
//	{
//		return super.toString()+"@"+hashCode();
//	}
	
	public void printStackTrace()
	{
		Thread.dumpStack();
		super.printStackTrace();
	}
}
