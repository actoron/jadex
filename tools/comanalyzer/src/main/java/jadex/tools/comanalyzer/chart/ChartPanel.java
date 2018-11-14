package jadex.tools.comanalyzer.chart;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.tools.comanalyzer.ComanalyzerPlugin;
import jadex.tools.comanalyzer.PaintMaps;
import jadex.tools.comanalyzer.ToolCanvas;
import jadex.tools.comanalyzer.ToolTab;


/**
 * The tooltab for displaying messages in a chart. 
 */
public class ChartPanel extends ToolTab implements ActionListener
{

	// -------- attributes --------

	/** The container for the chart */
	protected ChartCanvas panelcan;

	// the option controls
	private JScrollPane options;

	private JSplitPane main;

	private JRadioButton g_mgraph;

	private JRadioButton g_dgraph;

	private JCheckBox l_labels;

	private JCheckBox l_legend;

	private JRadioButton d_message;

	private JRadioButton d_convid;

	private JRadioButton d_perform;

	private JRadioButton d_protocol;

	private JCheckBox l_label_force;

	// -------- constructors --------

	/**
	 * Creates a new phart panel
	 */
	public ChartPanel(ComanalyzerPlugin plugin)
	{
		super(plugin, "Chart", null);

		// Show Graphical User Interface
		panelcan = new ChartCanvas(this);

		// Show options
		options = new JScrollPane();
		addBottomControls(options);

		this.main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelcan, new JScrollPane(options));
		main.setOneTouchExpandable(true);
		main.setResizeWeight(1.0);
		//		main.setDividerLocation(65535);	// Proportional (1.0) doesn't work.

		// Initialize tool bar.
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.NORTH, SGUI.createToolBar("BDI Viewer Options", getActions()));
		this.add(BorderLayout.CENTER, main);

		// set divider location after components are created
		// to make options entirely visible 
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				double mainWidth = main.getSize().getWidth();
				double optionsWidth = options.getPreferredSize().getWidth();
				// consider scrollbar width if visible
				double scrollWidth = options.getVerticalScrollBar().isVisible() ? options.getVerticalScrollBar().getWidth() : 0;
				double	loc	= (mainWidth - optionsWidth - scrollWidth) / mainWidth;
				if(loc>=0 && loc<=1)	// Might be NaN, if plugin is switched before panel is shown.
					main.setDividerLocation(loc);

			}
		});

	}

	// -------- ToolTab methods --------

	/** 
	 * Returns the ChartCanvas.
	 */
	public ToolCanvas getCanvas()
	{
		return panelcan;
	}

	/** 
	 * Adds the panel actions to the common tooltab actions
	 * @return The array of actions.
	 */
	public Action[] getActions()
	{

		if(actions == null)
		{
			List actionlist = SUtil.arrayToList(super.getActions());
			actions = (Action[])actionlist.toArray((new Action[actionlist.size()]));
		}

		return this.actions;
	}

	// -------- ChartPanel methods --------


	/**
	 * Creates the option panel by adding the controls to the given scroll pane.
	 * @param options The scroll pane to which the controls will be added
	 */
	protected void addBottomControls(JScrollPane options)
	{

		JPanel control_panel = new JPanel(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = GridBagConstraints.RELATIVE;

		// graph controls
		Box graph_panel = Box.createVerticalBox();
		graph_panel.setBorder(BorderFactory.createTitledBorder("Chart"));
		ButtonGroup bg_graph = new ButtonGroup();
		g_mgraph = new JRadioButton("Pie Chart");
		g_mgraph.setSelected(panelcan.chartType == ChartCanvas.CHARTTYPE_PIECHART);
		g_mgraph.addActionListener(this);
		g_dgraph = new JRadioButton("Bar Chart");
		g_dgraph.setSelected(panelcan.chartType == ChartCanvas.CHARTTYPE_BARCHART);
		g_dgraph.addActionListener(this);
		bg_graph.add(g_mgraph);
		bg_graph.add(g_dgraph);
		graph_panel.add(g_mgraph);
		graph_panel.add(g_dgraph);
		control_panel.add(graph_panel, gbc);

		Box layout_panel = Box.createVerticalBox();
		layout_panel.setBorder(BorderFactory.createTitledBorder("Layout"));
		JPanel v_label_panel = new JPanel(new GridLayout(2, 0));
		l_labels = new JCheckBox("Show Labels");
		l_labels.setSelected(panelcan.showLabels);
		l_labels.addActionListener(this);
		l_label_force = new JCheckBox("Force Labels");
		l_label_force.setEnabled(l_labels.isSelected() && panelcan.chartType != ChartCanvas.CHARTTYPE_PIECHART);
		l_label_force.setSelected(panelcan.forceLabels);
		l_label_force.addActionListener(this);
		v_label_panel.add(l_labels);
		v_label_panel.add(l_label_force);

		JPanel v_legend_panel = new JPanel(new GridLayout(1, 0));
		l_legend = new JCheckBox("Show Legend");
		l_legend.setSelected(panelcan.showLegend);
		l_legend.addActionListener(this);
		v_legend_panel.add(l_legend);

		layout_panel.add(v_label_panel);
		layout_panel.add(v_legend_panel);
		control_panel.add(layout_panel, gbc);

		JPanel color_panel = new JPanel(new GridLayout(4, 0));
		color_panel.setBorder(BorderFactory.createTitledBorder("Distribution Of"));
		ButtonGroup bg_colors = new ButtonGroup();
		d_message = new JRadioButton("Messages");
		d_message.setSelected(panelcan.getPaintMode() == PaintMaps.COLOR_COMPONENT);
		d_message.addActionListener(this);
		d_convid = new JRadioButton("Converstation");
		d_convid.setSelected(panelcan.getPaintMode() == PaintMaps.PAINTMODE_CONVERSATION);
		d_convid.addActionListener(this);
		d_perform = new JRadioButton("Performative");
		d_perform.setSelected(panelcan.getPaintMode() == PaintMaps.PAINTMODE_PERFORMATIV);
		d_perform.addActionListener(this);
		d_protocol = new JRadioButton("Protocol");
		d_protocol.setSelected(panelcan.getPaintMode() == PaintMaps.PAINTMODE_PROTOCOL);
		d_protocol.addActionListener(this);
		bg_colors.add(d_message);
		bg_colors.add(d_convid);
		bg_colors.add(d_perform);
		bg_colors.add(d_protocol);
		color_panel.add(d_message);
		color_panel.add(d_convid);
		color_panel.add(d_perform);
		color_panel.add(d_protocol);
		control_panel.add(color_panel, gbc);

		JPanel jp = new JPanel(new BorderLayout());
		jp.add(control_panel, BorderLayout.NORTH);
		options.setViewportView(jp);
	}

	/** 
	 * The action listener for the controls.
	 */
	public void actionPerformed(ActionEvent e)
	{
		AbstractButton source = (AbstractButton)e.getSource();
		if(source == g_mgraph)
		{
			panelcan.setChartType(ChartCanvas.CHARTTYPE_PIECHART);
			l_label_force.setEnabled(l_labels.isSelected() && panelcan.chartType != ChartCanvas.CHARTTYPE_PIECHART);
		}
		else if(source == g_dgraph)
		{
			panelcan.setChartType(ChartCanvas.CHARTTYPE_BARCHART);
			l_label_force.setEnabled(l_labels.isSelected() && panelcan.chartType != ChartCanvas.CHARTTYPE_PIECHART);
		}
		else if(source == l_labels)
		{
			panelcan.setShowLabels(source.isSelected());
			l_label_force.setEnabled(source.isSelected() && panelcan.chartType != ChartCanvas.CHARTTYPE_PIECHART);
			panelcan.chartPanel.repaint();
		}
		else if(source == l_label_force)
		{
			panelcan.setForceLabels(source.isSelected());
		}
		else if(source == l_legend)
		{
			panelcan.setShowLegend(source.isSelected());
		}
		else if(source == d_message)
		{
			panelcan.setPaintMode(PaintMaps.COLOR_COMPONENT);
		}
		else if(source == d_convid)
		{
			panelcan.setPaintMode(PaintMaps.PAINTMODE_CONVERSATION);
		}
		else if(source == d_perform)
		{
			panelcan.setPaintMode(PaintMaps.PAINTMODE_PERFORMATIV);
		}
		else if(source == d_protocol)
		{
			panelcan.setPaintMode(PaintMaps.PAINTMODE_PROTOCOL);
		}
	}

}
