package deco4mas.distributed.mechanism;

public interface ICoordinationMechanism {

	/**
	 * start the coordinationMechanism
	 */
	public void start();

	/**
	 * stop the coordinationMechanism
	 */
	public void stop();

	/**
	 * Used to consume and pass Coordination Events, that are produced by the Agent State Interpreter. These events are processed by the coordination medium which may cause the publication of
	 * percepts.
	 * 
	 * @param obj
	 *            the object
	 */
	public void perceiveCoordinationEvent(Object obj);
}
