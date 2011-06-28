package jadex.tools.debugger.bdi;

import jadex.bdi.runtime.impl.flyweights.CapabilityFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.BeliefInfo;
import jadex.bdi.runtime.interpreter.GoalInfo;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanInfo;
import jadex.bridge.BulkComponentChangeEvent;
import jadex.bridge.ComponentChangeEvent;
import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.IFilter;
import jadex.commons.SUtil;
import jadex.commons.collection.SortedList;
import jadex.commons.future.IFuture;
import jadex.commons.gui.JSplitPanel;
import jadex.commons.gui.SGUI;
import jadex.rules.state.IOAVState;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.border.BevelBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *  A panel showing the internals of a BDI agent.
 */
public class BDIViewerPanel extends JPanel
{
	//-------- constants --------
	
	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"belief",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/bulb2.png"),
		"beliefset",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/bulbs2.png"),
		"achieve",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2a.png"),
		"perform",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2p.png"),
		"maintain",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2m.png"),
		"query",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2q.png"),
		"goal",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2.png"),
		"plan",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/plan2.png")
	});
	
	//-------- attributes ---------
	
	/** The external access to the agent. */
	protected IExternalAccess	access;
	
	/** The beliefs. */
	protected List	beliefs;
	
	/** The selected belief (if any). */
	protected BeliefInfo	selbel;
	
	/** The goals. */
	protected List	goals;
	
	/** The plans. */
	protected List	plans;
	
	/** The component listener. */
	protected IComponentListener	listener;
	
	//--------- constructors --------
	
	/**
	 *  Create a BDI viewer panel.
	 */
	public BDIViewerPanel(IExternalAccess access)
	{
		this.access	= access;
		this.beliefs	= new SortedList(new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				BeliefInfo	info1	= (BeliefInfo)o1;
				BeliefInfo	info2	= (BeliefInfo)o2;
				int	caps1	= new StringTokenizer(info1.getType(), ".").countTokens();
				int	caps2	= new StringTokenizer(info2.getType(), ".").countTokens();
				return caps1!=caps2 ? caps1-caps2 : info1.getType().compareTo(info2.getType());
			}
		}, true);
		this.goals	= new SortedList(new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				GoalInfo	info1	= (GoalInfo)o1;
				GoalInfo	info2	= (GoalInfo)o2;
				int	caps1	= new StringTokenizer(info1.getType(), ".").countTokens();
				int	caps2	= new StringTokenizer(info2.getType(), ".").countTokens();
				return caps1!=caps2 ? caps1-caps2 : info1.getType().compareTo(info2.getType());
			}
		}, true);
		this.plans	= new SortedList(new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				PlanInfo	info1	= (PlanInfo)o1;
				PlanInfo	info2	= (PlanInfo)o2;
				int	caps1	= new StringTokenizer(info1.getType(), ".").countTokens();
				int	caps2	= new StringTokenizer(info2.getType(), ".").countTokens();
				return caps1!=caps2 ? caps1-caps2 : info1.getType().compareTo(info2.getType());
			}
		}, true);
		
		JPanel	beliefpanel	= new JPanel(new BorderLayout());
		beliefpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "Beliefs"));
		final AbstractTableModel	beliefmodel	= new AbstractTableModel()
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
				BeliefInfo	info	= (BeliefInfo)beliefs.get(row);
				if(col==0)
				{
					ret	= info.getType()+"#"+info.getId();
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
		goalpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "Goals"));
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
		planpanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED), "Plans"));
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
		this.add(sp2, BorderLayout.CENTER);
		
		listener = new IComponentListener()
		{
			protected IFilter filter = new IFilter()
			{
				@XMLClassname("filter")
				public boolean filter(Object obj)
				{
					IComponentChangeEvent cce = (IComponentChangeEvent)obj;
					return cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_FACT)
						|| cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_GOAL)
						|| cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_PLAN);
				}
			};
			
			public IFilter getFilter()
			{
				return filter;
			}
			
			public IFuture eventOccured(final IComponentChangeEvent cce)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						List beliefsel = getTableSelection(belieftable, beliefs);
						List goalsel = getTableSelection(goaltable, goals);
						List plansel = getTableSelection(plantable, plans);
						
						handleEvent(cce);
						
						updateTable(belieftable, beliefs, beliefsel);
						updateTable(goaltable, goals, goalsel);
						updateTable(plantable, plans, plansel);
						
						updateSelectedBelief(belieftable, factmodel);
					}
					
					public void handleEvent(IComponentChangeEvent event)
					{
						// todo: hide decomposing bulk events
						if(cce.getBulkEvents().length>0)
						{
							IComponentChangeEvent[] events = cce.getBulkEvents();
							for(int i=0; i<events.length; i++)
							{
								eventOccured(events[i]);
							}
						}
						
						else if(cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_FACT))
						{
							// Hack!!! create/disposal only for facts, not for beliefs, just check for changes, removal not supported.
							int	index	= beliefs.indexOf(cce.getDetails());
							if(index!=-1)
							{
								BeliefInfo	newinfo	= (BeliefInfo)cce.getDetails();
								BeliefInfo	oldinfo	= (BeliefInfo)beliefs.remove(index);
								newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
								beliefs.add(index, newinfo);
							}
							else
							{
								beliefs.add(cce.getDetails());
							}
						}
						
						else if(cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_GOAL))
						{
							if(IComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()))
							{
								goals.add(cce.getDetails());
							}
							else if(IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()))
							{
								goals.remove(cce.getDetails());
							}
							else if(IComponentChangeEvent.EVENT_TYPE_MODIFICATION.equals(cce.getEventType()))
							{
								int	index	= goals.indexOf(cce.getDetails());
								if(index!=-1)
								{
									GoalInfo	newinfo	= (GoalInfo)cce.getDetails();
									GoalInfo	oldinfo	= (GoalInfo)goals.remove(index);
									newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
									goals.add(index, newinfo);
								}
							}
						}
						
						else if(cce.getSourceCategory().equals(IComponentChangeEvent.SOURCE_CATEGORY_PLAN))
						{
							if(IComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType()))
							{
								plans.add(cce.getDetails());
							}
							else if(IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(cce.getEventType()))
							{
								plans.remove(cce.getDetails());
							}
							else if(IComponentChangeEvent.EVENT_TYPE_MODIFICATION.equals(cce.getEventType()))
							{
								int	index	= plans.indexOf(cce.getDetails());
								if(index!=-1)
								{
									PlanInfo	newinfo	= (PlanInfo)cce.getDetails();
									PlanInfo	oldinfo	= (PlanInfo)plans.remove(index);
									newinfo.setType(oldinfo.getType());	// Hack!!! Keep capability information which is unavailable for modified events.
									plans.add(index, newinfo);
								}
							}
						}
					}
				});
				return IFuture.DONE;
			}
		};

		final IComponentListener lis = listener;
		access.scheduleImmediate(new IComponentStep()
		{
			@XMLClassname("installListener")
			public Object execute(IInternalAccess ia)
			{
				BDIInterpreter	interpreter	= BDIInterpreter.getInterpreter(((CapabilityFlyweight)ia).getState());
				
				// Post current state to remote listener
				List	events	= new ArrayList();
				getInitialEvents(ia, interpreter.getState(), interpreter.getAgent(), events);
				lis.eventOccured(new BulkComponentChangeEvent((IComponentChangeEvent[])events.toArray(new IComponentChangeEvent[events.size()])));
				
				ia.addComponentListener(lis);
				return null;
			}
		});
	}
	
	//-------- helper methods --------
	
	/**
	 *  Generate added events for the current goals
	 */
	protected static void	getInitialEvents(IInternalAccess ia, IOAVState state, Object capa, List events)
	{
		// Beliefs of this capability.
		Collection	beliefs	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_beliefs);
		if(beliefs!=null)
		{
			for(Iterator it=beliefs.iterator(); it.hasNext(); )
			{
				Object	belief	= it.next();
				BeliefInfo	info = BeliefInfo.createBeliefInfo(state, belief, capa);
				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_FACT, info.getType(), belief.toString(), ia.getComponentIdentifier(), info));
			}
		}
		
		// Belief sets of this capability.
		Collection	beliefsets	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_beliefsets);
		if(beliefsets!=null)
		{
			for(Iterator it=beliefsets.iterator(); it.hasNext(); )
			{
				Object	beliefset	= it.next();
				BeliefInfo	info = BeliefInfo.createBeliefInfo(state, beliefset, capa);
				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_FACT, info.getType(), beliefset.toString(), ia.getComponentIdentifier(), info));
			}
		}
		
		// Goals of this capability.
		Collection	goals	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_goals);
		if(goals!=null)
		{
			for(Iterator it=goals.iterator(); it.hasNext(); )
			{
				Object	goal	= it.next();
				GoalInfo	info = GoalInfo.createGoalInfo(state, goal, capa);
				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_GOAL, info.getType(), goal.toString(), ia.getComponentIdentifier(), info));
			}
		}
		
		// Plans of this capability.
		Collection	plans	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_plans);
		if(plans!=null)
		{
			for(Iterator it=plans.iterator(); it.hasNext(); )
			{
				Object	plan	= it.next();
				PlanInfo	info = PlanInfo.createPlanInfo(state, plan, capa);
				events.add(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION, IComponentChangeEvent.SOURCE_CATEGORY_PLAN, info.getType(), plan.toString(), ia.getComponentIdentifier(), info));
			}
		}
		
		// Recurse for sub capabilities.
		Collection	capas	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_subcapabilities);
		if(capas!=null)
		{
			for(Iterator it=capas.iterator(); it.hasNext(); )
			{
				getInitialEvents(ia, state, state.getAttributeValue(it.next(), OAVBDIRuntimeModel.capabilityreference_has_capability), events);
			}
		}
	}
	
	/**
	 *  Dispose the panel.
	 */
	public IFuture	dispose()
	{
		final IComponentListener lis = listener;
		return access.scheduleImmediate(new IComponentStep()
		{
			@XMLClassname("removeListener")
			public Object execute(IInternalAccess ia)
			{
				ia.removeComponentListener(lis);
				return null;
			}
		});

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
}
