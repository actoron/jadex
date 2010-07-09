package jadex.service;

import jadex.commons.IFuture;

/**
 * 
 */
public class SServiceProvider
{
	protected static ISearchManager treemanager = new SequentialSearchManager();
	protected static ISearchManager upwardsmanager = new SequentialSearchManager(true, false);
	protected static IVisitDecider abortdecider = new DefaultVisitDecider();
	protected static IVisitDecider contdecider = new DefaultVisitDecider(false);
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getService(IServiceProvider provider, Class type)
	{
		return provider.getServices(treemanager, abortdecider, new TypeResultSelector(type));
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public static IFuture getServices(IServiceProvider provider, Class type)
	{
		return provider.getServices(treemanager, contdecider, new TypeResultSelector(type, false));
	}

	/**
	 *  Get one service of a type and only search upwards (parents).
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public static IFuture getServiceUpwards(IServiceProvider provider, Class type)
	{
		return provider.getServices(upwardsmanager, abortdecider, new TypeResultSelector(type));
	}
}
