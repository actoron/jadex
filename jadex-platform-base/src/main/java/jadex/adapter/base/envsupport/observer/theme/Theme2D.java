package jadex.adapter.base.envsupport.observer.theme;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.graphics.layer.ILayer;

/**
 * Theme used by the observer to visualize a view.
 */
public class Theme2D
{
	/** DrawableCombiners of the theme */
	protected Map drawcombiners;
	
	/** The prelayers */
	protected ILayer[] prelayers;
	
	/** The postlayers */
	protected ILayer[] postlayers;
	
	/** The marker drawable combiner */
	protected DrawableCombiner marker;
	
	public Theme2D()
	{
		drawcombiners = new HashMap();
		prelayers = new ILayer[0];
		postlayers = new ILayer[0];
		marker = null;
	}
	
	/**
	 * Returns a specific drawable combiner object
	 * @param id identifier of the drawable combiner object
	 * @return the drawable combiner object
	 */
	public DrawableCombiner getDrawableCombiner(Object id)
	{
		return (DrawableCombiner) drawcombiners.get(id);
	}
	
	/**
	 * Adds a new drawable combiner object.
	 * @param id identifier of the object
	 * @param drawable combiner the drawable combiner
	 */
	public void addDrawableCombiner(Object id, Object drawable)
	{
		drawcombiners.put(id, drawable);
	}
	
	/**
	 * Removes a new drawable combiner object.
	 * @param id identifier of the object
	 */
	public void removeDrawableCombiner(Object id)
	{
		drawcombiners.remove(id);
	}
	
	/**
	 * Returns the prelayers.
	 * @return the prelayers
	 */
	public ILayer[] getPrelayers()
	{
		return prelayers;
	}
	
	/**
	 * Sets the prelayers.
	 * @param prelayers the prelayers
	 */
	public void setPrelayers(ILayer[] prelayers)
	{
		this.prelayers = prelayers;
	}
	
	/**
	 * Returns the Postlayers.
	 * @return the Postlayers
	 */
	public ILayer[] getPostlayers()
	{
		return postlayers;
	}
	
	/**
	 * Sets the Postlayers.
	 * @param Postlayers the Postlayers
	 */
	public void setPostlayers(ILayer[] postlayers)
	{
		this.postlayers = postlayers;
	}
	
	/**
	 * Gets the drawable combiner object for the object marker
	 * @return the marker drawable
	 */
	public DrawableCombiner getMarkerDrawCombiner()
	{
		return marker;
	}
	
	/**
	 * Sets the drawable combiner object for the object marker
	 * @param marker the marker drawable
	 */
	public void setMarkerDrawCombiner(DrawableCombiner marker)
	{
		this.marker = marker;
	}
}
