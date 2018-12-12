package jadex.commons.gui;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JSplitPane;


/**
 *  Workaround for JSplitPane bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4101306
 *  
 *  setDividerLocation() can only be called after component is visible.
 *  Provides means for storing/fetching proportional divider location.
 */
public class JSplitPanel extends JSplitPane
{
	/**
	 * Creates a new <code>JSplitPane</code> configured to arrange the child
	 * components side-by-side horizontally with no continuous layout, using two
	 * buttons for the components.
	 */
	public JSplitPanel()
	{
	}


	/**
	 * Creates a new <code>JSplitPane</code> configured with the specified
	 * orientation and no continuous layout.
	 * 
	 * @param newOrientation <code>JSplitPane.HORIZONTAL_SPLIT</code> or
	 *        <code>JSplitPane.VERTICAL_SPLIT</code>
	 * @exception IllegalArgumentException if <code>orientation</code> is not
	 *            one of HORIZONTAL_SPLIT or VERTICAL_SPLIT.
	 */
	public JSplitPanel(int newOrientation)
	{
		super(newOrientation);
	}


	/**
	 * Creates a new <code>JSplitPane</code> with the specified orientation and
	 * redrawing style.
	 * 
	 * @param newOrientation <code>JSplitPane.HORIZONTAL_SPLIT</code> or
	 *        <code>JSplitPane.VERTICAL_SPLIT</code>
	 * @param newContinuousLayout a boolean, true for the components to redraw
	 *        continuously as the divider changes position, false to wait until
	 *        the divider position stops changing to redraw
	 * @exception IllegalArgumentException if <code>orientation</code> is not
	 *            one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
	 */
	public JSplitPanel(int newOrientation, boolean newContinuousLayout)
	{
		super(newOrientation, newContinuousLayout);
	}


	/**
	 * Creates a new <code>JSplitPane</code> with the specified orientation and
	 * with the specified components that do not do continuous redrawing.
	 * 
	 * @param newOrientation <code>JSplitPane.HORIZONTAL_SPLIT</code> or
	 *        <code>JSplitPane.VERTICAL_SPLIT</code>
	 * @param newLeftComponent the <code>Component</code> that will appear on
	 *        the left of a horizontally-split pane, or at the top of a
	 *        vertically-split pane
	 * @param newRightComponent the <code>Component</code> that will appear on
	 *        the right of a horizontally-split pane, or at the bottom of a
	 *        vertically-split pane
	 * @exception IllegalArgumentException if <code>orientation</code> is not
	 *            one of: HORIZONTAL_SPLIT or VERTICAL_SPLIT
	 */
	public JSplitPanel(int newOrientation, Component newLeftComponent,
			Component newRightComponent)
	{
		super(newOrientation, newLeftComponent, newRightComponent);
	}


	/**
	 * Creates a new <code>JSplitPane</code> with the specified orientation and
	 * redrawing style, and with the specified components.
	 * 
	 * @param newOrientation <code>JSplitPane.HORIZONTAL_SPLIT</code> or
	 *        <code>JSplitPane.VERTICAL_SPLIT</code>
	 * @param newContinuousLayout a boolean, true for the components to redraw
	 *        continuously as the divider changes position, false to wait until
	 *        the divider position stops changing to redraw
	 * @param newLeftComponent the <code>Component</code> that will appear on
	 *        the left of a horizontally-split pane, or at the top of a
	 *        vertically-split pane
	 * @param newRightComponent the <code>Component</code> that will appear on
	 *        the right of a horizontally-split pane, or at the bottom of a
	 *        vertically-split pane
	 * @exception IllegalArgumentException if <code>orientation</code> is not
	 *            one of HORIZONTAL_SPLIT or VERTICAL_SPLIT
	 */
	public JSplitPanel(int newOrientation, boolean newContinuousLayout,
			Component newLeftComponent, Component newRightComponent)
	{
		super(newOrientation, newContinuousLayout, newLeftComponent,
				newRightComponent);
	}

	// Mega HACK!!! Does not work if just set once :-(((
	int	isPainted = 3;

	boolean	hasProportionalLocation	= false;
	double	proportionalLocation;
	
//	boolean	hasLocation	= false;
//	int	location;

//	public void setDividerLocation(int location)
//	{
//		if(!isPainted)
//		{
//			hasLocation = true;
//			this.location = location;
//		}
//		else
//		{
//			super.setDividerLocation(location);
//		}
//	}
	
	public void setDividerLocation(double proportionalLocation)
	{
		if(proportionalLocation<0 || proportionalLocation>1)
			return;
		
		if(isPainted>0)
		{
			hasProportionalLocation = true;
			this.proportionalLocation = proportionalLocation;
		}
		else
		{
			super.setDividerLocation(proportionalLocation);
		}
	}

	public void paint(Graphics g)
	{
		if(isPainted>0 && isValid() && isVisible())
		{
//			if(hasLocation)
//			{
//				System.out.println("loc: "+location);
//				super.setDividerLocation(location);
//			}
//			else if(hasProportionalLocation)
			{
//				System.out.println("proploc: "+proportionalLocation);
				super.setDividerLocation(proportionalLocation);
			}
			isPainted--;
		}
		super.paint(g);
	}
	
	/**
	 *  Get the proportional split location.
	 */
	public double getProportionalDividerLocation()
	{
		double full = getOrientation() == JSplitPane.HORIZONTAL_SPLIT? getWidth()-getDividerSize(): getHeight()-getDividerSize();
		double ret = full<=0? 0: ((double)getDividerLocation())/full;
		return ret;
	}
}
