package jadex.platform.service.servicepool;

/**
 *  Struct for specifying how the handler should 
 *  work with the component instances.
 */
public class ServicePoolStrategy
{
	//-------- attributes --------
	
	/** The component type that should be used for realizing the service. */
	protected String componentmodel;
	
	/** The maximum number of components. */
	protected int max;

	//-------- constructors --------
	
	/**
	 *  Create a new ServicePoolStrategy. 
	 */
	public ServicePoolStrategy(String componentmodel, int max)
	{
		this.componentmodel = componentmodel;
		this.max = max;
	}

	//-------- methods --------
	
	/**
	 *  Get the component model.
	 *  @return The componentmodel.
	 */
	public String getComponentModel()
	{
		return componentmodel;
	}

	/**
	 *  Set the componentmodel.
	 *  @param componentmodel The componentmodel to set.
	 */
	public void setComponentModel(String componentmodel)
	{
		this.componentmodel = componentmodel;
	}

	/**
	 *  Get the max.
	 *  @return The max.
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 *  Set the max.
	 *  @param max The max to set.
	 */
	public void setMax(int max)
	{
		this.max = max;
	}
}
