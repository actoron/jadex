// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.client.gui.components.parts;

import java.awt.Color;

/**
 * IInputField has to be implemented by the fields for the GUI to get data from
 * the user. The input elements should usually be constructed giving the domain
 * as a paramter to the element.
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 * 
 */
public interface IInputField
{
	/**
	 * Returns the label text for this element.
	 * 
	 * @return returns the text used to label this input field.
	 */
	public String getLabel();

	/**
	 * Sets the label text for this input field to be displayed.
	 * 
	 * @param sLabel
	 *            the text to be used as label for this inpit field
	 */
	public void setLabel(String sLabel);

	/**
	 * Returns <code>true</code> if the input field is filled with some value.
	 * 
	 * @return
	 */
	public boolean hasValue();

	/**
	 * Enables this input field.
	 * 
	 * @param b
	 *            - <code>true</code>, if the field should be enabled
	 */
	public void setEnabled(boolean b);

	/**
	 * Indicates whether this input field is enabled or not.
	 * 
	 * @return
	 */
	public boolean isEnabled();

	/**
	 * Set the background color for the input field.
	 * 
	 * @param bgColor
	 *            the color to be used as background
	 */
	public void setBackgroundColor(Color bgColor);

	/**
	 * Returns the color used for the background of this input field.
	 * 
	 * @return the background color
	 */
	public Color getBackgroundColor();
}
