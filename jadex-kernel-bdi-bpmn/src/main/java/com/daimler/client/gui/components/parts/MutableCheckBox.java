package com.daimler.client.gui.components.parts;

import javax.swing.JCheckBox;

public class MutableCheckBox extends JCheckBox
{

	Object userObject = null;

	public MutableCheckBox()
	{
		super();
	}

	public MutableCheckBox(String sTitle)
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
