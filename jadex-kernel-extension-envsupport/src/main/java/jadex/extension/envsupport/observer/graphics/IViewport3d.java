package jadex.extension.envsupport.observer.graphics;

import java.awt.Canvas;
import java.util.Collection;
import java.util.List;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.perspective.IPerspective;

public interface IViewport3d
{
	
	/**
	 * 
	 * @return the spacecontroller
	 */
	public ISpaceController getSpaceController();
	
	
	/**
	 * Set the Selected Objects in the Viewport by the User
	 */
	public void setSelected(int selected, DrawableCombiner3d marker);
	
	/**
	 * Get the Selected Object by the user
	 */
	public int getSelected();
	
	/**
	 * get the Marker. A visual Object that visuals the selection
	 */
	public DrawableCombiner3d getMarker();
	
	/**
	 * Returns the canvas that is used for displaying the objects.
	 */
	public Canvas getCanvas();
	
//	/**
//	 * Sets the current objects to draw.
//	 * 
//	 * @param objectList objects that should be drawn
//	 */
//	public void setObjectList(List<Object[]>  objectList);
	
	
	/**
	 * Refreshes the viewport.
	 */
	public void refresh(List<Object[]>  objectList, Collection<DrawableCombiner3d> staticvisuals);
	
	
	/**
	 *  Get the perspective.
	 *  @return The perspective.
	 */
	public IPerspective getPerspective();
	
	/**
	 *  Starts the internal 3d Application
	 * 
	 */
	public void startApp();
	
	/**
	 *  Pause the internal 3d Application
	 * 
	 */
	public void pauseApp();
	
	/**
	 *  Stops the internal 3d Application
	 * 
	 */
	public void stopApp();
	
	/**
	 * Gets the maximum displayable size.
	 * 
	 * @return maximum area size.
	 */
	public IVector3 getAreaSize();
	
	/**
	 * Sets the maximum displayable size.
	 * 
	 * @param areaSize maximum area size.
	 */
	public void setAreaSize(IVector3 vector);

//	/** 
//	 * Visuals that are handled as statics
//	 * 
//	 * @param staticvisuals the Visuals
//	 */
//	public void setStaticList(Collection<DrawableCombiner3d> staticvisuals);

	public void isGridSpace(boolean isGrid);
	

}
