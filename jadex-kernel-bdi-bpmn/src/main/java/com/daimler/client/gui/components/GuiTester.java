package com.daimler.client.gui.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class GuiTester extends JFrame
{
	public GuiTester()
	{
		super("GuiTester");
	}
	
	public static void main(String[] args)
	{
		(new GuiTester()).execute();
	}
	
	public void execute()
	{
		List choices = new ArrayList();
		choices.add("Choice 1");
		choices.add("Choice 2");
		choices.add("Choice 3");
		add(new TextInputPanel("the name", "the text", "the tooltip", "the helptext", Color.white, true));
		pack();
		setVisible(true);
	}
}
