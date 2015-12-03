package jadex.tools.comanalyzer.diagram;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
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
import javax.swing.UIDefaults;

import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.tools.comanalyzer.ComanalyzerPlugin;
import jadex.tools.comanalyzer.PaintMaps;
import jadex.tools.comanalyzer.ToolCanvas;
import jadex.tools.comanalyzer.ToolTab;
import jadex.tools.comanalyzer.table.TablePanel;


/**
 * The tooltab for displaying messages in a diagram.
 */
public class DiagramPanel extends ToolTab implements ActionListener
{

	// -------- constants --------

	/** Icon paths */
	private static final String COMANALYZER_IMAGES = "/jadex/tools/comanalyzer/images/";

	/** The image icons. */
	protected static final UIDefaults defaults = new UIDefaults(new Object[]{
	// Menu icons.
			"scrolllock", SGUI.makeIcon(TablePanel.class, COMANALYZER_IMAGES + "scrolllock.png"), "autoscroll", SGUI.makeIcon(TablePanel.class, COMANALYZER_IMAGES + "autoscroll.png"),});

	// -------- attributes --------

	/** The container for the diagram */
	protected DiagramCanvas panelcan;

	// the controls
	protected JScrollPane options;

	protected JSplitPane main;

	protected JCheckBox e_label;

	protected JCheckBox e_font;

	protected JRadioButton e_nocolor;

	protected JRadioButton e_convid;

	protected JRadioButton e_perform;

	protected JRadioButton e_protocol;

	// -------- constructors --------

	/**
	 * Creates a new diagram panel.
	 */
	public DiagramPanel(ComanalyzerPlugin plugin)
	{
		super(plugin, "Diagram", null);

		// Initialize diagram.
		panelcan = new DiagramCanvas(this);

		options = new JScrollPane();
		addBottomControls(options);

		main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelcan, options);
		main.setOneTouchExpandable(true);
		main.setResizeWeight(1.0);
		// main.setDividerLocation(65535); // Proportional (1.0) doesn't work.

		// Initialize tool bar.
		this.setLayout(new BorderLayout());
		this.add(BorderLayout.NORTH, SGUI.createToolBar("Diagram Panal Options", getActions()));
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

	// -------- ToolTab methods --------

	/**
	 * Returns the DiagramCanvas
	 * 
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
			actionlist.add(null); // seperator
			actionlist.add(SCROLL_LOCK);
			actions = (Action[])actionlist.toArray((new Action[actionlist.size()]));
		}

		return this.actions;
	}

	// -------- DiagramPanel methods --------

	/**
	 * Creates the controls for the option panel
	 * 
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

		// message controls
		final Box message_panel = Box.createVerticalBox();
		message_panel.setBorder(BorderFactory.createTitledBorder("Messages"));

		JPanel e_label_panel = new JPanel(new GridLayout(2, 0));
		e_label = new JCheckBox("Show Labels");
		e_label.setSelected(panelcan.detail.show_label);
		e_label.addActionListener(this);
		e_font = new JCheckBox("Bold Text");
		e_font.setSelected(panelcan.detail.label_bold);
		e_font.setEnabled(e_label.isSelected());
		e_font.addActionListener(this);
		e_label_panel.add(e_label);
		e_label_panel.add(e_font);

		JPanel color_panel = new JPanel(new GridLayout(4, 0));
		color_panel.setBorder(BorderFactory.createTitledBorder("Color By"));
		ButtonGroup bg_colors = new ButtonGroup();
		e_nocolor = new JRadioButton("No colors");
		e_nocolor.setSelected(panelcan.detail.getPaintMode() == PaintMaps.PAINTMODE_DEFAULT);
		e_nocolor.addActionListener(this);
		e_convid = new JRadioButton("Converstation");
		e_convid.setSelected(panelcan.detail.getPaintMode() == PaintMaps.PAINTMODE_CONVERSATION);
		e_convid.addActionListener(this);
		e_perform = new JRadioButton("Performative");
		e_perform.setSelected(panelcan.detail.getPaintMode() == PaintMaps.PAINTMODE_PERFORMATIV);
		e_perform.addActionListener(this);
		e_protocol = new JRadioButton("Protocol");
		e_protocol.setSelected(panelcan.detail.getPaintMode() == PaintMaps.PAINTMODE_PROTOCOL);
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
		message_panel.add(color_panel);
		control_panel.add(message_panel, gbc);

		JPanel jp = new JPanel(new BorderLayout());
		jp.add(control_panel, BorderLayout.NORTH);
		options.setViewportView(jp);

	}

	/**
	 * Action listener for the controls.
	 */
	public void actionPerformed(ActionEvent e)
	{
		AbstractButton source = (AbstractButton)e.getSource();

		if(source == e_nocolor)
		{
			panelcan.detail.setPaintMode(PaintMaps.PAINTMODE_DEFAULT);
			panelcan.repaintCanvas();
		}
		else if(source == e_convid)
		{
			panelcan.detail.setPaintMode(PaintMaps.PAINTMODE_CONVERSATION);
			panelcan.repaintCanvas();
		}
		else if(source == e_perform)
		{
			panelcan.detail.setPaintMode(PaintMaps.PAINTMODE_PERFORMATIV);
			panelcan.repaintCanvas();
		}
		else if(source == e_protocol)
		{
			panelcan.detail.setPaintMode(PaintMaps.PAINTMODE_PROTOCOL);
			panelcan.repaintCanvas();

		}
		else if(source == e_label)
		{
			panelcan.detail.show_label = source.isSelected();
			e_font.setEnabled(source.isSelected());
			panelcan.repaintCanvas();

		}
		else if(source == e_font)
		{
			panelcan.detail.label_bold = source.isSelected();
			panelcan.repaintCanvas();
		}
	}

	// -------- Actions --------

	/** Toggle scroll lock */
	protected final AbstractAction SCROLL_LOCK = new AbstractAction("Scroll Lock", defaults.getIcon("scrolllock"))
	{
		public void actionPerformed(ActionEvent ae)
		{
			panelcan.setAutoScroll(!panelcan.isAutoScroll());
			SCROLL_LOCK.putValue(Action.SHORT_DESCRIPTION, panelcan.isAutoScroll() ? "Scroll Lock" : "Auto Scroll");
			SCROLL_LOCK.putValue(Action.SMALL_ICON, panelcan.isAutoScroll() ? defaults.getIcon("scrolllock") : defaults.getIcon("autoscroll"));
		}
	};

}
