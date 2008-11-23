package jadex.microkernel.examples;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.fipa.IAMS;
import jadex.bridge.IAgentAdapter;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IClockService;
import jadex.commons.concurrent.IResultListener;
import jadex.microkernel.MicroAgent;

/**
 *
 */
public class CreationTestAgent extends MicroAgent
{
	protected int step;
	
	/**
	 *  Execute an agent step.
	 */
	public boolean executeAction()
	{
		switch(step)
		{
			case 0:
			step++;
			
			Map args = getArguments();		
			
			if(args.size()==0)
			{
				args.put("num", new Integer(0));
				args.put("max", new Integer(1000));
				Long startmem = new Long(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
				Long starttime = new Long(((IClockService)getPlatform().getService(IClockService.class)).getTime());
				args.put("startmem", startmem);
				args.put("starttime", starttime);
			}
			
			Integer num = (Integer)getArgument("num");
			Integer max = (Integer)getArgument("max");
			
			System.out.println("Created peer: "+num);
			
			if(num.intValue()<1000)
			{
				args.put("num", new Integer(num.intValue()+1));
//				System.out.println("Args: "+num+" "+args);
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
			}
			else
			{
				Long startmem = (Long)args.get("startmem");
				Long starttime = (Long)args.get("starttime");
				long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
				long omem = (used-startmem.longValue())/1024;
				long upera = (used-startmem.longValue())/max.longValue()/1024;
				System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");

				long end = ((IClockService)getPlatform().getService(IClockService.class)).getTime();
				System.out.println("Last peer created. "+max+" agents started.");
				double dur = ((double)end-starttime.longValue())/1000.0;
				double pera = dur/max.longValue();
				System.out.println("Needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
			}
		}
		return false;
	}
}
