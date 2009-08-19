// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.client.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.daimler.util.StringUtils;
import com.daimler.util.swing.SwingUtils;

/**
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 */
public class ComplexRenderPanel extends AbstractInputPanel
{

	private String labelText;

	private String valueText;

	private String toolTip;

	private JPanel mainPanel;

	private JPanel labelPanel;

	private JLabel complexValueLabel;

	private JLabel label;

	public ComplexRenderPanel(String name, String text, String toolTip,
			String helpText, Color bgColor, Object initialValue, boolean isRequired)
	{
		super(name, helpText, bgColor, initialValue, isRequired);
		this.labelText = name;
		this.valueText = "<html>" + StringUtils.replace(text, "\n", "<br>")
				+ "</html>";
		this.toolTip = toolTip;
		initComponents();
		super.init();
	}

	private void initComponents()
	{
		mainPanel = new JPanel();
		mainPanel.setBackground(getBackgroundColor());
		mainPanel.setLayout(new BorderLayout());
		// theTextField = new JTextField();
		label = new JLabel(labelText);
		complexValueLabel = new JLabel(valueText);
		complexValueLabel.setBackground(getBackgroundColor());
		label.setBackground(getBackgroundColor());
		label.setLabelFor(complexValueLabel);
		if (toolTip != null && toolTip.trim().length() > 0)
		{
			complexValueLabel.setToolTipText(toolTip);
			label.setToolTipText(toolTip);
		}
		labelPanel = SwingUtils.getPanelWithComponent(label,
				getBackgroundColor(), Component.LEFT_ALIGNMENT,
				Component.CENTER_ALIGNMENT, getElementBorder());
		mainPanel.add(SwingUtils.getPanelWithComponent(complexValueLabel,
				getBackgroundColor(), Component.LEFT_ALIGNMENT,
				Component.CENTER_ALIGNMENT), BorderLayout.WEST);
	}
	
	protected void setEditableState(boolean editable)
    {
    	//TODO: FIXME
    }
	
	public String getTheLabel()
	{
		return labelText;
	}

	public int getWeight()
	{
		return 0;
	}

	public Border getBorder()
	{
		return null;
	}

	JPanel getMainPanel()
	{
		return mainPanel;
	}

	public boolean isValueFilled()
	{
		if (getCurrentValue() != null)
		{
			return true;
		}
		return false;
	}

	public void markError()
	{
	}

	public void markOK()
	{
	}

	public void setMainPanelEnabled(boolean b)
	{
		complexValueLabel.setEnabled(b);
		mainPanel.setEnabled(b);
		label.setEnabled(b);
	}

	public int getFieldCategory()
	{
		return AbstractInputPanel.CAT_TEXTFIELD;
	}

	public JPanel getLabelPanel()
	{
		return labelPanel;
	}

	public Object getCurrentValue()
	{
		return null;
	}

	public void setCurrentValue(Object currentValue)
	{
		complexValueLabel.setText(currentValue.toString());

	}

}
