package jadex.xml.stax;

/**
 * StaX API: Location
 */
public class Location implements ILocation
{
	
	// -------- attributes --------
	
	private String systemId;
	private String publicId;
	private int charOffset;
	private int column;
	private int line;
	
	// -------- constructors --------
	
	public Location(int line, int column, int charOffset, String publicId, String systemId)
	{
		this.line = line;
		this.column = column;
		this.charOffset = charOffset;
		this.publicId = publicId;
		this.systemId = systemId;
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
		return line;
	}

	/**
	 * Return the column number where the current event ends, returns -1 if none
	 * is available.
	 * 
	 * @return the current column number
	 */
	public int getColumnNumber()
	{
		return column;
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
		return charOffset;
	}

	/**
	 * Returns the public ID of the XML
	 * 
	 * @return the public ID, or null if not available
	 */
	public String getPublicId()
	{
		return publicId;
	}

	/**
	 * Returns the system ID of the XML
	 * 
	 * @return the system ID, or null if not available
	 */
	public String getSystemId()
	{
		return systemId;
	}

}
