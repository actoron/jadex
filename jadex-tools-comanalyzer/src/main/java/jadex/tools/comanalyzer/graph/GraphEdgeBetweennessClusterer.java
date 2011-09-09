package jadex.tools.comanalyzer.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * An algorithm for computing clusters (community structure) in graphs based on
 * edge betweenness. This is actualy a copy of the EdgeBetweennessClusterer
 * in the jung frame work. There is a bug the function transfrom() in the
 * original class and you cant subclass it, because all the relevant attributes
 * are private without any getter methods :(
 */
public class GraphEdgeBetweennessClusterer implements Transformer
{
	/** The number of edges to remove */
	private int mNumEdgesToRemove;

	/** The list of removed edges */
	private List mEdgesRemoved;

	/** The edge weights. */
	private Map mEdgeWeights = new HashMap();

	/**
	 * Constructs a new clusterer for the specified graph.
	 * 
	 * @param numEdgesToRemove the number of edges to be progressively removed
	 * from the graph
	 */
	public GraphEdgeBetweennessClusterer(int numEdgesToRemove)
	{
		mNumEdgesToRemove = numEdgesToRemove;
		mEdgesRemoved = new ArrayList();
	}

	/**
	 * Constructs a new clusterer for the specified graph.
	 * 
	 * @param numEdgesToRemove the number of edges to be progressively removed
	 * from the graph
	 * @param edgeWeights The map of edgeweights
	 */
	public GraphEdgeBetweennessClusterer(int numEdgesToRemove, Map edgeWeights)
	{
		mNumEdgesToRemove = numEdgesToRemove;
		mEdgesRemoved = new ArrayList();
		mEdgeWeights = edgeWeights;
	}

	/**
	 * Finds the set of clusters which have the strongest "community structure".
	 * The more edges removed the smaller and more cohesive the clusters.
	 * 
	 * @param g the graph
	 */
	public Object transform(Object graph)
	{

		if(mNumEdgesToRemove < 0 || mNumEdgesToRemove > ((Graph)graph).getEdgeCount())
		{
			throw new IllegalArgumentException("Invalid number of edges passed in.");
		}

		Map removedEdges = new HashMap();

		mEdgesRemoved.clear();

		for(int k = 0; k < mNumEdgesToRemove; k++)
		{
			BetweennessCentrality bc = new BetweennessCentrality((Graph)graph, false);
			bc.setEdgeWeights(mEdgeWeights);
			bc.setRemoveRankScoresOnFinalize(true);
			bc.evaluate();
			Ranking highestBetweenness = (Ranking)bc.getRankings().get(0);
			Object removedEdge = highestBetweenness.getRanked();
			Pair removedEdgeEndpoints = ((Graph)graph).getEndpoints(removedEdge);
			removedEdges.put(removedEdge, removedEdgeEndpoints);
			mEdgesRemoved.add(highestBetweenness.getRanked());
			((Graph)graph).removeEdge(highestBetweenness.getRanked());
		}

		WeakComponentClusterer wcSearch = new WeakComponentClusterer();
		Set clusterSet = (Set)wcSearch.transform((Graph)graph);
		for(Iterator it = mEdgesRemoved.iterator(); it.hasNext();)
		{
			// BUGFIX goes here !!!
			// Pair<V> endpoints = graph.getEndpoints(edge); // error !!!
			Object o = it.next();
			Pair endpoints = (Pair)removedEdges.get(o); // this is correct
			((Graph)graph).addEdge(o, endpoints.getFirst(), endpoints.getSecond());
		}
		return clusterSet;
	}

	/**
	 * Retrieves the list of all edges that were removed (assuming extract(...)
	 * was previously called. The edges returned are stored in order in which
	 * they were removed
	 * 
	 * @return the edges in the original graph
	 */
	public List getEdgesRemoved()
	{
		return mEdgesRemoved;
	}
}
