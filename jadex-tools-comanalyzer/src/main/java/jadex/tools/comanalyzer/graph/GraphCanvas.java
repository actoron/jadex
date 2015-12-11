package jadex.tools.comanalyzer.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.UIDefaults;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbsoluteCrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;
import jadex.commons.gui.SGUI;
import jadex.tools.comanalyzer.Component;
import jadex.tools.comanalyzer.Message;
import jadex.tools.comanalyzer.PaintMaps;
import jadex.tools.comanalyzer.ToolCanvas;
import jadex.tools.comanalyzer.graph.EdgeTransformer.GradientPaint;


/**
 * The container for the graph.
 */
public class GraphCanvas extends ToolCanvas
{

	// -------- constants --------

	/** Icon paths */
	private static final String COMANALYZER_IMAGES = "/jadex/tools/comanalyzer/images/";

	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]{"agent_standard_big", SGUI.makeIcon(GraphCanvas.class, COMANALYZER_IMAGES + "agent_standard_big.png"), "agent_standard_small",
			SGUI.makeIcon(GraphCanvas.class, COMANALYZER_IMAGES + "agent_standard_small.png"), "agent_dummy_big", SGUI.makeIcon(GraphCanvas.class, COMANALYZER_IMAGES + "agent_dummy_big.png"),
			"agent_dummy_small", SGUI.makeIcon(GraphCanvas.class, COMANALYZER_IMAGES + "agent_dummy_small.png"), "agent_dead_big",
			SGUI.makeIcon(GraphCanvas.class, COMANALYZER_IMAGES + "agent_dead_big.png"), "agent_dead_small", SGUI.makeIcon(GraphCanvas.class, COMANALYZER_IMAGES + "agent_dead_small.png"),});

	/** The graph mode for the directed multigraph */
	protected static final int DIRECTED_MULTIGRAPH = 0;

	/** The graph mode for the directed graph */
	protected static final int DIRECTED_GRAPH = 1;

	/** The graph mode for the undirected graph */
	protected static final int UNDIRECTED_GRAPH = 2;


	// -------- attributes --------

	/** The graph type */
	protected int graphType;

	/** adjust layout with each change */
	protected boolean autolayout;

	/** animate the layout adjustments */
	protected boolean animate;

	/** cluster the graph */
	protected boolean cluster;

	/** The list of messages displayed in the graph */
	protected List visible_messages;

	/** The list of agents displayed in the graph */
	protected List visible_agents;

	/** The graph viewer */
	protected VisualizationViewer vv;

	/** The graph. */
	protected ComponentGroupMultiGraph graph;

	/** The static layout. For animated transitions */
	protected StaticLayout staticLayout;

	/** The aggregat layout. Allows diffrent sublayouts */
	protected AggregateLayout layout;

	/** The factory for the graph */
	protected Factory graphFactory;

	/** Support for picking and transforming */
	protected DefaultModalGraphMouse gm;

	protected AbsoluteCrossoverScalingControl scaler;
	
	
	/** The transformer for vertex label */
	protected VertexTransformer.Label v_string;

	/** The transformer for vertex font */
	protected VertexTransformer.LabelFont v_font;

	/** The transformer for vertex shape */
	protected VertexTransformer.ShapeSize v_shape;

	/** The transformer for agent icon */
	protected VertexTransformer.IconSize v_icon;

	/** The transformer for edge label */
	protected EdgeTransformer.Label e_string;

	/** The transformer for edge font */
	protected EdgeTransformer.LabelFont e_font;

	/** The transformer for the edge line shape */
	protected EdgeShape.Line e_line;

	/** The transformer for the edge curve shape */
	protected EdgeShape.QuadCurve e_quad;

	/** The transformer for the edge wedge shape */
	protected EdgeShape.Wedge e_wedge;

	/** The transformer for the edge paint */
	protected EdgeTransformer.PaintMode e_paint;

	/** The advanced transformer for edge paint */
	protected EdgeTransformer.GradientPaint e_gradient;

	/** The transformer for edge stroke */
	protected EdgeTransformer.WeightStroke e_stroke;

	/**
	 * The predicate test for showing arrows in directed graphs and none in
	 * undirected.
	 */
	protected DirectionDisplayPredicate e_arrow;

	// -------- constructor --------

	/**
	 * Constructor for the container.
	 * 
	 * @param tooltab The tooltab.
	 */
	public GraphCanvas(GraphPanel tooltab)
	{
		super(tooltab);

		this.visible_agents = new ArrayList();
		this.visible_messages = new ArrayList();

		this.graphType = DIRECTED_MULTIGRAPH;
		this.autolayout = false;
		this.animate = false;

		// factory for agent groups
		final Factory vertexFactory = new Factory()
		{
			public Object create()
			{
				return new AgentGroup();
			}
		};

		// factory for message groups
		final Factory edgeFactory = new Factory()
		{
			public Object create()
			{
				return new MessageGroup();
			}
		};

		// the factory for the graph
		graphFactory = new Factory()
		{
			public Object create()
			{
				Graph delegate = MultidirectedMultiGraph.create();
				return ComponentGroupMultiGraph.createInstance(vertexFactory, edgeFactory, delegate);
			}
		};

		// create the graph, layout and and viewer
		graph = (ComponentGroupMultiGraph)graphFactory.create();
		layout = new AggregateLayout(new KKLayout(graph));
		layout.setSize(new Dimension(300, 300));
		staticLayout = new StaticLayout(graph, layout);
		vv = new VisualizationViewer(staticLayout);
		// vv.setToolTipText("<html><center>Use the mouse wheel to zoom<p>Click
		// and Drag the mouse to pan<p>Shift-click and Drag to
		// Rotate</center></html>");
		vv.setBackground(Color.white);
		// layout.setSize(vv.getSize());
		// vv.setSize(getSize());

		// create transformer for vertices
		v_string = new VertexTransformer.Label();
		v_font = new VertexTransformer.LabelFont();
		v_icon = new VertexTransformer.IconSize(graph);
		v_shape = new VertexTransformer.ShapeSize(graph, v_icon);
		v_icon.setScaling(false);

		// create transformer for edges
		e_string = new EdgeTransformer.Label();
		e_font = new EdgeTransformer.LabelFont();
		e_line = new EdgeShape.Line();
		e_quad = new EdgeShape.QuadCurve();
		e_wedge = new EdgeShape.Wedge(10);
		e_paint = new EdgeTransformer.PaintMode(tooltab.getPaintMaps(), PaintMaps.PAINTMODE_CONVERSATION);
		e_gradient = new EdgeTransformer.GradientPaint(e_paint, vv, GradientPaint.GRADIENT_RELATIVE);
		e_stroke = new EdgeTransformer.WeightStroke(graph);
		e_arrow = new DirectionDisplayPredicate(true, false);

		// set vertex transformer
		vv.getRenderContext().setVertexLabelTransformer(v_string);
		vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);
		vv.getRenderContext().setVertexFontTransformer(v_font);
		vv.getRenderContext().setVertexIconTransformer(v_icon);
		vv.getRenderContext().setVertexShapeTransformer(v_shape);
		vv.setVertexToolTipTransformer(new VertexTransformer.ToolTips());

		// set edge transformer
		vv.getRenderContext().setEdgeLabelTransformer(e_string);
		vv.getRenderContext().setEdgeFontTransformer(e_font);
		vv.getRenderContext().setEdgeShapeTransformer(e_quad);
		vv.getRenderContext().setEdgeDrawPaintTransformer(e_gradient);
		vv.getRenderContext().setEdgeStrokeTransformer(e_stroke);
		vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
		vv.getRenderContext().setArrowDrawPaintTransformer(new ConstantTransformer(Color.darkGray));
		vv.getRenderContext().setEdgeArrowPredicate(e_arrow);
		vv.setEdgeToolTipTransformer(new EdgeTransformer.ToolTips());

		// set edge index function after setEdgeShapeTransformer 
		//(the method sets the index function to default)
		e_quad.setEdgeIndexFunction(new MessageNumberIndexFunction());

		// create the graph mouse
		GraphZoomScrollPane scrollPane = new GraphZoomScrollPane(vv);
		gm = new DefaultModalGraphMouse();
		gm.setMode(Mode.PICKING);
		gm.add(new PopupGraphMousePlugin(this)); // handles mouse events
		vv.setGraphMouse(gm);
		
		scaler = new AbsoluteCrossoverScalingControl();

		this.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER, scrollPane);

	}

	// -------- ToolCanvas methods --------

	/**
	 * Update a message by adding it, if the message can be displayed or
	 * removing it if present.
	 * 
	 * @param message The message to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new messages)
	 */
	public void updateMessage(Message message, boolean isPresent)
	{
		Pair newpair = message.getEndpoints();

		if(newpair != null)
		{
			Component sender = (Component)newpair.getFirst();
			Component receiver = (Component)newpair.getSecond();
			// check if message is already displayed
			MessageGroup edge = (MessageGroup)graph.findEdge(message);
			if(edge != null)
			{
				// check if the message should be redirected
				Pair endpoints = graph.getEndpoints(edge);
				if(endpoints.getFirst().equals(sender) && endpoints.getSecond().equals(receiver))
				{
					return; // already displayed
				}
				else
				{
					// remove message, since the message is redirected
					if(isPresent)
						removeMessage(message);
				}
			}
			// now add the message with sender and receiver
			// given by displayMessage
			addMessage(message, sender, receiver);
		}
		else if(isPresent)
		{
			removeMessage(message);
		}

	}

	/**
	 * Removes a message.
	 * 
	 * @param message The message to remove.
	 */
	public void removeMessage(Message message)
	{
		graph.removeEdgeElement(message);
		visible_messages.remove(message);
	}

	/**
	 * Updates an agent by adding it, if the agent can be displayed or removing
	 * it if present.
	 * 
	 * @param agent The agent to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new agents)
	 */
	public void updateComponent(Component agent, boolean update)
	{
		if(agent.isVisible())
		{
			if(!graph.containsVertexElement(agent))
			{
				addAgent(agent);
			}
		}
		else if(update)
		{
			removeComponent(agent);
		}

	}

	/**
	 * Removes an agent.
	 * @param agent The agent to remove.
	 */
	public void removeComponent(Component agent)
	{
		graph.removeVertexElement(agent);
		visible_agents.remove(agent);
	}

	/**
	 * Clear the graph.
	 */
	public void clear()
	{
		graph.clear();
	}

	/**
	 * Repaint the graph.
	 */
	public void repaintCanvas()
	{
		StaticLayout staticLayout;

		if(autolayout)
		{
			// initialize layout
			layout.initialize();
			// for iterative layouts use visrunner (no effect for other layouts)
			Relaxer relaxer = new VisRunner((IterativeContext)layout);
			relaxer.stop();
			relaxer.prerelax();
			// assign to static layout
			staticLayout = new StaticLayout(graph, layout);

			if(!animate)
			{
				// if animator is off, set graphlayout
				vv.setGraphLayout(staticLayout);
			}
			else
			{
				// create transition from current layout to the new static one
				staticLayout.setSize(layout.getSize());
				GraphLayoutTransition lt = new GraphLayoutTransition(vv, vv.getGraphLayout(), staticLayout);

				Animator animator = new Animator(lt);
				animator.start();
			}
		}
		else
		{
			vv.repaint();
		}

	}

	// -------- GraphCanvas methods --------

	/**
	 * Repaints the canvas with reinitializes its layout.
	 */
	public void reinitializeCanvas() {
		
		StaticLayout staticLayout;		
		
		// initialize layout
		layout.initialize();
		// for iterative layouts use visrunner (no effect for other layouts)
		Relaxer relaxer = new VisRunner((IterativeContext)layout);
		relaxer.stop();
		relaxer.prerelax();
		// assign to static layout
		staticLayout = new StaticLayout(graph, layout);

		if(!animate)
		{
			// if animator is off, set graphlayout direct
			vv.setGraphLayout(staticLayout);
		}
		else
		{
			// create transition from current layout to the new static one
			staticLayout.setSize(layout.getSize());
			GraphLayoutTransition lt = new GraphLayoutTransition(vv, vv.getGraphLayout(), staticLayout);

			Animator animator = new Animator(lt);
			animator.start();
		}


		
	}
	
	/**
	 * Add message with given sender and receiver (for redirection)
	 * @param message The message to add.
	 * @param sender The sender in the presentation.
	 * @param receiver The receiver in the presentation. (e.g. dummy)
	 */
	public void addMessage(Message message, Component sender, Component receiver)
	{
		// consider different graph types
		switch(graphType)
		{
			case DIRECTED_MULTIGRAPH:
				graph.addEdgeElement(message, sender, receiver);
				break;
			case DIRECTED_GRAPH:
				graph.appendEdgeElement(message, sender, receiver);
				break;
			case UNDIRECTED_GRAPH:
				graph.appendEdgeElement(message, sender, receiver, EdgeType.UNDIRECTED);
				break;
			default:
				System.err.println("graphtype is not set");
				break;
		}

		visible_messages.add(message);
	}

	/**
	 * Adds the agent.
	 * 
	 * @param agent The agent to add.
	 */
	public void addAgent(Component agent)
	{
		graph.addVertexElement(agent);
		
		VisRunner runner = new VisRunner((IterativeContext)layout);		
		layout.initialize();
		runner.prerelax();

		visible_agents.add(agent);
	}

	/**
	 * Cluster the vertices with edge betweenness algorithm.
	 * 
	 * @param numEdgesToRemove The number of edges to remove.
	 * @param groupClusters <code>true</code> if the vertices should be
	 * clustered. <code>false</code> for cancel the grouping.
	 */
	public void clusterGraph(int numEdgesToRemove, boolean groupClusters)
	{
		if(numEdgesToRemove < 0 || numEdgesToRemove > graph.getEdgeCount())
			return;

		// remove all sublayouts
		layout.removeAll();

		GraphEdgeBetweennessClusterer clusterer = new GraphEdgeBetweennessClusterer(numEdgesToRemove); // ,edge_weight);

		Set clusterSet = (Set)clusterer.transform(graph);
//		List edges = clusterer.getEdgesRemoved();

//		int i = 0;
		// cluster each group of vertices
		for(Iterator cIt = clusterSet.iterator(); cIt.hasNext();)
		{
			Set vertices = (Set)cIt.next();
			if(groupClusters == true)
			{
				groupCluster(vertices);
			}
//			i++;
		}
	}

	/**
	 * Returns the graph type
	 * @return The graph type.
	 */
	public int getGraphType()
	{
		return graphType;
	}

	/**
	 * Sets the graph type.
	 * @param graphType The graph type to set.
	 */
	public void setGraphType(int graphType)
	{
		this.graphType = graphType;

		// remove only edges
		List egdes = new ArrayList(graph.getEdges());
		for(Iterator it = egdes.iterator(); it.hasNext();)
		{
			graph.removeEdge(it.next());
		}

		// copy visible edges and clear original
		List messages = new ArrayList(visible_messages);
		visible_messages.clear();

		// add visible items again
		for(Iterator iter = messages.iterator(); iter.hasNext();)
		{
			Message message = (Message)iter.next();
			updateMessage(message, false);
		}

		repaintCanvas();
	}

	// -------- helper methods --------

	/**
	 * Lock agent postions.
	 */
	protected void lockAgents()
	{
		// lock positions of all vertices for initializing only new vertices
		for(Iterator it = graph.getVertices().iterator(); it.hasNext();)
		{
			layout.lock(it.next(), true);
		}
	}

	/**
	 * Unlock agent positions
	 */
	protected void unlockAgents()
	{
		// unlock all vertices again
		for(Iterator it = graph.getVertices().iterator(); it.hasNext();)
		{
			layout.lock(it.next(), false);
		}
	}

	/**
	 * Groups a cluster (set) of agents into a sublayout.
	 * 
	 * @param vertices
	 */
	protected void groupCluster(Set vertices)
	{
		if(vertices.size() > 1 && vertices.size() < graph.getVertexCount())
		{
			// if(vertices.size() < layout.getGraph().getVertexCount()) {
			Point2D center = layout.transform(vertices.iterator().next());
			Graph subGraph = (Graph)graphFactory.create();
			for(Iterator it = vertices.iterator(); it.hasNext();)
			{
				subGraph.addVertex(it.next());
			}
			Layout subLayout = new GraphCircleLayout(subGraph);
			subLayout.setInitializer(vv.getGraphLayout());
			subLayout.setSize(new Dimension(40, 40));

			layout.put(subLayout, center);
		}
	}

	// -------- inner classes --------

	/**
	 * A prdicate class that shows arrows for directed edges and hides them for
	 * undirected edges.
	 * 
	 */
	protected final static class DirectionDisplayPredicate implements Predicate
	// extends AbstractGraphPredicate<V,E>
	{
		/** Show arrows for directed edges */
		protected boolean show_d;

		/** Show arrows for undirected edges (on both sides) */
		protected boolean show_u;

		/**
		 * Creates the predicate.
		 * @param show_d <code>true</code> if arrows are shown for directed edges.
		 * @param show_u <code>true</code> if arrows are shown for undirected edges.
		 */
		public DirectionDisplayPredicate(boolean show_d, boolean show_u)
		{
			this.show_d = show_d;
			this.show_u = show_u;
		}

		/**
		 * Set the predicate for directed edges.
		 * @param b <code>true</code> if arrows are shown for directed edges.
		 */
		public void showDirected(boolean b)
		{
			show_d = b;
		}

		/**
		 * Set the predicate for undirected edges.
		 * @param b <code>true</code> if arrows are shown for undirected edges.
		 */
		public void showUndirected(boolean b)
		{
			show_u = b;
		}

		/**
		 * Use the specified parameter to perform a test that returns true or
		 * false. Returns <code>true</code> if the arrow ought to be shown,
		 * else <code>false</code>.
		 */
		public boolean evaluate(Object context)
		{
			Graph graph = (Graph)((Context)context).graph;
			MessageGroup e = (MessageGroup)((Context)context).element;
			if(graph.getEdgeType(e) == EdgeType.DIRECTED && show_d)
			{
				return true;
			}
			if(graph.getEdgeType(e) == EdgeType.UNDIRECTED && show_u)
			{
				return true;
			}
			return false;
		}
	}

	/**
	 * A component group for agents. Used for agent groups in the graph.
	 */
	public class AgentGroup extends ComponentGroup
	{
		public AgentGroup()
		{

		}

		public AgentGroup(Component agent)
		{
			addElement(agent);
		}
	}

	/**
	 * A component group for messages. Used for message groups in the graph.
	 */
	public class MessageGroup extends ComponentGroup
	{

		public MessageGroup()
		{
		}

		public MessageGroup(Message message)
		{
			addElement(message);
		}
	}
}
