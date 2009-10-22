package jadex.bridge;


/**
 *  This model interface to be used (invoked) by tools and adapters.
 *  Can represent an application or an agent (also capability).
 *  Applications can be loaded by the application factory.
 *  @link{IApplicationFactory}
 *  Agent types can be loaded by the kernel's agent factory
 *  @link{IAgentFactory}. 
 */
public interface ILoadableElementModel
{
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName();
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription();
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IReport getReport();
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public String[] getConfigurations();
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments();
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable();
	
	/**
	 *  Get the model type.
	 *  @return The model type (kernel specific).
	 */
//	public String getType();
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename();
}
