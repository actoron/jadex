package jadex.tools.comanalyzer.graph;

import java.awt.Dimension;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.graph.Graph;


/**
 * Subclassing to support locked vertices (so they get not shuffled) and
 * initializing random locations on setSize(). <br> (Preventing exceptions when
 * initializing the layout.)
 */
public class GraphCircleLayout extends CircleLayout
{

	// -------- consructor --------

	/**
	 * Default constructor for a given graph.
	 * 
	 * @param g The graph.
	 */
	public GraphCircleLayout(Graph g)
	{
		super(g);
	}

	// -------- CircleLayout methods --------

//	/**
//	 * Specifies the order of vertices. The first element of the specified array
//	 * will be positioned with angle 0 (on the X axis), and the second one will
//	 * be positioned with angle 1/n, and the third one will be positioned with
//	 * angle 2/n, and so on. <p> Ordering (in fact shuffling) is only done for
//	 * not locked vertices.
//	 */
//	public void orderVertices(Object[] vertices)
//	{
//		List list = Arrays.asList(vertices);
//		// order only not locked vertices and append them shuffled
//		List order = new ArrayList();
//		for(Iterator it = list.iterator(); it.hasNext();)
//		{
//			Object o = it.next();
//			if(!isLocked(o))
//			{
//				order.add(o);
//			}
//		}
//		Collections.shuffle(order);
//	}

	/**
	 * Initialize with random locations .
	 */
	public void setSize(Dimension size)
	{
		setInitializer(new RandomLocationTransformer(size));
		super.setSize(size);
	}
}
