package jadex.adapter.base.envsupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class MEnvTheme
{
	//-------- attributes --------

	/** The name. */
	protected String name;
	
	/** The drawables. */
	protected List drawables;
	
	/** The prelayers. */
	// todo?
//	protected List prelayers;
	
	//-------- methods --------

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
	
	/**
	 *  Add a drawable.
	 *  @param theme The drawable.
	 */
	public void addMEnvDrawable(MEnvDrawable drawable)
	{
		if(drawables==null)
			drawables = new ArrayList();
		drawables.add(drawable);	
	}
	
	/**
	 *  Get the drawables.
	 *  @return The drawables.
	 */
	public List getMEnvDrawables()
	{
		return drawables;
	}
}
