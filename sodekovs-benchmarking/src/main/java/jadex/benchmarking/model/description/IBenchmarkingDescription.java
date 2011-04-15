package jadex.benchmarking.model.description;

import jadex.bridge.IComponentIdentifier;

/**
 *  Interface for benchmark service descriptions.
 */
public interface IBenchmarkingDescription
{
	/**
	 *  Get the name of this benchmark.
	 *  @return name The name.
	 */
	public String getName();
	
	/**
	 *  Get the type of this benchmark.
	 *  @return type The type.
	 */
	public String getType();
	
	/**
	 *  Get the status of this benchmark.
	 *  @return status The status
	 */
	public String getStatus();
	
	/**
	 *  Get the component id of this benchmark.
	 *  @return id The id.
	 */
	public IComponentIdentifier getSuTIdentifiertType();
}
