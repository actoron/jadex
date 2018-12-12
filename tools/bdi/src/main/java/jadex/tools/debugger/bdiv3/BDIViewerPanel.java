package jadex.tools.debugger.bdiv3;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.bdiv3.runtime.impl.AbstractBDIInfo;
import jadex.bdiv3.runtime.impl.BeliefInfo;
import jadex.bdiv3.runtime.impl.GoalInfo;
import jadex.bdiv3.runtime.impl.PlanInfo;
import jadex.bridge.BulkMonitoringEvent;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.collection.SortedList;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.tools.debugger.micro.MicroAgentViewPanel;

/**
 *  A panel showing the internals of a BDI agent.
 */
public class BDIViewerPanel extends JPanel
{
	//-------- constants --------
	
	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"belief",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/bulb2.png"),
		"beliefset",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/bulbs2.png"),
		"achieve",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/cloud2a.png"),
		"perform",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/cloud2p.png"),
		"maintain",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/cloud2m.png"),
		"query",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/cloud2q.png"),
		"goal",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/cloud2.png"),
		"plan",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/plan2.png"),
		"agent",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/bdi_agent.png"),
		"capa",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/debugger/bdi/images/bdi_capability.png")
	});
	
	//-------- attributes ---------
	
	/** The external access to the agent. */
	protected IExternalAccess access;
	
	/** The beliefs of all beliefs. */
	protected List<BeliefInfo> allbeliefs;
	
	/** The beliefs. */
	protected List<BeliefInfo>	beliefs;
	
	/** The selected belief (if any). */
	protected BeliefInfo selbel;
	
	/** The goals. */
	protected List<GoalInfo> allgoals;

	/** The goals of selected capabilities. */
	protected List<GoalInfo> goals;
	
	/** The plans. */
	protected List<PlanInfo> allplans;
	
	/** The plans of selected capabilities. */
	protected List<PlanInfo> plans;
	
	/** The component listener. */
//	protected IComponentListener	listener;
	protected ISubscriptionIntermediateFuture<IMonitoringEvent> sub;
	
	/** The known capabilities (full name). */
	protected Set<String> capas;
	
	/** The shown capabilities (full name). */
	protected Set<String> shown;
	
	//--------- constructors --------
	
	/**
	 *  Create a BDI viewer panel.
	 */
	public BDIViewerPanel(IExternalAccess access)
	{
		this.access	= access;
		this.capas	= new TreeSet<String>();
		this.shown	= capas;
		capas.add("<agent>");
		Comparator<AbstractBDIInfo>	comp	= new Comparator<AbstractBDIInfo>()
		{
			public int compare(AbstractBDIInfo info1, AbstractBDIInfo info2)
			{
//				AbstractBDIInfo	info1	= (AbstractBDIInfo)o1;
//				AbstractBDIInfo	info2	= (AbstractBDIInfo)o2;
				int	caps1	= new StringTokenizer(info1.getType(), ".").countTokens();
				int	caps2	= new StringTokenizer(info2.getType(), ".").countTokens();
				return caps1!=caps2 ? caps1-caps2 : info1.getType().compareTo(info2.getType());
			}
		};
		this.allbeliefs	= new SortedList<BeliefInfo>(comp, true);
		this.allgoals	= new SortedList<GoalInfo>(comp, true);
		this.allplans	= new SortedList<PlanInfo>(comp, true);
		this.beliefs	= new SortedList<BeliefInfo>(comp, true);
		this.goals	= new SortedList<GoalInfo>(comp, true);
		this.plans	= new SortedList<PlanInfo>(comp, true);
		
		JPanel	beliefpanel	= new JPanel(new BorderLayout());
		beliefpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Beliefs"));
		final AbstractTableModel beliefmodel	= new AbstractTableModel()
		{
			protected String[]	columnames	= new String[]{"Name", "Type", "Value"};
			
			public int getColumnCount()
			{
				return columnames.length;
			}
			
			public String getColumnName(int i)
			{
				return columnames[i];
			}
			
			public Object getValueAt(int row, int col)
			{
				Object	ret	= null;
				BeliefInfo info = (BeliefInfo)beliefs.get(row);
				if(col==0)
				{
					ret	= info.getId();//info.getType()+"#"+info.getId();
				}
				else if(col==1)
				{
					ret	= info.getValueType();
				}
				else if(col==2)
				{
					ret	= info.getValue();
					if(ret instanceof String[])
					{
						ret	= SUtil.arrayToString(ret);
					}
				}
				return ret;
			}
			
			public int getRowCount()
			{
				return beliefs.size();
			}
		};
		final JTable	belieftable	= new JTable(beliefmodel);
		belieftable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean sel, boolean foc, int row, int column)
			{
				BeliefInfo	info	= (BeliefInfo)beliefs.get(row);
				Icon	icon	= icons.getIcon(info.getKind());
				if(icon==null)
					icon	= icons.getIcon("belief");
				
				setIcon(icon);
				
				return super.getTableCellRendererComponent(table, value, sel, foc, row, column);
			}
		});
		final JPanel	beldetails	= new JPanel(new BorderLayout());
		final AbstractTableModel	factmodel	= new AbstractTableModel()
		{
			public int getColumnCount()
			{
				return 1;
			}
			public String getColumnName(int column)
			{
				return "Facts";
			}
			public int getRowCount()
			{
				return (selbel!=null && selbel.getValue() instanceof String[]) ? ((String[])selbel.getValue()).length : 1;
			}
			public Object getValueAt(int row, int column)
			{
				return (selbel!=null && selbel.getValue() instanceof String[]) ? ((String[])selbel.getValue())[row] : selbel!=null ? selbel.getValue() : "";
			}
		};
		beldetails.add(new JScrollPane(new JTable(factmodel)), BorderLayout.CENTER);
		JSplitPanel	spbel	= new JSplitPanel(JSplitPanel.VERTICAL_SPLIT, new JScrollPane(belieftable), beldetails);
		spbel.setOneTouchExpandable(true);
		spbel.setDividerLocation(0.75);
		beliefpanel.add(spbel, BorderLayout.CENTER);
		belieftable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JPanel	goalpanel	= new JPanel(new BorderLayout());
		goalpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Goals"));
		final AbstractTableModel	goalmodel	= new AbstractTableModel()
		{
			protected String[]	columnames	= new String[]{
				"Name", "Lifecycle State", "Processing State"};
			
			public int getColumnCount()
			{
				return columnames.length;
			}
			
			public String getColumnName(int i)
			{
				return columnames[i];
			}
			
			public Object getValueAt(int row, int col)
			{
				Object	ret	= null;
				GoalInfo	info	= (GoalInfo)goals.get(row);
				if(col==0)
				{
					ret	= info.getType()+"#"+info.getId();
				}
				else if(col==1)
				{
					ret	= info.getLifecycleState();
				}
				else if(col==2)
				{
					ret	= info.getProcessingState();
				}
				return ret;
			}
			
			public int getRowCount()
			{
				return goals.size();
			}
		};
		final JTable	goaltable	= new JTable(goalmodel);
		goaltable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean sel, boolean foc, int row, int column)
			{
				GoalInfo	info	= (GoalInfo)goals.get(row);
				Icon	icon	= icons.getIcon(info.getKind());
				if(icon==null)
					icon	= icons.getIcon("goal");
				
				setIcon(icon);
				
				return super.getTableCellRendererComponent(table, value, sel, foc, row, column);
			}
		});
		goalpanel.add(new JScrollPane(goaltable), BorderLayout.CENTER);
		
		JPanel	planpanel	= new JPanel(new BorderLayout());
		planpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Plans"));
		final AbstractTableModel	planmodel	= new AbstractTableModel()
		{
			protected String[]	columnames	= new String[]{"Name", "State"};
			
			public int getColumnCount()
			{
				return columnames.length;
			}
			
			public String getColumnName(int i)
			{
				return columnames[i];
			}
			
			public Object getValueAt(int row, int col)
			{
				Object	ret	= null;
				PlanInfo	info	= (PlanInfo)plans.get(row);
				if(col==0)
				{
					ret	= info.getType()+"#"+info.getId();
				}
				else if(col==1)
				{
					ret	= info.getState();
				}
				return ret;
			}
			
			public int getRowCount()
			{
				return plans.size();
			}
		};
		final JTable	plantable	= new JTable(planmodel);
		plantable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean sel, boolean foc, int row, int column)
			{
				Icon	icon	= icons.getIcon("plan");
				setIcon(icon);
				
				return super.getTableCellRendererComponent(table, value, sel, foc, row, column);
			}
		});
		planpanel.add(new JScrollPane(plantable), BorderLayout.CENTER);

		JSplitPanel	sp1	= new JSplitPanel(JSplitPane.VERTICAL_SPLIT, beliefpanel, goalpanel);
		sp1.setOneTouchExpandable(true);
		sp1.setDividerLocation(4.0/7.0);
		JSplitPanel	sp2	= new JSplitPanel(JSplitPane.VERTICAL_SPLIT, sp1, planpanel);
		sp2.setOneTouchExpandable(true);
		sp2.setDividerLocation(0.7);
		this.setLayout(new BorderLayout());
		
		JSplitPanel	sp3	= new JSplitPanel(JSplitPane.HORIZONTAL_SPLIT, sp2, new MicroAgentViewPanel(access, null, true));
		sp3.setDividerLocation(0.8);
		sp3.setOneTouchExpandable(true);
		
		this.add(sp3, BorderLayout.CENTER);
		JButton	hide	= new JButton("Hide...");
		JButton	show	= new JButton("Show All");
		hide.setPreferredSize(show.getPreferredSize());
		hide.setMinimumSize(show.getMinimumSize());
		hide.setToolTipText("Select capabilities to hide");
		show.setToolTipText("Show all capabilities");
		JPanel	showhide	= new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		showhide.add(hide);
		showhide.add(show);
		this.add(showhide, BorderLayout.SOUTH);
		hide.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JCheckBox[]	cbs	= new JCheckBox[capas.size()];
				JPanel	panel	= new JPanel(new GridBagLayout());
				panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Capabilities"));
				GridBagConstraints	gbc	= new GridBagConstraints();
				gbc.gridy	= 0;
				gbc.anchor	= GridBagConstraints.WEST;
				for(Iterator it=capas.iterator(); it.hasNext(); )
				{
					String	capa	= (String)it.next();
					cbs[gbc.gridy]	= new JCheckBox(capa, !shown.contains(capa));
					gbc.weightx	= 0;
					panel.add(new JLabel("<agent>".equals(capa) ? icons.getIcon("agent") : icons.getIcon("capa")), gbc);
					gbc.weightx	= 1;
					panel.add(cbs[gbc.gridy], gbc);
					gbc.gridy++;
				}
				int	option	= JOptionPane.showOptionDialog(BDIViewerPanel.this, panel, "Select capabilities to hide", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
				if(JOptionPane.OK_OPTION==option)
				{
					shown	= new HashSet();
					for(int i=0; i<cbs.length; i++)
					{
						if(!cbs[i].isSelected())
							shown.add(cbs[i].getText());
					}

					updateShown(belieftable, factmodel, goaltable, plantable);
				}
			}
		});
		show.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				shown = capas;
				updateShown(belieftable, factmodel, goaltable, plantable);
			}
		});
		
//		listener = new IComponentListener()
//		{
//			protected IFilter filter = new IFilter()
//			{
//				@Classname("filter")
//				public boolean filter(Object obj)
//				{
//					IComponentChangeEvent cce = (IComponentChangeEvent)obj;
//					return cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_FACT)
//						|| cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_GOAL)
//						|| cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_PLAN);
//				}
//			};
//			
//			public IFilter getFilter()
//			{
//				return filter;
//			}
//			
//			public IFuture eventOccured(final IComponentChangeEvent cce)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						List beliefsel = getTableSelection(belieftable, beliefs);
//						List goalsel = getTableSelection(goaltable, goals);
//						List plansel = getTableSelection(plantable, plans);
//						
//						handleEvent(cce);
//						
//						updateTable(belieftable, beliefs, beliefsel);
//						updateTable(goaltable, goals, goalsel);
//						updateTable(plantable, plans, plansel);
//						
//						updateSelectedBelief(belieftable, factmodel);
//					}
//					
//					public void handleEvent(IComponentChangeEvent event)
//					{
//						// todo: hide decomposing bulk events
//						if(cce.getBulkEvents().length>0)
//						{
//							IComponentChangeEvent[] events = cce.getBulkEvents();
//							for(int i=0; i<events.length; i++)
//							{
//								eventOccured(events[i]);
//							}
//						}
//						
//						else if(cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_FACT))
//						{
//							// Hack!!! create/disposal only for facts, not for beliefs, just check for changes, removal not supported.
//							int	index	= allbeliefs.indexOf(cce.getDetails());
//							if(index!=-1)
//							{
//								BeliefInfo	newinfo	= (BeliefInfo)cce.getDetails();
//								BeliefInfo	oldinfo	= (BeliefInfo)allbeliefs.remove(index);
//								beliefs.remove(newinfo);
//								newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
//								allbeliefs.add(newinfo);
//								if(checkCapa(newinfo.getType()))
//									beliefs.add(newinfo);								
//							}
//							else
//							{
//								allbeliefs.add(cce.getDetails());
//								if(checkCapa(((BeliefInfo)cce.getDetails()).getType()))
//									beliefs.add(cce.getDetails());
//							}
//						}
//						
//						else if(cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_GOAL))
//						{
//							if(IComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()))
//							{
//								allgoals.add(cce.getDetails());
//								if(checkCapa(((GoalInfo)cce.getDetails()).getType()))
//									goals.add(cce.getDetails());
//							}
//							else if(IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()))
//							{
//								allgoals.remove(cce.getDetails());
//								goals.remove(cce.getDetails());
//							}
//							else if(IComponentChangeEvent.EVENT_TYPE_MODIFICATION.equals(cce.getEventType()))
//							{
//								int	index	= allgoals.indexOf(cce.getDetails());
//								if(index!=-1)
//								{
//									GoalInfo	newinfo	= (GoalInfo)cce.getDetails();
//									GoalInfo	oldinfo	= (GoalInfo)allgoals.remove(index);
//									goals.remove(newinfo);
//									newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
//									allgoals.add(newinfo);
//									if(checkCapa(newinfo.getType()))
//										goals.add(newinfo);
//								}
//							}
//						}
//						
//						else if(cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_PLAN))
//						{
//							if(IComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()))
//							{
//								allplans.add(cce.getDetails());
//								if(checkCapa(((PlanInfo)cce.getDetails()).getType()))
//									plans.add(cce.getDetails());
//							}
//							else if(IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()))
//							{
//								allplans.remove(cce.getDetails());
//								plans.remove(cce.getDetails());
//							}
//							else if(IComponentChangeEvent.EVENT_TYPE_MODIFICATION.equals(cce.getEventType()))
//							{
//								int	index	= allplans.indexOf(cce.getDetails());
//								if(index!=-1)
//								{
//									PlanInfo	newinfo	= (PlanInfo)cce.getDetails();
//									PlanInfo	oldinfo	= (PlanInfo)allplans.remove(index);
//									plans.remove(newinfo);
//									newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
//									allplans.add(newinfo);
//									if(checkCapa(newinfo.getType()))
//										plans.add(newinfo);
//								}
//							}
//						}
//					}
//				});
//				return IFuture.DONE;
//			}
//		};
//
//		final IComponentListener lis = listener;
//		access.scheduleImmediate(new IComponentStep<Void>()
//		{
//			@Classname("installListener")
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				BDIInterpreter	interpreter	= BDIInterpreter.getInterpreter(((CapabilityFlyweight)ia).getState());
//				
//				// Post current state to remote listener
//				List	events	= new ArrayList();
//				getInitialEvents(ia, interpreter.getState(), interpreter.getAgent(), events);
//				lis.eventOccured(new BulkMonitoringEvent((IComponentChangeEvent[])events.toArray(new IComponentChangeEvent[events.size()])));
//				
//				ia.addComponentListener(lis);
//				return IFuture.DONE;
//			}
//		});
		
		
		sub = access.subscribeToEvents(new IFilter<IMonitoringEvent>()
		{
			public boolean filter(IMonitoringEvent ev)
			{
				return ev.getType().endsWith(IMonitoringEvent.SOURCE_CATEGORY_FACT)
					|| ev.getType().endsWith(IMonitoringEvent.SOURCE_CATEGORY_GOAL)
					|| ev.getType().endsWith(IMonitoringEvent.SOURCE_CATEGORY_PLAN);
			}
		}, true, PublishEventLevel.FINE);
		
		sub.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
		{
			public void exceptionOccurred(Exception exception)
			{
				// Ignore exception when monitored agent dies.
				if(!(exception instanceof FutureTerminatedException))
				{
					super.exceptionOccurred(exception);
				}					
			}
			
			public void intermediateResultAvailable(IMonitoringEvent event)
			{	
				// todo: hide decomposing bulk events
				List<BeliefInfo> beliefsel = getTableSelection(belieftable, beliefs);
				List<GoalInfo> goalsel = getTableSelection(goaltable, goals);
				List<PlanInfo> plansel = getTableSelection(plantable, plans);
				
				handleEvent(event);
				
				updateTable(belieftable, beliefs, beliefsel);
				updateTable(goaltable, goals, goalsel);
				updateTable(plantable, plans, plansel);
				
				updateSelectedBelief(belieftable, factmodel);
			}
			
			public void handleEvent(IMonitoringEvent event)
			{
				if(event==null)
					return;
				
//				System.out.println("got event: "+event);
				
				// todo: hide decomposing bulk events
				if(event instanceof BulkMonitoringEvent)
				{
					BulkMonitoringEvent bev = (BulkMonitoringEvent)event;
					if(bev.getBulkEvents().length>0)
					{
						IMonitoringEvent[] events = bev.getBulkEvents();
						for(int i=0; i<events.length; i++)
						{
							intermediateResultAvailable(events[i]);
						}
					}
				}
				else if(event.getType().endsWith(IMonitoringEvent.SOURCE_CATEGORY_FACT))
				{
					// Hack!!! create/disposal only for facts, not for beliefs, just check for changes, removal not supported.
					int	index	= allbeliefs.indexOf(event.getProperty("details"));
					if(index!=-1)
					{
						BeliefInfo	newinfo	= (BeliefInfo)event.getProperty("details");
						BeliefInfo	oldinfo	= (BeliefInfo)allbeliefs.remove(index);
						beliefs.remove(newinfo);
						newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
						allbeliefs.add(newinfo);
						if(checkCapa(newinfo.getType()))
							beliefs.add(newinfo);								
					}
					else
					{
						BeliefInfo bi = (BeliefInfo)event.getProperty("details");
						allbeliefs.add(bi);
						if(checkCapa(bi.getType()))
							beliefs.add(bi);
					}
				}
				
				else if(event.getType().endsWith(IMonitoringEvent.SOURCE_CATEGORY_GOAL))
				{
					if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_CREATION))
					{
						GoalInfo gi = (GoalInfo)event.getProperty("details");
						allgoals.add(gi);
						if(checkCapa(gi.getType()))
							goals.add(gi);
					}
					else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL))
					{
						allgoals.remove(event.getProperty("details"));
						goals.remove(event.getProperty("details"));
					}
					else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_MODIFICATION))
					{
						int	index	= allgoals.indexOf(event.getProperty("details"));
						if(index!=-1)
						{
							GoalInfo	newinfo	= (GoalInfo)event.getProperty("details");
							GoalInfo	oldinfo	= (GoalInfo)allgoals.remove(index);
							goals.remove(newinfo);
							newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
							allgoals.add(newinfo);
							if(checkCapa(newinfo.getType()))
								goals.add(newinfo);
						}
					}
				}
				
				else if(event.getType().endsWith(IMonitoringEvent.SOURCE_CATEGORY_PLAN))
				{
					if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_CREATION))
					{
						PlanInfo pi = (PlanInfo)event.getProperty("details");
						allplans.add(pi);
						if(checkCapa(pi.getType()))
							plans.add(pi);
					}
					else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_DISPOSAL))
					{
						allplans.remove(event.getProperty("details"));
						plans.remove(event.getProperty("details"));
					}
					else if(event.getType().startsWith(IMonitoringEvent.EVENT_TYPE_MODIFICATION))
					{
						int	index	= allplans.indexOf(event.getProperty("details"));
						if(index!=-1)
						{
							PlanInfo	newinfo	= (PlanInfo)event.getProperty("details");
							PlanInfo	oldinfo	= (PlanInfo)allplans.remove(index);
							plans.remove(newinfo);
							newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
							allplans.add(newinfo);
							if(checkCapa(newinfo.getType()))
								plans.add(newinfo);
						}
					}
				}
			}
		}));
	}
	
	//-------- helper methods --------
	
//	/**
//	 *  Generate added events for the current goals
//	 */
//	protected static void	getInitialEvents(IInternalAccess ia, IOAVState state, Object capa, List events)
//	{
//		// Beliefs of this capability.
//		Collection	beliefs	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_beliefs);
//		if(beliefs!=null)
//		{
//			for(Iterator it=beliefs.iterator(); it.hasNext(); )
//			{
//				Object	belief	= it.next();
//				BeliefInfo	info = BeliefInfo.createBeliefInfo(state, belief, capa);
//				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_FACT, info.getType(), belief.toString(), ia.getComponentIdentifier(), ia.getComponentDescription().getCreationTime(), info));
//			}
//		}
//		
//		// Belief sets of this capability.
//		Collection	beliefsets	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_beliefsets);
//		if(beliefsets!=null)
//		{
//			for(Iterator it=beliefsets.iterator(); it.hasNext(); )
//			{
//				Object	beliefset	= it.next();
//				BeliefInfo	info = BeliefInfo.createBeliefInfo(state, beliefset, capa);
//				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_FACT, info.getType(), beliefset.toString(), ia.getComponentIdentifier(), ia.getComponentDescription().getCreationTime(), info));
//			}
//		}
//		
//		// Goals of this capability.
//		Collection	goals	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_goals);
//		if(goals!=null)
//		{
//			for(Iterator it=goals.iterator(); it.hasNext(); )
//			{
//				Object	goal	= it.next();
//				GoalInfo	info = GoalInfo.createGoalInfo(state, goal, capa);
//				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_GOAL, info.getType(), goal.toString(), ia.getComponentIdentifier(), ia.getComponentDescription().getCreationTime(), info));
//			}
//		}
//		
//		// Plans of this capability.
//		Collection	plans	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_plans);
//		if(plans!=null)
//		{
//			for(Iterator it=plans.iterator(); it.hasNext(); )
//			{
//				Object	plan	= it.next();
//				PlanInfo	info = PlanInfo.createPlanInfo(state, plan, capa);
//				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_PLAN, info.getType(), plan.toString(), ia.getComponentIdentifier(), ia.getComponentDescription().getCreationTime(), info));
//			}
//		}
//		
//		// Recurse for sub capabilities.
//		Collection	capas	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_subcapabilities);
//		if(capas!=null)
//		{
//			for(Iterator it=capas.iterator(); it.hasNext(); )
//			{
//				getInitialEvents(ia, state, state.getAttributeValue(it.next(), OAVBDIRuntimeModel.capabilityreference_has_capability), events);
//			}
//		}
//	}
	
	/**
	 *  Dispose the panel.
	 */
	public IFuture<Void>	dispose()
	{
		sub.terminate();
		return IFuture.DONE;
//		final IComponentListener lis = listener;
//		return access.scheduleImmediate(new IComponentStep<Void>()
//		{
//			@Classname("removeListener")
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				ia.removeComponentListener(lis);
//				return IFuture.DONE;
//			}
//		});
	}

	/**
	 *  Get the currently selected items.
	 */
	protected List getTableSelection(JTable table, List items)
	{
		int[]	sel	= table.getSelectedRows();
		List	goalsel	= new ArrayList();
		for(int i=0; i<sel.length; i++)
			goalsel.add(items.get(sel[i]));
		return goalsel;
	}

	/**
	 *  Update the model and set the selected based on selected items.
	 */
	protected void updateTable(JTable table, List items, List selection)
	{
		((AbstractTableModel)table.getModel()).fireTableDataChanged();
		table.repaint();

		table.getSelectionModel().clearSelection();
		for(int i=0; i<selection.size(); i++)
		{
			int index	= items.indexOf(selection.get(i));
			if(index!=-1)
			{
				table.getSelectionModel().addSelectionInterval(index, index);
			}
		}
	}
	
	/**
	 *  Update the belief selection.
	 */
	protected void	updateSelectedBelief(JTable belieftable, AbstractTableModel factmodel)
	{
		selbel	= null;
		if(belieftable.getSelectedRow()>=0 && belieftable.getSelectedRow()<beliefs.size())
		{
			selbel	= (BeliefInfo)beliefs.get(belieftable.getSelectedRow());
		}
		factmodel.fireTableDataChanged();
	}
	
	/**
	 *  Check if the capability is shown.
	 */
	protected boolean	checkCapa(String name)
	{
		int i	= name.lastIndexOf('.');
		if(i!=-1)
		{
			name	= name.substring(0, i);
			capas.add(name);
		}
		else
		{
			name	= "<agent>";
		}
		return shown.contains(name);
	}

	/**
	 * 
	 * @param belieftable
	 * @param factmodel
	 * @param goaltable
	 * @param plantable
	 */
	protected void updateShown(final JTable belieftable,
		final AbstractTableModel factmodel, final JTable goaltable,
		final JTable plantable)
	{
		List<BeliefInfo> beliefsel = getTableSelection(belieftable, beliefs);
		List<GoalInfo> goalsel = getTableSelection(goaltable, goals);
		List<PlanInfo> plansel = getTableSelection(plantable, plans);

		beliefs.clear();
		goals.clear();
		plans.clear();
		for(int i=0; i<allbeliefs.size(); i++)
		{
			if(checkCapa(((AbstractBDIInfo)allbeliefs.get(i)).getType()))
			{
				beliefs.add(allbeliefs.get(i));
			}
		}
		for(int i=0; i<allgoals.size(); i++)
		{
			if(checkCapa(((AbstractBDIInfo)allgoals.get(i)).getType()))
			{
				goals.add(allgoals.get(i));
			}
		}
		for(int i=0; i<allplans.size(); i++)
		{
			if(checkCapa(((AbstractBDIInfo)allplans.get(i)).getType()))
			{
				plans.add(allplans.get(i));
			}
		}
		
		updateTable(belieftable, beliefs, beliefsel);
		updateTable(goaltable, goals, goalsel);
		updateTable(plantable, plans, plansel);
		
		updateSelectedBelief(belieftable, factmodel);
	}
}
