package jadex.tools.comanalyzer.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * Implementation of an Graph that can be used as a simple or a multi graph with
 * directed and undirected edges.
 */
public class MultidirectedMultiGraph extends SparseGraph implements Graph, Serializable
{
	//-------- static part --------

	/**
	 * Returns the factory for the graph.
	 * 
	 * <V> The vertex type.
	 * <E> The edge type.
	 * @return The Factory for the graph..
	 */
	public static Factory getFactory()
	{
		return new Factory()
		{
			public Object create()
			{
				return new MultidirectedMultiGraph();
			}
		};
	}

	/**
	 * Returns a new MultidirectedMultiGraph.
	 * <V> The vertex type.
	 * <E> The edge type.
	 * @return The MultidirectedMultiGraph.
	 */
	public static Graph create()
	{
		return new MultidirectedMultiGraph();
	};

	// -------- constants --------

	/** Indicates incoming edges in the map */
	protected static final int INCOMING = 0;

	/** Indicates outgoing edges in the map */
	protected static final int OUTGOING = 1;

	/** Indicates incident (undirected) edges in the map */
	protected static final int INCIDENT = 2;

	/**
	 * Map of vertices to adjacency maps of vertices to {incoming, outgoing,
	 * incident} edges
	 */
	protected Map mvertex_maps;

	/** Map of directed edges to incident vertex sets */
	protected Map mdirected_edges;

	/** Map of undirected edges to incident vertex sets */
	protected Map mundirected_edges;

	//-------- constructor --------

	/**
	 * Creates a new graph.
	 */
	public MultidirectedMultiGraph()
	{
		mvertex_maps = new HashMap();
		mdirected_edges = new HashMap();
		mundirected_edges = new HashMap();
	}

	//-------- Graph interface --------

	/**
	 * Adds a directed edge to the graph.
	 * 
	 * @param edge The edge.
	 * @param endpoints The endpoints.
	 * @return <code>true</code> if success.
	 */
	public boolean addEdge(Object edge, Pair endpoints)
	{
		return addEdge(edge, endpoints, EdgeType.DIRECTED);
	}

	/**
	 * Adds a edge to the graph with a specific edge type.
	 * 
	 * @param edge The edge.
	 * @param endpoints The endpoints.
	 * @param edgeType The edge type.
	 * @return <code>true</code> if success.
	 */
	public boolean addEdge(Object edge, Pair endpoints, EdgeType edgeType)
	{
		Pair new_endpoints = getValidatedEndpoints(edge, endpoints);
		if(new_endpoints == null)
		{
			return false;
		}

		Object v1 = new_endpoints.getFirst();
		Object v2 = new_endpoints.getSecond();

		if(!containsVertex(v1))
			this.addVertex(v1);

		if(!containsVertex(v2))
			this.addVertex(v2);

		// 
		if(edgeType == EdgeType.DIRECTED)
		{
			getOutgoing_internal(v1).add(edge);
			getIncoming_internal(v2).add(edge);
			mdirected_edges.put(edge, new_endpoints);
		}
		else
		{
			getIncident_internal(v1).add(edge);
			getIncident_internal(v2).add(edge);
			mundirected_edges.put(edge, new_endpoints);
		}

		return true;
	}

	/**
	 * Adds a directed edge to the graph.
	 * 
	 * @param edge The edge.
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return <code>true</code> if success.
	 */
	public boolean addEdge(Object e, Object v1, Object v2)
	{
		return addEdge(e, new Pair(v1, v2));
	}

	/**
	 * Adds a edge to the graph with a specific edge type.
	 * 
	 * @param edge The edge.
	 * @param v1 The first vertex.
	 * @param v2 The second vertex..
	 * @param edgeType The edge type.
	 * @return <code>true</code> if success.
	 */
	public boolean addEdge(Object e, Object v1, Object v2, EdgeType edgeType)
	{
		return addEdge(e, new Pair(v1, v2), edgeType);
	}

	/**
	 * Returns the first edge between the two vertices.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return The edge or <code>null</code> if there is no incident edge
	 * between the vertices.
	 */
	public Object findEdge(Object v1, Object v2)
	{
		if(!containsVertex(v1) || !containsVertex(v2))
			return null;

		for(Iterator it = getOutEdges(v1).iterator(); it.hasNext();)
		{
			Object e = it.next();
			if(getOpposite(v1, e).equals(v2))
				return e;
		}
		return null;
	}

	/**
	 * Returns a collection of edges between the two vertices.
	 * 
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return The collection of edges or <code>null</code> if there is no
	 * incident edge between the vertices.
	 */
	public Collection findEdgeSet(Object v1, Object v2)
	{
		if(!containsVertex(v1) || !containsVertex(v2))
			return null;

		Collection edges = new ArrayList();
		for(Iterator it = getOutEdges(v1).iterator(); it.hasNext();)
		{
			Object e = it.next();
			if(getOpposite(v1, e).equals(v2))
				edges.add(e);
		}
		return Collections.unmodifiableCollection(edges);
	}

	/**
	 * Returns the incoming edges of the vertex.
	 * @param vertex The vertx
	 * @return The collection of edges.
	 */
	public Collection getInEdges(Object vertex)
	{
		if(!containsVertex(vertex))
			return null;

		// combine directed inedges and undirected
		Collection in = new HashSet();
		in.addAll(getIncoming_internal(vertex));
		in.addAll(getIncident_internal(vertex));
		// remove null key if present
		// in.remove(null);
		return Collections.unmodifiableCollection(in);
	}

	/**
	 * Returns the outgoing edges of the vertex.
	 * @param vertex The vertx
	 * @return The collection of edges.
	 */
	public Collection getOutEdges(Object vertex)
	{
		if(!containsVertex(vertex))
			return null;

		// combine directed outedges and undirected
		Collection out = new HashSet();
		out.addAll(getIncident_internal(vertex));
		out.addAll(getOutgoing_internal(vertex));
		// remove null key if present
		// out.remove(null);
		return Collections.unmodifiableCollection(out);
	}

	/**
	 * Returns the vertices that have an outgoing edge to the given vertex.
	 * @param vertex The vertex.
	 * @return The collection of vertices.
	 */
	public Collection getPredecessors(Object vertex)
	{
		if(!containsVertex(vertex))
			return null;

		// consider only directed inedges
		Collection preds = new HashSet();
		for(Iterator it = getIncoming_internal(vertex).iterator(); it.hasNext();)
		{
			preds.add(getSource(it.next()));
		}
		return Collections.unmodifiableCollection(preds);
	}

	/**
	 * Returns the vertices that have an incoming edge from the given vertex.
	 * @param vertex The vertex.
	 * @return The collection of vertices.
	 */
	public Collection getSuccessors(Object vertex)
	{
		if(!containsVertex(vertex))
			return null;

		Collection succs = new HashSet();
		for(Iterator it = getOutgoing_internal(vertex).iterator(); it.hasNext();)
			succs.add(getDest(it.next()));

		return Collections.unmodifiableCollection(succs);
	}

	/**
	 * Returns the edges for a given edge type.
	 * @param edgeType The edge tzpe.
	 * @return The collection of edges.
	 */
	public Collection getEdges(EdgeType edgeType)
	{
		if(edgeType == EdgeType.DIRECTED)
			return Collections.unmodifiableCollection(mdirected_edges.keySet());
		else if(edgeType == EdgeType.UNDIRECTED)
			return Collections.unmodifiableCollection(mundirected_edges.keySet());
		else
			return null;
		// return Collections.unmodifiableCollection(new ArrayList<E>(0));
	}

	/**
	 * Returns the endpoints of the edge.
	 * @param edge The edge.
	 * @return The endpoints.
	 */
	public Pair getEndpoints(Object edge)
	{
		Pair endpoints;
		endpoints = (Pair)mdirected_edges.get(edge);
		if(endpoints == null)
			return (Pair)mundirected_edges.get(edge);
		else
			return endpoints;
	}

	/**
	 * Returns the edge type of en edge.
	 * @param edge The edge.
	 * @return The edge type.
	 */
	public EdgeType getEdgeType(Object edge)
	{
		if(mdirected_edges.containsKey(edge))
			return EdgeType.DIRECTED;
		else if(mundirected_edges.containsKey(edge))
			return EdgeType.UNDIRECTED;
		else
			return null;
	}

	/**
	 * Returns the source of a directed edge.
	 * @param directed_edge The edge.
	 * @return The vertex.
	 */
	public Object getSource(Object directed_edge)
	{
		if(getEdgeType(directed_edge) == EdgeType.DIRECTED)
			return ((Pair)mdirected_edges.get(directed_edge)).getFirst();
		else
			return null;
	}

	/**
	 * Returns the destination of a directed edge.
	 * @param directed_edge The edge.
	 * @return The vertex.
	 */
	public Object getDest(Object directed_edge)
	{
		if(getEdgeType(directed_edge) == EdgeType.DIRECTED)
			return ((Pair)mdirected_edges.get(directed_edge)).getSecond();
		else
			return null;
	}

	/**
	 * Returns <code>true</code> if the vertex is the source of the edge.
	 * @param vertex The vertex.
	 * @param edge The edge.
	 * @return <code>true</code> if the vertex is the source.
	 */
	public boolean isSource(Object vertex, Object edge)
	{
		if(!containsVertex(vertex) || !containsEdge(edge))
			return false;

		Object source = getSource(edge);
		if(source != null)
			return source.equals(vertex);
		else
			return false;
	}

	/**
	 * Returns <code>true</code> if the vertex is the destination of the edge.
	 * @param vertex The vertex.
	 * @param edge The edge.
	 * @return <code>true</code> if the vertex is the destination.
	 */
	public boolean isDest(Object vertex, Object edge)
	{
		if(!containsVertex(vertex) || !containsEdge(edge))
			return false;

		Object dest = getDest(edge);
		if(dest != null)
			return dest.equals(vertex);
		else
			return false;
	}

	/**
	 * Returns all edges of the graph.
	 * @return The collection of edges.
	 */
	public Collection getEdges()
	{
		Collection edges = new ArrayList();
		edges.addAll(mdirected_edges.keySet());
		edges.addAll(mundirected_edges.keySet());
		
		return Collections.unmodifiableCollection(edges);
	}

	/**
	 * Returns all vertices of the graph.
	 * @return The collection of vertices.
	 */
	public Collection getVertices()
	{
		return Collections.unmodifiableCollection(mvertex_maps.keySet());
	}

	/**
	 * Returns <code>true</code> if the graph contains the vertex.
	 * @param vertex The vertex.
	 * @return <code>true</code> if the vertex is in the graph.
	 */
	public boolean containsVertex(Object vertex)
	{
		return mvertex_maps.containsKey(vertex);
	}

	/**
	 * Returns <code>true</code> if the graph contains the edge.
	 * @param edge The edge.
	 * @return <code>true</code> if the edge is in the graph.
	 */
	public boolean containsEdge(Object edge)
	{
		return mdirected_edges.containsKey(edge) || mundirected_edges.containsKey(edge);
	}

	/**
	 * @return The edge count.
	 */
	public int getEdgeCount()
	{
		return mdirected_edges.size() + mundirected_edges.size();
	}

	/**
	 * @return The vertex count.
	 */
	public int getVertexCount()
	{
		return mvertex_maps.size();
	}

	/**
	 * Returns the neighbor vertices of a given vertex.
	 * @param vertex The vertex.
	 * @return The collection of neighbors.
	 */
	public Collection getNeighbors(Object vertex)
	{
		// consider directed edges and undirected edges
		Collection neighbors = new HashSet();
		for(Iterator it = getIncoming_internal(vertex).iterator(); it.hasNext();)
		{
			neighbors.add(getSource(it.next()));
		}
		for(Iterator it = getOutgoing_internal(vertex).iterator(); it.hasNext();)
		{
			neighbors.add(getDest(it.next()));
		}
		// A vertex isnt neighbor to itself
		for(Iterator it = getIncident_internal(vertex).iterator(); it.hasNext();)
		{
			Pair endpoints = getEndpoints(it.next());
			Object e_a = endpoints.getFirst();
			Object e_b = endpoints.getSecond();
			if(vertex.equals(e_a))
			{
				neighbors.add(e_b);
			}
			else
			{
				neighbors.add(e_a);
			}
		}

		return Collections.unmodifiableCollection(neighbors);
	}

	/**
	 * Returns the degree (number of incident edges) of a vertex.
	 * @param vertex The vertex.
	 * @return The degree.
	 */
	public int degree(Object vertex)
	{
		// if (!containsVertex(vertex))
		// return 0;

		return getIncidentEdges(vertex).size();
	}

	/**
	 * Returns the incident edges of the vertex.
	 * @param vertex The vertex.
	 * @return The collection of edges.
	 */
	public Collection getIncidentEdges(Object vertex)
	{
		if(!containsVertex(vertex))
			return null;

		// combine directed outedges and undirected
		Collection incident = new HashSet();
		incident.addAll(getIncident_internal(vertex));
		incident.addAll(getIncoming_internal(vertex));
		incident.addAll(getOutgoing_internal(vertex));
		return Collections.unmodifiableCollection(incident);
	}

	/**
	 * Adds a vertex to the graph.
	 * @param vertex The vertex.
	 * @return <code>true</code> if success.
	 */
	public boolean addVertex(Object vertex)
	{
		if(vertex == null)
		{
			throw new IllegalArgumentException("vertex may not be null");
		}
		if(!containsVertex(vertex))
		{
			mvertex_maps.put(vertex, new HashSet[]{new HashSet(), new HashSet(), new HashSet()});
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Removes a vertex from the graph.
	 * @param vertex The vertex.
	 * @return <code>true</code> if success
	 */
	public boolean removeVertex(Object vertex)
	{
		if(!containsVertex(vertex))
			return false;

		if(getIncidentEdges(vertex) == null)
		{
			System.err.println("no edge in main");
		}

		// copy to avoid concurrent modification in removeEdge
		List incident = new ArrayList(getIncidentEdges(vertex));

		for(int i = 0; i < incident.size(); i++)
			removeEdge(incident.get(i));

		mvertex_maps.remove(vertex);

		return true;
	}

	/**
	 * Removes a edge from the graph.
	 * @param edge The edge.
	 * @return <code>true</code> if success
	 */
	public boolean removeEdge(Object edge)
	{
		if(!containsEdge(edge))
			return false;

		Pair endpoints = getEndpoints(edge);
		Object v1 = endpoints.getFirst();
		Object v2 = endpoints.getSecond();

		// remove edge from incident vertices' adjacency maps
		if(getEdgeType(edge) == EdgeType.DIRECTED)
		{
			getOutgoing_internal(v1).remove(edge);
			getIncoming_internal(v2).remove(edge);
			mdirected_edges.remove(edge);
		}
		else
		{
			getIncident_internal(v1).remove(edge);
			getIncident_internal(v2).remove(edge);
			mundirected_edges.remove(edge);
		}
		return true;
	}

	// -------- helper methods --------

	/**
	 * Returns the incoming edges of the vertex
	 * @param vertex The vertex.
	 * @return The collection of edges.
	 */
	protected Collection getIncoming_internal(Object vertex)
	{
		return (Collection)((Object[])mvertex_maps.get(vertex))[INCOMING];
	}

	/**
	 * Returns the incident edges of the vertex
	 * @param vertex The vertex.
	 * @return The collection of edges.
	 */
	protected Collection getIncident_internal(Object vertex)
	{
		return (Collection)((Object[])mvertex_maps.get(vertex))[INCIDENT];
	}

	/**
	 * Returns the outgoing edges of the vertex
	 * @param vertex The vertex.
	 * @return The collection of edges.
	 */
	protected Collection getOutgoing_internal(Object vertex)
	{
		return (Collection)((Object[])mvertex_maps.get(vertex))[OUTGOING];
	}

}
