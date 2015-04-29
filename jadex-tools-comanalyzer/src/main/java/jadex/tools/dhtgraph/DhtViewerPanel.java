package jadex.tools.dhtgraph;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.DependencyResolver;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.tools.comanalyzer.graph.GraphCircleLayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;


/**
 *  Can be used to visualize a rete network.
 * @param <V>
 */
public class DhtViewerPanel extends JPanel
{
	//-------- attributes --------
	
	/** The graph. */
	protected DirectedSparseGraph<IID, String> g;
	
	/** The viewer. */
	protected VisualizationViewer<IID, String> vv;
	
	protected boolean showtxt = true;
	
	protected Layout<IID, String> layout;
	
	protected static final long	SEARCH_DELAY	= 30000;

	private IInternalAccess access;
	
	protected Map<IID, ProxyHolder> proxies;
	
	public class ProxyHolder
	{
		public IRingNode	ringNode;
//		public ISpaceObject spaceObject;
		public long lastSeen;
protected IFinger successor;
protected List<IFinger> fingers;
protected IFinger predecessor;
		public ProxyHolder(IRingNode ringNode, long lastSeen)
		{
			super();
//			this.spaceObject = proxyAgentCid;
			this.lastSeen = lastSeen;
			this.ringNode = ringNode;
		}
		
	}
	
	private JLabel idLabel;
	private JLabel preLabel;
	private JLabel sucLabel;
	private List<JLabel> fingerStartLabels = new ArrayList<JLabel>();
	private List<JLabel> fingerNodeLabels = new ArrayList<JLabel>();
	
	//-------- constructors --------
	
	/**
	 *  Create a new rete panel.
	 *  Set steppable to null for panel without breakpoints and step mode.
	 */
	public DhtViewerPanel(IInternalAccess agent)
	{
		proxies = new HashMap<IID, ProxyHolder>();
		this.setLayout(new BorderLayout());
		this.g = new DirectedSparseGraph<IID, String>();
//		this.layout = new KKLayout(g);
		this.layout = new GraphCircleLayout(g);
//		this.layout = new SpringLayout(g);
//		this.layout = new DAGLayout(g);
		this.access = agent;
		
		startSearch();
		
		buildGraph(g);
		
//		final NodePanel np = new NodePanel(null, mem, system.getState());
		
//		this.layout = new DAGLayout(g);
		this.vv =  new VisualizationViewer<IID, String>(layout, new Dimension(600,600));
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
		
		vv.addGraphMouseListener(new GraphMouseListener<IID>() {

			@Override
			public void graphClicked(IID arg0, MouseEvent arg1) {
				updateInfoPanel(arg0);
			}

			@Override
			public void graphPressed(IID arg0, MouseEvent arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void graphReleased(IID arg0, MouseEvent arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
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
				refreshConnections().get();
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
		
		JPanel detailsPanel = new JPanel() {{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(new JPanel() {{
				setLayout(new FlowLayout());
			
				add(new JPanel() {{
					
					setLayout(new GridLayout(4, 2));
					
					add(new JLabel("ID: "));
					add(new JLabel("idtext") {{ idLabel = this; }});
					
					add(new JLabel("Predecessor: "));
					add(new JLabel("pretext") {{ preLabel = this; }});
					
					add(new JLabel("Successor: "));
					add(new JLabel("suctext") {{ sucLabel = this; }});
					
				}});
			}});
			add(new JLabel("Fingers:"));
			
			add(new JPanel() {{
				setLayout(new FlowLayout());
			
				add(new JPanel() {{ 
					
					setLayout(new GridLayout(9, 3));
					
					add(new JLabel(""));
					add(new JLabel("start "));
					add(new JLabel("node"));
					
					add(new JLabel("0"));
					add(new JLabel("-") {{fingerStartLabels.add(this);}});
					add(new JLabel("-") {{fingerNodeLabels.add(this);}});
					
					add(new JLabel("1"));
					add(new JLabel("-") {{fingerStartLabels.add(this);}});
					add(new JLabel("-") {{fingerNodeLabels.add(this);}});
					
					add(new JLabel("2"));
					add(new JLabel("-") {{fingerStartLabels.add(this);}});
					add(new JLabel("-") {{fingerNodeLabels.add(this);}});
					
					add(new JLabel("3"));
					add(new JLabel("-") {{fingerStartLabels.add(this);}});
					add(new JLabel("-") {{fingerNodeLabels.add(this);}});
					
					add(new JLabel("4"));
					add(new JLabel("-") {{fingerStartLabels.add(this);}});
					add(new JLabel("-") {{fingerNodeLabels.add(this);}});
					
					add(new JLabel("5"));
					add(new JLabel("-") {{fingerStartLabels.add(this);}});
					add(new JLabel("-") {{fingerNodeLabels.add(this);}});
					
					add(new JLabel("6"));
					add(new JLabel("-") {{fingerStartLabels.add(this);}});
					add(new JLabel("-") {{fingerNodeLabels.add(this);}});
					
					add(new JLabel("7"));
					add(new JLabel("-") {{fingerStartLabels.add(this);}});
					add(new JLabel("-") {{fingerNodeLabels.add(this);}});
				}});
			}});
			
//			add(new JLabel("ID"));
//			add(new JLabel("ID"));
		}};
		this.add(detailsPanel, BorderLayout.LINE_END);
	}
	protected void updateInfoPanel(IID id) {
		ProxyHolder proxyHolder = proxies.get(id);
		if (proxyHolder != null) {
			String sucText = proxyHolder.successor == null ? "-" : proxyHolder.successor.getNodeId().toString();
			sucLabel.setText(sucText);
			idLabel.setText(""+id);
			String preText = proxyHolder.predecessor == null ? "-" : proxyHolder.predecessor.getNodeId().toString();
			preLabel.setText(preText);
			
			for (int i = 0; i < proxyHolder.fingers.size(); i++) {
				JLabel startLabel = fingerStartLabels.get(i);
				JLabel nodeIdLabel = fingerNodeLabels.get(i);
				
				if (startLabel != null && nodeIdLabel != null) {
					IFinger f = proxyHolder.fingers.get(i);
					startLabel.setText("" + f.getStart());
					nodeIdLabel.setText("" + f.getNodeId());
				} else {
					throw new RuntimeException("Too many finger entries: " + proxyHolder.fingers.size() +", i only support " + i);
				}
			}
		}
	}

	
	private void startSearch() {
		final IComponentStep<Void> step = new IComponentStep<Void>()
		{

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> myStep = this;
				System.out.println("proxy creator searching");
				IRequiredServicesFeature componentFeature = access.getComponentFeature(IRequiredServicesFeature.class);
				ITerminableIntermediateFuture<Object> requiredServices = componentFeature.getRequiredServices("ringnodes");
				
				requiredServices.addResultListener(new IntermediateDefaultResultListener<Object>()
				{
					
					
					@Override
					public void intermediateResultAvailable(Object result)
					{
						final IRingNode other = (IRingNode)result;
//								final IComponentIdentifier cid = ((IService)result).getServiceIdentifier().getProviderId();
						other.getId().addResultListener(new DefaultResultListener<IID>()
						{

							@Override
							public void resultAvailable(IID id)
							{
								ProxyHolder proxyHolder = proxies.get(id);
								System.out.println("found node: " + id);
								if (proxyHolder != null) {
									proxyHolder.lastSeen = System.currentTimeMillis();
								} else {
									proxyHolder = new ProxyHolder(other, System.currentTimeMillis());
									proxies.put(other.getId().get(), proxyHolder);
//									positionObjects();
								}
							}
						});
					}
					
					@Override
					public void finished()
					{
//						positionObjects().addResultListener(new DefaultResultListener<Void>()
//						{
//							@Override
//							public void resultAvailable(Void result)
//							{
//							}
//						});
						refreshConnections().get();
						buildGraph(g);
						vv.repaint();
						access.getExternalAccess().scheduleStep(myStep, SEARCH_DELAY);
						super.finished();
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("Error: ");
						exception.printStackTrace();
						super.exceptionOccurred(exception);
					}
				});
			return Future.DONE;
			}
		};
		
		if (access != null) {
			
			access.getExternalAccess().scheduleStep(step).addResultListener(new DefaultResultListener<Void>()
			{
	
				@Override
				public void resultAvailable(Void result)
				{
					access.getExternalAccess().scheduleStep(step, SEARCH_DELAY);
				}
			});
		}
				
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
		
		Set<Entry<IID,ProxyHolder>> entrySet = proxies.entrySet();
		
//		Map<T, NodeInfo<T>> nodes = dr.getNodes();

		for (Entry<IID, ProxyHolder> entry : entrySet) {
			IID key = entry.getKey();
			g.addVertex(key);
		}
		
		for (Entry<IID, ProxyHolder> entry : entrySet) {
			IID key = entry.getKey();
			ProxyHolder value = entry.getValue();
			if (value.successor != null) {
				String edgeString = key + "->" + value.successor.getNodeId();
				if (!g.containsEdge(edgeString)) {
					g.addEdge(edgeString, key, value.successor.getNodeId());
				}
			}
		}
		
//		for(Map.Entry<T, NodeInfo<T>> node: nodes.entrySet())
//		{
//			g.addVertex(node.getKey());
//		}
		
//		for(Map.Entry<T, NodeInfo<T>> node: nodes.entrySet())
//		{
//			for(T n: node.getValue().getMyDeps())
//			{
//				String edge = n+"->"+node.getKey();
//				if(!g.containsEdge(edge))
//				{
//					g.addEdge(edge, n, node.getKey());
////					System.out.println("adding: "+edge);
//				}
//			}
//			
//			for(T n: node.getValue().getMyDeps())
//			{
//				String edge = node.getKey()+"->"+n;
//				if(!g.containsEdge(edge))
//				{
//					g.addEdge(edge, n, node.getKey());
////					System.out.println("adding: "+edge);
//				}
//			}
//		}	
		
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		createFrame(null);
	}
	
	
	private IFuture<Void> refreshConnections() {
		Future<Void> future = new Future<Void>();
		
		Set<Entry<IID, ProxyHolder>> entrySet = proxies.entrySet();
		
		final CounterResultListener<Void> counter = new CounterResultListener<Void>(entrySet.size()*3, new DelegationResultListener<Void>(future));
		
		System.out.println("refreshing connections...");
		Set<IID> keySet = proxies.keySet();
		
		for(Entry<IID, ProxyHolder> entry : entrySet)
		{
			final IID id = entry.getKey();
			final ProxyHolder holder = entry.getValue();
			
			if (holder.lastSeen < System.currentTimeMillis() - 3*SEARCH_DELAY) {
				System.out.println("should be removed: " + id);
			}
			
			holder.ringNode.getSuccessor().addResultListener(new MyResultListener<IFinger>(id)
			{
				public void resultAvailable(IFinger result)
				{
					holder.successor = result;
					counter.resultAvailable(null);
				}
				
				@Override
				public void exceptionOccurred(Exception exception) {
					super.exceptionOccurred(exception);
					counter.resultAvailable(null);
				}

			});
			
			holder.ringNode.getPredecessor().addResultListener(new MyResultListener<IFinger>(id)
			{
				public void resultAvailable(IFinger result)
				{
					holder.predecessor = result;
					counter.resultAvailable(null);
				}
				
				@Override
				public void exceptionOccurred(Exception exception) {
					super.exceptionOccurred(exception);
					counter.resultAvailable(null);
				}
			});
			
			holder.ringNode.getFingers().addResultListener(new MyResultListener<List<IFinger>>(id)
			{
				public void resultAvailable(List<IFinger> result)
				{
					holder.fingers = result;
					counter.resultAvailable(null);
				}
				
				@Override
				public void exceptionOccurred(Exception exception) {
					super.exceptionOccurred(exception);
					counter.resultAvailable(null);
				}
			});
		}
		return future;
	}
	
	abstract class MyResultListener<T> implements IResultListener<T> {

		private IID	id;

		public MyResultListener(IID id)
		{
			this.id = id;
		}
		
		@Override
		public void exceptionOccurred(Exception exception)
		{
			final ProxyHolder proxyHolder = proxies.get(id);
			if (proxyHolder != null) {
				System.out.println("Removing proxy: " + proxyHolder);
				proxies.remove(proxyHolder);
			}
		}
		
	}

	/**
	 *  Create a frame for dr structure.
	 *  @param title	The title for the frame.
	 *  @param rs	The rule system.
	 *  @return	The frame.
	 */
	public static JFrame createFrame(IInternalAccess agent)
	{
		JFrame f = new JFrame("DHT Ring");
		f.getContentPane().setLayout(new BorderLayout());
		f.add("Center", new DhtViewerPanel(agent));
		f.pack();
        f.setVisible(true);
		return f;
	}
}
