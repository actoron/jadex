package haw.mmlab.production_line.state;

/**
 * Interface with the constants for the MainState.
 * 
 * @author thomas
 */
public interface MainState {

	/**
	 * The agent is running but has nothing to do.
	 */
	public static final int RUNNING_IDLE = 2;

	/**
	 * The agent is running.
	 */
	public static final int RUNNING = 1;

	/**
	 * The agent is waiting for reconfiguration.
	 */
	public static final int WAITING_FOR_RECONF = 0;
}