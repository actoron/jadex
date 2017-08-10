package jadex.bridge.service.search;

import java.util.Set;

/**
 *  Interface denoting key extraction functionality for indexers.
 */
public interface IKeyExtractor<T>
{
	/**
	 *  Extracts keys from a service.
	 *  
	 *  @param keytype The type of key being extracted.
	 *  @param value The value.
	 *  @return The keys matching the type.
	 */
	public Set<String> getKeys(String keytype, T value);
	
	/**
	 *  Get the key names for this type of extractor.
	 *  @return The key names.
	 */
	public String[] getKeyNames();
}
