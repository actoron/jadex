package jadex.wfms.simulation.gui;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.collection.TreeNode;
import jadex.commons.gui.SGUI;
import jadex.wfms.gui.images.SImage;
import jadex.wfms.simulation.ModelTreeNode;
import jadex.wfms.simulation.Scenario;
import jadex.wfms.simulation.stateset.gui.IStatePanel;
import jadex.wfms.simulation.stateset.gui.StatePanelFactory;

import java.awt.BorderLayout;
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
import javax.swing.DefaultListSelectionModel;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class SimulationWindow extends JFrame
{
	private static final String PROCESS_MENU_NAME = "Process";
	private static final String SIMULATION_MENU_NAME = "Simulation";
	
	public static final String OPEN_MENU_ITEM_NAME = "Open...";
	public static final String CLOSE_MENU_ITEM_NAME = "Close";
	public static final String EXIT_MENU_ITEM_NAME = "Exit";
	
	public static final String START_MENU_ITEM_NAME = "Start";
	public static final String STOP_MENU_ITEM_NAME = "Stop";
	public static final String ADD_SCENARIO_ITEM_NAME = "Add Scenario";
	public static final String RENAME_SCENARIO_ITEM_NAME = "Rename Scenario";
	public static final String REMOVE_SCENARIO_ITEM_NAME = "Remove Scenario";
	public static final String AUTO_FILL_MENU_ITEM_NAME = "Autofill States";
	
	public static final ImageIcon GPMN_ICON = createImageIcon("/" + SImage.IMAGE_PATH + "gpmnicon.png");
	public static final ImageIcon BPMN_ICON = createImageIcon("/" + SImage.IMAGE_PATH + "bpmnicon.png");
	public static final ImageIcon TASK_ICON = createImageIcon("/" + SImage.IMAGE_PATH + "taskicon.png");
	public static final ImageIcon PARAM_ICON = createImageIcon("/" + SImage.IMAGE_PATH + "paramicon.png");
	
	//new ImageIcon(SimulationWindow.class.getPackage().getName().replaceAll("\\.", "/") + "/images/gpmnicon.png");
	
	private static final JPanel EMPTY_PANEL = new JPanel();
	
	private static final TreeModel EMPTY_MODEL = new DefaultTreeModel(new DefaultMutableTreeNode("No Process"));
	
	private JSplitPane scenarioPane;
	
	private JSplitPane mainPane;
	
	private JTextArea logArea;
	
	private JLabel statusBar;
	
	private Map menuItems;
	
	private JTree processModelTree;
	
	private JTable scenarioTable;
	
	private JMenuBar menuBar;
	
	private ILibraryService libService;
	
	public SimulationWindow(final TableModel scenarios, ILibraryService libService)
	{
		super("Process Simulator");
		
		this.libService = libService;
		
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
		
		scenarioPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		scenarioPane.setOneTouchExpandable(true);
		mainPane.setLeftComponent(scenarioPane);
		
		processModelTree = new JTree(EMPTY_MODEL);
		processModelTree.setSelectionModel(new DefaultTreeSelectionModel());
		processModelTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		processModelTree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e)
		    {
		    	int loc = mainPane.getDividerLocation();
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
		        else if (node.getData() instanceof MActivity)
		        {
		        	mainPane.setRightComponent(new TaskActivationPanel(((MActivity) node.getData()).getActivityType(), SimulationWindow.this));
		        }
		        else if (node.getData() instanceof MParameter)
		        {
		        	MActivity activity = (MActivity) ((ModelTreeNode) node).getParent().getData();
		        	JPanel statePanel = StatePanelFactory.createStatePanel(activity, ((MParameter) node.getData()), SimulationWindow.this);
		        	if (statePanel == null)
		        		statePanel = EMPTY_PANEL;
		        	mainPane.setRightComponent(statePanel);
		        }
		        else if (node instanceof ModelTreeNode && ((ModelTreeNode) node).getParent() == null)
		        {
		        	mainPane.setRightComponent(new MainValidationPanel(SimulationWindow.this));
		        }
		        else
		        {
		        	mainPane.setRightComponent(EMPTY_PANEL);
		        }
		        mainPane.setDividerLocation(loc);
		    }
		});

		
		JScrollPane treePane = new JScrollPane(processModelTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scenarioPane.setRightComponent(treePane);
		
		scenarioTable = new JTable(scenarios);
		DefaultListSelectionModel selectionModel = new DefaultListSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scenarioTable.setSelectionModel(selectionModel);
		selectionModel.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				if (!mainPane.getRightComponent().equals(EMPTY_PANEL))
					((IStatePanel) mainPane.getRightComponent()).refreshPanel();
			}
		});
		scenarioTable.setEnabled(false);
		JScrollPane scenarioScrollPane = new JScrollPane(scenarioTable);
		scenarioPane.setLeftComponent(scenarioScrollPane);
		
		mainPane.setRightComponent(EMPTY_PANEL);
		
		buildMenuBar();
		
		pack();
		setSize(900,600);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		logPane.setDividerLocation(0.8);
		mainPane.setDividerLocation(0.5);
		scenarioPane.setDividerLocation(0.3);
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
		
		JMenuItem addScenarioMenuItem = new JMenuItem();
		addScenarioMenuItem.setText(ADD_SCENARIO_ITEM_NAME);
		simMenu.add(addScenarioMenuItem);
		menuItems.put(ADD_SCENARIO_ITEM_NAME, addScenarioMenuItem);
		
		JMenuItem renameScenarioMenuItem = new JMenuItem();
		renameScenarioMenuItem.setText(RENAME_SCENARIO_ITEM_NAME);
		simMenu.add(renameScenarioMenuItem);
		menuItems.put(RENAME_SCENARIO_ITEM_NAME, renameScenarioMenuItem);
		
		JMenuItem removeScenarioMenuItem = new JMenuItem();
		removeScenarioMenuItem.setText(REMOVE_SCENARIO_ITEM_NAME);
		simMenu.add(removeScenarioMenuItem);
		menuItems.put(REMOVE_SCENARIO_ITEM_NAME, removeScenarioMenuItem);
		
		JMenuItem quickFillMenuItem = new JMenuItem();
		quickFillMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Auto Fill undefined!");
			}
		});
		quickFillMenuItem.setText(AUTO_FILL_MENU_ITEM_NAME);
		simMenu.add(quickFillMenuItem);
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
	
	public void setCellRenderer(TreeCellRenderer renderer)
	{		
		processModelTree.setCellRenderer(renderer);
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
	
	public Scenario getSelectedScenario()
	{
		int row = scenarioTable.getSelectedRow();
		int column = scenarioTable.getSelectedColumn();
		if (row >= 0 && column >= 0)
			return (Scenario) scenarioTable.getValueAt(row, column);
		else if (scenarioTable.getRowCount() > 0)
		{
			scenarioTable.getSelectionModel().setSelectionInterval(0, 0);
			return (Scenario) scenarioTable.getValueAt(0, 0);
		}
		return null;
	}
	
	public void selectFirstScenario()
	{
		if (scenarioTable.getRowCount() > 0)
		{
			scenarioTable.setRowSelectionInterval(0, 0);
			scenarioTable.setColumnSelectionInterval(0, 0);
		}
	}
	
	public void selectNextScenario()
	{
		if (scenarioTable.getSelectedRow() >= 0 && scenarioTable.getSelectedRow() < scenarioTable.getRowCount() - 1)
		{
			int row = scenarioTable.getSelectedRow() + 1;
			scenarioTable.setRowSelectionInterval(row, row);
			scenarioTable.setColumnSelectionInterval(0, 0);
		}
	}
	
	public boolean isLastScenario()
	{
		return (scenarioTable.getSelectedRow() >= 0 && scenarioTable.getSelectedRow() == scenarioTable.getRowCount() - 1);
	}
	
	public void setSelectedScenario(Scenario scenario)
	{
		int row = 0;
		while ((row < scenarioTable.getRowCount()) && (!scenarioTable.getValueAt(row, 0).equals(scenario)))
			++row;
		if (row < scenarioTable.getRowCount())
		{
			scenarioTable.setRowSelectionInterval(row, row);
			scenarioTable.setColumnSelectionInterval(0, 0);
		}
	}
	
	public void enableMenuItem(String name, boolean enable)
	{
		JMenuItem item = (JMenuItem) menuItems.get(name);
		if (item != null)
			item.setEnabled(enable);
	}
	
	public void enableScenarioTable(boolean enable)
	{
		scenarioTable.setEnabled(enable);
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
	
	public ILibraryService getLibService()
	{
		return libService;
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
		URL imgURL = SimulationWindow.class.getResource(path);
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
