package jadex.commons.collection;

import java.util.Map;

/**
 * The entry cleaner (for performing cleanup code on removed entries in a LRU).
 */
public interface ILRUEntryCleaner<V, K>
{
	 /**
	  *  Cleanup the eldest entry. 
	  *  @param eldest The eldest map entry.
	  */
	 public void cleanupEldestEntry(Map.Entry<V, K> eldest);
}
