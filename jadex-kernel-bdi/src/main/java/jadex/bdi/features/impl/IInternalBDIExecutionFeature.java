package jadex.bdi.features.impl;


/**
 *  System access to BDI features.
 */
public interface IInternalBDIExecutionFeature
{
	/**
	 *  Set the current plan thread.
	 *  @param planthread The planthread.
	 */ 
	public void setPlanThread(Thread planthread);
	
	/**
	 *  Check if the agent thread is accessing.
	 *  @return True, if access is ok.
	 */ 
	public boolean isPlanThread();
}
