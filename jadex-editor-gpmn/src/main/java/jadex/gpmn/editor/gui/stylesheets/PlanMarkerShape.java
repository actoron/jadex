package jadex.gpmn.editor.gui.stylesheets;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.view.mxCellState;

import jadex.gpmn.editor.model.visual.VPlan.VPlanType;

public class PlanMarkerShape extends AbstractTextMarkerShape
{
	/**
	 *  Generates the shape.
	 */
	public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		Rectangle rect = state.getRectangle();
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;

		return new Rectangle2D.Double(x, y, w, h);
	}
	
	/**
	 *  Returns the text for the marker.
	 *  
	 *  @param state The cell state.
	 *  @return Text of the marker.
	 */
	protected String getText(mxCellState state)
	{
		return (String) ((VPlanType) state.getCell()).getValue();
	}
}
