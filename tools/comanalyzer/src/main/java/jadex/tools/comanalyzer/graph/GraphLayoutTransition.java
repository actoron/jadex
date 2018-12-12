package jadex.tools.comanalyzer.graph;

import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;


/**
 * Added ConcurrentModificationException handling in method step()
 */
public class GraphLayoutTransition extends LayoutTransition
{

	// -------- constructor --------

	public GraphLayoutTransition(VisualizationViewer vv, Layout startLayout, Layout endLayout)
	{
		super(vv, startLayout, endLayout);
	}

	// -------- LayoutTransition methods --------

	public void step()
	{
		Graph g = transitionLayout.getGraph();

		try
		{
			for(Iterator it = g.getVertices().iterator(); it.hasNext();)
			{
				Object v = it.next();
				Point2D tp = (Point2D)transitionLayout.transform(v);
				Point2D fp = (Point2D)endLayout.transform(v);
				double dx = (fp.getX() - tp.getX()) / (count - counter);
				double dy = (fp.getY() - tp.getY()) / (count - counter);
				transitionLayout.setLocation(v, new Point2D.Double(tp.getX() + dx, tp.getY() + dy));
			}
		}
		catch(ConcurrentModificationException e)
		{
			// vertices could be removed while animator is running
			// end animator because a new one is triggered anyways
			// System.err.println("ConcurrentModificationException in step " +
			// counter);
			counter = count;
		}
		counter++;
		if(counter >= count)
		{
			done = true;
			vv.setGraphLayout(endLayout);
		}
		vv.repaint();
	}
}
