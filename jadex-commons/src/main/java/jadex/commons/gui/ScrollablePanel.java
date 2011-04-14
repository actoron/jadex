package jadex.commons.gui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.border.Border;

/**
 *  A panel that can be used in scrollpanes.
 */
public class ScrollablePanel extends JPanel implements Scrollable
{
	//-------- attributes --------
	
	/** The preferred scrollable viewport size. */
	protected Dimension	preferredsize;
	
	/** True if the view should always be adjusted to scrollpane width (i.e. no horizontal scrolling). */
	protected boolean	trackwidth;
	
	/** True if the view should always be adjusted to scrollpane height (i.e. no vertical scrolling). */
	protected boolean	trackheight;
	
	//-------- constructors --------
	
	/**
	 *  Create a nee scrollable panel.
	 */
	public ScrollablePanel(Dimension preferredsize, boolean trackwidth, boolean trackheigth)
	{
		this.preferredsize	= preferredsize;
		this.trackwidth	= trackwidth;
		this.trackheight	= trackheigth;
	}

	//-------- Scrollable interface --------
	
    /**
     * Returns the preferred size of the viewport for a view component.
     * For example the preferredSize of a JList component is the size
     * required to accommodate all of the cells in its list however the
     * value of preferredScrollableViewportSize is the size required for
     * JList.getVisibleRowCount() rows.   A component without any properties
     * that would effect the viewport size should just return 
     * getPreferredSize() here.
     * 
     * @return The preferredSize of a JViewport whose view is this Scrollable.
     * @see JViewport#getPreferredSize
     */
    public Dimension	getPreferredScrollableViewportSize()
    {
    	return preferredsize;
    }
    
    public Dimension getPreferredSize()
    {
    	// Try to use dimension from containing scroll panel to expand to full size.
    	Dimension	pref	= super.getPreferredSize();
    	
    	Dimension	par	= null;
    	if(getParent()!=null && getParent().getParent() instanceof JScrollPane)
    	{
    		JScrollPane	scroll	= (JScrollPane)getParent().getParent();
    		par	= scroll.getSize();
    		Border	border	= scroll.getBorder();
    		if(border!=null)
    		{
	    		Insets	insets	= scroll.getBorder().getBorderInsets(scroll);
	    		par.width	-= insets.left + insets.right;
	    		par.height	-= insets.top + insets.bottom;
    		}
    	}
//    	System.out.println("par: "+par);
    	int	prefwidth	= pref!=null ? pref.width : 0;
    	int	prefheight	= pref!=null ? pref.height : 0;
    	int	parwidth	= par!=null ? par.width : 0;
    	int	parheight	= par!=null ? par.height : 0;
    	Dimension	ret	= new Dimension(Math.max(prefwidth, parwidth), Math.max(prefheight, parheight));
    	return ret;
    }


    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one new row
     * or column, depending on the value of orientation.  Ideally, 
     * components should handle a partially exposed row or column by 
     * returning the distance required to completely expose the item.
     * <p>
     * Scrolling containers, like JScrollPane, will use this method
     * each time the user requests a unit scroll.
     * 
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "unit" increment for scrolling in the specified direction.
     *         This value should always be positive.
     * @see JScrollBar#setUnitIncrement
     */
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
    	// hack???
    	return 16;
    }


    /**
     * Components that display logical rows or columns should compute
     * the scroll increment that will completely expose one block
     * of rows or columns, depending on the value of orientation. 
     * <p>
     * Scrolling containers, like JScrollPane, will use this method
     * each time the user requests a block scroll.
     * 
     * @param visibleRect The view area visible within the viewport
     * @param orientation Either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
     * @param direction Less than zero to scroll up/left, greater than zero for down/right.
     * @return The "block" increment for scrolling in the specified direction.
     *         This value should always be positive.
     * @see JScrollBar#setBlockIncrement
     */
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
    {
    	// hack???
    	return 16;
    }
    

    /**
     * Return true if a viewport should always force the width of this 
     * <code>Scrollable</code> to match the width of the viewport. 
     * For example a normal 
     * text view that supported line wrapping would return true here, since it
     * would be undesirable for wrapped lines to disappear beyond the right
     * edge of the viewport.  Note that returning true for a Scrollable
     * whose ancestor is a JScrollPane effectively disables horizontal
     * scrolling.
     * <p>
     * Scrolling containers, like JViewport, will use this method each 
     * time they are validated.  
     * 
     * @return True if a viewport should force the Scrollables width to match its own.
     */
    public boolean getScrollableTracksViewportWidth()
    {
//    	System.out.println("Track width: "+trackwidth);
    	return trackwidth;
    }

    /**
     * Return true if a viewport should always force the height of this 
     * Scrollable to match the height of the viewport.  For example a 
     * columnar text view that flowed text in left to right columns 
     * could effectively disable vertical scrolling by returning
     * true here.
     * <p>
     * Scrolling containers, like JViewport, will use this method each 
     * time they are validated.  
     * 
     * @return True if a viewport should force the Scrollables height to match its own.
     */
    public boolean getScrollableTracksViewportHeight()
    {
//    	System.out.println("Track height: "+trackheight);
    	return trackheight;
    }
}
