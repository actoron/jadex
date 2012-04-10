package jadex.android.xmlpullparser;

/**
 * Thrown if a Method is not (yet) implemented.
 */
public class MethodNotImplementedError extends Error {

	private static final long serialVersionUID = -8947124872505523486L;

	/**
	 * Constructs a new MethodNotImplementedError
	 * @param msg
	 */
	public MethodNotImplementedError(String msg) {
		super(msg);
	}
}
