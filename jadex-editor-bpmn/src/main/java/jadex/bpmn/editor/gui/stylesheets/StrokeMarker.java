package jadex.bpmn.editor.gui.stylesheets;

import java.awt.geom.Line2D;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxIMarker;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;

/**
 *  Marker for "strike-through" edge look of default edges.
 *
 */
public class StrokeMarker implements mxIMarker
{
	/**
	 *  Paints the marker.
	 */
	public mxPoint paintMarker(mxGraphics2DCanvas canvas, mxCellState state,
			String type, mxPoint pe, double nx, double ny, double size,
			boolean source)
	{
		double nx2 = nx * 0.5;
		double ny2 = ny * 0.5;
		Line2D line = new Line2D.Double(pe.getX() + ny2 - 0.3 * nx, pe.getY() + nx2 - 0.3 * ny, pe.getX() - nx - ny2, pe.getY() - nx2 - ny);
		Line2D line2 = new Line2D.Double(pe.getX(), pe.getY(), pe.getX() - nx, pe.getY() - ny);
		
		canvas.getGraphics().draw(line);
		canvas.getGraphics().draw(line2);

		return new mxPoint(-nx, -ny);
	}
	
}
