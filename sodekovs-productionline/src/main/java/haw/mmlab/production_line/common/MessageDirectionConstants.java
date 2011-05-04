package haw.mmlab.production_line.common;

/**
 * Constants determining the direction messages are sent.
 * 
 * @author peter
 */
public interface MessageDirectionConstants {
	/**
	 * Send messages forward through the outputs.
	 */
	public static final String FORWARD = "FORWARD";

	/**
	 * Send messages backward through the inputs.
	 */
	public static final String BACKWARD = "BACKWARD";

	/**
	 * Send messages in both directions (inputs and outputs).
	 */
	public static final String BOTH = "BOTH";
}