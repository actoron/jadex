package jadex.wfms.simulation.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import jadex.commons.SGUI;
import jadex.wfms.simulation.Simulator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;

public class SimulationWindow extends JFrame
{
	private static final String PROCESS_MENU_NAME = "Process";
	private static final String SIMULATION_MENU_NAME = "Simulation";
	
	private static final String OPEN_MENU_ITEM_NAME = "Open...";
	private static final String CLOSE_MENU_ITEM_NAME = "Close";
	private static final String EXIT_MENU_ITEM_NAME = "Exit";
	
	private static final String START_MENU_ITEM_NAME = "Start";
	private static final String STOP_MENU_ITEM_NAME = "Stop";
	
	private static final JPanel EMPTY_PANEL = new JPanel();
	
	private Simulator simulator;
	
	private JTree processModelTree;
	
	private JMenuBar menuBar;
	
	public SimulationWindow(Simulator simulator)
	{
		super("Process Simulator");
		this.simulator = simulator;
		
		JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainPane.setOneTouchExpandable(true);
		add(mainPane);
		
		JScrollPane treePane = new JScrollPane();
		mainPane.setLeftComponent(treePane);
		processModelTree = new JTree();
		treePane.add(processModelTree);
		
		mainPane.setRightComponent(EMPTY_PANEL);
		
		/*int row = 0;
		while (row < tree.getRowCount())
			tree.collapseRow(row++);*/
		
		buildMenuBar();
		
		pack();
		setSize(600,300);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		mainPane.setDividerLocation(0.4);
	}
	
	private void buildMenuBar()
	{
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu processMenu = new JMenu(PROCESS_MENU_NAME);
		processMenu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(processMenu);
		
		JMenuItem openItem = new JMenuItem();
		openItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Open active!");
			}
		});
		openItem.setText(OPEN_MENU_ITEM_NAME);
		openItem.setMnemonic(KeyEvent.VK_O);
		openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		processMenu.add(openItem);
		
		JMenuItem closeItem = new JMenuItem();
		closeItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Close active!");
			}
		});
		closeItem.setText(CLOSE_MENU_ITEM_NAME);
		closeItem.setMnemonic(KeyEvent.VK_C);
		closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		processMenu.add(closeItem);
		
		processMenu.addSeparator();
		
		JMenuItem exitItem = new JMenuItem();
		exitItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				SimulationWindow.this.dispose();
			}
		});
		exitItem.setText(EXIT_MENU_ITEM_NAME);
		exitItem.setMnemonic(KeyEvent.VK_X);
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		processMenu.add(exitItem);
		
		
		JMenu simMenu = new JMenu(SIMULATION_MENU_NAME);
		simMenu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(simMenu);
		
		JMenuItem startItem = new JMenuItem();
		startItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Start active!");
			}
		});
		startItem.setText(START_MENU_ITEM_NAME);
		startItem.setMnemonic(KeyEvent.VK_S);
		startItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		simMenu.add(startItem);
		
		JMenuItem stopItem = new JMenuItem();
		stopItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Stop active!");
			}
		});
		stopItem.setText(STOP_MENU_ITEM_NAME);
		stopItem.setMnemonic(KeyEvent.VK_T);
		stopItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		simMenu.add(stopItem);
	}
}
