package jadex.bytecode.vmhacks;

import java.lang.instrument.Instrumentation;

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
    	VmHacks.get().setInstrumentation(inst);
    }
}
