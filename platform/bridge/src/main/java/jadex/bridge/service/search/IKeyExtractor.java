package jadex.bridge.service.search;

import java.util.Set;

/**
 *  Interface denoting key extraction functionality for indexers.
 */
public interface IKeyExtractor<T>
{
	/**
	 *  Extracts key value from an object.
	 *  
	 *  @param keytype The type of key being extracted.
	 *  @param value The value.
	 *  @return The keys matching the type.
	 */
	public Set<String> getKeyValues(String keytype, T value);
	
	/**
	 *  Extracts the matching mode from a multivalued term.
	 *  true = AND, false = OR
	 *  
	 *  @param keytype The type of key being extracted.
	 *  @param value The value.
	 *  @return The key matching mode.
	 */
	public Boolean getKeyMatchingMode(String keytype, T value);
	
	/**
	 *  Get the key names for this type of extractor.
	 *  @return The key names.
	 */
	public String[] getKeyNames();
}
