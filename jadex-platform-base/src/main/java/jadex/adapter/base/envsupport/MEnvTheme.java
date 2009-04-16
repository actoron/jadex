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
	protected List prelayers;

	/** The postlayers. */
	protected List postlayers;
	
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
	
	/**
	 *  Add a prelayer.
	 *  @param theme The prelayer.
	 */
	public void addPreLayer(Object prelayer)
	{
		if(prelayers==null)
			prelayers = new ArrayList();
		prelayers.add(prelayer);	
	}
	
	/**
	 *  Get the prelayers.
	 *  @return The prelayers.
	 */
	public List getPreLayers()
	{
		return prelayers;
	}
	
	/**
	 *  Add a postlayer.
	 *  @param theme The postlayer.
	 */
	public void addPostLayer(Object postlayer)
	{
		if(postlayers==null)
			postlayers = new ArrayList();
		postlayers.add(postlayer);	
	}
	
	/**
	 *  Get the postlayers.
	 *  @return The postlayers.
	 */
	public List getPostLayers()
	{
		return postlayers;
	}
}
