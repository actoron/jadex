package jadex.bytecode.vmhacks;

import java.lang.instrument.Instrumentation;
import java.util.concurrent.Semaphore;

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
    	VmHacks.getUnsafe().setInstrumentation(inst);
    }
}
