package jadex.wfms.simulation.gui;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.commons.SGUI;
import jadex.commons.collection.TreeNode;
import jadex.gpmn.model.MGpmnModel;
import jadex.wfms.simulation.ModelTreeNode;
import jadex.wfms.simulation.SimLauncher;
import jadex.wfms.simulation.stateholder.IParameterStateSet;
import jadex.wfms.simulation.stateholder.gui.StatePanelFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
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
	
	private static final String OPEN_MENU_ITEM_NAME = "Open...";
	private static final String CLOSE_MENU_ITEM_NAME = "Close";
	private static final String EXIT_MENU_ITEM_NAME = "Exit";
	
	private static final String START_MENU_ITEM_NAME = "Start";
	private static final String STOP_MENU_ITEM_NAME = "Stop";
	
	private static final String IMAGE_PATH = "/" + SimulationWindow.class.getPackage().getName().replaceAll("\\.", "/") + "/images/";
	private static final ImageIcon GPMN_ICON = createImageIcon(IMAGE_PATH + "gpmnicon.png");
	private static final ImageIcon BPMN_ICON = createImageIcon(IMAGE_PATH + "bpmnicon.png");
	private static final ImageIcon TASK_ICON = createImageIcon(IMAGE_PATH + "taskicon.png");
	private static final ImageIcon PARAM_ICON = createImageIcon(IMAGE_PATH + "paramicon.png");
	
	//new ImageIcon(SimulationWindow.class.getPackage().getName().replaceAll("\\.", "/") + "/images/gpmnicon.png");
	
	private static final JPanel EMPTY_PANEL = new JPanel();
	
	private static final TreeModel EMPTY_MODEL = new DefaultTreeModel(new DefaultMutableTreeNode("No Process"));
	
	private JSplitPane mainPane;
	
	private JTree processModelTree;
	
	private JMenuBar menuBar;
	
	private JMenuItem openMenuItem;
	private JMenuItem closeMenuItem;
	
	public SimulationWindow()
	{
		super("Process Simulator");
		
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainPane.setOneTouchExpandable(true);
		add(mainPane);
		
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
						setIcon(PARAM_ICON);
					else if (data instanceof TreeNode)
						setForeground(Color.LIGHT_GRAY);
				}
				
				return this;
			}
		});
		processModelTree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e)
		    {
		    	
		        TreeNode node = (TreeNode) processModelTree.getLastSelectedPathComponent();
		        
		        if (node == null)
		        	return;
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
		
		openMenuItem = new JMenuItem();
		openMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Open undefined!");
			}
		});
		openMenuItem.setText(OPEN_MENU_ITEM_NAME);
		openMenuItem.setMnemonic(KeyEvent.VK_O);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		processMenu.add(openMenuItem);
		
		closeMenuItem = new JMenuItem();
		closeMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Close undefined!");
			}
		});
		closeMenuItem.setText(CLOSE_MENU_ITEM_NAME);
		closeMenuItem.setMnemonic(KeyEvent.VK_C);
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		processMenu.add(closeMenuItem);
		
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
		
		
		JMenu simMenu = new JMenu(SIMULATION_MENU_NAME);
		simMenu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(simMenu);
		
		JMenuItem startMenuItem = new JMenuItem();
		startMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Start undefined!");
			}
		});
		startMenuItem.setText(START_MENU_ITEM_NAME);
		startMenuItem.setMnemonic(KeyEvent.VK_S);
		startMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		simMenu.add(startMenuItem);
		
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
		simMenu.add(stopMenuItem);
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
	
	public void setOpenAction(Action action)
	{
		setMenuAction(openMenuItem, action);
	}
	
	public void setCloseAction(Action action)
	{
		setMenuAction(closeMenuItem, action);
	}
	
	public void setProcessTreeModel(TreeModel model)
	{
		if (model == null)
			model = EMPTY_MODEL;
		processModelTree.setModel(model);
		int row = 0;
		while (row < processModelTree.getRowCount())
			processModelTree.collapseRow(row++);
		row = 0;
		while (row < processModelTree.getRowCount())
			processModelTree.expandRow(row++);
		processModelTree.repaint();
	}
	
	private void setMenuAction(JMenuItem menuItem, Action action)
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
