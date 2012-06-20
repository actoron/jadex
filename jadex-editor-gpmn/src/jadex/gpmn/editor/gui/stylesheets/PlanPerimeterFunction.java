package jadex.gpmn.editor.gui.stylesheets;

import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxPerimeter;

public class PlanPerimeterFunction implements mxPerimeter.mxPerimeterFunction
{
	/**
	 * Implements a perimeter function.
	 * 
	 * @param bounds Rectangle that represents the absolute bounds of the
	 * vertex.
	 * @param vertex Cell state that represents the vertex.
	 * @param next Point that represents the nearest neighbour point on the
	 * given edge.
	 * @param orthogonal Boolean that specifies if the orthogonal projection onto
	 * the perimeter should be returned. If this is false then the intersection
	 * of the perimeter and the line between the next and the center point is
	 * returned.
	 * @return Returns the perimeter point.
	 */
	public mxPoint apply(mxRectangle bounds, mxCellState vertex, mxPoint next,
			boolean orthogonal)
	{
		return null;
	}
	
}
