package jadex.application.model;

import java.util.ArrayList;
import java.util.List;

/**
 *  Application instance representation.
 */
public class MApplicationInstance
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The list of spaces. */
	protected List spaces;
	
	/** The list of contained components. */
	protected List components;
	
	//-------- constructors --------
	
	/**
	 *  Create a new application.
	 */
	public MApplicationInstance()
	{
		this.spaces = new ArrayList();
		this.components = new ArrayList();
	}
	
	//-------- methods --------
	
	/**
	 *  Add an space.
	 *  @param space The space.
	 */
	public void addMSpaceInstance(MSpaceInstance space)
	{
		this.spaces.add(space);
	}
	
	/**
	 *  Get all spaces.
	 *  @return The spaces.
	 */
	public List getMSpaceInstances()
	{
		return spaces;
	}
	
	/**
	 *  Add an component.
	 *  @param component The component.
	 */
	public void addMComponentInstance(MComponentInstance component)
	{
		this.components.add(component);
	}
	
	/**
	 *  Get all components.
	 *  @return The components.
	 */
	public List getMComponentInstances()
	{
		return components;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}
	
}
