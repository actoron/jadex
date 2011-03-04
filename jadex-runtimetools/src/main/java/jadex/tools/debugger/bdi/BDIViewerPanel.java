package jadex.tools.debugger.bdi;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.rules.state.IOAVState;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
	
	/** The change event prefix for goal events. */
	public static final String	EVENT_GOAL	= "goal";
	
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
						if(RemoteChangeListenerHandler.EVENT_BULK.equals(event.getType()))
						{
							for(Iterator it=((Collection)event.getValue()).iterator(); it.hasNext(); )
							{
								handleEvent((ChangeEvent)it.next());
							}
						}
						else if(event.getType().startsWith(EVENT_GOAL))
						{
							if(event.getType().endsWith(RemoteChangeListenerHandler.EVENT_ADDED))
							{
								goals.add(event.getValue());
							}
							if(event.getType().endsWith(RemoteChangeListenerHandler.EVENT_CHANGED))
							{
								int	index	= goals.indexOf(event.getValue());
								if(index!=-1)
								{
									goals.remove(index);
									goals.add(index, event.getValue());
								}
							}
							if(event.getType().endsWith(RemoteChangeListenerHandler.EVENT_REMOVED))
							{
								goals.remove(event.getValue());
							}
						}
					}
				});
				return IFuture.DONE;
			}
		};
		
		final String	id	= SUtil.createUniqueId("bdiviewer");
		access.scheduleImmediate(new IComponentStep()
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
				rcl.changeOccurred(new ChangeEvent(null, RemoteChangeListenerHandler.EVENT_BULK, events));
				
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
				events.add(new ChangeEvent(null, EVENT_GOAL+RemoteChangeListenerHandler.EVENT_ADDED, createGoalInfo(state, it.next())));
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
}
