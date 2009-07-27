package com.daimler.client.gui;

import jadex.bpmn.runtime.ITaskContext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;

import com.daimler.client.connector.ClientConnector;
import com.daimler.client.connector.UserNotification;
import com.daimler.client.connector.INotificationStateListener;
import com.daimler.client.connector.UserNotificationStateChangeEvent;
import com.daimler.client.gui.components.parts.GuiBackgroundPanel;
import com.daimler.client.gui.components.parts.GuiHelpBrowser;
import com.daimler.client.gui.event.AbstractTaskSelectAction;
import com.daimler.client.gui.event.FetchDataTaskSelectAction;
import com.daimler.client.gui.event.ShowInfoTaskSelectAction;
import com.daimler.util.swing.autohidepanel.AutoHidePanel;
import com.daimler.util.swing.autohidepanel.HideablePanelGlassPane;
import com.daimler.util.swing.layout.EqualsLayout;

public class GuiClient
{
	private JXFrame mainFrame;

	private JLabel emptyLabel;

	private AutoHidePanel taskPanel;

	private GuiBackgroundPanel backgroundPanel;

	private JScrollPane scrollPane;

	private JButton okButton;
	
	private Map taskMapping;
	
	private UserNotification activeNotification;
	
	private GuiHelpBrowser helpBrowser;
	
	public GuiClient()
	{
		taskMapping = new HashMap();
		helpBrowser = new GuiHelpBrowser();
		initMainFrame();
		ClientConnector.getInstance().addNotificationStateListener(new ConnectorController());
	}
	
	public static void main(String[] args)
	{
		ClientConnector.getInstance();
	}
	
	public void activateComponent(Component comp)
	{
		activateComponent(comp, null);
	}
	
	public void activateComponent(final Component comp, final UserNotification notification)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (notification != null)
				{
					if (notification.equals(activeNotification))
						return;
					ClientConnector c = ClientConnector.getInstance();
					synchronized (c)
					{
						if (!c.isAvailable(notification))
							return;
						if (activeNotification != null)
						{
							c.releaseNotification(activeNotification);
						}
						activeNotification = notification;
						c.claimNotification(notification);
					}
				}
				scrollPane.getViewport().setView(comp);
				if (comp.equals(emptyLabel))
				{
					backgroundPanel.setTheCategory("");
					backgroundPanel.setTheTitle("");
				}
				comp.doLayout();
				mainFrame.getContentPane().repaint();
			}

		});
	}
	
	public JButton getOkButton()
	{
        return okButton;
    }
	
	public GuiBackgroundPanel getBackgroundPanel()
	{
        return backgroundPanel;
    }
	
	public GuiHelpBrowser getHelpBrowser()
	{
		return helpBrowser;
	}
	
	public AbstractTaskSelectAction showText(UserNotification notification)
	{
		ShowInfoTaskSelectAction sitsa = new ShowInfoTaskSelectAction(this, notification);
		addTaskSelectAction(notification, sitsa);
		return sitsa;
	}
	
	public AbstractTaskSelectAction fetchData(UserNotification notification)
	{
		FetchDataTaskSelectAction fdtsa = new FetchDataTaskSelectAction(this, notification);
		addTaskSelectAction(notification, fdtsa);
		return fdtsa;
	}
	
	private void initMainFrame()
	{
		mainFrame = new JXFrame();
		mainFrame.setTitle("Go4Flex Client");
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		emptyLabel = createStaticLabel("<html><h1>No open tasks!</h1></html>");

		backgroundPanel = new GuiBackgroundPanel();
		HideablePanelGlassPane gp = new HideablePanelGlassPane(backgroundPanel
				.getTaskPanePosition());
		taskPanel = gp.getThePanel();
		taskPanel.setTitle("Open Tasks");
		taskPanel.setAutoHideDelay(0);
		taskPanel.setAlpha(0.8f);
		taskPanel.setTitleFont(taskPanel.getTitleFont().deriveFont(20f));
		taskPanel.setTitleColor(new Color(0.6f, 0.8f, 1.0f));
		gp.setTopOffset(backgroundPanel.getTasks_Offset_Top());
		gp.setBottomOffset(backgroundPanel.getTasks_Offset_Bottom());

		mainFrame.setGlassPane(gp);
		gp.setVisible(true);

		JXPanel mainPanel = new JXPanel();
		mainPanel.setAlpha(1f);
		mainPanel.setInheritAlpha(true);
		mainPanel.setLayout(new BorderLayout());

		scrollPane = new JScrollPane();
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getViewport().setBorder(null);
		//scrollPane.setViewportBorder(null);
		
		//scrollPane.setViewportBorder(new EmptyBorder(0, 200, 0, 0));
		//scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.getViewport().setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		scrollPane.setBorder(null);
		mainPanel.add(scrollPane, BorderLayout.CENTER);
		
		/*JXPanel buffer = new JXPanel()
		{
			public Dimension getMinimumSize()
			{
				return new Dimension(2000, 1000);
			}
			
			public Dimension getSize()
			{
				return new Dimension(2000, 1000);
			}
		};
		buffer.setBackground(Color.WHITE);
		mainPanel.add(buffer, BorderLayout.WEST);*/

		okButton = new JButton("OK");
		JPanel pSouth = new JPanel(new EqualsLayout(5));
		pSouth.add(okButton);
		pSouth.setBackground(Color.WHITE);
		mainPanel.add(pSouth, BorderLayout.SOUTH);
		backgroundPanel.add(mainPanel);
		mainFrame.getContentPane().add(backgroundPanel);

		Dimension prefSize = backgroundPanel.getPreferredSize();
		mainFrame.setSize(prefSize);

		mainFrame.setResizable(false);
		activateComponent(emptyLabel);
		mainFrame.setVisible(true);
	}

	private JLabel createStaticLabel(String text)
	{
		JLabel lbTemp = new JLabel();
		lbTemp.setBackground(Color.WHITE);
		lbTemp.setHorizontalAlignment(JLabel.CENTER);
		lbTemp.setVerticalAlignment(JLabel.CENTER);
		lbTemp.setText(text);
		return lbTemp;
	}

	private void addTaskSelectAction(UserNotification notification, AbstractTaskSelectAction taskAction)
	{
		JXTaskPane tpg = null;
		for (int i = 0; i < taskPanel.getItemCount(); i++)
		{
			if (taskPanel.getItem(i) instanceof JXTaskPane)
			{
				tpg = (JXTaskPane) taskPanel.getItem(i);
			}
		}
		if (tpg == null)
		{
			tpg = new JXTaskPane();
			tpg.setTitle("Default Role");
			//tpg.setIcon(ri.getTheIcon());
			taskPanel.addItem(tpg);
		}
		Component c = tpg.add(taskAction);
		taskAction.setTaskListComponent(c);

		taskMapping.put(notification, taskAction);
		taskPanel.repaint();
		taskPanel.updateUI();
		if (emptyLabel.equals(scrollPane.getViewport().getView()))
		{
			ActionEvent a = new ActionEvent(this, 0, "");
			taskAction.actionPerformed(a);
		}
	}
	
	private void removeNotification(UserNotification notification)
	{
		AbstractTaskSelectAction action = (AbstractTaskSelectAction) taskMapping.get(notification);
		Component comp = action.getTaskListComponent();
        JXTaskPane tpgTemp;
        for (int i = 0; i < taskPanel.getItemCount(); ++i)
        {
            if (taskPanel.getItem(i) instanceof JXTaskPane)
            {
                tpgTemp = (JXTaskPane) taskPanel.getItem(i);
                tpgTemp.remove(comp);
                if (tpgTemp.getContentPane().getComponentCount() == 0) {
                    taskPanel.removeItem(tpgTemp);
                }
            }
        }
        /*if (act.getTheFetchedData() != null) {
            act.setOpen(false);
        } else {
            theTaskMapping.remove(ticket);
        }*/
        taskMapping.remove(notification);
        taskPanel.repaint();
        taskPanel.updateUI();
        if (taskPanel.getItemCount() == 0) {
            activateComponent(emptyLabel);
        }
        else
        {
        	JXTaskPane tpg = (JXTaskPane) taskPanel.getItem(0);
        	Iterator it = taskMapping.values().iterator();
        	Component c = tpg.getContentPane().getComponent(0);
        	while (it.hasNext())
        	{
        		action = (AbstractTaskSelectAction) it.next();
        		if (action.getTaskListComponent().equals(c))
        		{
        			action.actionPerformed(new ActionEvent(c, -1, ""));
        		}
        	}
        }
        
        taskPanel.repaint();
        taskPanel.updateUI();
	}
	
	private class ConnectorController implements INotificationStateListener
	{
		public void notificationAdded(UserNotificationStateChangeEvent event)
		{
			int type = event.getNotification().getType();
			switch (type)
			{
				case UserNotification.TEXT_INFO_NOTIFICATION_TYPE:
					showText(event.getNotification());
					break;
					
				case UserNotification.DATA_FETCH_NOTIFICATION_TYPE:
					fetchData(event.getNotification());
					break;
					
				default:
					throw new RuntimeException("Unknown Notification type: " + String.valueOf(type));
			}
		}
		
		public void notificationRemoved(UserNotificationStateChangeEvent event)
		{
			if (event.getNotification().equals(activeNotification))
			{
				removeNotification(event.getNotification());
				activeNotification = null;
			}
		}
		
		public void notificationClaimed(UserNotificationStateChangeEvent event)
		{
			if (!event.getNotification().equals(activeNotification))
			{
				removeNotification(event.getNotification());
			}
		}
		
		public void notificationReleased(UserNotificationStateChangeEvent event)
		{
			if (!taskMapping.containsKey(event.getNotification()))
				notificationAdded(event);
		}
	}
}
