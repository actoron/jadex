package jadex.bridge.service.search;

import java.util.Set;

/**
 *  Interface denoting key extraction functionality for indexers.
 */
public interface IKeyExtractor
{
	/**
	 *  Extracts keys from a service.
	 *  
	 *  @param keytype The type of key being extracted.
	 *  @param service The service.
	 *  @return The keys matching the type.
	 */
	public Set<String> getKeys(String keytype, Object service);
}
