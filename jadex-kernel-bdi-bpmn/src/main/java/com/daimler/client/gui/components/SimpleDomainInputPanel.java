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
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import com.daimler.util.swing.SwingUtils;

/**
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 */
public class SimpleDomainInputPanel extends AbstractInputPanel
{

	private String labelText;

	private String toolTip;

	private JPanel mainPanel;

	private JPanel labelPanel;

	private int fieldCategory = AbstractInputPanel.CAT_TEXTFIELD;

	private int weight = -100;

	private IInputComponent inputField;

	private JLabel label;

	private TitledBorder border;

	public SimpleDomainInputPanel(String name, String text, String labelText,
			String toolTip, String helpText, Color bgColor, Object initialValue, boolean isRequired,
			IInputComponent inputField)
	{
		super(name, helpText, bgColor, initialValue, isRequired);
		this.labelText = text;
		this.toolTip = toolTip;
		this.inputField = inputField;
		initComponents();
		super.init();
	}

	private void initComponents()
	{
		mainPanel = new JPanel();
		mainPanel.setBackground(getBackgroundColor());
		mainPanel.setLayout(new BorderLayout());
		String sLabText = labelText;
		if (isInputRequired())
		{
			sLabText += "*";
		}
		label = new JLabel(sLabText);
		label.setBackground(getBackgroundColor());
		/*
		 * if
		 * (getTheAccessible().getTheDomain().isInstanceOf(DomainFactory.DOM_STRING
		 * ) && ((SimpleContextVariableDomain)
		 * getTheAccessible().getTheDomain()).getThePattern() != null) { //We
		 * need a String input panel that accepts only valid entries
		 * theInputField = new RegExprTextField((SimpleContextVariableDomain)
		 * getTheAccessible().getTheDomain()); theFieldCategory =
		 * AbstractInputPanel.CAT_TEXTFIELD; theWeight = -100;
		 * 
		 * theLabelPanel = SwingUtils.getPanelWithComponent(theLabel,
		 * getTheBackgroundColor(), Component.LEFT_ALIGNMENT,
		 * Component.CENTER_ALIGNMENT, getTheElementBorder());
		 * theMainPanel.add(SwingUtils
		 * .getPanelWithComponentAndSize(theInputField.getTheComponent(),
		 * getTheBackgroundColor(), new Dimension(theInputField.getTheWidth(),
		 * theInputField.getTheHeight()), getTheElementBorder()),
		 * BorderLayout.WEST); } else if (((SimpleContextVariableDomain)
		 * getTheAccessible().getTheDomain()).getTheRestrictedDomain().size() >
		 * 3) { //We need a combobox to choose from theInputField = new
		 * ComboChoiceField((SimpleContextVariableDomain)
		 * getTheAccessible().getTheDomain()); theFieldCategory =
		 * AbstractInputPanel.CAT_TEXTFIELD; theWeight = -100; theLabelPanel =
		 * SwingUtils.getPanelWithComponent(theLabel, getTheBackgroundColor(),
		 * Component.LEFT_ALIGNMENT, Component.CENTER_ALIGNMENT,
		 * getTheElementBorder());
		 * theMainPanel.add(SwingUtils.getPanelWithComponentAndSize
		 * (theInputField.getTheComponent(), getTheBackgroundColor(), new
		 * Dimension(theInputField.getTheWidth(), theInputField.getTheHeight()),
		 * getTheElementBorder()), BorderLayout.WEST); } else if
		 * (((SimpleContextVariableDomain)
		 * getTheAccessible().getTheDomain()).getTheRestrictedDomain().size() <
		 * 4){ // we need some radiobuttons theInputField = new
		 * RadioChoiceField((SimpleContextVariableDomain)
		 * getTheAccessible().getTheDomain(), getTheBackgroundColor());
		 * theFieldCategory = AbstractInputPanel.CAT_COLLECTION_INPUT; theWeight
		 * = 0; theLabelPanel = null; theBorder =
		 * BorderFactory.createTitledBorder(BorderFactory .createEtchedBorder(),
		 * sLabText); theInputField.getTheComponent().setBorder(theBorder);
		 * theMainPanel =
		 * SwingUtils.getPanelWithComponent(theInputField.getTheComponent(),
		 * getTheBackgroundColor(), Component.LEFT_ALIGNMENT,
		 * Component.CENTER_ALIGNMENT , getTheElementBorder());
		 * 
		 * }
		 */

		if (inputField instanceof RadioChoiceField)
		{
			fieldCategory = AbstractInputPanel.CAT_COLLECTION_INPUT;
			weight = 0;
			labelPanel = null;
			border = BorderFactory.createTitledBorder(BorderFactory
					.createEtchedBorder(), sLabText);
			inputField.getComponent().setBorder(border);
			mainPanel = SwingUtils.getPanelWithComponent(inputField
					.getComponent(), getBackgroundColor(),
					Component.LEFT_ALIGNMENT, Component.CENTER_ALIGNMENT,
					getElementBorder());
		} else
		{
			fieldCategory = AbstractInputPanel.CAT_TEXTFIELD;
			weight = -100;
			labelPanel = SwingUtils.getPanelWithComponent(label,
					getBackgroundColor(), Component.LEFT_ALIGNMENT,
					Component.CENTER_ALIGNMENT, getElementBorder());
			mainPanel.add(SwingUtils.getPanelWithComponentAndSize(inputField
					.getComponent(), getBackgroundColor(), new Dimension(
					inputField.getWidth(), inputField.getHeight()),
					getElementBorder()), BorderLayout.WEST);
		}

		if (toolTip != null && toolTip.trim().length() > 0)
		{
			inputField.getComponent().setToolTipText(toolTip);
			label.setToolTipText(toolTip);
		}
		label.setLabelFor(inputField.getComponent());
		/*
		 * if (getTheAccessible().getTheValue() != null) {
		 * setTheCurrentValue(getTheAccessible().getTheValue()); }
		 */
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
		return weight;
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
		if (label != null)
		{
			label.setForeground(Color.RED);
		}
		if (border != null)
		{
			border.setTitleColor(Color.RED);
			mainPanel.repaint();
		}
	}

	public void markOK()
	{
		if (label != null)
		{
			label.setForeground(Color.BLACK);
		}
		if (border != null)
		{
			border.setTitleColor(Color.BLACK);
			mainPanel.repaint();
		}
	}

	public void setMainPanelEnabled(boolean b)
	{
		inputField.setEnabled(b);
		if (label != null)
		{
			label.setEnabled(b);
		}
		mainPanel.setEnabled(b);
	}

	public int getFieldCategory()
	{
		return fieldCategory;
	}

	public JPanel getLabelPanel()
	{
		return labelPanel;
	}

	public Object getCurrentValue()
	{
		return inputField.getValue();
	}

	public void setCurrentValue(Object currentValue)
	{
		inputField.setValue(currentValue);
	}

}
