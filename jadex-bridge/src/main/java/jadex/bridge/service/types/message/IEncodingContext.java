package jadex.bridge.service.types.message;

import java.util.Date;

/**
 *  A context containing additional information for codecs.
 *
 */
public interface IEncodingContext
{
	/** 
	 *  Returns the release date of the target of the encoded item.
	 * 
	 *  @return The release date of the target of the encoded item.
	 */
	public Date getTargetReleaseDate();
}
