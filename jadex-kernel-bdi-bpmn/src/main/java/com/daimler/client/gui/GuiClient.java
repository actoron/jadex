package com.daimler.client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;

import com.daimler.client.gui.components.parts.GuiBackgroundPanel;
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
	
	public GuiClient()
	{
		initMainFrame();
	}
	
	public static void main(String[] args)
	{
		new GuiClient();
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
		scrollPane.getViewport().setBackground(Color.WHITE);
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

		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				scrollPane.getViewport().setView(emptyLabel);
				backgroundPanel.setTheCategory("");
				backgroundPanel.setTheTitle("");
				emptyLabel.doLayout();
				mainFrame.getContentPane().repaint();
			}

		});

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
}
