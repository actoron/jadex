package jadex.tools.simcenter;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.collection.SCollection;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.jtable.ObjectTableModel;
import jadex.xml.annotation.XMLClassname;
import jadex.xml.annotation.XMLIncludeFields;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *  The timer panel.
 */
public class TimerPanel	extends JPanel
{	
	//-------- attributes --------

	/** The sim center panel. */
	protected SimCenterPanel simp;

	/** The timer count. */
	protected long eventcnt;
	
	/** The table model. */
	protected ObjectTableModel model;
	
	/** The table. */
	protected JTable timerst;
	
	/** The update flag. */
	protected JCheckBox	update;

	/** The saved row colors. */
	protected Map rowcols;

	/** The last known timer entries. */
	protected TimerEntries	lastentries;
		
	//-------- constructors --------
	
	/**
	 *  Create a timer panel.
	 */
	public TimerPanel(SimCenterPanel simp)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		this.simp = simp;
		setLayout(new BorderLayout());
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Active Timers "));
		//model = new ObjectTableModel(new String[]{"Number", "Timepoint", "Timed Object"});
		model = new ObjectTableModel(new String[]{"Timepoint", "Timed Object"});
		timerst = new JTable(model);
		JScrollPane sp = new JScrollPane(timerst);
		rowcols = SCollection.createHashMap();
		
		update = new JCheckBox("Update timer events", true);
		update.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				assert SwingUtilities.isEventDispatchThread();
				
				setActive(update.isSelected());
				if(!update.isSelected())
				{
					model.removeAllRows();
				}
			}
		});
		
		this.add(update, "North");
		this.add(sp, "Center");
		this.eventcnt = 0;
		
		timerst.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focus, int row, int column)
			{
				assert SwingUtilities.isEventDispatchThread();
				
				Component comp = super.getTableCellRendererComponent(table,
					value, selected, focus, row, column);
				setOpaque(true);
				
				if(!selected)
				{
					Color col = (Color)rowcols.get((model.getObjectForRow(row)));
					if(col!=null)
						comp.setBackground(col);
					else
						comp.setBackground(table.getBackground());
				}
				return comp;
			}
		});
				
		setActive(true);
	}
	
	/**
	 *  Update the view.
	 */
	public void	updateView()
	{
		assert SwingUtilities.isEventDispatchThread();

		if(lastentries!=null)
		{
			updateView(lastentries);
		}
	}
	
	/**
	 *  Update the view.
	 */
	public void	updateView(TimerEntries entries)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		this.lastentries	= entries;
		
		if(update.isSelected())
		{
			//System.out.println(SUtil.arrayToString(t));
			model.removeAllRows();
		
			Color first = Color.WHITE;
			Color sec = new Color(224, 226, 229);
			
			for(int i=0; i<entries.times.length; i++)
			{
				//model.addRow(new String[]{""+eventcnt++, ""+t[i].getNextTimepoint(), 
				//	""+((jadex.commons.concurrent.Timer)t[i]).getTimedObject()}, t[i]);
				model.addRow(new String[]{simp.formatTime(entries.times[i]), entries.objects[i]}, new Long(entries.times[i]));
				Color col = (Color)rowcols.get(new Long(entries.times[i]));
				if(col==null)
				{
					// Find out first color.
					if(i==0)
					{
						col = first;
					}
					else
					{
						Color tmp = (Color)rowcols.get(new Long(entries.times[i-1]));
						boolean same = entries.times[i] == entries.times[i-1];
							col = same? tmp: (tmp==first? sec: first);
						//System.out.println("...color "+i+" "+col);
					}
				}
				if(i==0)
					rowcols.clear();
				rowcols.put(new Long(entries.times[i]), col);
			}
		}
	}

	/**
	 *  Activate / deactivate updates.
	 */
	public void	setActive(final boolean active)
	{
		// Called from external -> update check box
		if(update.isSelected()!=active)
		{
			update.setSelected(active);
		}
		
		// Called from check box -> change state.
		else
		{
			final IRemoteChangeListener	rcl	= new IRemoteChangeListener()
			{
				public IFuture changeOccurred(ChangeEvent event)
				{
					handleEvent(event);
					return IFuture.DONE;
				}
				
				public void	handleEvent(final ChangeEvent event)
				{
					if(RemoteChangeListenerHandler.EVENT_BULK.equals(event.getType()))
					{
						Collection	events	= (Collection)event.getValue();
						for(Iterator it=events.iterator(); it.hasNext(); )
						{
							handleEvent((ChangeEvent)it.next());
						}
					}
					else
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								updateView((TimerEntries)event.getValue());
							}
						});
					}
				}
			};
			
			simp.getComponentForService().addResultListener(new SwingDefaultResultListener(TimerPanel.this)
			{
				public void customResultAvailable(Object result)
				{
					IExternalAccess	access	= (IExternalAccess)result;
					final String	id	= "TimerPanel"+TimerPanel.this.hashCode()+"@"+simp.jcc.getJCCAccess().getComponentIdentifier();
					final ISimulationService	simservice	= simp.getSimulationService();
					access.scheduleStep(new IComponentStep<Void>()
					{
						@XMLClassname("addListener")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							if(active)
							{
								IClockService	cs	= simservice.getClockService();
								RemoteTimerChangeListener	rccl	= new RemoteTimerChangeListener(id, ia, rcl, cs);
//								System.out.println("register listener: "+id);
								simservice.getClockService().addChangeListener(rccl);
								
								// Initial event.
								rccl.elementChanged("timers", TimerEntries.getTimerEntries(cs));
							}
							else
							{
								simservice.getClockService().removeChangeListener(new RemoteTimerChangeListener(id, ia, rcl, simservice.getClockService()));								
//								System.out.println("deregister listener: "+id);
							}
							return IFuture.DONE;
						}
					});
				}
			});
		}
	}
	
	//--------- helper classes --------
	
	/**
	 *  Information about the timers to be transferred.
	 */
	@XMLIncludeFields
	public static class TimerEntries
	{
		//-------- attributes --------
		
		/** The times. */
		public long[]	times;
		
		/** The objects. */
		public String[]	objects;
		
		//-------- constructors --------
		
		/**
		 *  Bean constructor.
		 */
		public TimerEntries()
		{
		}
		
		/**
		 *  Create timer entries
		 */
		public TimerEntries(long[] times, String[] objects)
		{
			this.times	= times;
			this.objects	= objects;
		}
		
		//-------- methods --------
		
		/**
		 *  The hash code.
		 *  Overridden to have only one clock state per update.
		 */
		public int hashCode()
		{
			return 123;
		}
		
		/**
		 *  Test if two objects are equal.
		 *  Overridden to have only one clock state per update.
		 */
		public boolean equals(Object obj)
		{
			return obj instanceof TimerEntries;
		}
		
		//-------- helper method --------
		
		/**
		 *  Get the current timer entries.
		 *  Only to be called with local clock service!
		 */
		public static TimerEntries	getTimerEntries(IClockService cs)
		{
			ITimer	next	= cs.getNextTimer();
			ITimer[]	t	= cs.getTimers();
			long[]	times;
			String[]	objects;
			// If next timer is tick timer add to list.
			// Todo: tick timer should be in list?
			if(next!=null && (t==null || t.length==0))
			{
				times	= new long[]{next.getNotificationTime()};
				objects	= new String[]{next.getTimedObject().toString()};
			}
			else if(next!=null && !next.equals(t[0]))
			{
				times	= new long[t.length+1];
				objects	= new String[t.length+1];
				times[0]	= next.getNotificationTime();
				objects[0]	= next.getTimedObject().toString();
				for(int i=0; i<t.length; i++)
				{
					times[i+1]	= t[i].getNotificationTime();
					objects[i+1]	= t[i].getTimedObject().toString();
				}
			}
			else
			{
				times	= new long[t.length];
				objects	= new String[t.length];
				for(int i=0; i<t.length; i++)
				{
					times[i]	= t[i].getNotificationTime();
					objects[i]	= t[i].getTimedObject().toString();
				}
			}
			return new TimerEntries(times, objects);
		}
	}
	
	/**
	 *  The remote clock change listener.
	 */
	public static class RemoteTimerChangeListener	extends RemoteChangeListenerHandler	implements IChangeListener
	{
		//-------- attributes --------
		
		/** The clock service. */
		protected IClockService	cs;
		
		//-------- constructors --------
		
		/**
		 *  Create a BPMN listener.
		 */
		public RemoteTimerChangeListener(String id, IInternalAccess instance, IRemoteChangeListener rcl, IClockService cs)
		{
			super(id, instance, rcl);
			this.cs	= cs;
		}
		
		//-------- IChangeListener interface --------
		
		/**
		 *  Called when the process executes.
		 */
		public void changeOccurred(ChangeEvent event)
		{
			// Use schedule step as clock runs on its own thread. 
			instance.getExternalAccess().scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					elementChanged("timers", TimerEntries.getTimerEntries(cs));
					return IFuture.DONE;
				}
			});
		}

		/**
		 *  Remove local listeners.
		 */
		protected void dispose()
		{
			super.dispose();
			
			cs.removeChangeListener(this);
//			System.out.println("dispose: "+id);
		}
	}
}
