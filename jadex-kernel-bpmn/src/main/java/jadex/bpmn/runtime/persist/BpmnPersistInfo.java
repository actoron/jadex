package jadex.bpmn.runtime.persist;

import jadex.bridge.IInternalAccess;
import jadex.kernelbase.DefaultPersistInfo;

/**
 *  Class containing persistence information about
 *  a Bpmn process instance.
 */
public class BpmnPersistInfo extends DefaultPersistInfo
{
	//-------- attribute --------
	
	/** The context variables. */
	protected ThreadInfo topthread;
	
	//-------- constructors --------

	/**
	 *  Create an empty persist info.
	 */
	public BpmnPersistInfo()
	{
		// bean constructors.
	}
	
	/**
	 *  Create a new BpmnPersistInfo.
	 */
	public BpmnPersistInfo(IInternalAccess interpreter)
	{
		super(interpreter);
//		topthread = new ThreadInfo(interpreter.getTopLevelThread());
	}
	
	//-------- methods --------
	
//	/**
//	 *  Get the top thread.
//	 */
//	public ThreadInfo	getTopLevelThread()
//	{
//		return topthread;
//	}
//	
//	/**
//	 *  Set the top thread.
//	 */
//	public void	setTopLevelThread(ThreadInfo topthread)
//	{
//		this.topthread	= topthread;
//	}
}
