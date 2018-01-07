package jadex.bytecode.vmhacks;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 *  Class for natively starting instrumentation.
 *
 */
public class InstrumentStarter
{
	/** Instrumentation library name. */
	public static final String JNA_LIBRARY_NAME = "instrument";
	
	/** Register native methods. */
	static
	{
//		System.loadLibrary("instrument");
		Native.register(JNA_LIBRARY_NAME);
	}
	
	/**
	 *  Method for starting the agent.
	 *  @param jarfile The jarfile of the agent.
	 */
	protected static final int startAgent(String jarfile)
	{
		long vmp = NativeHelper.getVm();
		return Agent_OnAttach(new Pointer(vmp), jarfile, Pointer.NULL);
	}
	
	/**
	 *  Linked native instrument method.
	 */
	private static native int Agent_OnAttach(Pointer vm, String args, Pointer reserved);
}
