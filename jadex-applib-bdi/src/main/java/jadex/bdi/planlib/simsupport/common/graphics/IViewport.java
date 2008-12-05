package jadex.bdi.planlib.simsupport.common.graphics;

import java.awt.Canvas;
import java.util.Comparator;
import java.util.List;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

/** This class manages the GUI and all user interaction.
 */
public interface IViewport
{
    /** Sets the position of the bottom left corner of
     *  the viewport.
     */
    public void setPosition(IVector2 pos);
    
    /** Sets the viewport size.
     *
     *  @param size new viewport size
     */
    public void setSize(IVector2 size);
    
    /** Sets whether the viewport should preserve the
     *  aspect ratio of the size by padding.
     *  
     *  @param preserveAR true to preserve aspect ratio, false otherwise 
     */
    public void setPreserveAspectRation(boolean preserveAR);
    
    /** Sets the layers that are applied before the drawables are drawn.
     *  
     *  @param layers new layers
     */
    public void setPreLayers(List layer);
    
    /** Sets the layers that are applied after the drawables are drawn.
     *  
     *  @param layers new layers
     */
    public void setPostLayers(List layer);
    
    /** Registers an IDrawable to be used in the object list.
     *  
     *  @param d the drawable
     */
    public void registerDrawable(IDrawable d);
    
    /** Sets the current objects to draw.
     * 
     *  @param objectList objects that should be drawn
     */
    public void setObjectList(List objectList);
    
    /** Returns the canvas that is used for displaying the objects.
     */
    public Canvas getCanvas();
    
    /** Refreshes the viewport.
     */
    public void refresh();
    
    /** Checks if this IViewport is showing on screen.
     *  
     *  @return true if the IViewport is showing, false otherwise
     */
    public boolean isShowing();
}
