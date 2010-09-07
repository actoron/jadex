package jadex.wfms.simulation.stateset.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateset.AbstractNumericStateSet;
import jadex.wfms.simulation.stateset.NumberRange;

public class NumericPanel extends JPanel implements IStatePanel
{
	private static final Pattern RANGE_PATTERN = Pattern.compile("-?[0-9]+( *- *-?[0-9]+)?");
	
	private JList rangeList;
	
	private String taskName;
	private String parameterName;
	private SimulationWindow simWindow;
	
	public NumericPanel(String tskName, String paramtrName, SimulationWindow simWdw)
	{
		this.taskName = tskName;
		this.parameterName = paramtrName;
		this.simWindow = simWdw;
		setLayout(new GridBagLayout());
		
		rangeList = new JList(new DefaultListModel());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		add(rangeList, c);
		
		refreshPanel();
		
		JButton addButton = new JButton(new AbstractAction("Add Range...")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() == null)
					return;
				NumberRange range = null;
				try
				{
					range = getRangeFromUser();
				}
				catch (RuntimeException e1)
				{
					JOptionPane.showMessageDialog(NumericPanel.this, "The range entered is invalid." , "Invalid Range" , JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if (range == null)
					return;
				
				AbstractNumericStateSet stateSet = (AbstractNumericStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName);
				try
				{
					stateSet.addRange(range);
				}
				catch(IllegalArgumentException e1)
				{
					JOptionPane.showMessageDialog(NumericPanel.this,
												  "The range specified is outside the range of valid values for this type (" + String.valueOf(stateSet.getLowerBound()) + " - " + String.valueOf(stateSet.getUpperBound()) + ")",
												  "Type Range Exceeded",
												  JOptionPane.ERROR_MESSAGE);
				}
				refreshPanel();
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		add(addButton, c);
		
		JButton removeButton = new JButton(new AbstractAction("Remove Range")
		{
			
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() == null)
					return;
				AbstractNumericStateSet stateSet = (AbstractNumericStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName);
				stateSet.removeRange(rangeList.getSelectedIndex());
				refreshPanel();
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		add(removeButton, c);
	}
	
	/**
	 * Refreshes the contents of the state panel.
	 */
	public void refreshPanel()
	{
		((DefaultListModel) rangeList.getModel()).clear();
		if (simWindow.getSelectedScenario() == null)
			return;
		List ranges = ((AbstractNumericStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).getRanges();
		for (Iterator it = ranges.iterator(); it.hasNext(); )
			((DefaultListModel) rangeList.getModel()).addElement(it.next().toString());
	}
	
	private NumberRange getRangeFromUser()
	{
		String inputString = (String) JOptionPane.showInputDialog(this,
                												  "Please enter a new range:",
                												  "New Range",
                												  JOptionPane.PLAIN_MESSAGE,
                												  null,
                												  null,
                												  null);
		if (inputString == null)
			return null;
		
		inputString = inputString.trim();
		Matcher matcher = RANGE_PATTERN.matcher(inputString);
		if (!matcher.matches())
			throw new RuntimeException("Pattern match failed.");
		
		inputString = inputString.replaceAll(" ", "");
		
		int endIndex = 0;
		if (inputString.startsWith("-"))
		{
			if ((endIndex = inputString.indexOf('-', 1)) == -1)
				endIndex = inputString.length();
		}
		else
			endIndex = inputString.indexOf('-');
		
		if (inputString.indexOf('-') == -1)
			endIndex = inputString.length();
		
		long firstVal = 0;
		long secondVal = 0;
		try
		{
			firstVal  = Long.parseLong(inputString.substring(0, endIndex));
			if (endIndex == inputString.length())
				secondVal = firstVal;
			else
				secondVal = Long.parseLong(inputString.substring(endIndex + 1));
		
			if (firstVal > secondVal)
				throw new RuntimeException("Second value smaller than first.");
		}
		catch(NumberFormatException e)
		{
			throw new RuntimeException(e);
		}
		
		return new NumberRange(firstVal, secondVal);
	}
}
