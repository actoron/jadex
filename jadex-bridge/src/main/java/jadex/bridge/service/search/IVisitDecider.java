package jadex.bridge.service.search;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceProvider;

import java.util.Collection;

/**
 *  Interface for deciding if a specific target service provider should be searched
 *  in a given search context.
 */
public interface IVisitDecider
{
	/**
	 *  Test if a specific node should be searched.
	 *  @param start The start provider.
	 *  @param source The source data provider.
	 *  @param target The target data provider.
	 *  @param results The collection of preliminary results.
	 */
	public boolean searchNode(IComponentIdentifier start, IComponentIdentifier source, IComponentIdentifier target, boolean ischild, Collection results);
	
	/**
	 *  Get the cache key.
	 *  Needs to identify this element with respect to its important features so that
	 *  two equal elements should return the same key.
	 */
	public Object getCacheKey();
}
