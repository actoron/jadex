package jadex.service;

/**
 *  Interface for deciding if a specific target service provider should be searched
 *  in a given search context.
 */
public interface IVisitDecider
{
	/**
	 *  Test if a specific node should be searched.
	 *  @param source The source data provider.
	 *  @param target The target data provider.
	 *  @param up A flag indicating the search direction.
	 */
	public boolean searchNode(IServiceProvider source, IServiceProvider target, boolean up);
}
