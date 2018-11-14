package jadex.extension.envsupport.observer.graphics;


import java.awt.Canvas;
import java.awt.Color;
import java.util.List;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.perspective.IPerspective;


/**
 * This class manages the GUI and all user interaction.
 */
public interface IViewport
{
	
	/**
	 * 
	 * @return the spacecontroller
	 */
	public ISpaceController getSpaceController();
	
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
	 * Refreshes the size of the canvas.
	 */
	public void refreshCanvasSize();

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
	public void setPreLayers(Layer[] layer);

	/**
	 * Sets the layers that are applied after the drawables are drawn.
	 * 
	 * @param layers new layers
	 */
	public void setPostLayers(Layer[] layer);

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
	 * Converts pixel coordinates into world coordinates
	 * 
	 * @param pixelX pixel x-coordinate
	 * @param pixelY pixel y-coordinate
	 * @return world coordinates
	 */
	public IVector2 getWorldCoordinates(int pixelX, int pixelY);

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
	
	/**
	 *  Get the perspective.
	 *  @return The perspective.
	 */
	public IPerspective getPerspective();
	
	/**
	 *  Draws a primitive
	 *  @param dc The combiner.
	 *  @param primitive The primitive.
	 *  @param obj The object being drawn.
	 */
	public void drawPrimitive(DrawableCombiner dc, Primitive primitive, Object obj);
	
	/**
	 *  Disposes the Viewport.
	 */
	public void dispose();
}
