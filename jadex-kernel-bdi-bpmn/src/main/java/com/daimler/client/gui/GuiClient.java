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

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;

import com.daimler.client.connector.ClientConnector;
import com.daimler.client.connector.ClientRequest;
import com.daimler.client.connector.IRequestListener;
import com.daimler.client.gui.components.parts.GuiBackgroundPanel;
import com.daimler.client.gui.event.AbstractTaskSelectAction;
import com.daimler.client.gui.event.ShowInfoTaskSelectAction;
import com.daimler.util.swing.autohidepanel.AutoHidePanel;
import com.daimler.util.swing.autohidepanel.HideablePanelGlassPane;
import com.daimler.util.swing.layout.EqualsLayout;

public class GuiClient implements IRequestListener
{
	private JXFrame mainFrame;

	private JLabel emptyLabel;

	private AutoHidePanel taskPanel;

	private GuiBackgroundPanel backgroundPanel;

	private JScrollPane scrollPane;

	private JButton okButton;
	
	private Map taskMapping;
	
	public GuiClient()
	{
		taskMapping = new HashMap();
		initMainFrame();
	}
	
	public static void main(String[] args)
	{
		ClientConnector.getInstance();
	}
	
	public void activateComponent(final Component comp)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
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
	
	public AbstractTaskSelectAction showText(ClientRequest request)
	{
		ShowInfoTaskSelectAction sitsa = new ShowInfoTaskSelectAction(this, request);
		addTaskSelectAction(request, sitsa);
		return sitsa;
	}
	
	public void finishedRequest(ClientRequest request)
	{
		AbstractTaskSelectAction action = (AbstractTaskSelectAction) taskMapping.get(request);
		Component comp = action.getParent();
        JXTaskPane tpgTemp;
        for (int i = 0; i < taskPanel.getItemCount(); i++) {
            if (taskPanel.getItem(i) instanceof JXTaskPane) {
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
        		if (action.getParent().equals(c))
        		{
        			action.actionPerformed(new ActionEvent(c, -1, ""));
        		}
        	}
        }
	}
	
	public void startedRequest(ClientRequest request)
	{
		//TODO: distinguish between requests
		showText(request);
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
		scrollPane.setViewportBorder(null);
		//scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.getViewport().setBackground(new Color(0.0f, 0.0f, 0.0f, 0.0f));
		scrollPane.setBorder(null);
		mainPanel.add(scrollPane, BorderLayout.CENTER);

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

	private void addTaskSelectAction(ClientRequest request, AbstractTaskSelectAction taskAction)
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
		taskAction.setParent(c);

		taskMapping.put(request, taskAction);
		taskPanel.repaint();
		taskPanel.updateUI();
		if (emptyLabel.equals(scrollPane.getViewport().getView()))
		{
			ActionEvent a = new ActionEvent(this, 0, "");
			taskAction.actionPerformed(a);
		}
	}
}
