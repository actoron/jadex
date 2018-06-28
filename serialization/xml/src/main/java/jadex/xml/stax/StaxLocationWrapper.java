package jadex.xml.stax;

/**
 * Wraps a javax.xml.stream.Location Object to provide the jadex.xml.stax.ILocation API.
 */
public class StaxLocationWrapper implements ILocation
{
	// -------- attributes --------
	
	/** Holds the wrapped Location */
	private javax.xml.stream.Location location;

	// -------- constructors --------
	
	/**
	 * Constructor.
	 * @param loc The location to be wrapped.
	 */
	public StaxLocationWrapper(javax.xml.stream.Location loc)
	{
		this.location = loc;
	}

	// -------- methods --------

	/**
	 * Return the line number where the current event ends, returns -1 if none
	 * is available.
	 * 
	 * @return the current line number
	 */
	public int getLineNumber()
	{
		return location.getLineNumber();
	}

	/**
	 * Return the column number where the current event ends, returns -1 if none
	 * is available.
	 * 
	 * @return the current column number
	 */
	public int getColumnNumber()
	{
		return location.getColumnNumber();
	}

	/**
	 * Return the byte or character offset into the input source this location
	 * is pointing to. If the input source is a file or a byte stream then this
	 * is the byte offset into that stream, but if the input source is a
	 * character media then the offset is the character offset. Returns -1 if
	 * there is no offset available.
	 * 
	 * @return the current offset
	 */
	public int getCharacterOffset()
	{
		return location.getCharacterOffset();
	}

	/**
	 * Returns the public ID of the XML
	 * 
	 * @return the public ID, or null if not available
	 */
	public String getPublicId()
	{
		return location.getPublicId();
	}

	/**
	 * Returns the system ID of the XML
	 * 
	 * @return the system ID, or null if not available
	 */
	public String getSystemId()
	{
		return location.getSystemId();
	}

	/**
	 * Static method to wrap a Location object
	 * @param loc The location object to be wrapped
	 * @return the wrapped Location
	 */
	public static ILocation fromLocation(javax.xml.stream.Location loc)
	{
		return new StaxLocationWrapper(loc);
	}

}
