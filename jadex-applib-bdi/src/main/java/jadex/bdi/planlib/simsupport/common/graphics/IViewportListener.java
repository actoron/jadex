package jadex.bdi.planlib.simsupport.common.graphics;

import jadex.bdi.planlib.simsupport.common.math.IVector2;

/** Listener for viewport user events
 */
public interface IViewportListener
{
	/** This method gets called on left clicks.
	 * 
	 *  @param position the absolute position in space that was clicked.
	 */
	public void leftClicked(IVector2 position);
	
	/** This method gets called on right clicks.
	 * 
	 *  @param position the absolute position in space that was clicked.
	 */
	public void rightClicked(IVector2 position);
}
