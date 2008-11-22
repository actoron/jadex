package jadex.microkernel.examples;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.concurrent.IResultListener;
import jadex.microkernel.MicroAgent;

/**
 *
 */
public class CreationTestAgent extends MicroAgent
{
	/**
	 *  Execute an agent step.
	 */
	public boolean executeAction()
	{
		Integer num = (Integer)getArgument("num");
		Integer max = (Integer)getArgument("max");
	
		if(num==null)
		{
			num = new Integer(0);
			max = new Integer(1000);
		}
//		System.out.println("Created peer: "+num);
		
		if(num.intValue()<1000)
		{
			Map args = new HashMap();
			args.put("num", new Integer(num.intValue()+1));
			args.put("max", max);
			System.out.println("Args: "+num+" "+args);
			final IAMS ams = (IAMS)getPlatform().getService(IAMS.class);
			ams.createAgent(null, "jadex.microkernel.examples.CreationTestAgent.class", null, args, new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					ams.startAgent((IAgentIdentifier)result, null);
				}
				public void exceptionOccurred(Exception exception)
				{
				}
			});
			
//			ams.createAgent("Peer_"+(num.intValue()+1), "jadex.microkernel.examples.CreationTestAgent.class", null, args, null);
		}
		
		return false;
	}
}
