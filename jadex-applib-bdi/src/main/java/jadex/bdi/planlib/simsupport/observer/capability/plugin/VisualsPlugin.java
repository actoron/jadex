package jadex.bdi.planlib.simsupport.observer.capability.plugin;

import jadex.bdi.planlib.simsupport.observer.capability.ObserverCenter;
import jadex.bdi.runtime.IExternalAccess;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
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
	private JList themeList_;
	
	/** The observer center
	 */
	private ObserverCenter observerCenter_;
	
	public VisualsPlugin()
	{
		mainPanel_ = new JPanel(new GridBagLayout());
		mainPanel_.setBorder(new TitledBorder(NAME));
		
		mainPanel_.setMinimumSize(new Dimension(200, 200));
		
		JPanel themePanel = new JPanel();
		themePanel.setBorder(new TitledBorder("Theme"));
		themeList_ = new JList(new DefaultComboBoxModel());
		themeList_.addListSelectionListener(new ListController());
		themePanel.add(themeList_);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		mainPanel_.add(themePanel, c);
	}
	
	/** Starts the plugin
	 *  
	 *  @param the observer center
	 */
	public void start(ObserverCenter main)
	{
		observerCenter_ = main;
		IExternalAccess agent = observerCenter_.getAgentAccess();
		Map objectThemes = (Map) agent.getBeliefbase().getBelief("object_themes").getFact();
		synchronized(objectThemes)
		{
			Set themeNames = objectThemes.keySet();
			for (Iterator it = themeNames.iterator(); it.hasNext(); )
			{
				String themeName = (String) it.next();
				((DefaultComboBoxModel) themeList_.getModel()).addElement(themeName);
			}
		}
		
		refresh();
	}
	
	/** Stops the plugin
	 *  
	 */
	public void shutdown()
	{
		
	}
	
	/** Returns the name of the plugin
	 *  
	 *  @return name of the plugin
	 */
	public String getName()
	{
		return NAME;
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
		String selection = (String) observerCenter_.getAgentAccess().getBeliefbase().getBelief("selected_theme").getFact();
		themeList_.setSelectedValue(selection, true);
	}
	
	private class ListController implements ListSelectionListener
	{
		public void valueChanged(ListSelectionEvent e)
		{
			String selection = (String) themeList_.getSelectedValue();
			observerCenter_.getAgentAccess().getBeliefbase().getBelief("selected_theme").setFact(selection);
		}
	}
}
