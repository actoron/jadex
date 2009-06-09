package jadex.adapter.base.envsupport.observer.graphics;


import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.graphics.layer.ILayer;

import java.awt.Canvas;
import java.awt.Color;
import java.util.List;


/**
 * This class manages the GUI and all user interaction.
 */
public interface IViewport
{
	/**
	 * Gets the position of the viewport.
	 */
	public IVector2 getPosition();
	
	/**
	 * Sets the position of the bottom left corner of the viewport.
	 */
	public void setPosition(IVector2 pos);
	
	/**
	 * Sets the background color.
	 * @param bgColor the background color
	 */
	public void setBackground(Color bgColor);
	
	/**
	 * Gets the maximum displayable size.
	 * 
	 * @return maximum area size.
	 */
	public IVector2 getAreaSize();
	
	/**
	 * Gets the size of the display area.
	 * 
	 * @return size of the display area, may be padded to preserve aspect
	 *        ratio
	 */
	public IVector2 getSize();
	
	/**
	 * Sets the viewport size.
	 * 
	 * @param size new viewport size
	 */
	public void setSize(IVector2 size);
	
	/**
	 * Sets the maximum displayable size.
	 * 
	 * @param areaSize maximum area size.
	 */
	public void setAreaSize(IVector2 areaSize);

	/**
	 * Sets whether the viewport should preserve the aspect ratio of the size by
	 * padding.
	 * 
	 * @param preserveAR true to preserve aspect ratio, false otherwise
	 */
	public void setPreserveAspectRation(boolean preserveAR);

	/**
	 * Sets the shift of all objects.
	 */
	public void setObjectShift(IVector2 objectShift);

	/**
	 * Returns true if the x-axis is inverted (right-left instead of
	 * left-right).
	 * 
	 * @return true, if the x-axis is inverted
	 */
	public boolean getInvertX();

	/**
	 * Returns true if the y-axis is inverted (top-down instead of bottom-up).
	 * 
	 * @return true, if the y-axis is inverted
	 */
	public boolean getInvertY();

	/**
	 * If set to true, inverts the x-axis (right-left instead of left-right).
	 * 
	 * @param b if true, inverts the x-axis
	 */
	public void setInvertX(boolean b);

	/**
	 * If set to true, inverts the y-axis (top-down instead of bottom-up).
	 * 
	 * @param b if true, inverts the y-axis
	 */
	public void setInvertY(boolean b);

	/**
	 * Sets the layers that are applied before the drawables are drawn.
	 * 
	 * @param layers new layers
	 */
	public void setPreLayers(ILayer[] layer);

	/**
	 * Sets the layers that are applied after the drawables are drawn.
	 * 
	 * @param layers new layers
	 */
	public void setPostLayers(ILayer[] layer);

	/**
	 * Sets the current objects to draw.
	 * 
	 * @param objectList objects that should be drawn
	 */
	public void setObjectList(List objectList);
	
	/**
	 * Sets the maximum zoom.
	 * @param zoomlimit the zoom limit
	 */
	public void setZoomLimit(double zoomlimit);

	/**
	 * Returns the canvas that is used for displaying the objects.
	 */
	public Canvas getCanvas();

	/**
	 * Refreshes the viewport.
	 */
	public void refresh();

	/**
	 * Checks if this IViewport is showing on screen.
	 * 
	 * @return true if the IViewport is showing, false otherwise
	 */
	public boolean isShowing();

	/**
	 * Adds a IViewportListener
	 * 
	 * @param listener new listener
	 */
	public void addViewportListener(IViewportListener listener);

	/**
	 * Removes a IViewportListener
	 * 
	 * @param listener the listener
	 */
	public void removeViewportListener(IViewportListener listener);
}
