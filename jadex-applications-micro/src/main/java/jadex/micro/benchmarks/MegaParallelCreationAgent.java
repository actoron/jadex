package jadex.micro.benchmarks;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.util.HashMap;
import java.util.Map;

/**
 *  Agent creation benchmark. 
 */
@Arguments({
	@Argument(name="num", defaultvalue="1", clazz=int.class)
})
public class MegaParallelCreationAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public IFuture<Void> executeBody()
	{
		Map arguments = getArguments();	
		if(arguments==null)
			arguments = new HashMap();
		final Map args = arguments;	

		int num = args.get("num")!=null? ((Integer)args.get("num")).intValue(): 1;
		
		System.out.println("Created peer: "+num+" "+getComponentIdentifier());
		
		return new Future<Void>(); // never kill?!
	}

}
