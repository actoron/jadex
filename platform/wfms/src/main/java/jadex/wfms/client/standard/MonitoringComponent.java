package jadex.wfms.client.standard;

import jadex.bridge.IComponentChangeEvent;
import jadex.wfms.service.IWorkitemHandlerService;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.IntervalBarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.DateRange;

/** Component for displaying monitoring-related information.
 */
public class MonitoringComponent extends JPanel
{
	protected static final String PROCESS_COLUMN_NAME = "Process";
	
	protected static final List<String> EVENT_CATEGORY_WHITELIST = new ArrayList<String>();
	
	protected static final DefaultCategoryDataset EMPTY_DATASET = new DefaultCategoryDataset();
	
	static
	{
		EVENT_CATEGORY_WHITELIST.add(IComponentChangeEvent.SOURCE_CATEGORY_COMPONENT);
		EVENT_CATEGORY_WHITELIST.add(IComponentChangeEvent.SOURCE_CATEGORY_GOAL);
		EVENT_CATEGORY_WHITELIST.add(IComponentChangeEvent.SOURCE_CATEGORY_PLAN);
		EVENT_CATEGORY_WHITELIST.add(IWorkitemHandlerService.SOURCE_CATEGORY_WORKITEM);
		EVENT_CATEGORY_WHITELIST.add(IWorkitemHandlerService.SOURCE_CATEGORY_ACTIVITY);
	}
	
	/** Container for log events. */
	protected ComponentEventContainer eventcontainer;
	
	protected JTable processtable;
	
	protected AbstractTableModel processtablemodel;
	
	protected JSplitPane splitpane;
	
	protected Timer updatetimer;
	
	protected Map<String, String> tasknames;
	
	protected CategoryPlot plot;
	
	public MonitoringComponent()
	{
		super(new BorderLayout());
		this.tasknames = new HashMap<String, String>();
		eventcontainer = new ComponentEventContainer();
		
		splitpane = new JSplitPane();
		add(splitpane, BorderLayout.CENTER);
		
		addComponentListener(new ComponentListener()
		{
			public void componentShown(ComponentEvent e)
			{
				splitpane.setDividerLocation(0.4);
				MonitoringComponent.this.removeComponentListener(this);
			}
			
			public void componentResized(ComponentEvent e)
			{
			}
			
			public void componentMoved(ComponentEvent e)
			{
			}
			
			public void componentHidden(ComponentEvent e)
			{
			}
		});
		
		processtablemodel = new AbstractTableModel()
		{
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				return eventcontainer.getComponent(rowIndex);
			}
			
			public int getRowCount()
			{
				return eventcontainer.getComponentCount();
			}
			
			public int getColumnCount()
			{
				return 1;
			}
			
			public String getColumnName(int column)
			{
				return PROCESS_COLUMN_NAME;
			}
		};
		
		processtable = new JTable(processtablemodel);
		JScrollPane pscrollpane = new JScrollPane(processtable);
		splitpane.setLeftComponent(pscrollpane);
		
		processtable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				updateDataset();
			}
		});
		
		IntervalBarRenderer renderer = new IntervalBarRenderer()
		{
			protected Point2D calculateLabelAnchorPoint(
					ItemLabelAnchor anchor, double x, double y,
					PlotOrientation orientation)
			{
				return super.calculateLabelAnchorPoint(ItemLabelAnchor.INSIDE2, x, y, orientation);
			}
		};
		//renderer.setBaseSeriesVisibleInLegend(false);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator()
		{
		    public String generateLabel(CategoryDataset dataset, int row, int column)
		    {
		    	TaskSeriesCollection tsc = ((TaskSeriesCollection) dataset);
		    	Comparable columnkey = tsc.getColumnKey(column);
		    	if (tasknames.isEmpty())
		    		return "";
		    	return tasknames.get(tsc.getSeries(row).get(columnkey.toString()).getDescription());
		    }
		});
		
		CategoryAxis ca = new CategoryAxis();
		ca.setVisible(false);
		DateAxis dateaxis = new DateAxis();
		plot = new CategoryPlot(EMPTY_DATASET, ca, dateaxis, renderer);
		plot.setOrientation(PlotOrientation.HORIZONTAL);
		JFreeChart chart = new JFreeChart(plot);
		//chart = ChartFactory.createGanttChart("ABCD", "CatAxisLabel", "dateaxislabel", tsc, true, true, false);
		ChartPanel cpanel = new ChartPanel(chart);
		splitpane.setRightComponent(cpanel);
		
		updateDataset();
	}
	
	public void addLogEvent(IComponentChangeEvent event)
	{
		if (EVENT_CATEGORY_WHITELIST.contains(event.getSourceCategory()))
		{
			//System.out.println("adding " + event);
			if (event.getSourceType().startsWith("ActivationPlan_") || !(event.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_CREATION) || event.getEventType().equals(IComponentChangeEvent.EVENT_TYPE_DISPOSAL)))
				return;
			eventcontainer.addEvent(event);
			int select = processtable.getSelectionModel().getMinSelectionIndex();
			processtablemodel.fireTableDataChanged();
			processtable.getSelectionModel().setSelectionInterval(select, select);
			updateDataset();
		}
	}
	
	protected void updateDataset()
	{
		if (updatetimer != null)
		{
			updatetimer.stop();
			updatetimer = null;
		}
		//int loc = splitpane.getDividerLocation();
		int index = processtable.getSelectedRow();
		if (index >= 0)
		{
			tasknames.clear();
			TaskSeriesCollection tsc = new TaskSeriesCollection();
			long lowest = Long.MAX_VALUE;
			long highest = Long.MIN_VALUE;
			Long now = new Long(System.currentTimeMillis());
			for (Iterator<String> catit = EVENT_CATEGORY_WHITELIST.iterator(); catit.hasNext(); )
			{
				String cat = catit.next();
				Map<String, TreeSet<IComponentChangeEvent>> eset = eventcontainer.getComponentEvents(index, cat);
				if (eset == null)
					continue;
				
				List<Task> tasks = new ArrayList<Task>();
				
				for (Iterator<Map.Entry<String, TreeSet<IComponentChangeEvent>>> it = eset.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry<String, TreeSet<IComponentChangeEvent>> entry = it.next();
					TreeSet<IComponentChangeEvent> events = entry.getValue();
					IComponentChangeEvent creationevent = null;
					IComponentChangeEvent disposalevent = null;
					
					for (Iterator<IComponentChangeEvent> it2 = events.iterator(); it2.hasNext() && (creationevent == null || disposalevent == null); )
					{
						IComponentChangeEvent event = it2.next();
						if (IComponentChangeEvent.EVENT_TYPE_CREATION.equals(event.getEventType()))
							creationevent = event;
						else if (IComponentChangeEvent.EVENT_TYPE_DISPOSAL.equals(event.getEventType()))
							disposalevent = event;
					}
					
					if (creationevent != null)
					{
						String name = creationevent.getSourceCategory() + ": " + creationevent.getSourceType();
						String id = creationevent.getSourceName();
						tasknames.put(id, name);
						
						long low = creationevent.getTime();
						long high = now;
						if (disposalevent != null)
							high = disposalevent.getTime();
						
						if (low < lowest)
							lowest = low;
						
						if (high > highest)
							highest = high;
						
						Task task = new Task(id, new Date(creationevent.getTime()), new Date(high));
						tasks.add(task);
						
						if (creationevent.getTime() == 0)
						{
							System.out.println("Broken Event: " + creationevent);
						}
					}
				}
				
				Collections.sort(tasks, new Comparator<Task>()
				{
					public int compare(Task o1, Task o2)
					{
						return o1.getDuration().getStart().compareTo(o2.getDuration().getStart());
					}
				});
				
				TaskSeries ts = new TaskSeries(cat);
				for (Task t : tasks)
					ts.add(t);
				
				if (!ts.isEmpty())
					tsc.add(ts);
			}
			
			if (tsc.getSeriesCount() > 0)
			{
				long hldiff = highest - lowest;
				lowest -= hldiff * 0.1;
				if (highest != now)
					highest += hldiff * 0.1;
				
				//renderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_VERTICAL));
				
				plot.setDataset(tsc);
				plot.getRangeAxis().setRange(new DateRange(new Date(lowest), new Date(highest)));
				plot.configureRangeAxes();
				
				if (highest == now)
				{
					updatetimer = new Timer(500, new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							updateDataset();
						}
					});
					updatetimer.start();
				}
			}
			return;
		}
		
		plot.setDataset(EMPTY_DATASET);
		return;
	}
}
