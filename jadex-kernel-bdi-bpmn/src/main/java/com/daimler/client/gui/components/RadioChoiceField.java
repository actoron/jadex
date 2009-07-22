package com.daimler.client.gui.components;

import java.awt.Color;
import java.awt.GridLayout;
import java.text.Format;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.daimler.client.gui.components.parts.MutableRadioButton;

public class RadioChoiceField extends JPanel implements IInputComponent
{

	private int width = 300;

	private int height = 20;

	private ButtonGroup theButtonGroup;

	public RadioChoiceField(List choices, Color bgColor)
	{
		super();
		setBackground(bgColor);
		theButtonGroup = new ButtonGroup();
		setLayout(new GridLayout(choices.size(), 1));
		for (int j = 0; j < choices.size(); ++j)
		{
			Object oItem = choices.get(j);
			String sItem = oItem.toString();
			MutableRadioButton mrb = new MutableRadioButton(sItem);
			mrb.setUserObject(oItem);
			mrb.setBackground(bgColor);
			mrb.setActionCommand(sItem);
			if (j == 0)
			{
				mrb.setSelected(true);
			}

			add(mrb);
			theButtonGroup.add(mrb);
		}
	}

	public Object getValue()
	{
		Enumeration rbs = theButtonGroup.getElements();
		while (rbs.hasMoreElements())
		{
			MutableRadioButton mrb = (MutableRadioButton) rbs.nextElement();
			if (mrb.isSelected())
			{
				return mrb.getUserObject();
			}
		}
		return null;
	}

	public void setValue(Object v)
	{
		Enumeration rbs = theButtonGroup.getElements();
		while (rbs.hasMoreElements())
		{
			MutableRadioButton mrb = (MutableRadioButton) rbs.nextElement();
			if (mrb.getUserObject().equals(v))
			{
				mrb.setSelected(true);
			}
		}
	}

	public JComponent getComponent()
	{
		return this;
	}

	public int getHeight()
	{
		return height;
	}

	public int getWidth()
	{
		return width;
	}

	public void setEnabled(boolean b)
	{
		Enumeration rbuttons = theButtonGroup.getElements();
		while (rbuttons.hasMoreElements())
		{
			((AbstractButton) rbuttons.nextElement()).setEnabled(b);
		}
	}

}
