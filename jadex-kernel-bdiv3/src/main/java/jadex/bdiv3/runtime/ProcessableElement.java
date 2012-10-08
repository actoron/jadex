package jadex.bdiv3.runtime;

/**
 * 
 */
public class ProcessableElement
{
	/** Never exclude plan candidates from apl. */
	public static final String EXCLUDE_NEVER = "never";

	/** Exclude tried plan candidates from apl. */ 
	public static final String EXCLUDE_WHEN_TRIED = "when_tried";
	
	/** Exclude failed plan candidates from apl. */
	public static final String EXCLUDE_WHEN_FAILED = "when_failed";

	/** Exclude succeeded plan candidates from apl. */
	public static final String EXCLUDE_WHEN_SUCCEEDED = "when_succeeded";

	/** The exclude mode. */
	protected String excludemode;
	
	
}
