package com.daimler.client.gui.components.parts;

import javax.swing.JRadioButton;

public class MutableRadioButton extends JRadioButton
{
	Object userObject;

	public MutableRadioButton()
	{
		super();
	}

	public MutableRadioButton(String sTitle)
	{
		super(sTitle);
	}

	public Object getUserObject()
	{
		return userObject;
	}

	public void setUserObject(Object userObject)
	{
		this.userObject = userObject;
	}

}
