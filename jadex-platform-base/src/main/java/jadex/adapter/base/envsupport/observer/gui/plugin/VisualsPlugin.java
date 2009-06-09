package jadex.adapter.base.envsupport.observer.gui.plugin;

import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class VisualsPlugin implements IObserverCenterPlugin
{
	/** Plugin name
	 */
	private static final String NAME = "Visuals";
	
	/** The main panel
	 */
	private JPanel mainPanel_;
	
	/** The perspectives
	 */
	private JList perspectivelist;
	
	/** The dataviews
	 */
	private JList dataviewlist;
	
	/** The observer center
	 */
	private ObserverCenter observerCenter_;
	
	/** The perspective selection controller
	 */
	private ListSelectionListener perspectiveController_;
	
	/** The dataview selection controller
	 */
	private ListSelectionListener dataviewController_;
	
	public VisualsPlugin()
	{
		mainPanel_ = new JPanel(new GridBagLayout());
		mainPanel_.setBorder(new TitledBorder(NAME));
		
		mainPanel_.setMinimumSize(new Dimension(200, 200));
		
		JPanel perspectivePanel = new JPanel(new GridBagLayout());
		perspectivePanel.setBorder(new TitledBorder("Perspective"));
		perspectivelist = new JList(new DefaultComboBoxModel());
		JScrollPane perspectiveScrollPane = new JScrollPane(perspectivelist);
		perspectiveController_ = new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				String selection = (String) perspectivelist.getSelectedValue();
				observerCenter_.setSelectedPerspective(selection);
			}
		};
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		perspectivePanel.add(perspectiveScrollPane, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		mainPanel_.add(perspectivePanel, c);
		
		JPanel dataviewPanel = new JPanel(new GridBagLayout());
		dataviewPanel.setBorder(new TitledBorder("Dataview"));
		dataviewlist = new JList(new DefaultComboBoxModel());
		JScrollPane dataviewScrollPane = new JScrollPane(dataviewlist);
		dataviewController_ = new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				String selection = (String) dataviewlist.getSelectedValue();
				observerCenter_.setSelectedDataView(selection);
			}
		};
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		dataviewPanel.add(dataviewScrollPane, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		mainPanel_.add(dataviewPanel, c);
	}
	
	/** Starts the plugin
	 *  
	 *  @param the observer center
	 */
	public void start(ObserverCenter main)
	{
		observerCenter_ = main;
		
		Map perspectives = observerCenter_.getPerspectives();
		synchronized(perspectives)
		{
			Set themeNames = perspectives.keySet();
			for (Iterator it = themeNames.iterator(); it.hasNext(); )
			{
				String name = (String) it.next();
				((DefaultComboBoxModel) perspectivelist.getModel()).addElement(name);
			}
		}
		
		perspectivelist.addListSelectionListener(perspectiveController_);
		
		Map dataviews = observerCenter_.getDataViews();
		synchronized(dataviews)
		{
			Set dataviewnames = dataviews.keySet();
			for (Iterator it = dataviewnames.iterator(); it.hasNext(); )
			{
				String name = (String) it.next();
				((DefaultComboBoxModel) dataviewlist.getModel()).addElement(name);
			}
		}
		
		dataviewlist.addListSelectionListener(dataviewController_);
		
		refresh();
		
		
	}
	
	/** Stops the plugin
	 *  
	 */
	public void shutdown()
	{
		perspectivelist.removeListSelectionListener(perspectiveController_);
		((DefaultComboBoxModel) perspectivelist.getModel()).removeAllElements();
		dataviewlist.removeListSelectionListener(dataviewController_);
		((DefaultComboBoxModel) dataviewlist.getModel()).removeAllElements();
	}
	
	/** Returns the name of the plugin
	 *  
	 *  @return name of the plugin
	 */
	public String getName()
	{
		return NAME;
	}
	
	/** Returns the path to the icon for the plugin in the toolbar.
	 * 
	 *  @return path to the icon
	 */
	public String getIconPath()
	{
		return getClass().getPackage().getName().replaceAll("gui.plugin","").concat("images.").replaceAll("\\.", "/").concat("visuals_icon.png");
	}
	
	/** Returns the viewable component of the plugin
	 *  
	 *  @return viewable component of the plugin
	 */
	public Component getView()
	{
		return mainPanel_;
	}
	
	/** Refreshes the display
	 */
	public void refresh()
	{
		String selection = observerCenter_.getSelectedPerspective().getName();
		Map perspectives = observerCenter_.getPerspectives();
		perspectivelist.removeListSelectionListener(perspectiveController_);
		((DefaultComboBoxModel) perspectivelist.getModel()).removeAllElements();
		synchronized(perspectives)
		{
			Set themeNames = perspectives.keySet();
			for (Iterator it = themeNames.iterator(); it.hasNext(); )
			{
				String name = (String) it.next();
				((DefaultComboBoxModel) perspectivelist.getModel()).addElement(name);
			}
		}
		perspectivelist.setSelectedValue(selection, true);
		perspectivelist.addListSelectionListener(perspectiveController_);
		
		selection = observerCenter_.getSelectedDataViewName();
		Map dataviews = observerCenter_.getDataViews();
		dataviewlist.removeListSelectionListener(dataviewController_);
		((DefaultComboBoxModel) dataviewlist.getModel()).removeAllElements();
		synchronized(dataviews)
		{
			Set dataviewnames = dataviews.keySet();
			for (Iterator it = dataviewnames.iterator(); it.hasNext(); )
			{
				String name = (String) it.next();
				((DefaultComboBoxModel) dataviewlist.getModel()).addElement(name);
			}
		}
		dataviewlist.setSelectedValue(selection, true);
		dataviewlist.addListSelectionListener(dataviewController_);
	}
}
