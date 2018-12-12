package jadex.tools.comanalyzer.graph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.tools.comanalyzer.ComanalyzerPlugin;
import jadex.tools.comanalyzer.PaintMaps;
import jadex.tools.comanalyzer.ToolCanvas;
import jadex.tools.comanalyzer.ToolTab;
import jadex.tools.comanalyzer.graph.EdgeTransformer.WeightStroke;
import jadex.tools.comanalyzer.graph.VertexTransformer.IconSize;
import jadex.tools.comanalyzer.table.TablePanel;


/**
 * The tooltab for displaying messages in a graph.
 */
public class GraphPanel extends ToolTab implements ActionListener, ChangeListener
{

	//-------- constants --------

	/** Icon paths */
	private static final String COMANALYZER_IMAGES = "/jadex/tools/comanalyzer/images/";

	/**
	 * The image icons. 
	 */
	protected static final UIDefaults defaults = new UIDefaults(new Object[]{"refresh", SGUI.makeIcon(TablePanel.class, COMANALYZER_IMAGES + "refresh2.png"), "zoom",
			SGUI.makeIcon(TablePanel.class, COMANALYZER_IMAGES + "zoom.png")});

	/** The map of different layouts. */
	public static final Map LAYOUTS;
	static
	{
		LAYOUTS = new LinkedHashMap();
		LAYOUTS.put("Kamada-Kawai", KKLayout.class);
		LAYOUTS.put("Fruchterman-Reingold ", FRLayout.class);
		LAYOUTS.put("Circle Layout", GraphCircleLayout.class);
		LAYOUTS.put("Spring Layout", SpringLayout2.class);
//		LAYOUTS.put("Spring Layout2", SpringLayout2.class);
		LAYOUTS.put("Meyer's self-organizing", GraphISOMLayout.class);
	}

	//-------- attributes --------

	/** The container for the graph */
	protected GraphCanvas panelcan;

	// graph controls
	protected JRadioButton g_mgraph;

	protected JRadioButton g_dgraph;

	protected JRadioButton g_ugraph;

	// layout controls
	protected JComboBox l_layout;
	
	protected JButton l_refresh;

	protected JCheckBox l_init;

	protected JCheckBox l_animate;

	protected JCheckBox l_gmmode;
	
	protected JSlider l_zoom_slider;
	
	protected JButton l_identity;

	// vertex controls
	protected JCheckBox v_label;

	protected JCheckBox v_font;

	protected JCheckBox v_size;

	protected JSlider v_size_slider;

	protected JSlider v_prop_slider;

	// edge controls
	protected JCheckBox e_label;

	protected JCheckBox e_font;

	protected JCheckBox e_size;

	protected JSlider e_size_slider;

	protected JSlider e_prop_slider;

	protected JRadioButton e_nocolor;

	protected JRadioButton e_convid;

	protected JRadioButton e_perform;

	protected JRadioButton e_protocol;

	// cluster controls
	protected JToggleButton cluster;

	protected JSlider cluster_slider;

	// -------- constructor --------

	/**
	 * Create the graph panel.
	 */
	public GraphPanel(ComanalyzerPlugin plugin)
	{
		super(plugin, "Graph", null);

		// Show Graphical User Interface
		this.panelcan = new GraphCanvas(this);

		// Show options
		final JScrollPane options = new JScrollPane();
		addBottomControls(options);

		final JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelcan, options);
		main.setOneTouchExpandable(true);
		main.setResizeWeight(1.0);
		// main.setDividerLocation(65535); // Proportional (1.0) doesn't work.

		// Initialize tool bar.
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.NORTH, SGUI.createToolBar("Graph Panel Options", getActions()));
		this.add(BorderLayout.CENTER, main);

		// set divider location after components are created
		// to make options entirely visible
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				double mainWidth = main.getWidth();
				double optionsWidth = options.getPreferredSize().getWidth();
				// consider scrollbar width if visible
				double scrollWidth = options.getVerticalScrollBar().isVisible() ? options.getVerticalScrollBar().getWidth() : 0;
				double	loc	= (mainWidth - optionsWidth - scrollWidth) / mainWidth;
				if(loc>=0 && loc<=1)	// Might be NaN, if plugin is switched before panel is shown.
					main.setDividerLocation(loc);
			}
		});

	}

	//-------- ToolTab methods --------

	/**
	 * Returns the canvas for the tooltab.
	 * @see jadex.tools.comanalyzer.ToolTab#getCanvas()
	 */
	public ToolCanvas getCanvas()
	{
		return panelcan;
	}

	/**
	 * Get the (menu/toolbar) actions of this tooltab.
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

	//-------- DiagramPanel methods --------

	/**
	 * Creates the controls for the option panel
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
		graph_panel.setBorder(BorderFactory.createTitledBorder("Graph"));
		ButtonGroup bg_graph = new ButtonGroup();
		g_mgraph = new JRadioButton("Directed Multigraph");
		g_mgraph.setSelected(panelcan.getGraphType() == GraphCanvas.DIRECTED_MULTIGRAPH);
		g_mgraph.addActionListener(this);
		g_dgraph = new JRadioButton("Directed Graph");
		g_dgraph.setSelected(panelcan.getGraphType() == GraphCanvas.DIRECTED_GRAPH);
		g_dgraph.addActionListener(this);
		g_ugraph = new JRadioButton("Undirected Graph");
		g_ugraph.setSelected(panelcan.getGraphType() == GraphCanvas.UNDIRECTED_GRAPH);
		g_ugraph.addActionListener(this);
		bg_graph.add(g_mgraph);
		bg_graph.add(g_dgraph);
		bg_graph.add(g_ugraph);
		graph_panel.add(g_mgraph);
		graph_panel.add(g_dgraph);
		graph_panel.add(g_ugraph);
		control_panel.add(graph_panel, gbc);

		// layout controls
		Box layout_panel = Box.createVerticalBox();
		layout_panel.setBorder(BorderFactory.createTitledBorder("Layout"));
		
		JPanel l_layout_panel = new JPanel(new GridBagLayout());		
		l_layout = new JComboBox((String[])LAYOUTS.keySet().toArray(new String[LAYOUTS.keySet().size()]));
		l_layout.setSelectedIndex(0); // TODO: select the one the graph is currently using
		l_layout.addActionListener(new LayoutComboListener());
		l_refresh = new JButton(defaults.getIcon("refresh"));
		l_refresh.setToolTipText("Reinitialize Layout");
		l_refresh.addActionListener(this);
		
		JPanel l_control_panel = new JPanel(new GridLayout(4, 0));
		l_init = new JCheckBox("Initialize On Change");
		l_init.setSelected(panelcan.autolayout);
		l_init.addActionListener(this);
		l_animate = new JCheckBox("Animate Transitions");
		l_animate.setSelected(panelcan.animate);
		l_animate.addActionListener(this);
		l_gmmode = new JCheckBox("Enable Transforming");
		l_gmmode.setSelected(panelcan.gm.getSelectedObjects()[0] == ModalGraphMouse.Mode.TRANSFORMING);
		l_gmmode.addActionListener(this);

		
		l_zoom_slider = new JSlider(JSlider.HORIZONTAL);
		l_zoom_slider.setMinimum(1);
		l_zoom_slider.setMaximum(300);
		l_zoom_slider.setValue(50);
		l_zoom_slider.setPreferredSize(new Dimension(60, 0));
		l_zoom_slider.addChangeListener(this);
		l_identity = new JButton(defaults.getIcon("zoom"));
		l_identity.setToolTipText("Set Zoom To Identity");
		l_identity.addActionListener(this);
		
//		JPanel zp = new JPanel(new BorderLayout());
//		zp.add(new JLabel("Zoom"), BorderLayout.WEST);
//		zp.add(zoom_slider, BorderLayout.CENTER);
		JPanel zp = new JPanel(new GridBagLayout());
		zp.add(new JLabel("Zoom"), new GridBagConstraints(0, 0, 1, 1, 0, 0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,6,2,2), 0, 0));
		zp.add(l_zoom_slider, new GridBagConstraints(1, 0, 1, 1, 1, 1,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		zp.add(l_identity, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		
//		l_layout_panel.add(l_layout);
//		l_layout_panel.add(l_refresh);
		l_layout_panel.add(l_layout, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2,3,2,2), 0, 0));
		l_layout_panel.add(l_refresh, new GridBagConstraints(1, 0, 1, 1, 1, 1,
				GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		
		
		l_control_panel.add(l_init);
		l_control_panel.add(l_animate);
		l_control_panel.add(l_gmmode);
		l_control_panel.add(zp);

		layout_panel.add(l_layout_panel);
		layout_panel.add(l_control_panel);
		control_panel.add(layout_panel, gbc);

		// agent controls
		Box agent_panel = Box.createVerticalBox();
		agent_panel.setBorder(BorderFactory.createTitledBorder("Agents"));

		JPanel v_label_panel = new JPanel(new GridLayout(0, 2));
		v_label = new JCheckBox("Show Labels");
		v_label.setSelected(panelcan.v_string.isEnabled());
		v_label.addActionListener(this);
		v_font = new JCheckBox("Bold Text");
		v_font.setSelected(panelcan.v_font.getBold());
		v_font.setEnabled(v_label.isSelected());
		v_font.addActionListener(this);
		v_label_panel.add(v_label);
		v_label_panel.add(v_font);

		// JPanel v_size_panel = new JPanel(new GridLayout(0,3));
		JPanel v_size_panel = new JPanel(new BorderLayout());
		v_size = new JCheckBox("Size by Degree");
		v_size.addActionListener(this);
		v_size.setSelected(panelcan.v_icon.isScaling());
		v_size_slider = new JSlider(JSlider.HORIZONTAL);
		v_size_slider.setEnabled(v_size.isSelected());
		v_size_slider.setMinimum(0);
		v_size_slider.setMaximum(IconSize.MAX_VERTEX_SIZE - IconSize.MIN_VERTEX_SIZE);
		v_size_slider.setValue(0);
		v_size_slider.setPreferredSize(new Dimension(60, 0));
		v_size_slider.addChangeListener(this);
		v_prop_slider = new JSlider(JSlider.HORIZONTAL);
		v_prop_slider.setEnabled(v_size.isSelected());
		v_prop_slider.setMinimum(1);
		v_prop_slider.setMaximum(100);
		v_prop_slider.setValue(50);
		v_prop_slider.setPreferredSize(new Dimension(60, 0));
		v_prop_slider.addChangeListener(this);
		v_size_panel.add(v_size, BorderLayout.WEST);
		v_size_panel.add(v_size_slider, BorderLayout.CENTER);
		v_size_panel.add(v_prop_slider, BorderLayout.EAST);
		agent_panel.add(v_label_panel);
		agent_panel.add(v_size_panel);
		control_panel.add(agent_panel, gbc);

		// message controls
		final Box message_panel = Box.createVerticalBox();
		message_panel.setBorder(BorderFactory.createTitledBorder("Messages"));

		JPanel e_label_panel = new JPanel(new GridLayout(0, 2));
		e_label = new JCheckBox("Show Labels");
		e_label.setSelected(panelcan.e_string.isEnabled());
		e_label.addActionListener(this);
		e_font = new JCheckBox("Bold Text");
		e_font.setSelected(panelcan.e_font.getBold());
		e_font.setEnabled(e_label.isSelected());
		e_font.addActionListener(this);
		e_label_panel.add(e_label);
		e_label_panel.add(e_font);

		JPanel e_size_panel = new JPanel(new BorderLayout());
		e_size = new JCheckBox("Size by Degree");
		e_size.addActionListener(this);
		e_size.setSelected(panelcan.e_stroke.isScaling());
		e_size_slider = new JSlider(JSlider.HORIZONTAL);
		e_size_slider.setEnabled(e_size.isSelected());
		e_size_slider.setPreferredSize(new Dimension(60, 0));
		e_size_slider.setMinimum(0);
		e_size_slider.setMaximum(WeightStroke.MAX_EDGE_SIZE - WeightStroke.MIN_EDGE_SIZE);
		e_size_slider.setValue(0);
		e_size_slider.addChangeListener(this);
		e_prop_slider = new JSlider(JSlider.HORIZONTAL);
		e_prop_slider.setEnabled(e_size.isSelected());
		e_prop_slider.setMinimum(1);
		e_prop_slider.setMaximum(100);
		e_prop_slider.setValue(50);
		e_prop_slider.setPreferredSize(new Dimension(60, 0));
		e_prop_slider.addChangeListener(this);
		e_size_panel.add(e_size, BorderLayout.WEST);
		e_size_panel.add(e_size_slider, BorderLayout.CENTER);
		e_size_panel.add(e_prop_slider, BorderLayout.EAST);
		JPanel color_panel = new JPanel(new GridLayout(2, 2));
		color_panel.setBorder(BorderFactory.createTitledBorder("Color By"));
		ButtonGroup bg_colors = new ButtonGroup();
		e_nocolor = new JRadioButton("No colors");
		e_nocolor.setSelected(panelcan.e_paint.getPaintMode() == PaintMaps.PAINTMODE_DEFAULT);
		e_nocolor.addActionListener(this);
		e_convid = new JRadioButton("Converstation");
		e_convid.setSelected(panelcan.e_paint.getPaintMode() == PaintMaps.PAINTMODE_CONVERSATION);
		e_convid.addActionListener(this);
		e_perform = new JRadioButton("Performative");
		e_perform.setSelected(panelcan.e_paint.getPaintMode() == PaintMaps.PAINTMODE_PERFORMATIV);
		e_perform.addActionListener(this);
		e_protocol = new JRadioButton("Protocol");
		e_protocol.setSelected(panelcan.e_paint.getPaintMode() == PaintMaps.PAINTMODE_PROTOCOL);
		e_protocol.addActionListener(this);
		bg_colors.add(e_nocolor);
		bg_colors.add(e_convid);
		bg_colors.add(e_perform);
		bg_colors.add(e_protocol);
		color_panel.add(e_nocolor);
		color_panel.add(e_convid);
		color_panel.add(e_perform);
		color_panel.add(e_protocol);

		message_panel.add(e_label_panel);
		message_panel.add(e_size_panel);
		message_panel.add(color_panel);
		control_panel.add(message_panel, gbc);

		// cluster controls (inactive)
		final Box cluster_panel = Box.createVerticalBox();
		cluster_panel.setBorder(BorderFactory.createTitledBorder("Cluster"));

		JPanel cl_panel = new JPanel(new GridLayout(2, 0));
		cluster = new JCheckBox("Enable Edge-Betweenness");
		cluster.setAlignmentX(Component.LEFT_ALIGNMENT);
		cluster.setSelected(panelcan.cluster);
		cluster.addActionListener(this);
		// Create slider to adjust the number of edges to remove when clustering
		cluster_slider = new JSlider(JSlider.HORIZONTAL);
		// edgeBetweennessSlider.setBackground(Color.WHITE);
		cluster_slider.setMaximum(100);
		cluster_slider.setMinimum(0);
		cluster_slider.setValue(0);
		// edgeBetweennessSlider.setMajorTickSpacing(10);
		// edgeBetweennessSlider.setPaintLabels(true);
		// edgeBetweennessSlider.setPaintTicks(true);
		cluster_slider.setPreferredSize(new Dimension(100, 0));
		cluster_slider.addChangeListener(this);
		cl_panel.add(cluster);
		cl_panel.add(cluster_slider);
		cluster_panel.add(cl_panel);
		// control_panel.add(cluster_panel,gbc);

		JPanel jp = new JPanel(new BorderLayout());
		jp.add(control_panel, BorderLayout.NORTH);
		options.setViewportView(jp);
	}

	// -------- ChangeListener interface --------

	/**
	 * Change listener for the sliders.
	 */
	public void stateChanged(ChangeEvent e)
	{
		JSlider source = (JSlider)e.getSource();
		if(source == l_zoom_slider) // && !source.getValueIsAdjusting())
		{
			int val = (int)source.getValue();
			panelcan.scaler.scale(panelcan.vv, val/50.0f, panelcan.vv.getCenter());
		}
		else if(source == v_size_slider) // && !source.getValueIsAdjusting())
		{
			panelcan.v_icon.setScaleRange(source.getValue());
			panelcan.vv.repaint();
		}
		else if(source == v_prop_slider) // && !source.getValueIsAdjusting())
		{
			panelcan.v_icon.setPropFactor(source.getValue());
			panelcan.vv.repaint();
		}
		else if(source == e_size_slider) // && !source.getValueIsAdjusting())
		{
			panelcan.e_stroke.setScaleRange(source.getValue());
			panelcan.vv.repaint();
		}
		else if(source == e_prop_slider) // && !source.getValueIsAdjusting())
		{
			panelcan.e_stroke.setPropValue(source.getValue());
			panelcan.vv.repaint();
		}
		else if(source == cluster_slider && !source.getValueIsAdjusting())
		{
			panelcan.lockAgents();
			panelcan.clusterGraph(cluster_slider.getValue(), cluster.isSelected());
			panelcan.repaintCanvas();
		}
	}

	//-------- ActionListener interface --------

	/**
	 * Action listener for the controls.
	 */
	public void actionPerformed(ActionEvent e)
	{
		AbstractButton source = (AbstractButton)e.getSource();
		if(source == g_mgraph)
		{
			panelcan.setGraphType(GraphCanvas.DIRECTED_MULTIGRAPH);
		}
		else if(source == g_dgraph)
		{
			panelcan.setGraphType(GraphCanvas.DIRECTED_GRAPH);
		}
		else if(source == g_ugraph)
		{
			panelcan.setGraphType(GraphCanvas.UNDIRECTED_GRAPH);
		}
		else if(source == l_refresh)
		{
			// Hack??
			l_layout.setSelectedIndex(l_layout.getSelectedIndex());
		}
		else if(source == l_init)
		{
			panelcan.autolayout = source.isSelected();
			l_animate.setEnabled(source.isSelected());
		}
		else if(source == l_animate)
		{
			panelcan.animate = source.isSelected();
		}
		else if(source == l_gmmode)
		{
			panelcan.gm.setMode(source.isSelected() ? ModalGraphMouse.Mode.TRANSFORMING : ModalGraphMouse.Mode.PICKING);
		}
		else if(source == l_identity)
		{
//			panelcan.scaler.scale(panelcan.vv, 1.0f, panelcan.vv.getCenter());
			panelcan.vv.getRenderContext().getMultiLayerTransformer().setToIdentity();						
			l_zoom_slider.setValue(50);
		}
		else if(source == e_nocolor)
		{
			panelcan.e_paint.setPaintMode(PaintMaps.PAINTMODE_DEFAULT);
			panelcan.vv.repaint();
		}
		else if(source == e_convid)
		{
			panelcan.e_paint.setPaintMode(PaintMaps.PAINTMODE_CONVERSATION);
			panelcan.vv.repaint();
		}
		else if(source == e_perform)
		{
			panelcan.e_paint.setPaintMode(PaintMaps.PAINTMODE_PERFORMATIV);
			panelcan.vv.repaint();
		}
		else if(source == e_protocol)
		{
			panelcan.e_paint.setPaintMode(PaintMaps.PAINTMODE_PROTOCOL);
			panelcan.vv.repaint();

		}
		else if(source == v_label)
		{
			panelcan.v_string.setEnabled(source.isSelected());
			v_font.setEnabled(source.isSelected());
			panelcan.vv.repaint();
		}
		else if(source == e_label)
		{
			panelcan.e_string.setEnabled(source.isSelected());
			e_font.setEnabled(source.isSelected());
			panelcan.vv.repaint();

		}
		else if(source == v_font)
		{
			panelcan.v_font.setBold(source.isSelected());
			panelcan.vv.repaint();
		}
		else if(source == e_font)
		{
			panelcan.e_font.setBold(source.isSelected());
			panelcan.vv.repaint();
		}
		else if(source == v_size)
		{
			panelcan.v_icon.setScaling(source.isSelected());
			v_size_slider.setEnabled(source.isSelected());
			v_prop_slider.setEnabled(source.isSelected());
			panelcan.vv.repaint();
		}
		else if(source == e_size)
		{
			panelcan.e_stroke.setScaling(source.isSelected());
			e_size_slider.setEnabled(source.isSelected());
			e_prop_slider.setEnabled(source.isSelected());
			panelcan.vv.repaint();
		}
		else if(source == cluster)
		{
			panelcan.lockAgents();
			panelcan.clusterGraph(cluster_slider.getValue(), source.isSelected());
			cluster_slider.setEnabled(source.isSelected());
			panelcan.repaintCanvas();
		}

	}

	// -------- Actions --------


	// -------- inner classes --------

	/**
	 * Listener for the layout combobox. Creates a new layout from the chosen
	 * entry of the combobox and assigns it to the graph.
	 */
	protected final class LayoutComboListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			JComboBox jcb = (JComboBox)arg0.getSource();
			Object[] constructorArgs = {panelcan.graph};

			Class layoutC = (Class)LAYOUTS.get(jcb.getSelectedItem());

			try
			{
				Constructor constructor = layoutC.getConstructor(new Class[]{Graph.class});
				Object o = constructor.newInstance(constructorArgs);
				Layout l = (Layout)o;

				l.setSize(panelcan.vv.getSize());

				panelcan.layout.removeAll();
				panelcan.layout.setDelegate(l);

				// set zoom to identity
				panelcan.vv.getRenderContext().getMultiLayerTransformer().setToIdentity();			
				l_zoom_slider.setValue(50);
				
				panelcan.reinitializeCanvas();

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
