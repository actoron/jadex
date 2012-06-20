package jadex.gpmn.editor.gui;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;

/** Helper methods for the GUI. */
public class SGuiHelper
{
	/**
	 *  Text extraction from the Java "Document" class is braindead,
	 *  throws non-RuntimeException: Bloat, bloat, bloat.
	 *  
	 *  @param doc The document.
	 *  @return The extracted String.
	 */
	public static final String getText(Document doc)
	{
		String ret = null;
        
		try
        {
            ret = doc.getText(0, doc.getLength());
        }
        catch (BadLocationException e)
        {
        }
		
		if (ret.length() == 0)
		{
			ret = null;
		}
        
        return ret;
	}
	
	/**
	 *  Refreshes the view for a cell.
	 *  
	 *  @param cell The cell.
	 */
	public static final void refreshCellView(mxGraph graph, mxCell cell)
	{
		//graph.getView().removeState(cell);
		graph.getView().clear(cell, true, false);
		graph.getView().invalidate(cell);
		Object[] selcells = graph.getSelectionModel().getCells();
		graph.getSelectionModel().removeCells(selcells);
		graph.getView().validate();
		graph.setSelectionCells(selcells);
	}
	
	public static final mxPoint getCenter(mxPoint[] points)
	{
		return getCenter(new mxPoint(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY), new mxPoint(0.0, 0.0), points);
	}
	
	/**
	 *  Gets the center of the rectangle described by the two points.
	 *  
	 *  @param min Minimum coordinates of the rectangle.
	 *  @param max Maximum coordinates of the rectangle.
	 *  @param addition Additional points to be included in the calculation.
	 *  @return The center.
	 */
	public static final mxPoint getCenter(mxPoint min, mxPoint max, mxPoint[] additional)
	{
		mxPoint tmpmin = new mxPoint(min);
		mxPoint ret = new mxPoint(max);
		if (additional != null)
		{
			for (int i = 0; i < additional.length; ++i)
			{
				tmpmin.setX(Math.min(tmpmin.getX(), additional[i].getX()));
				tmpmin.setY(Math.min(tmpmin.getY(), additional[i].getY()));
				ret.setX(Math.max(ret.getX(), additional[i].getX()));
				ret.setY(Math.max(ret.getY(), additional[i].getY()));
			}
		}
		
		ret = new mxPoint(tmpmin.getX() + (ret.getX() - tmpmin.getX()) * 0.5,
				 		  tmpmin.getY() + (ret.getY() - tmpmin.getY()) * 0.5);
		
		return ret;
	}
}
