package jadex.wfms.simulation.gui;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.commons.SGUI;
import jadex.commons.collection.TreeNode;
import jadex.gpmn.model.MGpmnModel;
import jadex.wfms.simulation.ClientProcessMetaModel;
import jadex.wfms.simulation.ModelTreeNode;
import jadex.wfms.simulation.SimLauncher;
import jadex.wfms.simulation.stateholder.IParameterStateSet;
import jadex.wfms.simulation.stateholder.gui.IStatePanel;
import jadex.wfms.simulation.stateholder.gui.StatePanelFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class SimulationWindow extends JFrame
{
	private static final String PROCESS_MENU_NAME = "Process";
	private static final String SIMULATION_MENU_NAME = "Simulation";
	private static final String TEST_MENU_NAME = "Test";
	
	public static final String OPEN_MENU_ITEM_NAME = "Open...";
	public static final String AUTO_FILL_MENU_ITEM_NAME = "Autofill States";
	public static final String CLOSE_MENU_ITEM_NAME = "Close";
	public static final String EXIT_MENU_ITEM_NAME = "Exit";
	
	public static final String START_MENU_ITEM_NAME = "Start";
	public static final String STOP_MENU_ITEM_NAME = "Stop";
	
	private static final String IMAGE_PATH = "/" + SimulationWindow.class.getPackage().getName().replaceAll("\\.", "/") + "/images/";
	private static final ImageIcon GPMN_ICON = createImageIcon(IMAGE_PATH + "gpmnicon.png");
	private static final ImageIcon BPMN_ICON = createImageIcon(IMAGE_PATH + "bpmnicon.png");
	private static final ImageIcon TASK_ICON = createImageIcon(IMAGE_PATH + "taskicon.png");
	private static final ImageIcon PARAM_ICON = createImageIcon(IMAGE_PATH + "paramicon.png");
	
	//new ImageIcon(SimulationWindow.class.getPackage().getName().replaceAll("\\.", "/") + "/images/gpmnicon.png");
	
	private static final JPanel EMPTY_PANEL = new JPanel();
	
	private static final TreeModel EMPTY_MODEL = new DefaultTreeModel(new DefaultMutableTreeNode("No Process"));
	
	private JSplitPane mainPane;
	
	private JTextArea logArea;
	
	private JLabel statusBar;
	
	private Map menuItems;
	
	private JTree processModelTree;
	
	private JMenuBar menuBar;
	
	public SimulationWindow()
	{
		super("Process Simulator");
		
		menuItems = new HashMap();
		
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		
		JSplitPane logPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		logPane.setOneTouchExpandable(true);
		add(logPane);
		JPanel logPanel = new JPanel(new BorderLayout());
		logPane.setBottomComponent(logPanel);
		logArea = new JTextArea();
		logArea.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(logArea);
		logPanel.add(logScrollPane, BorderLayout.CENTER);
		
		statusBar = new JLabel();
		statusBar.setFont(statusBar.getFont().deriveFont(Font.PLAIN));
		logPanel.add(statusBar, BorderLayout.SOUTH);
		statusBar.setText(" ");
		
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainPane.setOneTouchExpandable(true);
		logPane.setTopComponent(mainPane);
		
		processModelTree = new JTree(EMPTY_MODEL);
		processModelTree.setSelectionModel(new DefaultTreeSelectionModel());
		processModelTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		processModelTree.setCellRenderer(new DefaultTreeCellRenderer()
		{
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if (value instanceof TreeNode)
				{
					Object data = ((TreeNode) value).getData();
					if (data instanceof MGpmnModel)
						setIcon(GPMN_ICON);
					else if (data instanceof MBpmnModel)
						setIcon(BPMN_ICON);
					else if (data instanceof MActivity)
						setIcon(TASK_ICON);
					else if (data instanceof IParameterStateSet)
					{
						setIcon(PARAM_ICON);
						if (((IParameterStateSet) data).getStateCount() == 0)
							setForeground(new Color(128,128,128));
					}
					else if (data instanceof TreeNode)
						setForeground(new Color(128,128,128));
				}
				
				return this;
			}
		});
		processModelTree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e)
		    {
		    	Object selection = processModelTree.getLastSelectedPathComponent();
		    	if ((selection == null) || !(selection instanceof TreeNode))
		    		return;
		        TreeNode node = (TreeNode) selection;
		        if (node.getData() instanceof ModelTreeNode)
		        {
		        	ModelTreeNode mNode = (ModelTreeNode) node.getData();
		        	LinkedList pathList = new LinkedList();
		        	pathList.addFirst(mNode);
		        	while ((mNode = mNode.getParent()) != null)
		        		pathList.addFirst(mNode);
		        	TreePath path = new TreePath(pathList.toArray());
		        	processModelTree.expandPath(path);
		        	processModelTree.scrollPathToVisible(path);
		        	processModelTree.setSelectionPath(path);
		        }
		        else if (node.getData() instanceof IParameterStateSet)
		        {
		        	JPanel statePanel = StatePanelFactory.createStatePanel(((IParameterStateSet) node.getData()));
		        	if (statePanel == null)
		        		statePanel = EMPTY_PANEL;
		        	mainPane.setRightComponent(statePanel);
		        }
		        else
		        {
		        	mainPane.setRightComponent(EMPTY_PANEL);
		        }
		    }
		});

		
		JScrollPane treePane = new JScrollPane(processModelTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPane.setLeftComponent(treePane);
		//mainPane.setLeftComponent(processModelTree);
		
		mainPane.setRightComponent(EMPTY_PANEL);
		
		buildMenuBar();
		
		pack();
		setSize(600,400);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		logPane.setDividerLocation(0.8);
		mainPane.setDividerLocation(0.4);
		addLogMessage("Client Simulator started.");
	}
	
	private void buildMenuBar()
	{
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu processMenu = new JMenu(PROCESS_MENU_NAME);
		processMenu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(processMenu);
		
		JMenuItem openMenuItem = new JMenuItem();
		openMenuItem.setText(OPEN_MENU_ITEM_NAME);
		openMenuItem.setMnemonic(KeyEvent.VK_O);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		processMenu.add(openMenuItem);
		menuItems.put(OPEN_MENU_ITEM_NAME, openMenuItem);
		
		JMenuItem closeMenuItem = new JMenuItem();
		closeMenuItem.setText(CLOSE_MENU_ITEM_NAME);
		closeMenuItem.setMnemonic(KeyEvent.VK_C);
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		processMenu.add(closeMenuItem);
		menuItems.put(CLOSE_MENU_ITEM_NAME, closeMenuItem);
		
		processMenu.addSeparator();
		
		JMenuItem exitMenuItem = new JMenuItem();
		exitMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				SimulationWindow.this.dispose();
			}
		});
		exitMenuItem.setText(EXIT_MENU_ITEM_NAME);
		exitMenuItem.setMnemonic(KeyEvent.VK_X);
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		processMenu.add(exitMenuItem);
		menuItems.put(EXIT_MENU_ITEM_NAME, exitMenuItem);
		
		
		JMenu simMenu = new JMenu(SIMULATION_MENU_NAME);
		simMenu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(simMenu);
		
		JMenuItem startMenuItem = new JMenuItem();
		startMenuItem.setText(START_MENU_ITEM_NAME);
		startMenuItem.setMnemonic(KeyEvent.VK_S);
		startMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		startMenuItem.setEnabled(false);
		simMenu.add(startMenuItem);
		menuItems.put(START_MENU_ITEM_NAME, startMenuItem);
		
		JMenuItem stopMenuItem = new JMenuItem();
		stopMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Stop undefined!");
			}
		});
		stopMenuItem.setText(STOP_MENU_ITEM_NAME);
		stopMenuItem.setMnemonic(KeyEvent.VK_T);
		stopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		stopMenuItem.setEnabled(false);
		simMenu.add(stopMenuItem);
		menuItems.put(STOP_MENU_ITEM_NAME, stopMenuItem);
		
		JMenu testMenu = new JMenu(TEST_MENU_NAME);
		testMenu.setMnemonic(KeyEvent.VK_T);
		menuBar.add(testMenu);
		
		JMenuItem quickFillMenuItem = new JMenuItem();
		quickFillMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Auto Fill undefined!");
			}
		});
		quickFillMenuItem.setText(AUTO_FILL_MENU_ITEM_NAME);
		testMenu.add(quickFillMenuItem);
		menuItems.put(AUTO_FILL_MENU_ITEM_NAME, quickFillMenuItem);
	}
	
	public String showProcessPickerDialog(Set modelNames)
	{
		return (String)JOptionPane.showInputDialog(this,
                "Pick a process",
                "Processes",
                JOptionPane.PLAIN_MESSAGE,
                null,
                modelNames.toArray(), "");

	}
	
	public void showMessage(int type, String title, String text)
	{
		JOptionPane.showMessageDialog(this, text, title, type);
	}
	
	public void setMenuItemAction(String name, Action action)
	{
		JMenuItem item = (JMenuItem) menuItems.get(name);
		if (item != null)
			setMenuItemAction(item, action);
	}
	
	public void addLogMessage(String msg)
	{
		logArea.append(msg + "\n");
	}
	
	public void setStatusBar(String msg)
	{
		statusBar.setText(msg);
	}
	
	public void refreshParameterStates()
	{
		Object panel = mainPane.getRightComponent();
		if (panel instanceof IStatePanel)
			((IStatePanel) panel).refreshPanel();
		processModelTree.repaint();
	}
	
	public void enableMenuItem(String name, boolean enable)
	{
		JMenuItem item = (JMenuItem) menuItems.get(name);
		if (item != null)
			item.setEnabled(enable);
	}
	
	public void setProcessTreeModel(TreeModel model)
	{
		if (model == null)
		{
			model = EMPTY_MODEL;
			mainPane.setRightComponent(EMPTY_PANEL);
		}
		processModelTree.setModel(model);
		int row = 0;
		while (row < processModelTree.getRowCount())
			processModelTree.collapseRow(row++);
		row = 0;
		while (row < processModelTree.getRowCount())
			processModelTree.expandRow(row++);
		processModelTree.repaint();
	}
	
	private void setMenuItemAction(JMenuItem menuItem, Action action)
	{
		int mnemonic = menuItem.getMnemonic();
		KeyStroke accelerator = menuItem.getAccelerator();
		String text = menuItem.getText();
		
		menuItem.setAction(action);
		
		menuItem.setMnemonic(mnemonic);
		menuItem.setAccelerator(accelerator);
		menuItem.setText(text);
	}
	
	private static final ImageIcon createImageIcon(String path)
	{
		URL imgURL = SimLauncher.class.getResource(path);
		if (imgURL != null)
			try
			{
				return new ImageIcon(ImageIO.read(imgURL).getScaledInstance(20, 20, Image.SCALE_SMOOTH));
			} catch (IOException e)
			{
				System.err.println("Icon not found: " + path);
			}
		else
			System.err.println("Icon not found: " + path);
		return null;
	}
}
