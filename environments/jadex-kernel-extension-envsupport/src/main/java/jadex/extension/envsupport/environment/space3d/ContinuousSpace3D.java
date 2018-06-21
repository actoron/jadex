package jadex.extension.envsupport.environment.space3d;

import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector3;

/**
 *  Continuous version of 2D space.
 */
public class ContinuousSpace3D extends Space3D
{
	//-------- constants --------
	
	/** The default ID for this space */
	public static final String DEFAULT_NAME = ContinuousSpace3D.class.getName();
	
	//-------- constructors --------
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 */
	public ContinuousSpace3D()
	{
		this(null);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with the default name.
	 * @param spaceexecutor executor for the space
	 * @param actionexecutor executor for component actions
	 * @param areasize the size of the 3D area
	 */
	public ContinuousSpace3D(IVector3 areasize)
	{
		this(DEFAULT_NAME, areasize);
	}
	
	/**
	 * Creates a new {@link ContinuousSpace2D} with a special ID.
	 * @param name the name of this space
	 * @param areasize the size of the 2D area
	 * @param actionexecutor executor for component actions
	 */
	public ContinuousSpace3D(Object name, IVector3 areasize)
	{
		super(areasize);
		setProperty("name", name);
	}
}
