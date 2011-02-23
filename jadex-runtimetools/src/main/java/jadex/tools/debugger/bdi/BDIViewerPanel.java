package jadex.tools.debugger.bdi;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.rules.state.IOAVState;
import jadex.rules.state.IOAVStateListener;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
	
	/** The change event denoting a new goal. */
	public static final String	EVENT_GOAL_ADDED	= "goal-added";
	
	/** The change event denoting a changed goal. */
	public static final String	EVENT_GOAL_CHANGED	= "goal-changed";
	
	/** The change event denoting a dropped goal. */
	public static final String	EVENT_GOAL_REMOVED	= "goal-removed";
	
	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"achieve",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2a.png"),
		"perform",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2p.png"),
		"maintain",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2m.png"),
		"query",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2q.png"),
		"goal",	SGUI.makeIcon(BDIViewerPanel.class, "/jadex/tools/common/images/cloud2.png")
	});
	
	//-------- attributes ---------
	
	/** The external access to the agent. */
	protected IExternalAccess	access;
	
	/** The goals. */
	protected List	goals;
	
	//--------- constructors --------
	
	/**
	 *  Create a BDI viewer panel.
	 */
	public BDIViewerPanel(IExternalAccess access)
	{
		this.access	= access;
		this.goals	= new ArrayList();
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), "Goals"));
		this.setLayout(new BorderLayout());
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
		final JTable	table	= new JTable(goalmodel);
		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer()
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
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		
		final IRemoteChangeListener	rcl	= new IRemoteChangeListener()
		{
			public IFuture changeOccurred(final ChangeEvent event)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						handleEvent(event);
						goalmodel.fireTableDataChanged();
						table.repaint();
					}
					
					public void handleEvent(ChangeEvent event)
					{
						if(EVENT_GOAL_ADDED.equals(event.getType()))
						{
							goals.add(event.getValue());
						}
						else if(EVENT_GOAL_CHANGED.equals(event.getType()))
						{
							int	index	= goals.indexOf(event.getValue());
							if(index!=-1)
							{
								goals.remove(index);
								goals.add(index, event.getValue());
							}
						}
						else if(EVENT_GOAL_REMOVED.equals(event.getType()))
						{
							goals.remove(event.getValue());
						}
						else if(event.getValue() instanceof Collection)
						{
							for(Iterator it=((Collection)event.getValue()).iterator(); it.hasNext(); )
							{
								handleEvent((ChangeEvent)it.next());
							}
						}
					}
				});
				return IFuture.DONE;
			}
		};
		
		final String	id	= SUtil.createUniqueId("bpmnviewer");
		access.scheduleStep(new IComponentStep()
		{
			@XMLClassname("installListener")
			public Object execute(IInternalAccess ia)
			{
				// Hack!!! Shouldn't require cast!?
				IOAVState	state	= ((ElementFlyweight)ia).getState();
				BDIInterpreter	interpreter	= BDIInterpreter.getInterpreter(state);
				
				// Post current state to remote listener
				List	events	= new ArrayList();
				getInitialGoalEvents(state, interpreter.getAgent(), events);
				rcl.changeOccurred(new ChangeEvent(null, null, events));
				
				// Add listener for updates
				state.addStateListener(new BDIChangeListener(id, interpreter, rcl), false);
				return null;
			}
		});
	}
	
	//-------- helper methods --------
	
	/**
	 *  Create an info object for a goal.
	 */
	protected static GoalInfo	createGoalInfo(IOAVState state, Object goal)
	{
		String	id	= goal.toString();
		if(id.indexOf('@')!=-1)	// 'goal_<num>@stateid'
		{
			id	= id.substring(0, id.indexOf('@'));
		}
		if(id.startsWith("goal_"))	// 'goal_<num>@stateid'
		{
			id	= id.substring(5);
		}
		Object	mgoal	= state.getAttributeValue(goal, OAVBDIRuntimeModel.element_has_model);
		String	kind	= state.getType(mgoal).getName();
		kind	= kind.substring(1, kind.length()-4); // 'm<xyz>goal'
		return new GoalInfo(id, kind,
			(String)state.getAttributeValue(mgoal, OAVBDIMetaModel.modelelement_has_name),
			(String)state.getAttributeValue(goal, OAVBDIRuntimeModel.goal_has_lifecyclestate),
			(String)state.getAttributeValue(goal, OAVBDIRuntimeModel.goal_has_processingstate));
	}
	
	/**
	 *  Generate added events for the current goals
	 */
	protected static void	getInitialGoalEvents(IOAVState state, Object capa, List events)
	{
		// Goals of this capability.
		Collection	goals	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_goals);
		if(goals!=null)
		{
			for(Iterator it=goals.iterator(); it.hasNext(); )
			{
				events.add(new ChangeEvent(null, EVENT_GOAL_ADDED, createGoalInfo(state, it.next())));
			}
		}
		
		// Recurse for sub capabilities.
		Collection	capas	= state.getAttributeValues(capa, OAVBDIRuntimeModel.capability_has_subcapabilities);
		if(capas!=null)
		{
			for(Iterator it=capas.iterator(); it.hasNext(); )
			{
				getInitialGoalEvents(state, state.getAttributeValue(it.next(), OAVBDIRuntimeModel.capabilityreference_has_capability), events);
			}
		}
	}
	
	//-------- helper classes --------
	
	/**
	 *  The listener installed remotely in the BDI agent.
	 */
	public static class BDIChangeListener	implements IOAVStateListener
	{
		//-------- constants --------
		
		/** Update delay. */
		// todo: make configurable.
		protected static final long UPDATE_DELAY	= 100;	
		
		/** Maximum number of events per delay period. */
		// todo: make configurable.
		protected static final int MAX_EVENTS	= 5;
		
		//-------- attributes --------
		
		/** The id for remote listener deregistration. */
		protected String	id;
		
		/** The agent instance. */
		protected BDIInterpreter	instance;
		
		/** The change listener (proxy) to be informed about important changes. */
		protected IRemoteChangeListener	rcl;
		
		/** The added goals (if any). */
		protected Set	added;
		
		/** The changed goals (if any). */
		protected Set	changed;
		
		/** The removed goals (if any). */
		protected Set	removed;
		
		/** The update timer (if any). */
		protected Timer	timer;
		
		//-------- constructs --------
		
		/**
		 *  Create a BDI listener.
		 */
		public BDIChangeListener(String id, BDIInterpreter instance, IRemoteChangeListener rcl)
		{
			this.id	= id;
			this.instance	= instance;
			this.rcl	= rcl;
		}
		
		//-------- IOAVStateListener interface --------
		
		/**
		 *  Called when an object is removed.
		 */
		public void objectRemoved(Object id, OAVObjectType type)
		{
		}
		
		/**
		 *  Called when an object is modified.
		 */
		public void objectModified(Object id, OAVObjectType type, OAVAttributeType attr, Object oldvalue, Object newvalue)
		{
			if(instance.getState().getType(id).isSubtype(OAVBDIRuntimeModel.capability_type)
				&& OAVBDIRuntimeModel.capability_has_goals.equals(attr))
			{
				// Goal added.
				if(oldvalue==null && newvalue!=null)
				{
					final GoalInfo	info	= createGoalInfo(instance.getState(), newvalue);
					if(removed!=null && removed.contains(info))
					{
						removed.remove(info);
					}
					else
					{
						if(changed!=null)
							changed.remove(info);
						
						if(added==null)
							added	= new LinkedHashSet();
						added.add(info);
					}
				}
				
				// Goal removed
				else if(oldvalue!=null && newvalue==null)
				{
					final GoalInfo	info	= createGoalInfo(instance.getState(), oldvalue);
					if(added!=null && added.contains(info))
					{
						added.remove(info);
					}
					else
					{
						if(changed!=null)
							changed.remove(info);
						
						if(removed==null)
							removed	= new LinkedHashSet();
						removed.add(info);
					}
				}
			}
			
			// Goal changed.
			else if(instance.getState().getType(id).isSubtype(OAVBDIRuntimeModel.goal_type)
				&& (OAVBDIRuntimeModel.goal_has_lifecyclestate.equals(attr)
					|| OAVBDIRuntimeModel.goal_has_processingstate.equals(attr)))
			{
				final GoalInfo	info	= createGoalInfo(instance.getState(), id);
				if(removed==null || !removed.contains(info))
				{
					if(added!=null && added.contains(info))
					{
						// Replace added goal.
						added.remove(info);
						added.add(info);
					}
					else
					{					
						if(changed==null)
							changed	= new LinkedHashSet();
						changed.add(info);
					}
				}
			}
			
			if(removed!=null || added!=null || changed!=null)
			{
				startTimer();
			}
			
		}
		
		/**
		 *  Called when an object is added.
		 */
		public void objectAdded(Object id, OAVObjectType type, boolean root)
		{
		}

		protected void startTimer()
		{
			if(timer==null)
			{
				timer	= new Timer(true);
				timer.schedule(new TimerTask()
				{
					public void run()
					{
						instance.getAgentAdapter().invokeLater(new Runnable()
						{
							public void run()
							{
								List	events	= new ArrayList();
								timer	= null;
								if(removed!=null)
								{
									for(Iterator it=removed.iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
									{
										events.add(new ChangeEvent(null, EVENT_GOAL_REMOVED, it.next()));
										it.remove();
									}
								}
								if(added!=null)
								{
									for(Iterator it=added.iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
									{
										events.add(new ChangeEvent(null, EVENT_GOAL_ADDED, it.next()));
										it.remove();
									}
								}
								if(changed!=null)
								{
									for(Iterator it=changed.iterator(); events.size()<MAX_EVENTS && it.hasNext(); )
									{
										events.add(new ChangeEvent(null, EVENT_GOAL_CHANGED, it.next()));
										it.remove();
									}
								}
								
								if(removed!=null && removed.isEmpty())
									removed	= null;
								if(added!=null && added.isEmpty())
									added	= null;
								if(changed!=null && changed.isEmpty())
									changed	= null;
								
								if(!events.isEmpty())
								{
									rcl.changeOccurred(new ChangeEvent(null, null, events)).addResultListener(new IResultListener()
									{
										public void resultAvailable(Object result)
										{
//											System.out.println("update succeeded: "+desc);
										}
										public void exceptionOccurred(Exception exception)
										{
//											exception.printStackTrace();
											if(instance!=null)
											{
//												System.out.println("Removing listener due to failed update: "+RemoteCMSListener.this.id);
												try
												{
													instance.getState().removeStateListener(BDIChangeListener.this);
												}
												catch(RuntimeException e)
												{
//													System.out.println("Listener already removed: "+id);
												}
												instance	= null;	// Set to null to avoid multiple removal due to delayed errors. 
											}
										}
									});
								}
								
								if(removed!=null || added!=null || changed!=null)
								{
									startTimer();
								}
							}
						});						
					}
				}, UPDATE_DELAY);
			}
		}
	}
}
