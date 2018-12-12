package jadex.rules.tools.reteviewer;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.rules.rulesystem.rete.Tuple;
import jadex.rules.rulesystem.rete.constraints.ConstraintIndexer;
import jadex.rules.rulesystem.rete.constraints.IConstraintEvaluator;
import jadex.rules.rulesystem.rete.nodes.AbstractBetaNode;
import jadex.rules.rulesystem.rete.nodes.AlphaNode;
import jadex.rules.rulesystem.rete.nodes.CollectNode;
import jadex.rules.rulesystem.rete.nodes.INode;
import jadex.rules.rulesystem.rete.nodes.ReteMemory;
import jadex.rules.rulesystem.rete.nodes.SplitNode;
import jadex.rules.rulesystem.rete.nodes.TerminalNode;
import jadex.rules.rulesystem.rete.nodes.TestNode;
import jadex.rules.rulesystem.rete.nodes.TypeNode;
import jadex.rules.state.IOAVState;

/**
 *  Display information about a single node.
 */
public class NodePanel extends JPanel
{
	//-------- attributes --------
	
	/** The node. */
	protected INode node;
	
	/** The rete memory. */
	protected ReteMemory mem;
	
	/** The state. */
	protected IOAVState state;
	
	/** The property panel. */
	protected PropertyPanel pp;
	
	/** The memory panel. */
	protected MemoryPanel mp;
	
	//-------- constructors --------
	
	/**
	 *  Create a new node panel.
	 */
	public NodePanel(INode node, ReteMemory mem, IOAVState state)
	{
		this.node = node;
		this.mem = mem;
		this.state = state;
		
		this.pp = new PropertyPanel(getNodeProperties());
		this.mp = new MemoryPanel();
		
		pp.setBorder(BorderFactory.createTitledBorder("Node properties"));		
		mp.setBorder(BorderFactory.createTitledBorder("Memory"));
		
		this.setLayout(new BorderLayout());
		this.add(pp, BorderLayout.NORTH);
		this.add(mp, BorderLayout.CENTER);
	}
	
	/**
	 *  Set the node.
	 *  @param node The node.
	 */
	public void setNode(INode node)
	{
		this.node = node;
		refresh();
	}
	
	/**
	 *  Get the node.
	 *  @return The node.
	 */
	public INode getNode()
	{
		return node;
	}
	
	/**
	 *  Get the node properties.
	 *  @return The node properties.
	 */
	public Map getNodeProperties()
	{
		Map ret = new LinkedHashMap();
		if(node!=null)
		{
			ret.put("Type", SReflect.getUnqualifiedClassName(node.getClass()));
			ret.put("Id", Integer.valueOf(node.getNodeId()));
			//ret.put("Use count", node.getUseCount());
			
			if(node instanceof TypeNode)
			{
				TypeNode n = (TypeNode)node;
				ret.put("Object type:", n.getObjectType().getName());
			}
			else if(node instanceof SplitNode)
			{
				SplitNode n = (SplitNode)node;
				ret.put("Attribute:", n.getAttribute().getName());
				ret.put("Split in:", Integer.valueOf(n.getSplitPattern().length));
				ret.put("Split pattern:", SUtil.arrayToString(n.getSplitPattern()));
			}
			else if(node instanceof TestNode)
			{
				TestNode n = (TestNode)node;
				ret.put("Evaluator:", n.getConstraintEvaluator());
			}
			else if(node instanceof AlphaNode)
			{
				AlphaNode n = (AlphaNode)node;
				IConstraintEvaluator[] evas = n.getConstraintEvaluators();
				for(int i=0; evas!=null && i<evas.length; i++)
				{
					ret.put("Evaluator_"+i+":", evas[i]);
				}
			}
			else if(node instanceof AbstractBetaNode)
			{
				AbstractBetaNode n = (AbstractBetaNode)node;
				ConstraintIndexer[] ids = n.getConstraintIndexers();
				for(int i=0; ids!=null && i<ids.length; i++)
				{
					ret.put("Indexer_"+i+":", ids[i]);
				}
				IConstraintEvaluator[] evas = n.getConstraintEvaluators();
				for(int i=0; evas!=null && i<evas.length; i++)
				{
					ret.put("Evaluator_"+i+":", evas[i]);
				}
			}
			else if(node instanceof CollectNode)
			{
				CollectNode n = (CollectNode)node;
				ret.put("Tuple index:", Integer.valueOf(n.getTupleIndex()));
				IConstraintEvaluator[] evas = n.getConstraintEvaluators();
				for(int i=0; evas!=null && i<evas.length; i++)
				{
					ret.put("Evaluator_"+i+":", evas[i]);
				}
			}
			else if(node instanceof TerminalNode)
			{
				TerminalNode n = (TerminalNode)node;
				ret.put("Rule name:", n.getRule().getName());
				ret.put("Rule:", n.getRule());
			}
		}
		return ret;
	}
	
	/**
	 *  Refresh the view.
	 */
	public void refresh()
	{
		pp.setProperties(getNodeProperties());
		mp.refresh();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				invalidate();
				Window	root	= SGUI.getWindowParent(NodePanel.this);
				if(root!=null)
				{
					root.doLayout();
					root.repaint();
				}
//				paintComponents(getGraphics());
			}
		});
	}
	
	/**
	 *  Memory panel for displaying the result memory.
	 */
	public class MemoryPanel extends JPanel
	{
		//-------- attributes --------
		
		/** The list model. */
		protected DefaultListModel	model;
		
		/** The refresh code. */
		protected Runnable	runrefresh;

		//-------- constructors --------
		
		/**
		 *  Create a new panel. 
		 */
		public MemoryPanel()
		{
			this.runrefresh	= new Runnable()
			{
				public void run()
				{
					Collection nodemem = node==null? null: node.getNodeMemory(mem);

					model.clear();
					if(nodemem!=null)
					{					
						for(Iterator it= nodemem.iterator(); it.hasNext();)
							model.addElement(it.next());
					}
				
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							MemoryPanel.this.invalidate();
							MemoryPanel.this.doLayout();
							MemoryPanel.this.repaint();
						}
					});
				}
			};
			
			setLayout(new BorderLayout());

			this.model	= new DefaultListModel();
			final JList	list	= new JList(model);
			JScrollPane sp = new JScrollPane(list);

			final JPopupMenu	popup	= new JPopupMenu("Object Actions");
			JMenuItem	item	= new JMenuItem("Find References");
			popup.add(item);
			item.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					Object	value	= list.getSelectedValue();
					if(value instanceof Tuple)
						JOptionPane.showMessageDialog(list, "Action not supported for beta memory.", "Info", JOptionPane.INFORMATION_MESSAGE);
					else
					{
						Collection	refs	= state.getReferencingObjects(value);
						System.out.println(refs);
					}
				}
			});

			list.addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					mouseClicked(e);
				}
				public void mouseReleased(MouseEvent e)
				{
					mouseClicked(e);
				}
				public void mouseClicked(MouseEvent me)
				{
					if(me.isPopupTrigger())
					{
						int index	= list.locationToIndex(me.getPoint());
						if(index < list.getModel().getSize())
						{
							list.setSelectedIndex(index);
							popup.show(list, me.getX(), me.getY());
						}
					}
				}
			});

			refresh();

			this.add(sp, BorderLayout.CENTER);
		}
		
		/**
		 *  Refresh the panel.
		 */
		public void refresh()
		{
			// Use state synchronizator for accessing rete memory (hack???)
			if(state.getSynchronizator()!=null)
			{
				try
				{
					state.getSynchronizator().invokeLater(runrefresh);
				}
				catch(Exception e)
				{
					//System.out.println("refresh problem");
					// Refresh did not work.
				}
			}
			else
			{
				runrefresh.run();
			}
		}
	}
}