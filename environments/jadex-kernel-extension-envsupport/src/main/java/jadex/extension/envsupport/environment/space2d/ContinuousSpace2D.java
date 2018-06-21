package jadex.extension.envsupport.environment.space2d;

import jadex.extension.envsupport.math.IVector2;

/**
 *  Continuous version of 2D space.
 */
public class ContinuousSpace2D extends Space2D
{
	//-------- constants --------
	
	/** The default ID for this space */
	public static final String DEFAULT_NAME = ContinuousSpace2D.class.getName();
	
	//-------- constructors --------
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 */
	public ContinuousSpace2D()
	{
		this(null);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 * @param spaceexecutor executor for the space
	 * @param actionexecutor executor for component actions
	 * @param areasize the size of the 2D area
	 */
	public ContinuousSpace2D(IVector2 areasize)
	{
		this(DEFAULT_NAME, areasize);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with a special ID.
	 * @param name the name of this space
	 * @param areasize the size of the 2D area
	 * @param actionexecutor executor for component actions
	 */
	public ContinuousSpace2D(Object name, IVector2 areasize)
	{
		super(areasize);
		setProperty("name", name);
	}
}
