package jadex.base;

/**
 *  Interface for platform configuration.
 */
public interface IPlatformConfiguration extends IStarterConfiguration, IRootComponentConfiguration
{
	/**
	 *  Returns the configuration of the root component.
	 *  @return RootComponentConfiguration
	 */
	public IRootComponentConfiguration getRootConfig();

	/**
	 *  Returns the configuration of the root component.
	 *  @return RootComponentConfiguration
	 */
	public IStarterConfiguration getStarterConfig();
	
	/**
	 *  Check the consistency.
	 */
	public void checkConsistency();
	
	/**
	 *  Enhance this config with given other config. Will overwrite all values
	 *  that are set in the other config.
	 */
	public void enhanceWith(IPlatformConfiguration other);
}
