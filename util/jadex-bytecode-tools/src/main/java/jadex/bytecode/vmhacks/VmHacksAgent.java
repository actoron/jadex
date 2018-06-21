package jadex.bytecode.vmhacks;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


/**
 *  Agent for acquiring instrumentation access.
 *
 */
public class VmHacksAgent
{
	/**
     * The entry point invoked when this agent is started. 
     */
    public static void agentmain(String agentargs, Instrumentation inst)
    {
    	// Get main global store.
    	@SuppressWarnings("unchecked")
		ArrayList<Object> vmhs = (ArrayList<Object>) Logger.getLogger("23070273").getFilter();
    	@SuppressWarnings("unchecked")
		LinkedBlockingQueue<Object> queue = (LinkedBlockingQueue<Object>) vmhs.get(0);
    	queue.offer(inst);
    	
//    	VmHacks.get().setInstrumentation(inst);
    }
}
