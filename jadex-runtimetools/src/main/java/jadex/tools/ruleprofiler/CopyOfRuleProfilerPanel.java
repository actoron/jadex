package jadex.tools.ruleprofiler;

import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.SGUI;
import jadex.rules.state.viewer.OAVTreeModel;
import jadex.tools.common.GuiProperties;

import java.util.Comparator;

import javax.swing.JPanel;
import javax.swing.UIDefaults;

/**
 *  The panel showing the rule profiler content for a single agent.
 */
public class CopyOfRuleProfilerPanel	extends JPanel
{
	// -------- constants --------

	/**
	 * The image icons.
	 */
	// Todo: new icons for profiler elements.
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"root", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/new_agent.png"),
		"rule", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/arrow_right.png"),
		"object", SGUI.makeIcon(OAVTreeModel.class, "/jadex/rules/state/viewer/images/object.png"),
		"event", SGUI.makeIcon(GuiProperties.class, "/jadex/tools/common/images/goalevent.png"),
	});
	
	//-------- attributes --------

	/** The agent access. */
	protected IExternalAccess	agent;
	
	/** The agent to observe. */
	protected IAgentIdentifier	observed;
	
	//-------- constructors --------

	/**
	 *  Create a new tool panel for a remote agent.
	 *  @param agent	The agent access.
	 *  @param active	Flags indicating which tools should be active.
	 * /
	public CopyOfRuleProfilerPanel(IExternalAccess agent, IAgentIdentifier observed)
	{
		this.agent	= agent;
		this.observed	= observed;

        // Hack!?!?!
		((IAMS)agent.getPlatform().getService(IAMS.class, SFipa.AMS_SERVICE))
			.getAgentAdapter(observed, new IResultListener()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			public void resultAvailable(final Object result)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						StandaloneAgentAdapter	adapter	= (StandaloneAgentAdapter)result;
						BDIInterpreter	bdii	= (BDIInterpreter)adapter.getJadexAgent();
						final IProfiler	profiler	= new Profiler();
						bdii.getState().setProfiler(profiler);
						
						final ProfileNode	root	= new ProfileNode(null, IProfiler.TYPE_ROOT,
//							new String[]{IProfiler.TYPE_RULE, IProfiler.TYPE_OBJECT, IProfiler.TYPE_EVENT}, null);
								new String[]{IProfiler.TYPE_NODE, IProfiler.TYPE_NODEEVENT, IProfiler.TYPE_OBJECT, IProfiler.TYPE_OBJECTEVENT, IProfiler.TYPE_RULE}, null);
//								new String[]{IProfiler.TYPE_OBJECT, IProfiler.TYPE_OBJECTEVENT, IProfiler.TYPE_RULE}, null);
						final Map	nodes	= new HashMap();
						nodes.put(root, root);

						final AbstractTreeTableModel	model	= new DefaultTreeTableModel(root,
							new String[]{"Name", "Time", "Occurrences"});
						final JTreeTable	tree	= new JTreeTable(model);
						new TreeExpansionHandler(tree.getTree());
						tree.getTree().setCellRenderer(new DefaultTreeCellRenderer()
						{
							public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
							{
								ProfileNode	node	= (ProfileNode)value;
								Icon	icon	= icons.getIcon(node.getType());
								if(icon!=null)
								{
									setClosedIcon(icon);
									setOpenIcon(icon);
									setLeafIcon(icon);
								}
								return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
							}
						});
						
						tree.getColumnModel().getColumn(1).setCellRenderer(new TableCellRenderer()
						{
							JProgressBar	prog	= new JProgressBar(0, 1000);
							public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
							{
								String	string	= (String)value;
								int	left	= string.indexOf('(');
								int	right	= string.indexOf(')');
								if(left!=-1 && right!=-1 && left<right)
								{
									double	val	= Double.parseDouble(string.substring(left+1, right-1));
									prog.setValue((int)(val*10));
								}
								else
								{
									prog.setValue(0);
								}
								prog.setStringPainted(true);
								prog.setString(string);
								return prog;
							}
						});
						
						// Make headers resizable.
						ResizeableTableHeader header = new ResizeableTableHeader();
						header.setColumnModel(tree.getColumnModel());
						header.setAutoResizingEnabled(false); //default
						header.setIncludeHeaderWidth(false); //default
						tree.setTableHeader(header);
						// Set the preferred, minimum and maximum column widths
						header.setAllColumnWidths(145, -1, -1);
						header.setColumnWidths(tree.getColumnModel().getColumn(0), 200, 100, -1);
						
						// Required, otherwise column sizes are reset on every update.
						tree.setAutoCreateColumnsFromModel(false);
						
						JScrollPane	scroll	= new JScrollPane(tree);
						CopyOfRuleProfilerPanel.this.setLayout(new BorderLayout());
						CopyOfRuleProfilerPanel.this.add(BorderLayout.CENTER, scroll);
						
						CopyOfRuleProfilerPanel.this.invalidate();
						CopyOfRuleProfilerPanel.this.doLayout();
						CopyOfRuleProfilerPanel.this.repaint();

						Timer	timer	= new Timer(2000, new ActionListener()
						{
							int	start	= 0;
							Comparator	comp	= new TimeComparator();
							
							public void actionPerformed(ActionEvent e)
							{
								ProfilingInfo[]	infos	= profiler.getProfilingInfos(start);
								start	+= infos.length;
								updateProfileTree(root, nodes, infos);
								root.sort(comp);
								model.reload(root);
								
//								System.out.println("Updated "+infos.length+" profiling infos.");
							}
						});
						timer.start();
					}
				});
			}
		});
	}*/

	/**
	 *  Build the profile tree node structure.
	 * /
	protected static void	updateProfileTree(ProfileNode root, Map nodes, ProfilingInfo[] infos)
	{
		String[]	ordering	= root.getOrdering();
		for(int i=0; i<infos.length; i++)
		{
			ProfileNode	parent	= root;
			parent.accumulate(infos[i]);
			
			for(int j=0; j<ordering.length; j++)
			{
				ProfileNode	tmp	= new ProfileNode(parent, ordering[j], ordering, infos[i]);
				ProfileNode	node	= (ProfileNode)nodes.get(tmp);
				if(node==null)
				{
					node	= tmp;
					nodes.put(node, node);
					parent.addSubnode(node);
				}
				
				node.accumulate(infos[i]);				
				parent	= node;
			}
		}
	}*/
	
	//-------- helper classes --------
	
	/**
	 *  Comparator sorting profile nodes by time.
	 */
	public static class TimeComparator	implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			ProfileNode	node1	= (ProfileNode)o1;
			ProfileNode	node2	= (ProfileNode)o2;
			long	comp	= node2.time - node1.time;	// Descending order.
			return comp<0 ? -1 : comp>0 ? 1 : 0;
		}
	}
}
