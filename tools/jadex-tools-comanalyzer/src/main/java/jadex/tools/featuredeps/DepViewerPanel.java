package jadex.tools.featuredeps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import jadex.bridge.component.DependencyResolver;
import jadex.bridge.component.DependencyResolver.NodeInfo;


/**
 *  Can be used to visualize a rete network.
 */
public class DepViewerPanel<T> extends JPanel
{
	//-------- attributes --------
	
	/** The graph. */
	protected DirectedSparseGraph g;
	
	/** The viewer. */
	protected VisualizationViewer vv;
	
	protected boolean showtxt = true;
	
	protected DependencyResolver<T> dr;
	
	protected Layout layout;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rete panel.
	 *  Set steppable to null for panel without breakpoints and step mode.
	 */
	public DepViewerPanel(DependencyResolver<T> dr)
	{
		this.setLayout(new BorderLayout());
		this.g = new DirectedSparseGraph();
		this.layout = new KKLayout(g);
//		this.layout = new SpringLayout(g);
//		this.layout = new DAGLayout(g);
		this.dr = dr;
		buildGraph(g);
		
//		final NodePanel np = new NodePanel(null, mem, system.getState());
		
//		this.layout = new DAGLayout(g);
		this.vv =  new VisualizationViewer(layout, new Dimension(600,600));
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
		vv.getRenderContext().setVertexLabelTransformer(new Transformer()
		{
			public Object transform(Object arg0)
			{
				return showtxt? arg0.toString(): null;
			}
		});
//		vv.getRenderContext().setVertexStrokeTransformer(new Transformer()
//		{
//			public Object transform(Object node)
//			{
//				Collection	coll	= ((INode)node).getNodeMemory(mem);
//				return (coll==null || coll.isEmpty())
//					? new BasicStroke(1) : new BasicStroke(2);
//			}
//		});
//		vv.getRenderContext().setVertexFillPaintTransformer(new Transformer()
//		{
//			public Object transform(Object node)
//			{
//				Color	fg;
//				if(node instanceof ReteNode)
//					fg	= Color.WHITE;
//				else if(node instanceof TypeNode)
//					fg	= Color.PINK;
//				else if(node instanceof AlphaNode)
//					fg	= Color.RED;
//				else if(node instanceof LeftInputAdapterNode)
//					fg	= Color.ORANGE;
//				else if(node instanceof RightInputAdapterNode)
//					fg	= Color.ORANGE;
//				else if(node instanceof NotNode)
//					fg	= Color.YELLOW;
//				else if(node instanceof BetaNode)
//					fg	= Color.GREEN;
//				else if(node instanceof SplitNode)
//					fg	= Color.MAGENTA;
//				else if(node instanceof TerminalNode)
//					fg	= Color.CYAN;
//				else if(node instanceof TestNode)
//					fg	= new Color(0, 255, 100);
//				else if(node instanceof InitialFactNode)
//					fg	= new Color(255, 100, 0);
//				else
//					fg	= Color.GRAY;
//				
//				return fg;
////				Collection	coll	= ((INode)node).getNodeMemory(mem);
////				return (coll==null || coll.isEmpty())
////					? fg : new LinearGradientPaint(0,0, 5,5, new float[]{0.8f, 1f}, new Color[]{fg,Color.BLACK});
//			}
//		});
//		vv.getRenderContext().setEdgeDrawPaintTransformer(new Transformer()
//		{
//			public Object transform(Object edge)
//			{
//				if(edge instanceof ReteEdge)
//				{
//					if(((ReteEdge)edge).isTuple())
//					{
//						return Color.GREEN;
//					}
//					else
//					{
//						return Color.RED;
//					}
//				}
//				else
//				{
//					return Color.GRAY;
//				}
//			}
//		});
		vv.setVertexToolTipTransformer(new ToStringLabeller());
		vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
		
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);
		
//		vv.addGraphMouseListener(new GraphMouseListener()
//		{
//			public void graphClicked(Object arg0, MouseEvent arg1)
//			{
//				//System.out.println("clicked: "+arg0+" "+arg1);
//				if(arg0 instanceof INode)
//				{
//					np.setNode((INode)arg0);
//					infopanels.setSelectedComponent(getInfoPanel(NODE_DETAILS_NAME));
//				}
//			}
//
//			public void graphPressed(Object arg0, MouseEvent arg1)
//			{
////				System.out.println("pressed: "+arg0+" "+arg1);
//			}
//
//			public void graphReleased(Object arg0, MouseEvent arg1)
//			{
////				System.out.println("released: "+arg0+" "+arg1);
//			}
//		});
		
//		final JButton hidenodes = new JButton("Show Subgraph");
//		hidenodes.setMargin(new Insets(2,4,2,4));
//		hidenodes.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent ae)
//			{
//				Set subgraph	= new HashSet(vv.getPickedVertexState().getPicked());
//				hideMarkedNodes(subgraph);
//				layout.graphChanged();
//				vv.repaint();
//			}
//		});
 
		/*final JButton showdesc = new JButton("Show text");
		showdesc.setMargin(new Insets(0,0,0,0));
		showdesc.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				showtxt = !showtxt;
				vv.repaint();
				showdesc.setText(showtxt? "Hide text": "Show text");
			}
		});*/
		
		JButton refresh = new JButton("Refresh");
//		refresh.setMargin(new Insets(0,0,0,0));
		refresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				buildGraph(g);
				vv.repaint();
			}
		});
		
		this.add(refresh, BorderLayout.SOUTH);
		
		vv.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				layout.setSize(vv.getSize());
			}
		});
		
		this.add(vv, BorderLayout.CENTER);
	}
	
	/**
	 *  Build (or rebuild) the graph from the root node.
	 *  @param g The graph.
	 *  @param root The root node.
	 */
	protected void buildGraph(DirectedSparseGraph g)
	{
		Object[] edges = g.getEdges().toArray();
		for(int i=0; i<edges.length; i++)
		{
			g.removeEdge(edges[i]);
		}
		Object[] vers= g.getVertices().toArray();
		for(int i=0; i<vers.length; i++)
		{
			g.removeVertex(vers[i]);
		}
		
		List todo = new ArrayList();
		Map<T, NodeInfo<T>> nodes = dr.getNodes();

		for(Map.Entry<T, NodeInfo<T>> node: nodes.entrySet())
		{
			g.addVertex(node.getKey());
		}
		
		for(Map.Entry<T, NodeInfo<T>> node: nodes.entrySet())
		{
			for(T n: node.getValue().getMyDeps())
			{
				String edge = n+"->"+node.getKey();
				if(!g.containsEdge(edge))
				{
					g.addEdge(edge, n, node.getKey());
//					System.out.println("adding: "+edge);
				}
			}
			
			for(T n: node.getValue().getMyDeps())
			{
				String edge = node.getKey()+"->"+n;
				if(!g.containsEdge(edge))
				{
					g.addEdge(edge, n, node.getKey());
//					System.out.println("adding: "+edge);
				}
			}
		}	
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		DependencyResolver<String> dr = new DependencyResolver<String>();
		
		createFrame("DR", dr);
		
		dr.addDependency("c", "a");
		dr.addDependency("d", "b");
		dr.addDependency("e", "c");
		dr.addDependency("e", "d");
		dr.addDependency("f", "a");
		dr.addDependency("f", "b");
		dr.addDependency("g", "e");
		dr.addDependency("g", "f");
		dr.addDependency("h", "g");
		dr.addDependency("i", "a");
		dr.addDependency("j", "b");
//		dr.resolveDependencies();
	}

	/**
	 *  Create a frame for dr structure.
	 *  @param title	The title for the frame.
	 *  @param rs	The rule system.
	 *  @return	The frame.
	 */
	public static JFrame createFrame(String title, DependencyResolver dr)
	{
		JFrame f = new JFrame(title);
		f.getContentPane().setLayout(new BorderLayout());
		f.add("Center", new DepViewerPanel(dr));
		f.pack();
        f.setVisible(true);
		return f;
	}
}
