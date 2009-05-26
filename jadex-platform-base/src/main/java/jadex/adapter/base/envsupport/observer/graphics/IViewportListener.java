package jadex.adapter.base.envsupport.observer.graphics;

import jadex.adapter.base.envsupport.math.IVector2;



/**
 * Listener for viewport user events
 */
public interface IViewportListener
{
	/**
	 * This method gets called on left clicks.
	 * 
	 * @param position the absolute position in space that was clicked.
	 */
	public void leftClicked(IVector2 position);
}
