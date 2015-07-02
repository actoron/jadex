package jadex.bridge.service.types.message;

import java.util.Date;

/**
 *  Encoding context for codecs.
 *
 */
public class EncodingContext implements IEncodingContext
{
	/** Release date of the target platform. */
	protected Date releasedate;
	
	/**
	 *  Creates a new encoding context.
	 * 
	 *  @param releasedatestring Release date of the target platform as milliseconds string.
	 */
	public EncodingContext(Date releasedate)
	{
		this.releasedate = releasedate;
	}
	
	/** 
	 *  Returns the release date of the target of the encoded item.
	 * 
	 *  @return The release date of the target of the encoded item.
	 */
	public Date getTargetReleaseDate()
	{
		return releasedate;
	}
}
