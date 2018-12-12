package jadex.tools.comanalyzer.graph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.RadiusGraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.util.RandomLocationTransformer;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;

/**
 * Implements a self-organizing map layout algorithm, based on Meyer's
 * self-organizing graph methods. <br> This is actualy a copy of the ISOMLayout
 * in the jung frame work. There is a bug the function adjust() in the original
 * class and you cant subclass it, because all the relevant attributes are
 * private without any getter methods :(
 */
public class GraphISOMLayout extends AbstractLayout implements IterativeContext {

	Map isomVertexData = LazyMap.decorate(new HashMap(), new Factory() {
		public Object create()
		{
			return new ISOMVertexData();
		}
	});

	private int maxEpoch;

	private int epoch;

	private int radiusConstantTime;

	private int radius;

	private int minRadius;

	private double adaption;

	private double initialAdaption;

	private double minAdaption;

	protected GraphElementAccessor elementAccessor = new RadiusGraphElementAccessor();

	private double coolingFactor;

	private List queue = new ArrayList();

	private String status = null;

	/**
	 * Returns the current number of epochs and execution status, as a string.
	 */
	public String getStatus()
	{
		return status;
	}

	public GraphISOMLayout(Graph g) {
		super(g);
	}

	public void initialize()
	{

		setInitializer(new RandomLocationTransformer(getSize()));
		maxEpoch = 2000;
		epoch = 1;

		radiusConstantTime = 100;
		radius = 5;
		minRadius = 1;

		initialAdaption = 90.0D / 100.0D;
		adaption = initialAdaption;
		minAdaption = 0;

		// factor = 0; //Will be set later on
		coolingFactor = 2;

		// temperature = 0.03;
		// initialJumpRadius = 100;
		// jumpRadius = initialJumpRadius;

		// delay = 100;
	}

	/**
	 * Advances the current positions of the graph elements.
	 */
	public void step()
	{
		status = "epoch: " + epoch + "; ";
		if (epoch < maxEpoch) {
			adjust();
			updateParameters();
			status += " status: running";

		} else {
			status += "adaption: " + adaption + "; ";
			status += "status: done";
			// done = true;
		}
	}

	ISOMVertexData tempISOM;

	Point2D tempXYD;

	private synchronized void adjust()
	{
		// Generate random position in graph space
		tempISOM = new ISOMVertexData();
		tempXYD = new Point2D.Double();

		// creates a new XY data location
		tempXYD.setLocation(10 + Math.random() * getSize().getWidth(), 10 + Math.random() * getSize().getHeight());

		// Get closest vertex to random position
		Object winner = elementAccessor.getVertex(this, tempXYD.getX(), tempXYD.getY());

		// BUGFIX goes hier: winner is null if there are no vertices in the
		// graph
		// NullPointerExeption in adjustVertex()!!!
		if (winner == null) return;

		while (true) {
			try {
				for(Iterator it=getGraph().getVertices().iterator(); it.hasNext(); ) 
				{
					ISOMVertexData ivd = getISOMVertexData(it.next());
					ivd.distance = 0;
					ivd.visited = false;
				}
				break;
			}
			catch (ConcurrentModificationException cme) {
			}
		}
		adjustVertex(winner);
	}

	private synchronized void updateParameters()
	{
		epoch++;
		double factor = Math.exp(-1 * coolingFactor * (1.0 * epoch / maxEpoch));
		adaption = Math.max(minAdaption, factor * initialAdaption);
		// jumpRadius = (int) factor * jumpRadius;
		// temperature = factor * temperature;
		if ((radius > minRadius) && (epoch % radiusConstantTime == 0)) {
			radius--;
		}
	}

	private synchronized void adjustVertex(Object v)
	{
		queue.clear();
		ISOMVertexData ivd = getISOMVertexData(v);
		ivd.distance = 0;
		ivd.visited = true;
		queue.add(v);
		Object current;

		while (!queue.isEmpty()) {
			current = queue.remove(0);
			ISOMVertexData currData = getISOMVertexData(current);
			Point2D currXYData = transform(current);

			double dx = tempXYD.getX() - currXYData.getX();
			double dy = tempXYD.getY() - currXYData.getY();
			double factor = adaption / Math.pow(2, currData.distance);

			currXYData.setLocation(currXYData.getX() + (factor * dx), currXYData.getY() + (factor * dy));
			// currXYData.addX(factor * dx);
			// currXYData.addY(factor * dy);

			if (currData.distance < radius) {
				Collection s = getGraph().getNeighbors(current);
				// current.getNeighbors();
				while (true) {
					try {
						for(Iterator it=s.iterator(); it.hasNext(); ) {
							// for (Iterator iter = s.iterator();
							// iter.hasNext();) {
							Object child = it.next();
							ISOMVertexData childData = getISOMVertexData(child);
							if (childData != null && !childData.visited) {
								childData.visited = true;
								childData.distance = currData.distance + 1;
								queue.add(child);
							}
						}
						break;
					}
					catch (ConcurrentModificationException cme) {
					}
				}
			}
		}
	}

	public ISOMVertexData getISOMVertexData(Object v)
	{
		return (ISOMVertexData)isomVertexData.get(v);
	}

	/**
	 * This one is an incremental visualization.
	 * 
	 * @return <code>true</code> is the layout algorithm is incremental,
	 * <code>false</code> otherwise
	 */
	public boolean isIncremental()
	{
		return true;
	}

	/**
	 * For now, we pretend it never finishes.
	 * 
	 * @return <code>true</code> is the increments are done,
	 * <code>false</code> otherwise
	 */
	public boolean done()
	{
		return epoch >= maxEpoch;
	}

	public static class ISOMVertexData {
		public DoubleMatrix1D disp;

		int distance;

		boolean visited;

		public ISOMVertexData() {
			initialize();
		}

		public void initialize()
		{
			disp = new DenseDoubleMatrix1D(2);

			distance = 0;
			visited = false;
		}

		public double getXDisp()
		{
			return disp.get(0);
		}

		public double getYDisp()
		{
			return disp.get(1);
		}

		public void setDisp(double x, double y)
		{
			disp.set(0, x);
			disp.set(1, y);
		}

		public void incrementDisp(double x, double y)
		{
			disp.set(0, disp.get(0) + x);
			disp.set(1, disp.get(1) + y);
		}

		public void decrementDisp(double x, double y)
		{
			disp.set(0, disp.get(0) - x);
			disp.set(1, disp.get(1) - y);
		}
	}

	public void reset()
	{
		epoch = 0;
	}
}