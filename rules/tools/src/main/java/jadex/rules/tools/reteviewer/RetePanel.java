package jadex.rules.tools.reteviewer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import jadex.commons.ChangeEvent;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IChangeListener;
import jadex.commons.ICommand;
import jadex.commons.ISteppable;
import jadex.rules.rulesystem.Activation;
import jadex.rules.rulesystem.IAgendaListener;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rete.RetePatternMatcherState;
import jadex.rules.rulesystem.rete.nodes.AlphaNode;
import jadex.rules.rulesystem.rete.nodes.BetaNode;
import jadex.rules.rulesystem.rete.nodes.INode;
import jadex.rules.rulesystem.rete.nodes.IObjectConsumerNode;
import jadex.rules.rulesystem.rete.nodes.IObjectSourceNode;
import jadex.rules.rulesystem.rete.nodes.ITupleConsumerNode;
import jadex.rules.rulesystem.rete.nodes.ITupleSourceNode;
import jadex.rules.rulesystem.rete.nodes.InitialFactNode;
import jadex.rules.rulesystem.rete.nodes.LeftInputAdapterNode;
import jadex.rules.rulesystem.rete.nodes.NotNode;
import jadex.rules.rulesystem.rete.nodes.ReteMemory;
import jadex.rules.rulesystem.rete.nodes.ReteNode;
import jadex.rules.rulesystem.rete.nodes.RightInputAdapterNode;
import jadex.rules.rulesystem.rete.nodes.SplitNode;
import jadex.rules.rulesystem.rete.nodes.TerminalNode;
import jadex.rules.rulesystem.rete.nodes.TestNode;
import jadex.rules.rulesystem.rete.nodes.TypeNode;


/**
 *  Can be used to visualize a rete network.
 */
public class RetePanel extends JPanel
{
	//-------- constants --------

	/** The name of the node details panel. */
	public static final String	NODE_DETAILS_NAME	= "Node Details";
	
	/** The name of the agenda panel. */
	public static final String	AGENDA_NAME	= "Agenda";
	
	//-------- attributes --------
	
	/** Flag if node text should be shown. */
	protected boolean showtxt;
	
	/** The rulebase panel. */
	protected IBreakpointPanel rulebasepanel;

	/** The agenda panel. */
	protected AgendaPanel ap;

	/** The info panels on the right hand side. */
	protected JTabbedPane infopanels;
	
	/** The currently removed nodes and edges. */
	protected List	remnodes;
	protected List	remedges;
	
	/** The graph. */
	protected DirectedSparseGraph g;
	
	/** The viewer. */
	protected VisualizationViewer vv;
	
	/** The layout. */
	protected ReteLayout	layout;
	
	/** The agenda listener. */
	protected IAgendaListener	agendalistener;
	
	/** The rule system. */
	protected RuleSystem	system;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rete panel.
	 *  Set steppable to null for panel without breakpoints and step mode.
	 */
	public RetePanel(final RuleSystem system, final ISteppable steppable, final IBreakpointPanel rulebasepanel)
	{
		this.rulebasepanel = rulebasepanel;
		this.infopanels = new JTabbedPane();
		this.system	= system;
		
		final ReteNode root = ((RetePatternMatcherFunctionality)system.getMatcherFunctionality()).getReteNode();
		final ReteMemory mem = ((RetePatternMatcherState)system.getMatcherState()).getReteMemory();
		this.g = new DirectedSparseGraph();
		buildGraph(g, root);
		hideMarkedNodes(Collections.EMPTY_SET);
		
		final NodePanel np = new NodePanel(null, mem, system.getState());
		
		this.layout = new ReteLayout(g);
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
		vv.getRenderContext().setVertexStrokeTransformer(new Transformer()
		{
			public Object transform(Object node)
			{
				Collection	coll	= ((INode)node).getNodeMemory(mem);
				return (coll==null || coll.isEmpty())
					? new BasicStroke(1) : new BasicStroke(2);
			}
		});
		vv.getRenderContext().setVertexFillPaintTransformer(new Transformer()
		{
			public Object transform(Object node)
			{
				Color	fg;
				if(node instanceof ReteNode)
					fg	= Color.WHITE;
				else if(node instanceof TypeNode)
					fg	= Color.PINK;
				else if(node instanceof AlphaNode)
					fg	= Color.RED;
				else if(node instanceof LeftInputAdapterNode)
					fg	= Color.ORANGE;
				else if(node instanceof RightInputAdapterNode)
					fg	= Color.ORANGE;
				else if(node instanceof NotNode)
					fg	= Color.YELLOW;
				else if(node instanceof BetaNode)
					fg	= Color.GREEN;
				else if(node instanceof SplitNode)
					fg	= Color.MAGENTA;
				else if(node instanceof TerminalNode)
					fg	= Color.CYAN;
				else if(node instanceof TestNode)
					fg	= new Color(0, 255, 100);
				else if(node instanceof InitialFactNode)
					fg	= new Color(255, 100, 0);
				else
					fg	= Color.GRAY;
				
				return fg;
//				Collection	coll	= ((INode)node).getNodeMemory(mem);
//				return (coll==null || coll.isEmpty())
//					? fg : new LinearGradientPaint(0,0, 5,5, new float[]{0.8f, 1f}, new Color[]{fg,Color.BLACK});
			}
		});
		vv.getRenderContext().setEdgeDrawPaintTransformer(new Transformer()
		{
			public Object transform(Object edge)
			{
				if(edge instanceof ReteEdge)
				{
					if(((ReteEdge)edge).isTuple())
					{
						return Color.GREEN;
					}
					else
					{
						return Color.RED;
					}
				}
				else
				{
					return Color.GRAY;
				}
			}
		});
		vv.setVertexToolTipTransformer(new ToStringLabeller());
		vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
		
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		gm.setMode(ModalGraphMouse.Mode.PICKING);
		vv.setGraphMouse(gm);
		
		vv.addGraphMouseListener(new GraphMouseListener()
		{
			public void graphClicked(Object arg0, MouseEvent arg1)
			{
				//System.out.println("clicked: "+arg0+" "+arg1);
				if(arg0 instanceof INode)
				{
					np.setNode((INode)arg0);
					infopanels.setSelectedComponent(getInfoPanel(NODE_DETAILS_NAME));
				}
			}

			public void graphPressed(Object arg0, MouseEvent arg1)
			{
//				System.out.println("pressed: "+arg0+" "+arg1);
			}

			public void graphReleased(Object arg0, MouseEvent arg1)
			{
//				System.out.println("released: "+arg0+" "+arg1);
			}
		});
		
		final JButton hidenodes = new JButton("Show Subgraph");
		hidenodes.setMargin(new Insets(2,4,2,4));
		hidenodes.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				Set subgraph	= new HashSet(vv.getPickedVertexState().getPicked());
				hideMarkedNodes(subgraph);
				layout.graphChanged();
				vv.repaint();
			}
		});
 
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
				np.refresh();
				vv.repaint();
			}
		});
		
//		JButton remrule = new JButton("Remove rule");
//		remrule.setMargin(new Insets(0,0,0,0));
		/*remrule.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				Set subgraph	= new HashSet(vv.getPickedVertexState().getPicked());
				if(subgraph.isEmpty())
				{
					System.out.println("Please select a terminal node(s) first.");
				}
				else
				{
					for(Iterator it=subgraph.iterator(); it.hasNext(); )
					{
						INode node = (INode)it.next();
						if(node instanceof TerminalNode)
						{
							TerminalNode tn = (TerminalNode)node;
							root.removeRule(tn.getRule());
							buildGraph(g, root);
						}
					}
					vv.repaint();
				}
			}
		});*/
		
		int mw = (int)hidenodes.getMinimumSize().getWidth();
		int pw = (int)hidenodes.getPreferredSize().getWidth();
		int mh = (int)hidenodes.getMinimumSize().getHeight();
		int ph = (int)hidenodes.getPreferredSize().getHeight();
		hidenodes.setMinimumSize(new Dimension(mw, mh));
		hidenodes.setPreferredSize(new Dimension(pw, ph));
//		showdesc.setMinimumSize(new Dimension(mw, mh));
//		showdesc.setPreferredSize(new Dimension(pw, ph));
		refresh.setMinimumSize(new Dimension(mw, mh));
		refresh.setPreferredSize(new Dimension(pw, ph));
//		remrule.setMinimumSize(new Dimension(mw, mh));
//		remrule.setPreferredSize(new Dimension(pw, ph));
		
		JPanel buts = new JPanel(new GridBagLayout());
//		buts.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(
//			BevelBorder.LOWERED), "Node Settings"));
		buts.add(hidenodes, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.EAST,
			GridBagConstraints.NONE,new Insets(2,4,4,2),0,0));
//		buts.add(showdesc, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.EAST,
//			GridBagConstraints.NONE,new Insets(2,4,4,2),0,0));
		buts.add(refresh, new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.EAST,
			GridBagConstraints.NONE,new Insets(2,4,4,2),0,0));
//		buts.add(remrule, new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.EAST,
//			GridBagConstraints.NONE,new Insets(2,4,4,2),0,0));
			
		JPanel tmp = new JPanel(new BorderLayout());
		tmp.add(np, BorderLayout.CENTER);
		tmp.add(buts, BorderLayout.SOUTH);
		this.ap = new AgendaPanel(system.getAgenda());
		JPanel tmp3 = new JPanel(new BorderLayout());
		final JCheckBox followact = new JCheckBox("Follow activation", true);
		followact.setToolTipText("Follow the selected activation by displaying the rule.");
		tmp3.add(ap, BorderLayout.CENTER);
//		tmp3.add(followact, BorderLayout.SOUTH);
		
		// The step action
		final JButton	step	= new JButton("Step");
		if(steppable!=null)
		{
			JPanel tmp4 = new JPanel(new GridBagLayout());
			step.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					steppable.doStep();
				}
			});
			step.setEnabled(steppable.isStepmode() && !system.getAgenda().isEmpty());		
			final JCheckBox	stepmode = new JCheckBox("Step Mode", steppable.isStepmode());
			stepmode.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					steppable.setStepmode(stepmode.isSelected());
					step.setEnabled(steppable.isStepmode() && !system.getAgenda().isEmpty());		
				}
			});
			steppable.addBreakpointCommand(new ICommand()
			{
				public void execute(Object args)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							stepmode.setSelected(steppable.isStepmode());
							step.setEnabled(steppable.isStepmode() && !system.getAgenda().isEmpty());		
						}
					});
				}
			});
			int row	= 0;
			int	col	= 0;
			tmp4.add(followact, new GridBagConstraints(col, row++, 2, 1,
				1,0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
			tmp4.add(stepmode, new GridBagConstraints(col++, row, 1, 1,
				1,0, GridBagConstraints.LINE_END, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
			tmp4.add(step, new GridBagConstraints(col, row++, GridBagConstraints.REMAINDER, 1,
				0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(1,1,1,1), 0,0));
			
			tmp3.add(tmp4, BorderLayout.SOUTH);
		}
		
		addInfoPanel(NODE_DETAILS_NAME, tmp);
		addInfoPanel(AGENDA_NAME, tmp3);
		
		JSplitPane sp2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sp2.setOneTouchExpandable(true);
		sp2.setResizeWeight(1);
		sp2.setDividerLocation(500);
		sp2.add(vv);
		sp2.add(infopanels);
		this.setLayout(new BorderLayout());
		this.add(sp2, BorderLayout.CENTER);
		
		vv.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				layout.setSize(vv.getSize());
			}
		});
		
		rulebasepanel.addBreakpointListener(new IChangeListener()
		{
			 public void changeOccurred(ChangeEvent e)
			 {
				 String[] rules = (String[])rulebasepanel.getSelectedBreakpoints();
//				 System.out.println("Selected: "+SUtil.arrayToString(rules)+" "+e);
				 
				 if(rules!=null && rules.length>0)
				 {
					 // Show all nodes
					 if(remnodes!=null)
						 showHiddenNodes();
				 
					 // Build subgraph of selected rules (terminal nodes).
					 Set subgraph = new HashSet();
					 for(int i=0; i<rules.length; i++)
					 {
						 INode node = root.getTerminalNode(system.getRulebase().getRule(rules[i]));
						 subgraph.add(node);
					 }
					 
					 // Hide all but selected nodes
					 hideMarkedNodes(subgraph);
					 
					 if(np.getNode()==null || !subgraph.contains(np.getNode()))
						 np.setNode(root.getTerminalNode(system.getRulebase().getRule(rules[0])));
				 }
				 
				 // Show no nodes at all.
				 else
				 {
					 hideMarkedNodes(Collections.EMPTY_SET);
				 }

				 layout.graphChanged();
				 vv.repaint();
			 }
		});
		
		this.agendalistener	= new IAgendaListener()
		{
			boolean	invoked	= false;
			Activation	next	= null;
			public void agendaChanged()
			{
				synchronized(RetePanel.this)
				{
					next	= system.getAgenda().getNextActivation();
				}
				if(!invoked)
				{
					invoked	= true;
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							invoked	= false;
//							System.out.println("Next activation: "+act);
							if(followact.isSelected())
							{
								IRule	rule;
								synchronized(RetePanel.this)
								{
									rule	= next!=null ? next.getRule() : null;
								}
								if(rule!=null)
								{
									rulebasepanel.setSelectedBreakpoints(new String[]{rule.getName()});
								}
								else
								{
									rulebasepanel.setSelectedBreakpoints(new String[0]);
								}
							}
							if(steppable!=null)
							{
								step.setEnabled(steppable.isStepmode() && !system.getAgenda().isEmpty());
							}
						}
					});
				}
			}
		};
		system.getAgenda().addAgendaListener(agendalistener);
		followact.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(followact.isSelected())
				{
					Activation act = system.getAgenda().getNextActivation();
					if(act!=null && followact.isSelected())
					{
						rulebasepanel.setSelectedBreakpoints(new String[]{act.getRule().getName()});
					}
				}
				else
				{
					rulebasepanel.setSelectedBreakpoints(new String[0]);
				}
			}
		});
		if(followact.isSelected())
		{
			Activation act = system.getAgenda().getNextActivation();
			if(act!=null && followact.isSelected())
			{
				rulebasepanel.setSelectedBreakpoints(new String[]{act.getRule().getName()});
			}
		}
		/*ap.getActivationsList().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if(!e.getValueIsAdjusting())
				{
					Activation act = (Activation)ap.getActivationsList().getSelectedValue();
					System.out.println("Sel activation: "+act);
					if(act!=null)// && followact)
					{
						rulebasepanel.getList().setSelectedValue(act.getRule(), true);
					}
				}
			}
		});*/
	}
	
	/**
	 *  Dispose the panel and remove any listeners.
	 */
	public void	dispose()
	{
		ap.dispose();
		rulebasepanel.dispose();
		system.getAgenda().removeAgendaListener(agendalistener);
	}
	
	/**
	 *  Build (or rebuild) the graph from the root node.
	 *  @param g The graph.
	 *  @param root The root node.
	 */
	protected void buildGraph(DirectedSparseGraph g, ReteNode root)
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
		todo.add(root);

		for(int i=0; i<todo.size(); i++)
		{
			INode current = (INode)todo.get(i);
			g.addVertex(current);

			if(current instanceof IObjectSourceNode)
			{
				IObjectConsumerNode[]	cons	= ((IObjectSourceNode)current).getObjectConsumers();
				for(int j=0; cons!=null && j<cons.length; j++)
				{
					if(!todo.contains(cons[j]))
						todo.add(cons[j]);
					
					g.addEdge(new ReteEdge(current, cons[j], false), current, cons[j]);
				}
			}

			if(current instanceof ITupleSourceNode)
			{
				ITupleConsumerNode[]	cons	= ((ITupleSourceNode)current).getTupleConsumers();
				for(int j=0; cons!=null && j<cons.length; j++)
				{
					if(!todo.contains(cons[j]))
						todo.add(cons[j]);
	
					g.addEdge(new ReteEdge(current, cons[j], true), current, cons[j]);
				}
			}
		}
	}
	
	/**
	 *  Add an info panel (right hand side).
	 */
	public void addInfoPanel(String name, JComponent panel)
	{
		boolean	found	= false;
		for(int i=0; !found && i<infopanels.getTabCount(); i++)
			if(name.equals(infopanels.getTitleAt(i)))
				// claas - removed - @since 1.6
				//infopanels.setTabComponentAt(i, panel);
				infopanels.setComponentAt(i, panel);
		if(!found)
			infopanels.addTab(name, panel);
		infopanels.setSelectedComponent(panel);
	}
	
	/**
	 *  Get an info panel with a given name.
	 */
	public JComponent getInfoPanel(String name)
	{
		JComponent	ret	= null;
		for(int i=0; ret==null && i<infopanels.getTabCount(); i++)
			if(name.equals(infopanels.getTitleAt(i)))
				ret	= (JComponent)infopanels.getComponentAt(i);
		return ret;
	}
	
	/**
	 *  Hide the marked nodes.
	 *  @param subgraph The subgraph.
	 */
	protected void hideMarkedNodes(Set subgraph)
	{
		if(remnodes!=null || remedges!=null)
			showHiddenNodes();
		
		// Build subgraph of connected nodes to retain.
		Set	upnodes	= new HashSet(subgraph);
		Set	downnodes	= new HashSet(subgraph);
		while(!upnodes.isEmpty() || !downnodes.isEmpty())
		{
			if(upnodes.isEmpty())
			{
				Object	node = downnodes.iterator().next();
				downnodes.remove(node);
				if(node instanceof IObjectSourceNode)
				{
					Object[]	nexts	= ((IObjectSourceNode)node).getObjectConsumers();
					for(int j=0; j<nexts.length; j++)
					{
						if(!subgraph.contains(nexts[j]))
						{
							subgraph.add(nexts[j]);
							downnodes.add(nexts[j]);
						}
					}
				}
				if(node instanceof ITupleSourceNode)
				{
					Object[]	nexts	= ((ITupleSourceNode)node).getTupleConsumers();
					for(int j=0; j<nexts.length; j++)
					{
						if(!subgraph.contains(nexts[j]))
						{
							subgraph.add(nexts[j]);
							downnodes.add(nexts[j]);
						}
					}
				}
			}
			else
			{
				Object	node = upnodes.iterator().next();
				upnodes.remove(node);
				if(node instanceof IObjectConsumerNode)
				{
					Object	next	= ((IObjectConsumerNode)node).getObjectSource();
					if(!subgraph.contains(next))
					{
						subgraph.add(next);
						upnodes.add(next);
					}
				}
				if(node instanceof ITupleConsumerNode)
				{
					Object	next	= ((ITupleConsumerNode)node).getTupleSource();
					if(!subgraph.contains(next))
					{
						subgraph.add(next);
						upnodes.add(next);
					}
				}
			}
		}
		
		// Remove all nodes not in subgraph
		remnodes	= new ArrayList();
		remedges	= new ArrayList();
		Object[]	nodes	= g.getVertices().toArray();
		for(int n=0; n<nodes.length; n++)
		{
			if(!subgraph.contains(nodes[n]))
			{
				Object[]	edges	= g.getInEdges(nodes[n]).toArray();
				for(int i=0; i<edges.length; i++)
				{
					remedges.add(edges[i]);
					g.removeEdge(edges[i]);
				}
				edges	= g.getOutEdges(nodes[n]).toArray();
				for(int i=0; i<edges.length; i++)
				{
					remedges.add(edges[i]);
					g.removeEdge(edges[i]);
				}
				remnodes.add(nodes[n]);
				g.removeVertex(nodes[n]);
			}
		}
	}
	
	/**
	 *  Show the hidden nodes.
	 */
	public void showHiddenNodes()
	{
		for(int i=0; remnodes!=null && i<remnodes.size(); i++)
		{
			g.addVertex(remnodes.get(i));
		}
		for(int i=0; remedges!=null && i<remedges.size(); i++)
		{
			ReteEdge	edge	= (ReteEdge)remedges.get(i);
			g.addEdge(edge, edge.getStart(), edge.getEnd());
		}
		remnodes	= null;
		remedges	= null;
	}
	
	/**
	 *  Get the rulebase panel.
	 * /
	public RulebasePanel	getRulebasePanel()
	{
		return rulebasepanel;
	}*/
	
	/**
	 *  Main for testing.
	 * /
	public static void main(String[] args)
	{
		RuleSystem s = OAVBlockMetamodel.createReteSystem();
		createReteFrame("Blocksworld Test", ((RetePatternMatcherFunctionality)s.getMatcherFunctionality()).getReteNode(), 
			((RetePatternMatcherState)s.getMatcherState()).getReteMemory(), new Object());
	}*/

	/**
	 *  Create a frame for a rete structure.
	 *  @param title	The title for the frame.
	 *  @param rs	The rule system.
	 *  @return	The frame.
	 */
	public static JFrame createReteFrame(String title, RuleSystem rs, ISteppable steppable)
	{
		JFrame f = new JFrame(title);
		f.getContentPane().setLayout(new BorderLayout());
		RulebasePanel	rbp	= new RulebasePanel(rs.getRulebase(), steppable);
		RetePanel rp = new RetePanel(rs, steppable, rbp);
		rp.showHiddenNodes();
		f.add("West", rbp);
		f.add("Center", rp);
		f.pack();
        f.setVisible(true);
		
		return f;
	}
}
