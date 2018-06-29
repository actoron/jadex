package jadex.gpmn.editor.gui.stylesheets;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxActorShape;
import com.mxgraph.view.mxCellState;

public class VirtualActivationEdgeMarkerShape extends mxActorShape
{
	public Shape createShape(mxGraphics2DCanvas canvas, mxCellState state)
	{
		Rectangle rect = state.getRectangle();
		int x = rect.x;
		int y = rect.y;
		int w = rect.width;
		int h = rect.height;

		Path2D.Double ret = new Path2D.Double();
		
		Ellipse2D.Double ellipse = new Ellipse2D.Double(x, y, w, h);
		ret.append(ellipse, false);
		
		double shiftx = w * 0.25;
		double shifty = h * 0.25;
		
		double w2 = w * 0.5;
		double h2 = h * 0.5;
		Line2D.Double vert = new Line2D.Double(x + w2, y + shifty, x + w2, y + h - shifty);
		ret.append(vert, false);
		
		Line2D.Double hor = new Line2D.Double(x + shiftx, y + h2, x + w - shiftx, y + h2);
		ret.append(hor, false);
		
		return ret;
	}
}
