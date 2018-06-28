package jadex.tools.comanalyzer;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jadex.commons.gui.BrowserPane;


/**
 * A generic tool panel for easy integration of the tooltabs
 */
public class ToolPanel extends JPanel
{
	/** The text when nothing is selected in the details view. */
	public static final String NOTHING = "No element selected for detailed view.\nUse double-click to view element.";

	// -------- attributes --------

	/** The content pane. */
	protected JSplitPane content;

	/** The details panel. */
	protected BrowserPane details;

	/** The tab panel. */
	protected JTabbedPane tabs;

	/** The tool components. */
	protected ToolTab[] tools;

	// -------- constructors --------

	/**
	 * Creates a tool panel with tabs for each tool tab.
	 * 
	 * @param tools The tools to be presented in a tabbed pane.
	 */
	public ToolPanel(ToolTab[] tools)
	{
		this.details = new BrowserPane();
		this.tabs = new JTabbedPane();
		this.tools = tools;

		for(int i = 0; i < tools.length; i++)
		{
			this.tabs.addTab(tools[i].getName(), tools[i].getIcon(), tools[i]);
		}

		// add the change listener to the tab
		tabs.addChangeListener(new PaneChangeListener());

		this.content = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabs, new JScrollPane(details));
		content.setOneTouchExpandable(true);
		content.setResizeWeight(1.0);
		content.setDividerLocation(65535); // Proportional (1.0) doesn't work.

		this.setLayout(new BorderLayout());
		this.add(BorderLayout.CENTER, content);

	}

	// -------- methods --------

	/**
	 * Show element details.
	 */
	public void showElementDetails(Map element)
	{
		// Todo: better layout
		this.details.setText(element.toString());
		// Hack? to show detail panel.
		if(content.getDividerLocation() > content.getMaximumDividerLocation())
			content.setDividerLocation(content.getLastDividerLocation());
	}
	
	/**
	 *  Get the tools.
	 */
	public ToolTab[] getTools()
	{
		return tools;
	}

	// -------- inner classes --------

	/**
	 * Fires when a new tab is aktivated and refreshes the toolbar to change the
	 * icons fors the globel tool buttons. 
	 * TODO: Create a global toolbar.
	 */
	private final class PaneChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent e)
		{
			// refresh toolbar of selected tooltab
			JTabbedPane pane = (JTabbedPane)e.getSource();
			ToolTab tab = (ToolTab)pane.getSelectedComponent();
			if(tab != null)
				tab.refreshToolBar();
		}
	}

}
