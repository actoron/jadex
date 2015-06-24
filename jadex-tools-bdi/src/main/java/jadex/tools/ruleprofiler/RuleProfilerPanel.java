package jadex.tools.ruleprofiler;

import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ImmediateComponentStep;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.TreeExpansionHandler;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.jtable.ResizeableTableHeader;
import jadex.commons.gui.jtreetable.AbstractTreeTableModel;
import jadex.commons.gui.jtreetable.DefaultTreeTableModel;
import jadex.commons.gui.jtreetable.JTreeTable;
import jadex.rules.state.IProfiler;
import jadex.rules.state.IProfiler.ProfilingInfo;
import jadex.rules.tools.profiler.Profiler;
import jadex.rules.tools.stateviewer.OAVTreeModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *  The panel showing the rule profiler content for a single agent.
 */
public class RuleProfilerPanel	extends JPanel
{
	// -------- constants --------

	/**
	 * The image icons.
	 */
	// Todo: new icons for profiler elements.
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"root", SGUI.makeIcon(RuleProfilerPanel.class, "/jadex/tools/ruleprofiler/images/new_agent.png"),
		"rule", SGUI.makeIcon(RuleProfilerPanel.class, "/jadex/tools/ruleprofiler/images/arrow_right.png"),
		"object", SGUI.makeIcon(OAVTreeModel.class, "/jadex/tools/ruleprofiler/images/object.png"),
		"event", SGUI.makeIcon(RuleProfilerPanel.class, "/jadex/tools/ruleprofiler/images/goalevent.png"),
	});
	
	//-------- attributes --------

	/** The agent to observe. */
	protected IComponentIdentifier	observed;
	
	//-------- constructors --------

	/**
	 *  Create a new tool panel for a remote agent.
	 *  @param agent	The agent access.
	 *  @param active	Flags indicating which tools should be active.
	 */
	public RuleProfilerPanel(IExternalAccess provider, final IComponentIdentifier observed)
	{
		this.observed	= observed;

		SServiceProvider.getService(provider,
			IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(RuleProfilerPanel.this)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				cms.getExternalAccess(observed).addResultListener(new SwingDefaultResultListener(RuleProfilerPanel.this)
				{
					public void customResultAvailable(final Object result)
					{
//						StandaloneAgentAdapter	adapter	= (StandaloneAgentAdapter)result;
//						final BDIInterpreter	bdii	= (BDIInterpreter)adapter.getJadexAgent();
						// Hack!!!
						final IInternalBDIAgentFeature bdii = ((ElementFlyweight)result).getBDIFeature();

						// Load profiling info.
						IProfiler	tmp	= bdii.getState().getProfiler();
//						try
//						{
//							File	file	= new File("./"+adapter.getAgentIdentifier().getLocalName()+".profile.ser");
//							ObjectInputStream	ois	= new ObjectInputStream(new FileInputStream(file));
//							tmp	= (IProfiler)ois.readObject();
//							ois.close();
//						}
//						catch(Exception e)
//						{
//							e.printStackTrace();
//						}
//						if(tmp instanceof NoProfiler)	// hack!!!
						{
							tmp	= new Profiler(null);
							final IProfiler prof	= tmp;
							((ElementFlyweight)result).getInterpreter().getComponentFeature(IExecutionFeature.class).scheduleStep(new ImmediateComponentStep<Void>()
							{
								public IFuture<Void> execute(IInternalAccess ia)
								{
									bdii.getState().setProfiler(prof);
									return IFuture.DONE;
								}
							});
						}
						final IProfiler	profiler	= tmp;
						
						final ProfileNode	root	= new ProfileNode(null, IProfiler.TYPE_ROOT, null);
						final Map	nodes	= new HashMap();
						nodes.put(root, root);

						final AbstractTreeTableModel	model	= new DefaultTreeTableModel(root,
							new String[]{"Name", "Time", "Inherent", "Occurrences"});
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
								else
								{
									setClosedIcon(getDefaultClosedIcon());
									setOpenIcon(getDefaultOpenIcon());
									setLeafIcon(getDefaultLeafIcon());
								}
								return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
							}
						});
						
						TableCellRenderer	progressbarrenderer	= new TableCellRenderer()
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
						};
						tree.getColumnModel().getColumn(1).setCellRenderer(progressbarrenderer);
						tree.getColumnModel().getColumn(2).setCellRenderer(progressbarrenderer);

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
						RuleProfilerPanel.this.setLayout(new BorderLayout());
						RuleProfilerPanel.this.add(BorderLayout.CENTER, scroll);
						
						RuleProfilerPanel.this.invalidate();
						RuleProfilerPanel.this.doLayout();
						RuleProfilerPanel.this.repaint();

						Timer	timer	= new Timer(2000, new ActionListener()
						{
							int	start	= 0;
							Comparator	comp	= new TimeComparator();
							
							public void actionPerformed(ActionEvent e)
							{
								((Timer)e.getSource()).stop();
								
								ProfilingInfo[]	infos	= profiler.getProfilingInfos(start);
								if(infos.length>0)
								{
									start	+= infos.length;
									updateProfileTree(root, nodes, infos);
									root.sort(comp);
									model.reload(root);
//									invalidate();
//									doLayout();
//									repaint();
//									System.out.println("++++ Updated "+infos.length+" profiling infos.");
								}
								
								((Timer)e.getSource()).start();
							}
						});
						timer.start();
					}
				});
			}
		});
	}	

	/**
	 *  Build the profile tree node structure.
	 */
	protected static void	updateProfileTree(ProfileNode root, Map nodes, ProfilingInfo[] infos)
	{
		for(int i=0; i<infos.length; i++)
		{
			root.accumulate(infos[i]);
			
			String	type	= (String)infos[i].type;
			Object	item	= infos[i].item;
			ProfileNode	tmp	= new ProfileNode(root, type, item);
			ProfileNode	node	= (ProfileNode)nodes.get(tmp);
			if(node==null)
			{
//				System.out.println("new node: "+tmp);
				node	= tmp;
				nodes.put(node, node);
				root.addSubnode(node);
			}
				
			node.accumulate(infos[i]);				
		}
	}
	
	//-------- helper classes --------
	
	/**
	 *  Comparator sorting profile nodes by time.
	 */
	public static class TimeComparator	implements Comparator, Serializable	// Serializable as suggested by findbugs
	{
		public int compare(Object o1, Object o2)
		{
			ProfileNode	node1	= (ProfileNode)o1;
			ProfileNode	node2	= (ProfileNode)o2;
			long	comp	= node2.inherent - node1.inherent;	// Descending order.
			return comp<0 ? -1 : comp>0 ? 1 : 0;
		}
	}
}
