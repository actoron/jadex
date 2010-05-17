package jadex.distributed.service.monitor;

public interface IMonitorServiceListener {
	
	/**
	 * Notify a registered listener of type IMonitorServiceListener that
	 * current state of the platforms changed and the PlatformInfo objects
	 * reflect those new changes.
	 */
	public void notifyIMonitorListenerChange();
	
	/**
	 * Notify a registered listener of type IMonitorServiceListener that
	 * current state of a single platform changed and the PlatformInfo object
	 * representing this change reflects those new changes.
	 * @param platformInfo object representing the slave platform which state just changed
	 */
	public void notifyIMonitorListenerChange(PlatformInfo platformInfo);
	
	/**
	 * Notify a registered listener of type IMonitorServoceListner that a new slave
	 * platform is available. The current state of the new slave is represented with
	 * the passed PlatformInfo.
	 * @param platformInfo the representing the current state of the new slave platform
	 */
	public void notifyIMonitorListenerAdd(PlatformInfo platformInfo);
	
	/**
	 * Notify a registered listener of type IMonitorServoceListner that new slave
	 * platforms are available. The current states of the new slaves are
	 * represented with the passed PlatformInfo objects.
	 * @param platformInfo an array of PlatformInfo objects
	 */
	public void notifyIMonitorListenerAdd(PlatformInfo[] platformInfo);
	
	/**
	 * Notify a registered listener of type IMonitorServiceListner that a old slave
	 * platform is not available anymore; the slave platform leaved the group of
	 * platforms. The reference of the PlatformInfo representing the current state
	 * of the slave platform is passed, so the IMonitorServiceListener can
	 * perform any necessary steps.
	 * @param platformInfo object which represented the state of the removed slave platform
	 */
	public void notifyIMonitorListenerRemove(PlatformInfo platformInfo);
}