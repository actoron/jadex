package jadex.wfms.bdi.client.standard.parametergui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.wfms.client.IClientActivity;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ActivityComponent extends JScrollPane
{
	private static final String FINISH_BUTTON_LABEL  = "Finish";
	private static final String SUSPEND_BUTTON_LABEL = "Suspend";
	private static final String CANCEL_BUTTON_LABEL  = "Cancel";
	
	private JPanel parameterPanel;
	
	private JPanel buttonPanel;
	
	private IClientActivity activity;
	
	private JButton cancelButton;
	
	private JButton suspendButton;
	
	private JButton finishButton;
	
	private List parameterPanels;
	
	public ActivityComponent(IClientActivity activity)
	{
		JPanel mainPanel = new JPanel(new GridBagLayout());
		setViewportView(mainPanel);
		this.activity = activity;
		parameterPanels = new ArrayList();
		
		parameterPanel = new JPanel(new GridBagLayout());
		parameterPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), activity.getName()));
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
		
		int y = 0;
		Set parameterNames = activity.getParameterNames();
		for (Iterator it = parameterNames.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			
			AbstractParameterPanel panel = SParameterPanelFactory.createParameterPanel(name, activity.getParameterType(name), activity.getParameterValue(name), activity.isReadOnly(name));
			
			if (panel.requiresLabel())
			{
				JLabel parameterLabel = new JLabel(name);
				parameterLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
				GridBagConstraints g = new GridBagConstraints();
				g.gridx = 0;
				g.gridy = y;
				g.insets = new Insets(5, 0 , 5, 0);
				g.fill = GridBagConstraints.NONE;
				g.anchor = GridBagConstraints.NORTHWEST;
				parameterPanel.add(parameterLabel, g);
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
			g.insets = new Insets(5, 0 , 5, 0);
			g.anchor = GridBagConstraints.NORTH;
			parameterPanel.add(panel, g);
			
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
