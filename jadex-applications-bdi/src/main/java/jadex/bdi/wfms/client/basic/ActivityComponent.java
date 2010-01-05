package jadex.bdi.wfms.client.basic;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jadex.wfms.client.IClientActivity;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class ActivityComponent extends JPanel
{
	private IClientActivity activity;
	
	private List parameterPanels;
	
	public ActivityComponent(IClientActivity activity)
	{
		super(new GridBagLayout());
		this.activity = activity;
		parameterPanels = new ArrayList();
		
		setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), activity.getName()));
		
		addParameterPanels();
	}
	
	public IClientActivity getActivity()
	{
		return activity;
	}
	
	private void addParameterPanels()
	{
		
		int y = 0;
		Set parameterNames = activity.getParameterNames();
		for (Iterator it = parameterNames.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			
			JLabel parameterLabel = new JLabel(name);
			parameterLabel.setBorder(new EmptyBorder(0, 0, 0, 20));
			GridBagConstraints g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = y;
			g.fill = GridBagConstraints.NONE;
			g.weighty = 1;
			g.anchor = GridBagConstraints.NORTH;
			add(parameterLabel, g);
			
			AbstractParameterPanel panel = SParameterPanelFactory.createParameterPanel(name, activity.getParameterType(name), activity.getParameterValue(name));
			g = new GridBagConstraints();
			g.gridx = 1;
			g.gridy = y;
			g.fill = GridBagConstraints.HORIZONTAL;
			g.weightx = 1;
			g.weighty = 1;
			g.anchor = GridBagConstraints.NORTH;
			add(panel, g);
			
			parameterPanels.add(panel);
			
			++y;
		}
		
	}
}
