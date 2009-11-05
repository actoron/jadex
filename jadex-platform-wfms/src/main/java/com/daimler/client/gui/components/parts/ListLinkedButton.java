package com.daimler.client.gui.components.parts;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;

public class ListLinkedButton extends JButton
{

	private JList linkedList;
	private JTextField inputField;

	public ListLinkedButton()
	{
		super();
	}

	public ListLinkedButton(String sTitle)
	{
		super(sTitle);
	}

	public JList getLinkedList()
	{
		return linkedList;
	}

	public void setLinkedList(JList theLinkedList)
	{
		this.linkedList = theLinkedList;
	}

	public JTextField getInputField()
	{
		return inputField;
	}

	public void setInputField(JTextField theInputField)
	{
		this.inputField = theInputField;
	}
}
