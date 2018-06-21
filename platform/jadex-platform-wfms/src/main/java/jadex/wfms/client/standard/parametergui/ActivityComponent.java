package jadex.wfms.client.standard.parametergui;

import jadex.wfms.client.IClientActivity;
import jadex.wfms.guicomponents.SGuiHelper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class ActivityComponent extends JScrollPane
{
	private static final String FINISH_BUTTON_LABEL  = "Finish";
	private static final String SUSPEND_BUTTON_LABEL = "Suspend";
	private static final String CANCEL_BUTTON_LABEL  = "Cancel";
	
	private JPanel parameterPanel;
	
	private JPanel buttonPanel;
	
	private Map categoryPanels;
	
	private IClientActivity activity;
	
	private JButton cancelButton;
	
	private JButton suspendButton;
	
	private JButton finishButton;
	
	private List parameterPanels;
	
	public ActivityComponent(IClientActivity activity)
	{
		setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		JPanel mainPanel = new JPanel(new GridBagLayout())
		{
			public Dimension getPreferredSize()
			{
				Dimension d = super.getPreferredSize();
				d.width = ActivityComponent.this.getViewportBorderBounds().width;
				return d;
			}
		};
		setViewportView(mainPanel);
		this.activity = activity;
		categoryPanels = new HashMap();
		parameterPanels = new ArrayList();
		
		parameterPanel = new JPanel(new GridBagLayout());
		//parameterPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), activity.getName()));
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.BOTH;
		g.weightx = 1;
		g.weighty = 1;
		g.gridx = 0;
		g.gridy = 0;
		g.anchor = GridBagConstraints.NORTH;
		mainPanel.add(parameterPanel, g);
		
		buttonPanel = new JPanel(new GridBagLayout());
		buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		g = new GridBagConstraints();
		g.fill = GridBagConstraints.NONE;
		g.weightx = 0;
		g.weighty = 0;
		g.gridx = 0;
		g.gridy = 1;
		g.anchor = GridBagConstraints.SOUTHEAST;
		mainPanel.add(buttonPanel, g);
		
		addParameterPanels();
		
		addButtons();
	}
	
	public IClientActivity getActivity()
	{
		return activity;
	}
	
	public void setCancelAction(Action action)
	{
		cancelButton.setAction(action);
		cancelButton.setText(CANCEL_BUTTON_LABEL);
	}
	
	public void setSuspendAction(Action action)
	{
		suspendButton.setAction(action);
		suspendButton.setText(SUSPEND_BUTTON_LABEL);
	}
	
	public void setFinishAction(Action action)
	{
		finishButton.setAction(action);
		finishButton.setText(FINISH_BUTTON_LABEL);
	}
	
	/**
	 * Tests if the activity is ready to be finished.
	 * @return true if the activity is ready, false otherwise
	 */
	public boolean isReadyForFinish()
	{
		boolean ret = true;
		
		for (Iterator it = parameterPanels.iterator(); it.hasNext(); )
		{
			AbstractParameterPanel panel = (AbstractParameterPanel) it.next();
			if (!panel.isReadOnly())
				ret &= panel.isParameterValueValid();
		}
		
		return ret;
	}
	
	/**
	 * Returns all parameters values gathered so far.
	 * @return parameter values
	 */
	public Map getParameterValues()
	{
		Map ret = new HashMap();
		
		for (Iterator it = parameterPanels.iterator(); it.hasNext(); )
		{
			AbstractParameterPanel panel = (AbstractParameterPanel) it.next();
			if ((!panel.isReadOnly()) && (panel.isParameterValueValid()))
				ret.put(panel.getParameterName(), panel.getParameterValue());
		}
		
		return ret;
	}
	
	private void addParameterPanels()
	{
		List parameterNames = new LinkedList(activity.getParameterNames());
		
		int y = 0;
		//Set parameterNames = activity.getParameterNames();
		for (Iterator it = parameterNames.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			Map metaProperties = activity.getParameterMetaProperties(name);
			AbstractParameterPanel panel = SParameterPanelFactory.createParameterPanel(name, activity.getParameterType(name), activity.getParameterValue(name), metaProperties, activity.isReadOnly(name));
			
			JLabel parameterLabel = null;
			if (panel.requiresLabel())
			{
				String labelText = (String) metaProperties.get("short_description");
				if (labelText == null)
					labelText = SGuiHelper.beautifyName(name);
				parameterLabel = new JLabel(labelText);
				parameterLabel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 20)));
			}
			
			GridBagConstraints g = new GridBagConstraints();
			if (!panel.requiresLabel())
			{
				g.gridx = 0;
				g.gridwidth = 2;
			}
			else
				g.gridx = 1;
			g.gridy = y;
			g.fill = GridBagConstraints.HORIZONTAL;
			g.weightx = 1;
			g.insets = new Insets(5, 5 , 5, 5);
			g.anchor = GridBagConstraints.NORTH;
			
			if (activity.getParameterMetaProperties(name).containsKey("category"))
			{
				String category = (String) activity.getParameterMetaProperties(name).get("category");
				CategoryPanel catPanel = (CategoryPanel) categoryPanels.get(category);
				if (catPanel == null)
				{
					catPanel = new CategoryPanel(category);
					categoryPanels.put(category, catPanel);
					parameterPanel.add(catPanel, g);
				}
				
				catPanel.addParameterPanel(parameterLabel, panel);
			}
			else
			{
				if (parameterLabel != null)
				{
					GridBagConstraints lg = new GridBagConstraints();
					lg.gridx = 0;
					lg.gridy = y;
					lg.insets = new Insets(5, 5 , 5, 5);
					lg.fill = GridBagConstraints.NONE;
					lg.anchor = GridBagConstraints.NORTHWEST;
					parameterPanel.add(parameterLabel, lg);
				}
				parameterPanel.add(panel, g);
			}
			
			parameterPanels.add(panel);
			
			++y;
		}
		
		JPanel filler = new JPanel();
		GridBagConstraints g = new GridBagConstraints();
		g.gridy = y;
		g.gridwidth = 2;
		g.weighty = 1;
		g.anchor = GridBagConstraints.SOUTH;
		parameterPanel.add(filler, g);
	}
	
	private void addButtons()
	{
		cancelButton = new JButton(CANCEL_BUTTON_LABEL);
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.fill = GridBagConstraints.NONE;
		g.insets = new Insets(0, 5, 0, 5);
		g.anchor = GridBagConstraints.SOUTHEAST;
		buttonPanel.add(cancelButton, g);
		
		suspendButton = new JButton(SUSPEND_BUTTON_LABEL);
		g = new GridBagConstraints();
		g.gridx = 1;
		g.gridy = 0;
		g.fill = GridBagConstraints.NONE;
		g.insets = new Insets(0, 5, 0, 5);
		g.anchor = GridBagConstraints.SOUTHEAST;
		buttonPanel.add(suspendButton, g);
		
		finishButton = new JButton(FINISH_BUTTON_LABEL);
		g = new GridBagConstraints();
		g.gridx = 2;
		g.gridy = 0;
		g.fill = GridBagConstraints.NONE;
		g.insets = new Insets(0, 5, 0, 5);
		g.anchor = GridBagConstraints.SOUTHEAST;
		buttonPanel.add(finishButton, g);
	}
}
