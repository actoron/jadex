package jadex.tools.comanalyzer.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.TableOrder;

import jadex.tools.comanalyzer.Component;
import jadex.tools.comanalyzer.Message;
import jadex.tools.comanalyzer.MessageFilterMenu;
import jadex.tools.comanalyzer.PaintMaps;
import jadex.tools.comanalyzer.ToolCanvas;
import jadex.tools.comanalyzer.ToolTab;
import jadex.tools.comanalyzer.chart.ChartLabelGenerator.KeyRenderer;


/**
 * The container for the chart.
 */
public class ChartCanvas extends ToolCanvas
{

	// -------- constants --------

	/** The chart type for a pie chart */
	public static final int CHARTTYPE_PIECHART = 0;

	/** The chart type for a bar chart */
	public static final int CHARTTYPE_BARCHART = 1;

	// -------- attributes --------

	/** The list of messages displayed in the chart */
	protected List visible_messages;

	/** The list of components displayed in the chart */
	protected List visible_components;

	/** The dataset for sent messages */
	protected CategoryPieDataset dataset_sent;

	/** The dataset for received messages */
	protected CategoryPieDataset dataset_received;

	/** The dataset for messages distribution */
	protected CategoryPieDataset dataset_total;

	/** The JFreeChart chart panel */
	protected org.jfree.chart.ChartPanel chartPanel;

	/** The chart */
	protected JFreeChart chart;

	/** The chart type (pie vs bar) */
	protected int chartType;

	/** The paint mode */
	protected int paintMode;

	/** If labels should be displayed */
	protected boolean showLabels;

	/** If the legend should be displayed */
	protected boolean showLegend;

	/**
	 * If labels should be displayed, even if they dont fit. (Only applies for
	 * the bar chart.)
	 */
	protected boolean forceLabels;

	// -------- constructors --------

	/**
	 * Constructor for the container.
	 * 
	 * @param tooltab The tooltab.
	 */
	public ChartCanvas(ToolTab tool)
	{
		super(tool);

		this.showLabels = true;
		this.forceLabels = false;
		this.showLegend = true;
		this.chartType = CHARTTYPE_PIECHART;
		this.paintMode = PaintMaps.PAINTMODE_CONVERSATION;

		this.visible_messages = new ArrayList();
		this.visible_components = new ArrayList();

		this.dataset_sent = new CategoryPieDataset();
		this.dataset_received = new CategoryPieDataset();
		this.dataset_total = new CategoryPieDataset();

		chart = chartType == CHARTTYPE_PIECHART ? createPieChart() : createBarChart();

		chartPanel = new org.jfree.chart.ChartPanel(chart);
		chartPanel.setMouseZoomable(true, true);
		chartPanel.setPopupMenu(null); // deactivate standard popup

		this.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER, chartPanel);

		chartPanel.addMouseListener(new ChartMouseListener());

	}

	// -------- ToolCanvas methods --------

	/**
	 * Update a message by adding it, if the message can be displayed or
	 * removing it if present.
	 * 
	 * @param message The message to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new messages)
	 */
	public void updateMessage(Message message, boolean isPresent)
	{

		if(message.getEndpoints() != null)
		{
			if(!visible_messages.contains(message))
			{
				addMessage(message);
			}
		}
		else if(isPresent)
		{
			removeMessage(message);
		}

	}

	/**
	 * Removes a message.
	 * 
	 * @param message The message to remove.
	 */
	public void removeMessage(Message message)
	{

		String type = null;
		switch(paintMode)
		{
			case PaintMaps.PAINTMODE_CONVERSATION:
				type = Message.CONVERSATION_ID;
				break;
			case PaintMaps.PAINTMODE_PERFORMATIV:
				type = Message.PERFORMATIVE;
				break;
			case PaintMaps.PAINTMODE_PROTOCOL:
				type = Message.PROTOCOL;
				break;
		}

		if(paintMode == PaintMaps.COLOR_COMPONENT)
		{
			dataset_sent.removeElement(message, "sent", message.getSender());
			dataset_received.removeElement(message, "received", message.getReceiver());
			dataset_total.removeElement(message, "sent", message.getSender());
			dataset_total.removeElement(message, "received", message.getReceiver());
		}
		else
		{
			String key = (String)message.getParameter(type);
			dataset_sent.removeElement(message, key, message.getSender());
			dataset_received.removeElement(message, key, message.getReceiver());
			dataset_total.removeElement(message, type, key);
		}

		visible_messages.remove(message);

	}

	/**
	 * Updates an agent by adding it, if the agent can be displayed or removing
	 * it if present.
	 * 
	 * @param agent The agent to add.
	 * @param isPresent <code>true</code> if removal is skipped. (Can be
	 * applied to new agents)
	 */
	public void updateComponent(Component agent, boolean update)
	{
		if(agent.isVisible())
		{
			if(!visible_components.contains(agent))
			{
				addAgent(agent);
			}
		}
		else if(update)
		{
			removeComponent(agent);
		}
		return;
	}

	/**
	 * Removes an agent.
	 * 
	 * @param agent The agent to remove.
	 */
	public void removeComponent(Component agent)
	{
		visible_components.remove(agent);
	}

	/**
	 * Clears the canvas and datasets and lists.
	 */
	public void clear()
	{
		dataset_sent.clear();
		dataset_received.clear();
		dataset_total.clear();
		visible_messages.clear();
		visible_components.clear();
	}

	/**
	 * Repaints the chart.
	 */
	public void repaintCanvas()
	{
		chart.fireChartChanged();
	}

	// -------- ChartCanvas methods --------

	/**
	 * Adds an agent.
	 * 
	 * @param agent The agent to add.
	 */
	public void addAgent(Component agent)
	{
		visible_components.add(agent);
	}

	/**
	 * Ad a message.
	 * 
	 * @param message The message to add.
	 */
	public void addMessage(Message message)
	{

		String type = null;
		switch(paintMode)
		{
			case PaintMaps.PAINTMODE_CONVERSATION:
				type = Message.CONVERSATION_ID;
				break;
			case PaintMaps.PAINTMODE_PERFORMATIV:
				type = Message.PERFORMATIVE;
				break;
			case PaintMaps.PAINTMODE_PROTOCOL:
				type = Message.PROTOCOL;
				break;
		}

		if(paintMode == PaintMaps.COLOR_COMPONENT)
		{
			dataset_sent.addElement(message, "sent", message.getSender());
			dataset_received.addElement(message, "received", message.getReceiver());
			dataset_total.addElement(message, "sent", message.getSender());
			dataset_total.addElement(message, "received", message.getReceiver());
		}
		else
		{
			String key = (String)message.getParameter(type);
			dataset_sent.addElement(message, key, message.getSender());
			dataset_received.addElement(message, key, message.getReceiver());
			dataset_total.addElement(message, type, key);
		}

		visible_messages.add(message);

	}

	/**
	 * @return The paint mode.
	 */
	public int getPaintMode()
	{
		return paintMode;
	}

	/**
	 * Set the paint mode.
	 * 
	 * @param paintMode The paint mode.
	 */
	public void setPaintMode(int paintMode)
	{
		this.paintMode = paintMode;

		Plot plot = chart.getPlot();

		// for the bar chart one must use the bar renderer
		if(plot instanceof CategoryPlot)
		{
			CategoryPlot catplot = (CategoryPlot)plot;
			((ChartGroupedStackedBarRenderer)catplot.getRenderer()).setPaintMode(paintMode);
		}

		// for the pie chart one must use the plot
		if(plot instanceof ChartMultiplePiePlot)
		{
			ChartMultiplePiePlot pieplot = (ChartMultiplePiePlot)plot;
			pieplot.setPaintMode(paintMode);
		}

		// clear datasets and adding all the visible items again
		dataset_sent.clear();
		dataset_received.clear();
		dataset_total.clear();

		// copy visible items and clear original
		List agents = new ArrayList(visible_components);
		List messages = new ArrayList(visible_messages);
		visible_components.clear();
		visible_messages.clear();

		// add visible items again
		for(Iterator iter = agents.iterator(); iter.hasNext();)
		{
			Component agent = (Component)iter.next();
			addAgent(agent);
		}
		for(Iterator iter = messages.iterator(); iter.hasNext();)
		{
			Message message = (Message)iter.next();
			addMessage(message);
		}

	}

	/**
	 * @return The chart type.
	 */
	public int getChartType()
	{
		return chartType;
	}

	/**
	 * Sets the type of the chart.
	 * 
	 * @param chartType The chart type.
	 */
	public void setChartType(int chartType)
	{

		this.chartType = chartType;

		// first cleanup for gc
		if(chart.getPlot() instanceof CategoryPlot)
		{
			GroupedCategoryDataset dataset = (GroupedCategoryDataset)chart.getCategoryPlot().getDataset();
			dataset.cleanup(); // needed for circle references
			((CategoryPlot)chart.getPlot()).setDataset(null);
		}
		if(chart.getPlot() instanceof MultiplePiePlot)
		{
			((MultiplePiePlot)chart.getPlot()).setDataset(null);
		}

		chart = chartType == CHARTTYPE_PIECHART ? createPieChart() : createBarChart();
		chartPanel.setChart(chart);
	}

	/**
	 * @return <code>true</code> if labels are shown.
	 */
	public boolean isShowLabels()
	{
		return showLabels;
	}

	/**
	 * @param showLabels <code>true</code> if labels are to be shown.
	 */
	public void setShowLabels(boolean showLabels)
	{
		this.showLabels = showLabels;

		Plot plot = chart.getPlot();

		if(plot instanceof MultiplePiePlot)
		{
			PiePlot pieplot = (PiePlot)((MultiplePiePlot)plot).getPieChart().getPlot(); // ????
			ChartLabelGenerator generator = new ChartLabelGenerator(ChartLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT);
			generator.setDefaultRenderer(Component.class, new AgentKeyRenderer());
			pieplot.setLabelGenerator(showLabels ? generator : null);
		}
		if(plot instanceof CategoryPlot)
		{
			((CategoryPlot)plot).getRenderer().setBaseItemLabelsVisible(showLabels);
		}

	}

	/**
	 * @return <code>true</code> if the legend is shown.
	 */
	public boolean isShowLegend()
	{
		return showLegend;
	}

	/**
	 * @param showLegend <code>true</code> if the legend is to be shown.
	 */
	public void setShowLegend(boolean showLegend)
	{
		this.showLegend = showLegend;

		if(showLegend)
		{
			chart.addLegend(createLegend(chart.getPlot()));
		}
		else
		{
			chart.removeLegend();

		}
	}

	/**
	 * @return <code>true</code> if labels are forced to be displayed
	 * regardless of their size.
	 */
	public boolean isForceLabels()
	{
		return forceLabels;
	}

	/**
	 * An option for the barchart to display labels, even if they dont fit in
	 * the bar.
	 * 
	 * @param forceLabels <code>true</code> if labels are forced to
	 * bedisplayed regardless of their size.
	 */
	public void setForceLabels(boolean forceLabels)
	{
		this.forceLabels = forceLabels;

		// sanity check
		if(chartType == CHARTTYPE_PIECHART)
		{
			return;
		}

		BarRenderer renderer = (BarRenderer)chart.getCategoryPlot().getRenderer();
		if(forceLabels)
		{
			ItemLabelPosition itemlabelposition = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0.0D);
			renderer.setPositiveItemLabelPositionFallback(itemlabelposition);
			renderer.setNegativeItemLabelPositionFallback(itemlabelposition);
		}
		else
		{
			renderer.setNegativeItemLabelPositionFallback(null);
			renderer.setPositiveItemLabelPositionFallback(null);
		}

	}

	// -------- helper methods --------

	/**
	 * Creates the barchart.
	 * 
	 * @return The barchart.
	 */
	private JFreeChart createBarChart()
	{
		// The y-axis
		ValueAxis valueAxis = new NumberAxis();
		valueAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		// The x-axis
		ChartSubCategoryAxis categoryAxis = new ChartSubCategoryAxis(null);
		categoryAxis.setCategoryMargin(0.1D);

		// The renderer for the bar chart
		ChartGroupedStackedBarRenderer renderer = new ChartGroupedStackedBarRenderer(tooltab.getPaintMaps());
		renderer.setPaintMode(paintMode);
		renderer.setItemMargin(0.04D);

		// The label renderer. Labels are provided by the bar renderer.
		ChartLabelGenerator generator_simple = new ChartLabelGenerator(ChartLabelGenerator.DEFAULT_LABEL_FORMAT);
		generator_simple.setDefaultRenderer(Component.class, new AgentKeyRenderer());
		generator_simple.setDefaultRenderer(String.class, new GroupKeyRenderer());
		ChartLabelGenerator generator_advanced = new ChartLabelGenerator(ChartLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT);
		generator_advanced.setDefaultRenderer(Component.class, new AgentKeyRenderer());
		generator_advanced.setDefaultRenderer(String.class, new GroupKeyRenderer());

		renderer.setLegendItemLabelGenerator(generator_simple);
		// renderer.setLegendItemToolTipGenerator(legend_generator);
		renderer.setBaseItemLabelGenerator(generator_simple);
		renderer.setBaseToolTipGenerator(generator_advanced);

		// Wether to show labels or not
		renderer.setBaseItemLabelsVisible(showLabels);
		renderer.setDrawBarOutline(false);

		// fallback to force showing labels, even if they dont fit in the bar
		ItemLabelPosition itemlabelposition = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0.0D);
		renderer.setPositiveItemLabelPositionFallback(forceLabels ? itemlabelposition : null);
		renderer.setNegativeItemLabelPositionFallback(forceLabels ? itemlabelposition : null);

		// the plot
		CategoryPlot plot = new CategoryPlot(null, categoryAxis, valueAxis, renderer);
		plot.setOrientation(PlotOrientation.VERTICAL);

		// the chart
		JFreeChart chart = new JFreeChart(null, null, plot, false);

		// the datasets for grouping
		GroupedCategoryDataset combined = new GroupedCategoryDataset(chart);
		combined.addCategoryDataset(dataset_sent, "sent");
		combined.addCategoryDataset(dataset_received, "received");
		plot.setDataset(combined);

		if(showLegend)
		{
			chart.addLegend(createLegend(plot));
		}

		return chart;

	}

	/**
	 * Creates a piechart.
	 * 
	 * @return The piechart.
	 */
	private JFreeChart createPieChart()
	{
		ChartMultiplePiePlot plot = new ChartMultiplePiePlot(dataset_total, tooltab.getPaintMaps());
		plot.setPaintMode(paintMode);
		plot.setDataExtractOrder(TableOrder.BY_ROW);
		// plot.setBackgroundPaint(null);
		// plot.setOutlineStroke(null);

		// create label renderers
		ChartLabelGenerator generator_simple = new ChartLabelGenerator(ChartLabelGenerator.DEFAULT_LABEL_FORMAT);
		generator_simple.setDefaultRenderer(Component.class, new AgentKeyRenderer());
		ChartLabelGenerator generator_advanced = new ChartLabelGenerator(ChartLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT);
		generator_advanced.setDefaultRenderer(Component.class, new AgentKeyRenderer());

		// legend is generated in multiple pie plot
		// plot.setBaseItemLabelGenerator(showLabels ? generator_advanced :
		// null);
		// plot.setBaseToolTipGenerator(generator_advanced);
		plot.setLegendItemLabelGenerator(generator_simple);

		// labels and tooltips are generated in the plot of the piechart
		PiePlot pieplot = (PiePlot)plot.getPieChart().getPlot();
		pieplot.setLabelGenerator(showLabels ? generator_advanced : null);
		pieplot.setToolTipGenerator(generator_advanced);
		pieplot.setIgnoreNullValues(true);
		// pieplot.setLegendLabelGenerator(generator_simple);
		// pieplot.setLegendLabelToolTipGenerator(clg);

		JFreeChart chart = new JFreeChart(null, null, plot, false);

		if(showLegend)
		{
			chart.addLegend(createLegend(plot));
		}

		return chart;
	}

	/**
	 * Creates a legend for the specified plot.
	 * 
	 * @param plot The plot.
	 * @return The legend for the plot.
	 */
	private LegendTitle createLegend(Plot plot)
	{

		LegendTitle legend = new LegendTitle(plot);
		legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
		legend.setFrame(new LineBorder());
		legend.setBackgroundPaint(Color.white);
		legend.setPosition(RectangleEdge.BOTTOM);

		return legend;

	}

	/**
	 * The mouselistener for displaying the filter menu on pie sections and bar
	 * series.
	 */
	protected final class ChartMouseListener extends MouseAdapter
	{
		public void mouseReleased(MouseEvent e)
		{
			if(e.isPopupTrigger())
			{
				ChartEntity ce = chartPanel.getEntityForPoint(e.getX(), e.getY());

				List messages = null;
				if(ce instanceof CategoryItemEntity)
				{
					CategoryItemEntity entity = (CategoryItemEntity)ce;
					GroupedCategoryDataset dataset = (GroupedCategoryDataset)entity.getDataset();
					messages = dataset.getList(entity.getRowKey(), entity.getColumnKey());
				}
				if(ce instanceof PieSectionEntity)
				{
					PieSectionEntity entity = (PieSectionEntity)ce;
					messages = dataset_total.getList(entity.getSectionKey(), entity.getPieIndex());

				}
				if(messages != null)
				{
					MessageFilterMenu mpopup = new MessageFilterMenu(tooltab.getPlugin(), (Message[])messages.toArray(new Message[messages.size()]));
					mpopup.show(e.getComponent(), e.getX(), e.getY());
				}

			}

		}
	}

	// -------- inner classes --------

	/**
	 * A key renderer for groupnames that returns the original name.
	 */
	private final class GroupKeyRenderer implements KeyRenderer
	{
		public String render(Comparable key)
		{
			GroupedCategoryDataset dataset = (GroupedCategoryDataset)chart.getCategoryPlot().getDataset();
			Comparable orgRowKey = dataset.getOriginalRowKey(key);
			return orgRowKey.toString();
		}
	}

	/**
	 * A key renderer for agents that returns the name of the agent.
	 */
	public static final class AgentKeyRenderer implements KeyRenderer
	{
		public String render(Comparable key)
		{
			return ((Component)key).getId();
		}
	}

}
