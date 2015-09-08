package jadex.tools.dhtgraph;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.dht.IFinger;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IDistributedKVStoreService;
import jadex.bridge.service.types.dht.IRingNodeService;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.PickableVertexPaintTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;


/**
 *  Can be used to visualize a rete network.
 * @param <V>
 */
public class DhtViewerPanel extends JPanel
{
	//-------- attributes --------

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The graph. */
	protected DirectedSparseGraph<IID, String> g;
	
	/** The viewer. */
	protected VisualizationViewer<IID, String> vv;
	
	protected boolean showtxt = true;
	
	protected CircleLayout<IID, String> layout;
	
	protected static final long	SEARCH_DELAY	= 30000;

	private static final String	TEXT_LOADING	= "Loading...";
	
	private static final String	TEXT_NOPROXY	= "No local proxy!";

	private IInternalAccess access;
	
	protected Map<IID, ProxyHolder> proxies;
	
	public class ProxyHolder
	{
		public IRingNodeService	ringNode;
		public long lastSeen;
		protected IFinger successor;
		protected List<IFinger> fingers;
		protected IFinger predecessor;
		protected IDistributedKVStoreService store;
		protected String overlayId;

		public ProxyHolder(IRingNodeService ringNode, long lastSeen) {
			super();
			this.lastSeen = lastSeen;
			this.ringNode = ringNode;
		}
		
	}
	
	private JLabel componentLabel;
	private JLabel overlayIdLabel;
	private JLabel idLabel;
	private JLabel preLabel;
	private JLabel sucLabel;
	
	private JTextField saveKeyTf;
	private JTextField saveValueTf;
	private JButton saveValueBtn;
	
	private JTextField readKeyTf;
	private JLabel readValueLabel;
	private JButton readValueBtn;
	
	private JContentsTable contentsTable;
	
	private JLabel statusTf;
	
	private JFingerTable fingerTable;

	private HighlightablePaintTransformer<IID> paintTrans;
	

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
		this.layout = new CircleLayout<IID, String>(g);
		this.access = agent;
		startSearch();
		
		buildGraph(g);
		
		
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
		
		paintTrans = new HighlightablePaintTransformer<IID>(vv.getRenderContext().getVertexFillPaintTransformer());
		vv.getRenderContext().setVertexFillPaintTransformer(paintTrans);
		
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
		
		
		this.add(new JPanel() {{
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridy = 0;
			
			c.gridx = 0;
			
			final JButton refreshNodes = new JButton("Refresh Nodes");
			
			refreshNodes.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					startSearch();
				}
			});
			add(refreshNodes, c);
			
			c.gridx = 1;
			
			final JButton refresh = new JButton("Refresh connections");
			
			refresh.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent ae)
				{
					refresh();
				}
			});
			
			add(refresh, c);
			
			c.gridy = 1;
			
			add(new JLabel("Status: ready") {{ 
				statusTf = this; 
			}},c);
			
		}}, BorderLayout.SOUTH);
		
		vv.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				layout.setSize(vv.getSize());
				layout.reset();
				
			}
		});
		
		this.add(vv, BorderLayout.CENTER);
		
		JPanel detailsPanel = new JPanel() {{
			setLayout(new BorderLayout());
			
			add(new JPanel() {{
				setLayout(new GridBagLayout());
				GridBagConstraints c = new GridBagConstraints();
				
				c.fill = GridBagConstraints.HORIZONTAL;
				c.gridx = 0;
				c.gridy = 0;
				c.gridwidth = 2;
				
				add(new JLabel("comptext") {{ componentLabel = this; }}, c);
				
				c.gridwidth = 1;
				c.gridy++;
				c.gridx = 0;
				add(new JLabel("overlay: "),c);
				c.gridx = 1;
				add(new JLabel("overlaytext") {{ overlayIdLabel = this; }},c);
				
				c.gridwidth = 1;
				c.gridy++;
				c.gridx = 0;
				add(new JLabel("ID: "),c);
				c.gridx = 1;
				add(new JLabel("idtext") {{ idLabel = this; }},c);
				
				c.gridy++;
				c.gridx = 0;
				add(new JLabel("Predecessor: "),c);
				c.gridx = 1;
				add(new JLabel("pretext") {{ preLabel = this; }},c);
				
				c.gridy++;
				c.gridx = 0;
				add(new JLabel("Successor: "),c);
				c.gridx = 1;
				add(new JLabel("suctext") {{ sucLabel = this; }},c);
				
				
				c.gridy++;
				c.gridx = 0;
				c.gridwidth = 2;
				
				c.fill = GridBagConstraints.HORIZONTAL;
				
				add(new JLabel("Fingertable (index|start|nodeId)") ,c);
				c.gridy++;
				
				add(new JFingerTable() 
				{{
					fingerTable = this;
				}}, c);
				
//				add(new JScrollPane(new JFingerTable() 
//					{{
//						fingerTable = this;
//					}}) 
//				{{
//					setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//				}},c);
					
			}}, BorderLayout.NORTH);
				
			
			
//			add(new JPanel() {{
//				setLayout(new BorderLayout());
			
			add(new JPanel() {{
//				setLayout(new GridLayout(2, 1));
				setLayout(new BorderLayout());
//				GridBagConstraints c = new GridBagConstraints();
//				c.gridy = 0;
//				c.fill = GridBagConstraints.HORIZONTAL;
				add(new JLabel("Contents:"), BorderLayout.NORTH);
//				c.gridy = 1;
//				add(new JTextArea() {{
//					contentsTa = this;
//					setEditable(false);
//				}}, BorderLayout.CENTER);
				add(new JScrollPane(new JContentsTable() {{
					contentsTable = this;
				}}), BorderLayout.CENTER);
			}}, BorderLayout.CENTER);
			
			add(new JPanel() {{ 
				
				setLayout(new GridLayout(8, 2));
				
				add(new JLabel("Save Value"));
				add(new JLabel());
				
				add(new JLabel("Key: "));
				add(new JTextField("") {{ saveKeyTf = this; }});
				
				add(new JLabel("Value: "));
				add(new JTextField("") {{ saveValueTf = this; }} );
				
				add(new JLabel(""));
				add(new JButton("Save") {{ saveValueBtn = this; }});
				
				add(new JLabel("Read Value"));
				add(new JLabel());
				
				add(new JLabel("Key: "));
				add(new JTextField("") {{ readKeyTf = this; }});
				
				add(new JLabel("Value: "));
				add(new JLabel("") {{ 
					readValueLabel = this; 
				}});
				
				add(new JLabel(""));
				add(new JButton("Read") {{ readValueBtn = this; }});
			}}, BorderLayout.SOUTH);
			
		}};
		this.add(detailsPanel, BorderLayout.LINE_END);
		
		
		saveValueBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				save(saveKeyTf.getText(), saveValueTf.getText());
			}
		});
		
		readValueBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				read(readKeyTf.getText());
			}
		});
	}
	protected void read(final String key) {
		if (key != null && !key.isEmpty()) {
			statusTf.setText("Status: Trying to read by calling local store service");
			
			access.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("storage").addResultListener(new DefaultResultListener<Object>() {

				@Override
				public void resultAvailable(Object result) {
					final IDistributedKVStoreService storage = (IDistributedKVStoreService) result;
					
					storage.lookupResponsibleStore(key).addResultListener(new DefaultResultListener<IID>() {

						@SuppressWarnings("unchecked")
						@Override
						public void resultAvailable(final IID respId) {
							
							IFuture< Object > lookup = (IFuture<Object>)storage.lookup(key);
							lookup.addResultListener(new DefaultResultListener<Object>() {
								
								@Override
								public void resultAvailable(Object result) {
									String labelText = String.format("<html><div style=\"width:%dpx;\">%s</div><html>", 150, toListString(result));
									readValueLabel.setText(labelText);
									readValueLabel.setToolTipText(labelText);
									statusTf.setText("Status: Read successful from node: " + respId);
									blink(respId);
								}
								
								@Override
								public void exceptionOccurred(Exception exception) {
									super.exceptionOccurred(exception);
									statusTf.setText("Status: Read unsuccessful, couldn't read stored value!");
								}
								
							});
						}
						
						@Override
						public void exceptionOccurred(Exception exception) {
							super.exceptionOccurred(exception);
							statusTf.setText("Status: Read unsuccessful, couldn't find responsible node!");
						}
					});
					
				}
				
				@Override
				public void exceptionOccurred(Exception exception) {
					super.exceptionOccurred(exception);
					statusTf.setText("Status: Read unsuccessful, couldn't find store service!");
				}
			});
		}
	}

	protected void save(final String key, final String value) {
		if (key != null && value != null && !key.isEmpty() && !value.isEmpty()) {
			
			Entry<IID, ProxyHolder> next = proxies.entrySet().iterator().next();
			final IService service = (IService) next.getValue().ringNode;
			
			statusTf.setText("Status: Trying to save by calling local store service");
			
			access.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("storage").addResultListener(new DefaultResultListener<Object>() {

				@Override
				public void resultAvailable(Object result) {
					IDistributedKVStoreService storage = (IDistributedKVStoreService) result;
					statusTf.setText("Status: Found corresponding store service: " + result);
					storage.put(key, value).addResultListener(new DefaultResultListener<IID>() {

						@Override
						public void resultAvailable(IID result) {
							statusTf.setText("Status: Stored successfully in node: " + result);
							blink(result);
						}
						
						@Override
						public void exceptionOccurred(Exception exception) {
							super.exceptionOccurred(exception);
							statusTf.setText("Status: Save unsuccessful, couldn't store value!");
						}

					});
				}
				
				@Override
				public void exceptionOccurred(Exception exception) {
					super.exceptionOccurred(exception);
					statusTf.setText("Status: Save unsuccessful, couldn't find store service!");
				}
			});
		}
	}
	
	private void refresh()
	{
		refreshConnections().get();
		buildGraph(g);
		layout.reset();
		vv.repaint();
	}
	
	private void blink(IID result) {
		if (g.containsVertex(result)) {
			PickedState<IID> pickedVertexState = vv.getPickedVertexState();
			paintTrans.blinkVertex(result);
		}
		
	}

	protected void updateInfoPanel(final IID id) {
		new Thread() {
			@Override
			public void run()
			{
				final ProxyHolder proxyHolder = proxies.get(id);
				
				SwingUtilities.invokeLater(new Runnable()
				{
					
					@Override
					public void run()
					{
						idLabel.setText(""+id);
						sucLabel.setText(TEXT_LOADING);
						componentLabel.setText(TEXT_LOADING);
						overlayIdLabel.setText(TEXT_LOADING);
						preLabel.setText(TEXT_LOADING);
						
						fingerTable.setSortedFingers(new ArrayList<IFinger>());
						contentsTable.setSortedContent(new ArrayList<Tuple2<String,Object>>());
					}
				});
				
				if (proxyHolder != null) {
					final String sucText = proxyHolder.successor == null ? "-" : proxyHolder.successor.getNodeId().toString();
					final String preText = proxyHolder.predecessor == null ? "-" : proxyHolder.predecessor.getNodeId().toString();
						
						
					Set<String> set = proxyHolder.store.getLocalKeySet().get();
					ArrayList<String> keyList = new ArrayList<String>(set);
					Collections.sort(keyList);
					
					final ArrayList<Tuple2<String, Object>> tuples = new ArrayList<Tuple2<String,Object>>();
					
					for (String key : keyList) {
						Object value = proxyHolder.store.lookup(key).get();
						Tuple2<String, Object> tup = new Tuple2<String,Object>(key, value);
						tuples.add(tup);
					}
					
					SwingUtilities.invokeLater(new Runnable()
					{
						
						@Override
						public void run()
						{
							sucLabel.setText(sucText);
							componentLabel.setText(""+proxyHolder.ringNode);
							overlayIdLabel.setText(proxyHolder.overlayId);
							preLabel.setText(preText);
							fingerTable.setSortedFingers(proxyHolder.fingers);
							contentsTable.setSortedContent(tuples);
						}
					});
				} else {
					SwingUtilities.invokeLater(new Runnable()
					{
						

						@Override
						public void run()
						{
							sucLabel.setText(TEXT_NOPROXY);
							componentLabel.setText(TEXT_NOPROXY);
							overlayIdLabel.setText(TEXT_NOPROXY);
							preLabel.setText(TEXT_NOPROXY);
							
							fingerTable.setSortedFingers(new ArrayList<IFinger>());
							contentsTable.setSortedContent(new ArrayList<Tuple2<String,Object>>());
						}
					});
				}
			}
		}.start();
	}

	
	private void startSearch() {
		final IComponentStep<Void> step = new IComponentStep<Void>()
		{

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final IComponentStep<Void> myStep = this;
				statusTf.setText("Looking up nodes...");
				IRequiredServicesFeature componentFeature = access.getComponentFeature(IRequiredServicesFeature.class);
				ITerminableIntermediateFuture<Object> requiredServices = componentFeature.getRequiredServices("ringnodes");
				
				requiredServices.addResultListener(new IntermediateDefaultResultListener<Object>()
				{
					
					
					@Override
					public void intermediateResultAvailable(Object result)
					{
						final IRingNodeService other = (IRingNodeService)result;
						final IService service = (IService)result;
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
									final IComponentIdentifier cid = service.getServiceIdentifier().getProviderId();
									final ProxyHolder newProxyHolder = new ProxyHolder(other, System.currentTimeMillis());
									newProxyHolder.overlayId = other.getOverlayId();
									proxyHolder = newProxyHolder;
									
									IFuture<IDistributedKVStoreService> storeFut = access.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<IDistributedKVStoreService>() {

										@Override
										public IFuture<IDistributedKVStoreService> execute(
												IInternalAccess ia) {
											final Future<IDistributedKVStoreService> fut = new Future<IDistributedKVStoreService>();
											
											IComponentManagementService cms = (IComponentManagementService) access.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get();
											
//											IComponentManagementService cms = SServiceProvider.getService(access, IComponentManagementService.class).get();
											
//											IComponentManagementService cms = SServiceProvider.getDeclaredService(access, IComponentManagementService.class).get();
											cms.getExternalAccess(cid).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, IDistributedKVStoreService>(fut) {
												@Override
												public void customResultAvailable(
														IExternalAccess result) throws Exception {
													SServiceProvider.getService(result, IDistributedKVStoreService.class).addResultListener(new DelegationResultListener<IDistributedKVStoreService>(fut));
													super.customResultAvailable(result);
												}
											});
											return fut;
										}
									});
									
									storeFut.addResultListener(new ComponentResultListener<IDistributedKVStoreService>(new DefaultResultListener<IDistributedKVStoreService>() {

										@Override
										public void resultAvailable(
												IDistributedKVStoreService result) {
											newProxyHolder.store = result;
											proxies.put(other.getId().get(), newProxyHolder);
											
											buildGraph(g);
											layout.reset();
											vv.repaint();
										}
										
										public void exceptionOccurred(Exception exception) {
//											super.exceptionOccurred(exception);
											IID iid = other.getId().get();
											System.out.println("Didn't find Store Service on node: " + iid);
											proxies.put(iid, newProxyHolder);
											
											buildGraph(g);
											layout.reset();
											vv.repaint();
										};
									}, access));
									
								}
							}
						});
					}
					
					@Override
					public void finished()
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							
							@Override
							public void run()
							{
								statusTf.setText("Lookup finished.");
							}
						});
						refresh();
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
	protected void buildGraph(DirectedSparseGraph<IID, String> g)
	{
		String[] edges = g.getEdges().toArray(new String[g.getEdgeCount()]);
		for(int i=0; i<edges.length; i++)
		{
			g.removeEdge(edges[i]);
		}
		IID[] vers= g.getVertices().toArray(new IID[g.getVertexCount()]);
		for(int i=0; i<vers.length; i++)
		{
			g.removeVertex(vers[i]);
		}
		
		Set<Entry<IID,ProxyHolder>> entrySet = proxies.entrySet();
		
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
		
		List<IID> keyList = new ArrayList<IID>();
		keyList.addAll(g.getVertices());
		Collections.sort(keyList, new Comparator<IID>() {

			@Override
			public int compare(IID o1, IID o2) {
				return o1.compareTo(o2);
			}
		});
		
		layout.setVertexOrder(keyList);
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		createFrame(null);
	}
	
	
	private IFuture<Void> refreshConnections() {
		
		statusTf.setText("Retrieving successors...");
		
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
		future.addResultListener(new DefaultResultListener<Void>()
		{

			@Override
			public void resultAvailable(Void result)
			{
				statusTf.setText("Ready.");				
			}
		});
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
				System.out.println("Removing proxy: " + id);
				proxies.remove(id);
				g.removeVertex(id);
				vv.repaint();
			}
		}
		
	}
	
	public class HighlightablePaintTransformer<T> implements
	Transformer<IID, Paint> {

		private Transformer<IID, Paint> delegate;

		private IID blinkVertex;

		public HighlightablePaintTransformer(Transformer<IID, Paint> delegate) {
			this.delegate = delegate;
		}
		
		@Override
		public Paint transform(IID paramI) {
			if (paramI.equals(blinkVertex)) {
				return Color.green;
			} else {
				return delegate.transform(paramI);
			}
		}
		
		public void blinkVertex(final IID vertex) {
			this.blinkVertex = vertex;
			ActionListener timerlistener = new ActionListener() {
				int repeats = 0;
				@Override
				public void actionPerformed(ActionEvent e) {
					if (blinkVertex == null) {
						blinkVertex = vertex;
					} else {
						blinkVertex = null;
					}
					repeats++;
					if (repeats > 5) {
						((Timer) e.getSource()).stop();
						blinkVertex = null;
					}
					vv.repaint();
				}
			};
			Timer timer = new Timer(350, timerlistener);
			timer.setRepeats(true);
			timer.start();
			vv.repaint();
		}

	}
	
	public static String toListString(Object value)
	{
		final StringBuilder display = new StringBuilder();
		if (value instanceof Collection) {
			Collection col = (Collection)value;
//	            	display.append("Collection:\n");
			for(Object object : col)
			{
				display.append(String.valueOf(object) + "\n");
			}
		} else {
			display.append(value);
		}
		return display.toString();
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
