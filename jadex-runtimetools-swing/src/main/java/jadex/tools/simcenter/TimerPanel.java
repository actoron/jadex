package jadex.tools.simcenter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import jadex.base.SRemoteClock;
import jadex.base.SRemoteClock.TimerEntries;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.collection.SCollection;
import jadex.commons.gui.future.SwingIntermediateDefaultResultListener;
import jadex.commons.gui.jtable.ObjectTableModel;

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
	protected Map<Long, Color> rowcols;

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
				model.addRow(new String[]{simp.formatTime(entries.times[i]), entries.objects[i]}, Long.valueOf(entries.times[i]));
				Color col = (Color)rowcols.get(Long.valueOf(entries.times[i]));
				if(col==null)
				{
					// Find out first color.
					if(i==0)
					{
						col = first;
					}
					else
					{
						Color tmp = (Color)rowcols.get(Long.valueOf(entries.times[i-1]));
						boolean same = entries.times[i] == entries.times[i-1];
							col = same? tmp: (tmp==first? sec: first);
						//System.out.println("...color "+i+" "+col);
					}
				}
				if(i==0)
					rowcols.clear();
				rowcols.put(Long.valueOf(entries.times[i]), col);
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
			String	id	= "TimerPanel"+TimerPanel.this.hashCode()+"@"+simp.jcc.getJCCAccess().getComponentIdentifier();
			ISimulationService	simservice	= simp.getSimulationService();
			
			if(active)
			{
				SRemoteClock.addTimerListener(id, simservice, simp.getJCC().getPlatformAccess())
					.addResultListener(new SwingIntermediateDefaultResultListener<TimerEntries>()
				{
					public void customIntermediateResultAvailable(TimerEntries result)
					{
						updateView(result);
					}
				});
			}
			else
			{
				SRemoteClock.removeTimerListener(id, simservice, simp.getJCC().getPlatformAccess());
			}
		}
	}

}
