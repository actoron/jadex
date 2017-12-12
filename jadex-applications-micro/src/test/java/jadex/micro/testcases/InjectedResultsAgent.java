package jadex.micro.testcases;

import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentResult;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@Arguments(
{
	@Argument(name="myarg", clazz=String.class, defaultvalue="\"def_val\"")
})
@Results(
{
	@Result(name="myres", clazz=String.class),
	@Result(name="myint", clazz=int.class, defaultvalue="-1")
})
public class InjectedResultsAgent
{
	@AgentArgument(value="myarg")
	@AgentResult
	protected String myres;
	
	@AgentResult(value="myint", convert="\"\"+$value", convertback="Integer.parseInt($value)")
	protected String someint;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
//		System.out.println("myres: "+myres);
//		System.out.println("someint: "+someint);
		
		someint = "99";
		
		return IFuture.DONE;
	}
}
