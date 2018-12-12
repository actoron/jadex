package jadex.bridge.service.component;

/**
 *  Interface for determining if the method invocation interceptor should
 *  do a switch call, i.e. take the next call and make it to the current.
 *  This is the normal behavior when a service object is invoked but there
 *  are at least two exception when it should not be done:
 *  - when a remote method is called (switch is done the at the remote side)
 *  - when a transition from required to provided side is done (switch is done at provided side)
 */
public interface ISwitchCall
{
	/**
	 *  Check if a switch call should be done.
	 *  @return True, if switch should be done.
	 */
	public boolean isSwitchCall();
}
