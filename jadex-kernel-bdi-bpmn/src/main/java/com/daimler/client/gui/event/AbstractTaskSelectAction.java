package com.daimler.client.gui.event;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.daimler.client.gui.GuiClient;

public abstract class AbstractTaskSelectAction extends AbstractAction
{

	protected static final ImageIcon ICON_TASK = new ImageIcon(Toolkit
			.getDefaultToolkit().createImage(
					ClassLoader.getSystemResource(AbstractTaskSelectAction.class.getPackage().getName()
							.replaceAll("event", "images.")
							.replaceAll("\\.", "/")
							+ "text_file.gif")));
	protected static final ImageIcon ICON_INFO = new ImageIcon(Toolkit
			.getDefaultToolkit().createImage(
					ClassLoader.getSystemResource(AbstractTaskSelectAction.class.getPackage().getName()
							.replaceAll("event", "images.")
							.replaceAll("\\.", "/")
							+ "info.gif")));

	private Component theContent;

	private String title;
	
	private Component parent;

	protected GuiClient client;

	public AbstractTaskSelectAction(GuiClient client, String title)
	{
		this.client = client;
		this.title = title;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == client.getOkButton())
		{
			okButtonPressed();
		} else
		{
			client.activateComponent(getTheContent());
			client.getBackgroundPanel().setTheTitle(title);
			client.getBackgroundPanel().setTheCategory("NoCategory");
			JButton okButton = client.getOkButton();
			ActionListener[] listeners = okButton.getActionListeners();
			for (int i = 0; listeners != null && i < listeners.length; i++)
			{
				okButton.removeActionListener(listeners[i]);
			}
			okButton.addActionListener(this);
		}
	}

	public abstract void okButtonPressed();

	public void dispose()
	{
		theContent = null;
		client.getOkButton().removeActionListener(this);
	}

	public Component getTheContent()
	{
		return theContent;
	}

	public void setTheContent(Component theContent)
	{
		this.theContent = theContent;
	}
	
	public void setParent(Component parent)
	{
		this.parent = parent;
	}
	
	public Component getParent()
	{
		return parent;
	}
}
