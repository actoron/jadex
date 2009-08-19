// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.client.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.daimler.util.swing.SwingUtils;

/**
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 */
public class TextInputPanel extends AbstractInputPanel
{

	private String labelText;

	private String toolTip;

	private JPanel mainPanel;

	private TextTextField textField;

	private TitledBorder border;

	public TextInputPanel(String name, String text, String toolTip,
			String helpText, Color bgColor, Object initialValue, boolean isRequired)
	{
		super(name, helpText, bgColor, initialValue, isRequired);
		this.labelText = text;
		this.toolTip = toolTip;
		initComponents();
		super.init();
	}

	private void initComponents()
	{
		mainPanel = new JPanel();
		mainPanel.setBackground(getBackgroundColor());
		mainPanel.setLayout(new BorderLayout());
		textField = new TextTextField();
		String sLabText = labelText;
		if (isInputRequired())
		{
			sLabText += "*";
		}
		border = BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), sLabText);
		if (toolTip != null && toolTip.trim().length() > 0)
		{
			textField.setToolTipText(toolTip);
		}
		mainPanel.add(SwingUtils.getPanelWithComponentAndSize(textField,
				getBackgroundColor(), new Dimension(textField.getWidth(),
						textField.getHeight()), getElementBorder()),
				BorderLayout.WEST);
		mainPanel.setBorder(border);
	}
	
	protected void setEditableState(boolean editable)
    {
    	textField.setEnabled(editable);
    }
	
	public String getLabel()
	{
		return labelText;
	}

	public int getWeight()
	{
		return -25;
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
			if (getCurrentValue() instanceof String
					&& ((String) getCurrentValue()).trim().length() == 0)
			{
				return false;
			}
			return true;
		}
		return false;
	}

	public void markError()
	{
		border.setTitleColor(Color.RED);
		mainPanel.repaint();
	}

	public void markOK()
	{
		border.setTitleColor(Color.BLACK);
		mainPanel.repaint();
	}

	public void setMainPanelEnabled(boolean b)
	{
		textField.setEnabled(b);
		mainPanel.setEnabled(b);
	}

	public int getFieldCategory()
	{
		return AbstractInputPanel.CAT_COLLECTION_INPUT;
	}

	public JPanel getLabelPanel()
	{
		return null;
	}

	public Object getCurrentValue()
	{
		return textField.getValue();
	}

	public void setCurrentValue(Object currentValue)
	{
		textField.setValue(currentValue);

	}

}
