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
	
	/** The themes
	 */
	private JList perspectivelist;
	
	/** The observer center
	 */
	private ObserverCenter observerCenter_;
	
	/** The perspective selection controller
	 */
	private ListSelectionListener perspectiveController_;
	
	public VisualsPlugin()
	{
		mainPanel_ = new JPanel(new GridBagLayout());
		mainPanel_.setBorder(new TitledBorder(NAME));
		
		mainPanel_.setMinimumSize(new Dimension(200, 200));
		
		JPanel perspectivePanel = new JPanel(new GridBagLayout());
		perspectivePanel.setBorder(new TitledBorder("Perspective"));
		perspectivelist = new JList(new DefaultComboBoxModel());
		JScrollPane themeScrollPane = new JScrollPane(perspectivelist);
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
		c.fill = GridBagConstraints.HORIZONTAL;
		perspectivePanel.add(themeScrollPane, c);
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel_.add(perspectivePanel, c);
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
		
		refresh();
		
		
	}
	
	/** Stops the plugin
	 *  
	 */
	public void shutdown()
	{
		perspectivelist.removeListSelectionListener(perspectiveController_);
		((DefaultComboBoxModel) perspectivelist.getModel()).removeAllElements();
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
		perspectivelist.setSelectedValue(selection, true);
	}
}
