// Title         : Agile Processes
// Description   : Demonstrator for more flexibility in large business processes
//                 using beliefs, desires and intentions.
// Copyright (c) : 2005-2007 DaimlerChrysler AG All right reserved
// Company       : MentalProof Software GmbH
//
package com.daimler.client.gui.components.parts;

import java.awt.Color;
import java.util.Date;
import java.util.List;

import com.daimler.client.gui.components.AbstractInputPanel;
import com.daimler.client.gui.components.BooleanInputPanel;
import com.daimler.client.gui.components.StringInputPanel;
import com.daimler.client.gui.components.TextInputPanel;

/**
 * 
 * @author Christian Wiech (christian.wiech@mentalproof.com)
 */
public class InputFieldFactory
{
	public static AbstractInputPanel createInputPanel(String name, String labelText, String toolTip, String helpText, Color bgColor, boolean isRequired, Class clazz, Object initialValue)
	{
		if (String.valueOf(clazz).equals("boolean"))
			clazz = Boolean.class;
		/*if (property instanceof List)
		{((List) property).get(0)
			return new CollectionInputPanel(name, labelText, helpText, bgColor, isRequired, null, 0);//taskProperty, bgColor);
		}
		if (property instanceof SetContextVariableDomain)
		{
			return new CollectionInputPanel(taskProperty, bgColor);
		}
		if (property instanceof SimpleContextVariableDomain)
		{
			return new SimpleDomainInputPanel();
		}*/
		if (clazz.equals(Date.class))
		{
			return new StringInputPanel(name, labelText, toolTip, helpText, bgColor, initialValue, isRequired, StringInputPanel.DATE_FORMAT);
		}
		if (clazz.equals(Integer.class))
		{
			return new StringInputPanel(name, labelText, toolTip, helpText, bgColor, initialValue, isRequired, StringInputPanel.INTEGER_FORMAT);
		}
		if (clazz.equals(Double.class))
		{
			return new StringInputPanel(name, labelText, toolTip, helpText, bgColor, initialValue, isRequired, StringInputPanel.DOUBLE_FORMAT);
		}
		if (clazz.equals(Long.class))
		{
			return new StringInputPanel(name, labelText, toolTip, helpText, bgColor, initialValue, isRequired, StringInputPanel.INTEGER_FORMAT);
		}
		/*if (clazz.equals(String.class))
		{
			return new StringInputPanel(name, labelText, toolTip, helpText, bgColor, isRequired, null);
		}*/
		if (clazz.equals(String.class))
		{
			return new TextInputPanel(name, labelText, toolTip, helpText, bgColor, initialValue, isRequired);
		}
		if (clazz.equals(Boolean.class))
		{
			return new BooleanInputPanel(name, labelText, toolTip, helpText, bgColor, initialValue, isRequired);
		}
		//if (property instanceof ComplexContextVariableDomain)
		//{
			// we return here a simple label that prints the content of a
			// complex contextVariable
			//return new ComplexRenderPanel(taskProperty, bgColor);
			// System.err.println(">>>>>>>>>>>>>>Input fields for complex variables are not supported yet!\n"
			// +
			// "Please query every attribute on its own");
			// return null;
		//}
		return null;
	}
}
