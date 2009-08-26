package jadex.tools.simcenter;

import jadex.commons.collection.SCollection;
import jadex.commons.jtable.ObjectTableModel;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimer;
import jadex.service.clock.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *  The timer panel.
 */
public class TimerPanel extends AbstractTimePanel
{	
	//-------- attributes --------

	/** The sim center panel. */
	protected SimCenterPanel simp;

	/** The timer count. */
	protected long eventcnt;
	
	/** The table model. */
	protected ObjectTableModel model;
	
	/** The clock. */
	//protected IClock clock;
	//protected ExecutionContext context;
	
	/** The table. */
	protected JTable timerst;

	/** The saved row colors. */
	protected Map rowcols;
		
	//-------- constructors --------
	
	/**
	 *  Create a timer panel.
	 */
	public TimerPanel(SimCenterPanel simp)
	{
		super(simp.getPlatform());
		//this.clock = clock;
		//this.context = context;
		this.simp = simp;
		setLayout(new BorderLayout());
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Active Timers "));
		//model = new ObjectTableModel(new String[]{"Number", "Timepoint", "Timed Object"});
		model = new ObjectTableModel(new String[]{"Timepoint", "Timed Object"});
		timerst = new JTable(model);
		JScrollPane sp = new JScrollPane(timerst);
		rowcols = SCollection.createHashMap();
		
		final JCheckBox showtimers = new JCheckBox("Update timer events", true);
		showtimers.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				synchronized(TimerPanel.this)
				{
					setActive(showtimers.isSelected());
					if(!showtimers.isSelected())
					{
						model.removeAllRows();
					}
				}
			}
		});
		
		this.add(showtimers, "North");
		this.add(sp, "Center");
		this.eventcnt = 0;
		
		timerst.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focus, int row, int column)
			{
				Component comp = super.getTableCellRendererComponent(table,
					value, selected, focus, row, column);
				setOpaque(true);
				
				if(!selected)
				{
					Color col = (Color)rowcols.get(model.getObjectForRow(row));
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
	 *  Get the execution context.
	 *  @return The execution context.
	 * /
	public ExecutionContext getContext()
	{
		return simp.getControl();
	}*/
		
	/**
	 *  Get the clock.
	 *  @return The clock.
	 * /
	protected IClock getClock()
	{
		return clock;
	}*/

//	long time;
//	int cnt;		
		
	/**
	 *  Update the view.
	 */
	public synchronized void updateView()
	{
//		cnt++;
//		if(System.currentTimeMillis()-time>1000)
//		{
//			System.out.println("TimerPanel.updateView called "+cnt+" times.");
//			cnt	= 0;
//			time	= System.currentTimeMillis();
//		}

		if(!active)
			return;
		
		ITimer[] t = ((IClockService)getPlatform().getService(IClockService.class)).getTimers();
		//System.out.println(SUtil.arrayToString(t));
		model.removeAllRows();
	
//		Color first = timerst.getBackground();
//		Color sec = Color.yellow;
//		Color first = new Color(255, 247, 231);
//		Color sec = new Color(222, 239, 255);
		Color first = Color.WHITE;
		Color sec = new Color(224, 226, 229);
		
		if(t.length>0)
		{
			for(int i=0; i<t.length; i++)
			{
				//model.addRow(new String[]{""+eventcnt++, ""+t[i].getNextTimepoint(), 
				//	""+((jadex.commons.concurrent.Timer)t[i]).getTimedObject()}, t[i]);
				model.addRow(new String[]{simp.formatTime(t[i].getNotificationTime()), 
					""+((Timer)t[i]).getTimedObject()}, t[i]);
				//Long time = new Long(t[i].getNextTimepoint());
				//Color col = (Color)rowcols.get(time);
				Color col = (Color)rowcols.get(t[i]);
				if(col==null)
				{
					// Find out first color.
					if(i==0)
					{
						if(rowcols.size()==0 || t.length==1)
						{
							//col = Color.red;
							col = first;
						}
						else
						{
							boolean same = true;
							long time = t[0].getNotificationTime();
							for(int j=1; j<t.length && col==null; j++)
							{
								if(time!=t[j].getNotificationTime())
								{
									time = t[j].getNotificationTime();
									same = false;
								}
								Color tmp = (Color)rowcols.get(t[j]);
								if(tmp!=null)
								{
									//col = same? tmp: (tmp==Color.red? Color.green: Color.red);
									col = same? tmp: (tmp==first? sec: first);
								}
							}
						}
						//System.out.println("first color "+i+" "+col);
					}
					else
					{
						Color tmp = (Color)rowcols.get(t[i-1]);
						boolean same = t[i].getNotificationTime()==t[i-1].getNotificationTime();
						col = same? tmp: (tmp==first? sec: first);
						//System.out.println("...color "+i+" "+col);
					}
				}
				if(i==0)
					rowcols.clear();
				rowcols.put(t[i], col);
			}
			//System.out.println("end.....");
		}
		
		//System.out.println("saved: "+rowcols.size());
		
		/*Set timers = SUtil.arrayToSet(t);
		// First check which old timers are obsolete
		Set contained = SCollection.createHashSet();
		for(int i=model.getRowCount()-1; i>=0 ; i--)
		{
			if(!timers.contains(model.getObjectForRow(i)))
				model.removeRow(i);
			else
				contained.add(model.getObjectForRow(i));
		}
		
		// Secondly insert new timers.
		for(int i=0; i<t.length; i++)
		{
			if(!contained.contains(t[i]))
			{
				boolean added = false;
				for(int j=0; j<model.getRowCount() && !added; j++)
				{
					if(t[i].getNextTimepoint()<((ITimer)model.getObjectForRow(j)).getNextTimepoint())
					{
						model.insertRow(j, new String[]{""+eventcnt++, ""+t[i].getNextTimepoint(), 
							""+((jadex.commons.concurrent.Timer)t[i]).getTimedObject()}, t[i]);
						added = true;
					}
				}
				if(!added)
				{
					model.addRow(new String[]{""+eventcnt++, ""+t[i].getNextTimepoint(), 
						""+((jadex.commons.concurrent.Timer)t[i]).getTimedObject()}, t[i]);
				}
			}
		}*/
	}
}
