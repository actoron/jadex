package jadex.bytecode.vmhacks;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import jadex.bytecode.vmhacks.VmHacks.InstrumentationCommand;


/**
 *  Agent for acquiring instrumentation access.
 *
 */
public class VmHacksAgent
{
	/**
     * The entry point invoked when this agent is started. 
     */
	@SuppressWarnings("unchecked")
    public static void agentmain(String agentargs, final Instrumentation inst)
    {
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				// Get main global store.
				ArrayList<Object> vmhs = (ArrayList<Object>) Logger.getLogger("23070273").getFilter();
//				LinkedBlockingQueue<Object> queue = (LinkedBlockingQueue<Object>) vmhs.get(0);
//		    	queue.offer(inst);
		    	
//		    	System.out.println(Thread.currentThread().getName());
//		    	System.out.println("DAEMON " +Thread.currentThread().isDaemon());
		    	
		    	LinkedBlockingQueue<InstrumentationCommand> queue = (LinkedBlockingQueue<InstrumentationCommand>) vmhs.get(0);
		    	
		    	while (true)
		    	{
		    		try
					{
		    			InstrumentationCommand cmd = queue.take();
		    			cmd.execute(inst);
					}
					catch (InterruptedException e)
					{
					}
		    	}
			}
		});
		t.setDaemon(true);
		t.setName("Jadex Instrumentation Thread");
		t.start();
    }
}
