/**
 * 
 */
package haw.mmlab.production_line.state;

/**
 * Interface with the constants for the DeficientState
 * 
 * @author thomas
 */
public interface DeficientState {

	/**
	 * The agent is deficient by break.
	 */
	public static final int DEFICIENT_BY_BREAK = 1;

	/**
	 * The agent is deficient by change.
	 */
	public static final int DEFICIENT_BY_CHANGE = 2;

	/**
	 * The agent is not deficient.
	 */
	public static final int NOT_DEFICIENT = 0;
}