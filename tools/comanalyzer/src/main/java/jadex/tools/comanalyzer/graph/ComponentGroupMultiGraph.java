package jadex.tools.comanalyzer.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import jadex.commons.collection.SortedList;

/**
 * A generic Graph with edges and vertices consisting of groups of elements. The
 * elements are in a container that inplements the IComponentGroup interface.
 * (You cant use simply List because the edges and vertices are stored in
 * HashMaps and Lists changes their hashcode when elements are added or removed.
 * The List is no longer to be found in the HashMap)
 * 
 * Edge- and vertex element are stored in this component while the vertices and
 * edges are delegated to a different graph. This implementation provides acces
 * to either the elements and (per delegate) to the vertex and edges of the
 * graph.
 * 
 * <VE> The vertex element type.
 * <EE> The edge element type.
 * <V> The vertex type.
 * <E> The edge type.
 */
public class ComponentGroupMultiGraph implements Graph 
{
	//-------- static part --------
	
	/**
	 * Factory method for the graph.
	 * 
	 * @param vertex_factory The factory for vertices.
	 * @param edge_factory The factory for edges.
	 * @param delegate The delegated graph.
	 * @return The ComponentGroupMultiGraph.
	 */
	public static ComponentGroupMultiGraph createInstance(
		Factory vertex_factory, Factory edge_factory, Graph delegate)
	{
		return new ComponentGroupMultiGraph(vertex_factory, edge_factory, delegate);
	}
	
	// -------- attributes --------

	/** Factoriy for vertices */
	protected Factory vertex_factory;

	/** Factories for edges */
	protected Factory edge_factory;

	/** The map of edge elements */
	protected Map edge_elemets;

	/** The map of vertex elements */
	protected Map vertex_elemets;

	/** The delegated graph */
	protected Graph delegate;

	/**
	 * The edge weights is the number of edge elements contained in the edge.
	 * The list is sorted that the edge with the highest number of elements is
	 * at the end of the list. The highest edge weight is therefore identified
	 * by accessing the last element of the list.
	 */
	protected GenericSortedList edge_weights;

	/**
	 * The vertex weights is the number of edges going in or out the vertex. The
	 * list is sorted that the vertex with the highest number of incident edges
	 * is at the end of the list. The highest vertex weight is therefore
	 * identified by accessing the last element of the list
	 */
	protected GenericSortedList vertex_weights;

	// -------- constructors --------

	/**
	 * The constructor. Besides the delegated graph, you must provide factories
	 * for the vertex and edges, since its not possible to instantiate type
	 * bound parameter (at least not that easy)
	 * 
	 * @param vertex_factory The factory for vertices.
	 * @param edge_factory The factory for edges.
	 * @param delegate The delegated graph.
	 */
	protected ComponentGroupMultiGraph(Factory vertex_factory, Factory edge_factory, Graph delegate) 
	{
		this.delegate = delegate;

		this.vertex_factory = vertex_factory;
		this.edge_factory = edge_factory;
		this.edge_elemets = new HashMap();
		this.vertex_elemets = new HashMap();

//		delegate = new MultidirectedMultiGraph();

		this.edge_weights = new GenericSortedList(new Comparator() {

			/**
			 * The edges are ordered by their element count. "Note: this
			 * comparator imposes orderings that are inconsistent with equals."
			 */
			public int compare(final Object e1, final Object e2)
			{
				return ((ComponentGroup)e1).size() - ((ComponentGroup)e2).size();
			}

		});

		this.vertex_weights = new GenericSortedList(new Comparator() {

			/**
			 * The vertex are ordered by their degree. "Note: this comparator
			 * imposes orderings that are inconsistent with equals."
			 */
			public int compare(Object v1, Object v2)
			{
				return degree(v1) - degree(v2);
			}

		});

	}

	// -------- ComponentGroupMultiGraph methods --------


	/**
	 * Clear all elements from the delegated graph and this graph.
	 */
	public void clear()
	{
		List vertices = new ArrayList(getVertices());
		for(int i=0; i<vertices.size(); i++) 
		{
			removeVertex(vertices.get(i));
		}
		edge_elemets.clear();
		vertex_elemets.clear();
		edge_weights.clear();
		vertex_weights.clear();

	}

	/**
	 * Adds a new vertex containing the vertex element to the graph.
	 * 
	 * @param ve The vertex element to add.
	 * @return <code>true</code> if success.
	 */
	public boolean addVertexElement(Object ve)
	{
		if (ve == null) {
			throw new IllegalArgumentException("vertex may not be null");
		}
		if (!containsVertexElement(ve)) {
			IComponentGroup v = (IComponentGroup)vertex_factory.create();
			v.addElement(ve);
			addVertex(v);
			return true;
		}

		return false;
	}

	/**
	 * Append a vertex element to a vertex.
	 * 
	 * @param ve The vertex element to append.
	 * @param v The vertex the element is append to.
	 * @return <code>true</code> if success.
	 */
	public boolean appendVertexElement(Object ve, Object v)
	{
		if (ve == null || v == null) {
			throw new IllegalArgumentException("vertex may not be null");
		}

		if (!containsVertex(v)) addVertex(v);

		if (!containsVertexElement(ve)) {
			((IComponentGroup)v).addElement(ve);
			vertex_elemets.put(ve, v);
			vertex_weights.replace(v);
			return true;
		}

		return false;
	}

	/**
	 * Adds an edge element with given vertex elements and edge type to the
	 * graph. If the two vertex elements are already part of vertices, a new
	 * edge between these vertices is added. If the vertex elements dont already
	 * exist in the graph, new vertices are created. A new edge is created in
	 * any case.
	 * 
	 * @return <code>true</code> if success.
	 */
	public boolean addEdgeElement(Object ee, Object ve1, Object ve2, EdgeType edgeType)
	{
		if (ee == null || ve1 == null || ve2 == null) {
			throw new IllegalArgumentException("graph elements may not be null");
		}
		if (!containsVertexElement(ve1)) {
			addVertexElement(ve1);
		}

		if (!containsVertexElement(ve2)) {
			addVertexElement(ve2);
		}

		Object v1 = vertex_elemets.get(ve1);
		Object v2 = vertex_elemets.get(ve2);

		IComponentGroup  e = (IComponentGroup)edge_factory.create();
		e.addElement(ee);
		addEdge(e, v1, v2, edgeType);

		return true;
	}

	/**
	 * Adds an directed edge element with given vertex elements to the graph. If
	 * the two vertex elements are already part of vertices, a new edge between
	 * these vertices is added. If the vertex elements dont already exist in the
	 * graph, new vertices are created. A new edge is created in any case.
	 * 
	 * @return <code>true</code> if success.
	 */
	public boolean addEdgeElement(Object ee, Object ve1, Object ve2)
	{
		return addEdgeElement(ee, ve1, ve2, EdgeType.DIRECTED);
	}

	/**
	 * Appends an directed edge element with given vertex elements to the graph.
	 * If the two vertex elements are already part of vertices, and these
	 * vertices have a connection, the edge element is appended to this edge.
	 * Otherwise the components are added as new edge and new vertices to the
	 * graph.
	 * 
	 * @return <code>true</code> if success.
	 */
	public boolean appendEdgeElement(Object ee, Object ve1, Object ve2)
	{
		return appendEdgeElement(ee, ve1, ve2, EdgeType.DIRECTED);
	}

	/**
	 * Appends an edge element with given vertex elements and edge type to the
	 * graph. If the two vertex elements are already part of vertices, and these
	 * vertices have a connection, the edge element is appended to this edge.
	 * Otherwise the components are added as a new edge and new vertices to the
	 * graph. The type of the edge can be directed or undirected
	 * 
	 * @return <code>true</code> if success.
	 */
	public boolean appendEdgeElement(Object ee, Object ve1, Object ve2, EdgeType edgeType)
	{
		if (ee == null || ve1 == null || ve2 == null) {
			throw new IllegalArgumentException("graph elements may not be null");
		}
		if (!containsVertexElement(ve1)) {
			addVertexElement(ve1);
		}

		if (!containsVertexElement(ve2)) {
			addVertexElement(ve2);
		}

		// EG eg = edge_factory.create();
		Object v1 = vertex_elemets.get(ve1);
		Object v2 = vertex_elemets.get(ve2);

		IComponentGroup e = (IComponentGroup)findEdge(v1, v2);
		if (e == null) {
			e = (IComponentGroup)edge_factory.create();
			e.addElement(ee);
			addEdge(e, v1, v2, edgeType);
		} else {
			e.addElement(ee);
			edge_elemets.put(ee, e);
			edge_weights.replace(e);
		}
		return true;
	}

	/**
	 * Returns the edge (group) for an edge element.
	 * 
	 * @param ee The edge element to find.
	 * @return The edge for this element.
	 */
	public IComponentGroup findEdge(Object ee)
	{
		return (IComponentGroup)edge_elemets.get(ee);
	}

	/**
	 * Returns the vertex (group) for a vertex element.
	 * 
	 * @param ve The vertex element to find
	 * @return The vertex for this element.
	 */
	public IComponentGroup findVertex(Object ve)
	{
		return (IComponentGroup)vertex_elemets.get(ve);
	}

	/**
	 * Returns <code>true</code> if the graph contains the given edge element.
	 * 
	 * @param ee The edge element to query
	 * @return <code>true</code> if present
	 */
	public boolean containsEdgeElement(Object ee)
	{
		return edge_elemets.containsKey(ee);
	}

	/**
	 * Returns <code>true</code> if the graph contains the given vertex
	 * element.
	 * 
	 * @param ve The vertex element to query
	 * @return <code>true</code> if success
	 */
	public boolean containsVertexElement(Object ve)
	{
		return vertex_elemets.containsKey(ve);
	}

	/**
	 * Removes an edge element from the graph.
	 * 
	 * @param ee The edge element to remove
	 * @return <code>true</code> if success.
	 */
	public boolean removeEdgeElement(Object ee)
	{
		if (!containsEdgeElement(ee)) return false;

		IComponentGroup e = findEdge(ee);
		e.removeElement(ee);
		edge_elemets.remove(ee);

		if (e.size() == 0) {
			removeEdge(e);
		} else {
			edge_weights.replace(e);
		}
		return true;
	}

	/**
	 * Removes a vertex element from the graph.
	 * @param ve The vertex element to remove
	 * @return <code>true</code> if success
	 */
	public boolean removeVertexElement(Object ve)
	{
		if (!containsVertexElement(ve)) return false;

		IComponentGroup v = findVertex(ve);
		v.removeElement(ve);
		vertex_elemets.remove(ve);

		if (v.size() == 0) {
			removeVertex(v);
		} else {
			vertex_weights.replace(v);
		}
		return true;
	}

	/**
	 * Returns all edge elements.
	 * @return The collection of all edge elements.
	 */
	public Collection getAllEdgeElements()
	{
		return Collections.unmodifiableCollection(edge_elemets.keySet());
	}

	/**
	 * Returns all vertex elements.
	 * @return The collection of all vertex elements.
	 */
	public Collection getAllVertexElements()
	{
		return Collections.unmodifiableCollection(vertex_elemets.keySet());
	}

	/**
	 * Returns the highest edge weight
	 * @return The highest weight of all edges.
	 */
	public int getHighestEdgeWeight()
	{
		// returns the element count of the last edge in the sorted list.
		if (!edge_weights.isEmpty()) {
			return ((IComponentGroup)edge_weights.getLast()).getElements().size();
		} else {
			System.err.println("edgeweight=0");
			return 0;
		}
	}

	/**
	 * Returns the highest vertex weight.
	 * @return The highest weight of all vertices.
	 */
	public int getHighestVertexWeight()
	{
		if (!vertex_weights.isEmpty()) {
			// returns the element count of the last vertex in the sorted list.
			return degree(vertex_weights.getLast());
		} else {
			System.err.println("vertexweight=0");
			return 0;
		}
	}

	// -------- Graph interface --------

	/**
	 * Adds an edge to the graph.
	 * @param edge The edge.
	 * @param vertices A collection of veritexes. The size of the Collection
	 * must be 2.
	 * @return <code>true</code> if success.
	 */
	public boolean addEdge(Object edge, Collection vertices)
	{
		Pair pair = null;
		if (vertices instanceof Pair) {
			pair = (Pair) vertices;
		} else {
			pair = new Pair(vertices);
		}
		return addEdge(edge, pair.getFirst(), pair.getSecond());
	}

	/**
	 * Adds an edge with a given edge type to the graph.
	 * @param e The edge.
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @param edgeType The edge type.
	 * @return <code>true</code> if success.
	 */
	public boolean addEdge(Object e, Object v1, Object v2, EdgeType edgeType)
	{
		if (delegate.addEdge(e, v1, v2, edgeType))
		{
			internal_addEdge(e);
			internal_addVertex(v1);
			internal_addVertex(v2);
			return true;
		} else {
			return false;
		}
	}
	
	 /**
     * Adds <code>edge</code> to this graph with type <code>edge_type</code>.
     * Fails under the following circumstances:
     * <ul>
     * <li/><code>edge</code> is already an element of the graph 
     * <li/>either <code>edge</code> or <code>vertices</code> is <code>null</code>
     * <li/><code>vertices</code> has the wrong number of vertices for the graph type
     * <li/><code>vertices</code> are already connected by another edge in this graph,
     * and this graph does not accept parallel edges
     * <li/><code>edge_type</code> is not legal for this graph
     * </ul>
     * 
     * @param edge
     * @param vertices
     * @return <code>true</code> if the add is successful, and <code>false</code> otherwise
     * @throws IllegalArgumentException if <code>edge</code> or <code>vertices</code> is null, 
     * or if a different vertex set in this graph is already connected by <code>edge</code>, 
     * or if <code>vertices</code> are not a legal vertex set for <code>edge</code> 
     */
    public boolean addEdge(Object edge, Collection vertices, EdgeType edge_type)
    {
    	if(delegate.addEdge(edge, vertices, edge_type))
		{
			internal_addEdge(edge);
			for(Iterator it=vertices.iterator(); it.hasNext(); )
				internal_addVertex(it.next());
			return true;
		} else {
			return false;
		}
    }

	/**
	 * Adds an directed edge to the graph.
	 * @param e The edge.
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return <code>true</code> if success.
	 */
	public boolean addEdge(Object e, Object v1, Object v2)
	{
		if (delegate.addEdge(e, v1, v2) == true) {
			internal_addEdge(e);
			internal_addVertex(v1);
			internal_addVertex(v2);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds a vertex to the graph.
	 * @param vertex The vertex.
	 * @return <code>true</code> if success.
	 */
	public boolean addVertex(Object vertex)
	{
		if (delegate.addVertex(vertex) == true) {
			internal_addVertex(vertex);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Removes an edge.
	 * @param edge The edge to remove.
	 * @return <code>true</code> if success.
	 */
	public boolean removeEdge(Object edge)
	{
		internal_removeEdge(edge);
		return delegate.removeEdge(edge);
	}

	/**
	 * Removes a vertex.
	 * @param vertex The vertex.
	 * @return <code>true</code> if success.
	 */
	public boolean removeVertex(Object vertex)
	{
		internal_removeVertex(vertex);
		return delegate.removeVertex(vertex);
	}

	//-------- delegated Graph interface --------

	/** 
	 * Returns <code>true</code> if the edge is an incident of the vertex.
	 * @param vertex The vertex.
	 * @param edge The edge.
	 * @return <code>true</code> if edge is incident of the vertex.
	 * /
	public boolean areIncident(Object vertex, Object edge)
	{
		return delegate.getIncidentEdges(vertex).contains(edge);
		return delegate.areIncident(vertex, edge);
	}*/

	/** 
	 * Returns <code>true</code> if the vertices are connected.
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return <code>true</code> if the vertices are neighbors. 
	 * /
	public boolean areNeighbors(Object v1, Object v2)
	{
		return delegate.getNeighbors(v1).contains(v2);
	}*/
	
	/**
     * Returns <code>true</code> if <code>vertex</code> and <code>edge</code> 
     * are incident to each other.
     * Equivalent to <code>getIncidentEdges(vertex).contains(edge)</code> and to
     * <code>getIncidentVertices(edge).contains(vertex)</code>.
     * @param vertex
     * @param edge
     * @return <code>true</code> if <code>vertex</code> and <code>edge</code> 
     * are incident to each other
     */
    public boolean isIncident(Object vertex, Object edge)
    {
    	return delegate.isIncident(vertex, edge);
    }
	
	/**
     * Returns <code>true</code> if <code>v1</code> and <code>v2</code> share an incident edge.
     * Equivalent to <code>getNeighbors(v1).contains(v2)</code>.
     * 
     * @param v1 the first vertex to test
     * @param v2 the second vertex to test
     * @return <code>true</code> if <code>v1</code> and <code>v2</code> share an incident edge
     */
    public boolean isNeighbor(Object v1, Object v2)
    {
    	return delegate.isNeighbor(v1, v2);
    }
	
	/**
	 * Returns <code>true</code> if the graph contains the edge.
	 * @param edge The edge.
	 * @return <code>true</code> if the edge is in the graph.
	 */
	public boolean containsEdge(Object edge)
	{
		return delegate.containsEdge(edge);
	}
	/**
	 * Returns <code>true</code> if the graph contains the vertex.
	 * @param vertex The vertex.
	 * @return <code>true</code> if the vertex is in the graph.
	 */
	public boolean containsVertex(Object vertex)
	{
		return delegate.containsVertex(vertex);
	}
	/**
	 * Returns the degree (number of incident edges) of a vertex.
	 * @param vertex The vertex.
	 * @return The degree.
	 */
	public int degree(Object vertex)
	{
		return delegate.degree(vertex);
	}
	
	/**
	 * Returns the first edge between the two vertices.
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return The edge or <code>null</code> if there is no incident edge
	 * between the vertices.
	 */
	public Object findEdge(Object v1, Object v2)
	{
		return (IComponentGroup)delegate.findEdge(v1, v2);
	}
	
	/**
	 * Returns a collection of edges between the two vertices.
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return The collection of edges or <code>null</code> if there is no
	 * incident edge between the vertices.
	 */
	public Collection findEdgeSet(Object v1, Object v2)
	{
		return delegate.findEdgeSet(v1, v2);
	}
	
	/**
	 * Returns the destination of a directed edge.
	 * @param directed_edge The edge.
	 * @return The vertex.
	 */
	public Object getDest(Object directed_edge)
	{
		return delegate.getDest(directed_edge);
	}
	
	/**
	 * @return The edge count.
	 */
	public int getEdgeCount()
	{
		return delegate.getEdgeCount();
	}
	
	/**
	 * @return The edge count.
	 */
	public int getEdgeCount(EdgeType et)
	{
		return delegate.getEdgeCount(et);
	}
	
	/**
	 * Returns all edges of the graph.
	 * 
	 * @return The collection of edges.
	 */
	public Collection getEdges()
	{
		return delegate.getEdges();
	}
	
	/**
	 * Returns the edges for a given edge type.
	 * 
	 * @param edgeType The edge tzpe.
	 * @return The collection of edges.
	 */
	public Collection getEdges(EdgeType edgeType)
	{
		return delegate.getEdges(edgeType);
	}
	
	/**
	 * Returns the edge type of en edge.
	 * @param edge The edge.
	 * @return The edge type.
	 */
	public EdgeType getEdgeType(Object edge)
	{
		return delegate.getEdgeType(edge);
	}
	
	/**
	 * Returns the endpoints of the edge.
	 * @param edge The edge.
	 * @return The endpoints.
	 */
	public Pair getEndpoints(Object edge)
	{
		return delegate.getEndpoints(edge);
	}

	/** 
	 * Returns 2 if the edge has two different endpoints. If ist a loop the method returns 1.
	 * @param edge The edge.
	 * @return The incident count.
	 */
	public int getIncidentCount(Object edge)
	{
		return delegate.getIncidentCount(edge);
	}
	
	/**
	 * Returns the incident edges of the vertex.
	 * @param vertex The vertex.
	 * @return The collection of edges.
	 */
	public Collection getIncidentEdges(Object vertex)
	{
		return delegate.getIncidentEdges(vertex);
	}

	public Collection getIncidentVertices(Object edge)
	{
		return delegate.getIncidentVertices(edge);
	}
	
	/**
	 * Returns the incoming edges of the vertex.
	 * @param vertex The vertx
	 * @return The collection of edges.
	 */
	public Collection getInEdges(Object vertex)
	{
		return delegate.getInEdges(vertex);
	}

	/** 
	 * Returns the neighbor count.
	 * @param vertex The vertex.
	 * @return The count of neighbors.
	 */
	public int getNeighborCount(Object vertex)
	{
		return delegate.getNeighborCount(vertex);
	}
	
	/**
	 * Returns the neighbor vertices of a given vertex.
	 * @param vertex The vertex.
	 * @return The collection of neighbors.
	 */
	public Collection getNeighbors(Object vertex)
	{
		return delegate.getNeighbors(vertex);
	}

	/** 
	 * Returns the other endpoint of the edge.
	 * @param vertex The vertex.
	 * @param edge The edge.
	 * @return The opposit.
	 */
	public Object getOpposite(Object vertex, Object edge)
	{
		return delegate.getOpposite(vertex, edge);
	}
	
	/**
	 * Returns the outgoing edges of the vertex.
	 * @param vertex The vertx
	 * @return The collection of edges.
	 */
	public Collection getOutEdges(Object vertex)
	{
		return delegate.getOutEdges(vertex);
	}

	/**
	 * Returns the number of vertices that have an outgoing edge to the given vertex.
	 * @param vertex The vertex.
	 * @return The predecessor count.
	 */
	public int getPredecessorCount(Object vertex)
	{
		return delegate.getPredecessorCount(vertex);
	}
	
	/**
	 * Returns the vertices that have an outgoing edge to the given vertex.
	 * @param vertex The vertex.
	 * @return The collection of vertices.
	 */
	public Collection getPredecessors(Object vertex)
	{
		return delegate.getPredecessors(vertex);
	}
	
	/**
	 * Returns the source of a directed edge.
	 * @param directed_edge The edge.
	 * @return The vertex.
	 */
	public Object getSource(Object directed_edge)
	{
		return delegate.getSource(directed_edge);
	}
	
	/**
	 * Returns the number of vertices that have an incoming edge from the given vertex.
	 * @param vertex The vertex.
	 * @return The successor count.
	 */
	public int getSuccessorCount(Object vertex)
	{
		return delegate.getSuccessorCount(vertex);
	}
	
	/**
	 * Returns the vertices that have an incoming edge from the given vertex.
	 * @param vertex The vertex.
	 * @return The collection of vertices.
	 */
	public Collection getSuccessors(Object vertex)
	{
		return delegate.getSuccessors(vertex);
	}
	
	/**
	 * @return The vertex count.
	 */
	public int getVertexCount()
	{
		return delegate.getVertexCount();
	}
	
	/**
	 * Returns all vertices of the graph.
	 * @return The collection of vertices.
	 */
	public Collection getVertices()
	{
		return delegate.getVertices();
	}
	
	/** 
	 * Returns the count of incoming edges to the vertex.
	 * @param vertex The vertex.
	 * @return The incoming degree.
	 */
	public int inDegree(Object vertex)
	{
		return delegate.inDegree(vertex);
	}
	
	/**
	 * Returns <code>true</code> if the vertex is the destination of the edge.
	 * @param vertex The vertex.
	 * @param edge The edge.
	 * @return <code>true</code> if the vertex is the destination.
	 */
	public boolean isDest(Object vertex, Object edge)
	{
		return delegate.isDest(vertex, edge);
	}

	/** 
	 * Returns <code>true</code> if there is an edge from first vertex to the second vertex.
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return <code>true</code> if the first vertex is predecessor of the second vertex.
	 */
	public boolean isPredecessor(Object v1, Object v2)
	{
		return delegate.isPredecessor(v1, v2);
	}
	
	/**
	 * Returns <code>true</code> if the vertex is the source of the edge.
	 * 
	 * @param vertex The vertex.
	 * @param edge The edge.
	 * @return <code>true</code> if the vertex is the source.
	 */
	public boolean isSource(Object vertex, Object edge)
	{
		return delegate.isSource(vertex, edge);
	}
	
	/** 
	 * Returns <code>true</code> if there is an edge from second vertex to the first vertex.
	 * @param v1 The first vertex.
	 * @param v2 The second vertex.
	 * @return <code>true</code> if the second vertex is successor of the fist vertex.
	 */
	public boolean isSuccessor(Object v1, Object v2)
	{
		return delegate.isSuccessor(v1, v2);
	}

	/** 
	 * Returns the count of outgoing edges from the vertex.
	 * @param vertex The vertex.
	 * @return The outgoing degree.
	 */
	public int outDegree(Object vertex)
	{
		return delegate.outDegree(vertex);
	}

	//-------- Object methodes --------

	/**
	 * Text representation for this graph
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer("Vertices:");
		for(Iterator it=getVertices().iterator(); it.hasNext(); ) 
		{
			sb.append("(");
			for(Iterator it2=((IComponentGroup)it.next()).getElements().iterator(); it2.hasNext(); ) 
			{
				sb.append(it2.next() + ",");
			}
			sb.setLength(sb.length() - 1);
			sb.append("),");
		}
		sb.setLength(sb.length() - 1);
		sb.append("\nEdges:");
		
		for(Iterator it=getEdges().iterator(); it.hasNext(); ) 
		{
			sb.append("(");
			for(Iterator it2=((IComponentGroup)it.next()).getElements().iterator(); it2.hasNext(); ) 
			{
				Object ee = it2.next();
				Pair ep = getEndpoints(ee);
				sb.append(ee + "[" + ep.getFirst() + "," + ep.getSecond() + "]");
			}
			sb.setLength(sb.length() - 1);
			sb.append("),");
		}
		return sb.toString();
	}

	//-------- helper methods --------

	/**
	 * @param vertex The vertex to add to the internal structure
	 */
	private void internal_addVertex(Object vertex)
	{
		for(Iterator it=((IComponentGroup)vertex).getElements().iterator(); it.hasNext(); ) 
		{
			vertex_elemets.put(it.next(), vertex);
		}
		vertex_weights.replace(vertex);

	}

	/**
	 * @param vertex The vertex to remove from the internal structure
	 */
	private void internal_removeVertex(Object vertex)
	{
		if (!containsVertex(vertex)) return;

		for(Iterator it=((IComponentGroup)vertex).getElements().iterator(); it.hasNext(); ) 
		{
			vertex_elemets.remove(it.next());
		}
		vertex_weights.remove(vertex);

		// copy to avoid concurrent modification in removeEdge
		Collection incident = new ArrayList(getIncidentEdges(vertex));

		for(Iterator it=incident.iterator(); it.hasNext(); ) 
		{
			internal_removeEdge(it.next());
		}

	}

	/**
	 * @param edge The edge to add to the internal structure
	 */
	private void internal_addEdge(Object edge)
	{
		for(Iterator it=((IComponentGroup)edge).getElements().iterator(); it.hasNext(); ) 
		{
			edge_elemets.put(it.next(), edge);
		}
		edge_weights.replace(edge);
	}

	/**
	 * @param edge The edge to remove from the internal structure
	 */
	private void internal_removeEdge(Object edge)
	{
		if (!containsEdge(edge)) return;

		for(Iterator it=((IComponentGroup)edge).getElements().iterator(); it.hasNext(); ) 
		{
			edge_elemets.remove(it.next());
		}
		edge_weights.remove(edge);
	}

	/**
	 * 
	 */
	public EdgeType getDefaultEdgeType()
	{
		return null;
	}
	
	//-------- inner classes --------

	/**
	 * A generic SortedList with a replace method. That is the element is
	 * removed and then reinserted again with an possible different index if the
	 * element has changed meanwhile.
	 */
	private class GenericSortedList extends SortedList {

		public GenericSortedList(Comparator comperator) {
			super(comperator, true);
		}

		public Object getLast()
		{
			return super.getLast();
		}

		public void replace(Object t)
		{
			super.remove(t);
			super.add(t);
		}

	}
}
