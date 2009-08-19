package com.daimler.client.gui.event;

import jadex.wfms.client.GuiClient;
import jadex.wfms.client.IWorkitem;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;

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
	
	private IWorkitem workitem;

	private Component theContent;

	private String title;
	
	private Component taskListComponent;

	protected GuiClient client;

	public AbstractTaskSelectAction(GuiClient client, String title, IWorkitem workitem)
	{
		this.client = client;
		this.title = title;
		this.workitem = workitem;
	}
	
	public IWorkitem getWorkitem()
	{
		return workitem;
	}
	
	public String getTitle()
	{
		return title;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == client.getOkButton())
		{
			okButtonPressed();
		} else
		{
			client.activateComponent(getTheContent(), workitem);
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
	
	public void setTaskListComponent(Component parent)
	{
		this.taskListComponent = parent;
	}
	
	public Component getTaskListComponent()
	{
		return taskListComponent;
	}
}
