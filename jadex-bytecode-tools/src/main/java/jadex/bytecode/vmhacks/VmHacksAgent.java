package jadex.bytecode.vmhacks;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Semaphore;

/**
 *  Agent for acquiring instrumentation access.
 *
 */
public class VmHacksAgent
{
	protected static final Semaphore WAIT = new Semaphore(0);
	
	private static Instrumentation INSTRUMENTATION;
	
	/**
     * The entry point invoked when this agent is started. 
     */
    public static void agentmain(String agentargs, Instrumentation inst)
    {
//    	System.out.println("Agent PID: " + ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
//    	runqueue = new LinkedBlockingQueue<Tuple2<Future<Object>, IResultCommand<Object, Instrumentation>>>();
//    	System.out.println("Setting true");
    	INSTRUMENTATION = inst;
    	WAIT.release();
    }
    
    static Instrumentation getInstrumentation()
	{
		return INSTRUMENTATION;
	}
}
