package jadex.micro.benchmarks;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.util.HashMap;
import java.util.Map;

/**
 *  Agent creation benchmark. 
 */
@Arguments({
	@Argument(name="num", defaultvalue="1", typename="int"),
})
public class MegaParallelCreationAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		Map arguments = getArguments();	
		if(arguments==null)
			arguments = new HashMap();
		final Map args = arguments;	

		int num = args.get("num")!=null? ((Integer)args.get("num")).intValue(): 1;
		
		System.out.println("Created peer: "+num+" "+getComponentIdentifier());
	}

}
