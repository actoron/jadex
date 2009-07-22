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

import com.daimler.client.gui.components.parts.HelpButton;
import com.daimler.client.gui.components.parts.NoHelpPlaceholder;
import com.daimler.util.swing.SwingUtils;

/**
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 */
public abstract class AbstractInputPanel extends JPanel implements Comparable
{

	public static final int CAT_TEXTFIELD = 2;

	public static final int CAT_COLLECTION_INPUT = 1;

	public static final int CAT_BOOLEAN = 1;

	public static final int CAT_COLLECTION = 1;

	private String helpText = null;;

	private Border elementBorder;

	private Color backgroundColor;

	private boolean isRequired = false;

	public AbstractInputPanel(String helpText,
			Color bgColor, boolean isRequired)
	{
		super();
		this.isRequired = isRequired;
		this.backgroundColor = bgColor;
		this.helpText = helpText;
		if (this.helpText != null && this.helpText.trim().length() == 0)
		{
			this.helpText = null;
		}
		this.elementBorder = BorderFactory.createMatteBorder(5, 10, 5, 10, bgColor);
	}

	protected void init()
	{
		// add(SwingUtils.getPanelWithComponent(lb, getBackground(),
		// Component.LEFT_ALIGNMENT, Component.CENTER_ALIGNMENT,
		// theElementBorder));
		setBackground(backgroundColor);
		JPanel p1 = getMainPanel();
		JPanel p2;
		if (helpText != null && helpText.trim().length() > 0)
		{

			p2 = SwingUtils.getPanelWithComponentAndSize(new HelpButton(
					helpText, getBackground()), getBackground(),
					new Dimension(20, 20), elementBorder);
		} else
		{
			p2 = SwingUtils.getPanelWithComponentAndSize(new NoHelpPlaceholder(
					getBackground()), getBackground(), new Dimension(20, 20),
					elementBorder);
		}
		setLayout(new BorderLayout());
		add(p2, BorderLayout.EAST);
		add(p1, BorderLayout.CENTER);
		if (getBorder() != null)
		{
			setBorder(getBorder());
		}
	}

	public abstract JPanel getLabelPanel();

	abstract JPanel getMainPanel();

	public abstract Border getBorder();

	public abstract int getFieldCategory();

	public String getHelptext()
	{
		return helpText;
	}

	public boolean isInputRequired()
	{
		return isRequired;
	}

	public abstract void markError();

	/**
	 * Changes the color of a label that corresponds to the GUI-element of a
	 * taskproperty to black. This indicates that the entered value is
	 * acceptable.
	 */
	public abstract void markOK();

	/**
	 * Changes the color of a label that corresponds to the GUI-element of a
	 * taskproperty to red. This indicates that the entered value is not
	 * acceptable.
	 */
	public abstract void setMainPanelEnabled(boolean b);

	public void setEnabled(boolean b)
	{
		super.setEnabled(b);
		setMainPanelEnabled(b);
	}

	public abstract boolean isValueFilled();

	public abstract int getWeight();

	public int compareTo(AbstractInputPanel o)
	{
		return getWeight() - o.getWeight();
	}

	protected Border getElementBorder()
	{
		return elementBorder;
	}

	protected Color getBackgroundColor()
	{
		return backgroundColor;
	}

	public abstract Object getCurrentValue();

	// public abstract Object getTheCurrentValue() {
	// return theCurrentValue;
	// }

	public abstract void setCurrentValue(Object currentValue);

	// public void setTheCurrentValue(Object theCurrentValue) {
	// this.theCurrentValue = theCurrentValue;
	// }

	// public IAccessible getTheAccessible() {
	// return theAccessible;
	// }
	
	
	public int compareTo(Object arg0)
	{
		// TODO Fixme
		return 0;
	}
}
