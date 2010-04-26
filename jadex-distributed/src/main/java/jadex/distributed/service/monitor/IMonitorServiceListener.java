package jadex.distributed.service.monitor;

public interface IMonitorServiceListener {
	
	/**
	 * Notify a registered listener of type IMonitorServiceListener that
	 * current state of the platforms changed and the PlatformInfo objects
	 * reflect those new changes.
	 */
	public void notifyIMonitorListener();
	
	/**
	 * Notify a registered listener of type IMonitorServoceListner that a new slave
	 * platform is available. The current state of the new slave is represented with
	 * the passed PlatformInfo.
	 * @param addr
	 */
	public void notifyIMonitorListenerAdd(PlatformInfo platformInfo);
	
	/**
	 * Notify a registered listener of type IMonitorServiceListner that a old slave
	 * platform is not available anymore; the slave platform leaved the group of
	 * platforms. The reference of the PlatformInfo representing the current state
	 * of the slave platform is passed, so the IMonitorServiceListener can
	 * perform any necessary steps.
	 * @param addr
	 */
	public void notifyIMonitorListenerRemove(PlatformInfo platformInfo);
}