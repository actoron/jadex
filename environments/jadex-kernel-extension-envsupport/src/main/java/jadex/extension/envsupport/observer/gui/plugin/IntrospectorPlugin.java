package jadex.extension.envsupport.observer.gui.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import jadex.extension.envsupport.environment.IObjectTask;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.gui.ObserverCenter;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.extension.envsupport.observer.perspective.IPerspective;

/** The Introspector
 */
public class IntrospectorPlugin implements IObserverCenterPlugin
{
	/** Plugin name
	 */
	private static final String NAME = "Introspector";
	
	/** Column names
	 */
	private static final String[] COLUMM_NAMES = {"Property", "Value"};
	
	/** The main panel
	 */
	private JTabbedPane mainPane_;
	
	/** Space property table */
	private JTable spacePropertyTable_;
	
	/** Perspective property table */
	private JTable perspPropertyTable_;
	
	/** Process list */
	private JList processList_;
	
	/** Process property table */
	private JTable processPropertyTable_;
	
	/** Task list */
	private JList taskList_;
	
	/** Task property table */
	private JTable taskPropertyTable_;
	
	/** The ID label
	 */
	private JLabel objIdLabel_;
	
	/** The type label
	 */
	private JLabel objTypeLabel_;
	
	/** Object property table
	 */
	private JTable objPropertyTable_;
	
	/** The observer center
	 */
	private ObserverCenter observerCenter_;
	
	/** 
	 * Object selection listener.
	 */
	private ChangeListener selectionListener;
	
	/**
	 * 
	 */
	public IntrospectorPlugin()
	{
		mainPane_ = new JTabbedPane();
		mainPane_.setMinimumSize(new Dimension(50, 400));
		
		JSplitPane spacePane = new JSplitPane();
		spacePane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		spacePane.setOneTouchExpandable(true);
		spacePane.setDividerLocation(100);
		spacePane.setResizeWeight(0.33);
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		mainPane_.add("Space", spacePane);
		
		JPanel spacePropertyPanel = new JPanel();
		spacePropertyPanel.setLayout(new GridBagLayout());
		spacePane.setTopComponent(spacePropertyPanel);
		
		spacePropertyTable_ = new JTable(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
		JScrollPane spcPropScrollPane = new JScrollPane(spacePropertyTable_);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		spacePropertyPanel.add(spcPropScrollPane, c);
		TableColumn col = spacePropertyTable_.getColumnModel().getColumn(0);
		col.setPreferredWidth(80);
		col.setMaxWidth(300);

		
		JPanel processPanel = new JPanel();
		processPanel.setBorder(new TitledBorder("Processes"));
		processPanel.setLayout(new GridBagLayout());
		spacePane.setBottomComponent(processPanel);
		
		JSplitPane processPane = new JSplitPane();
		processPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		processPane.setOneTouchExpandable(true);
		processPane.setDividerLocation(60);
		processPane.setResizeWeight(0.5);
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		processPanel.add(processPane, c);
		
		processList_ = new JList(new DefaultComboBoxModel());
		processList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		processList_.setBorder(new BevelBorder(BevelBorder.LOWERED));
		processPane.setTopComponent(processList_);
		
		processPropertyTable_ = new JTable(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
		JScrollPane procPropScrollPane = new JScrollPane(processPropertyTable_);
		processPane.setBottomComponent(procPropScrollPane);
		col = processPropertyTable_.getColumnModel().getColumn(0);
		col.setPreferredWidth(80);
		col.setMaxWidth(300);
		
		final JSplitPane objectPane = new JSplitPane();
		objectPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		objectPane.setOneTouchExpandable(true);
		objectPane.setDividerLocation(100);
		objectPane.setResizeWeight(0.33);
		mainPane_.add("Object", objectPane);
		
		final JPanel perspPropertyPane = new JPanel(new BorderLayout());
		mainPane_.add("Perspective", perspPropertyPane);
		perspPropertyTable_ = new JTable(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
		JScrollPane sp = new JScrollPane(perspPropertyTable_);
		perspPropertyPane.add(sp, BorderLayout.CENTER);
		col = perspPropertyTable_.getColumnModel().getColumn(0);
		col.setPreferredWidth(80);
		col.setMaxWidth(300);
				
		JPanel objectPropertyPanel = new JPanel();
		objectPropertyPanel.setLayout(new GridBagLayout());
		objectPane.setTopComponent(objectPropertyPanel);
		
		JLabel idLabelDesc = new JLabel("Object ID");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.ipadx = 10;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		objectPropertyPanel.add(idLabelDesc, c);
		
		objIdLabel_ = new JLabel("");
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		objectPropertyPanel.add(objIdLabel_, c);
		
		JLabel typeLabelDesc = new JLabel("Type");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.ipadx = 10;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		objectPropertyPanel.add(typeLabelDesc, c);
		
		objTypeLabel_ = new JLabel("");
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		objectPropertyPanel.add(objTypeLabel_, c);
		
		objPropertyTable_ = new JTable(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
		JScrollPane tableScrollPane = new JScrollPane(objPropertyTable_);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		objectPropertyPanel.add(tableScrollPane, c);
		col = objPropertyTable_.getColumnModel().getColumn(0);
		col.setPreferredWidth(80);
		col.setMaxWidth(300);
		
		/////
		JPanel taskPanell = new JPanel();
		taskPanell.setBorder(new TitledBorder("Tasks"));
		taskPanell.setLayout(new GridBagLayout());
		objectPane.setBottomComponent(taskPanell);
		
		JSplitPane taskPane = new JSplitPane();
		taskPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		taskPane.setOneTouchExpandable(true);
		taskPane.setDividerLocation(60);
		taskPane.setResizeWeight(0.5);
		c = new GridBagConstraints();
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		taskPanell.add(taskPane, c);
		
		taskList_ = new JList(new DefaultComboBoxModel());
		taskList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		taskList_.setBorder(new BevelBorder(BevelBorder.LOWERED));
		taskPane.setTopComponent(taskList_);
		
		taskPropertyTable_ = new JTable(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
		JScrollPane taskPropScrollPane = new JScrollPane(taskPropertyTable_);
		taskPane.setBottomComponent(taskPropScrollPane);
		col = taskPropertyTable_.getColumnModel().getColumn(0);
		col.setPreferredWidth(80);
		col.setMaxWidth(300);
		
		selectionListener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				Object selection = observerCenter_.getSelectedPerspective().getSelectedObject();
				if (selection != null)
					mainPane_.setSelectedComponent(objectPane);
			}
		};
	}

	public synchronized void start(ObserverCenter main)
	{
		observerCenter_ = main;
		observerCenter_.addSelectedObjectListener(selectionListener);
	}
	
	public synchronized void shutdown()
	{
		observerCenter_.removeSelectedObjectListener(selectionListener);
	}
	
	public synchronized String getName()
	{
		return NAME;
	}
	
	/** Returns the path to the icon for the plugin in the toolbar.
	 * 
	 *  @return path to the icon
	 */
	public String getIconPath()
	{
		return getClass().getPackage().getName().replaceAll("gui\\.plugin","").concat("images.").replaceAll("\\.", "/").concat("introspector_icon.png");
	}
	
	public synchronized Component getView()
	{
		return mainPane_;
	}
	
	public synchronized void refresh()
	{
		fillPropertyTable(spacePropertyTable_, observerCenter_.getSpace());
		
		DefaultComboBoxModel plModel = (DefaultComboBoxModel)processList_.getModel();
		Object selection = processList_.getSelectedValue();
		plModel.removeAllElements();
		Set processIds = observerCenter_.getSpace().getSpaceProcessNames();
		for(Iterator it = processIds.iterator(); it.hasNext(); )
		{
			Object id = it.next();
//			String str = SReflect.getUnqualifiedClassName(it.next().getClass())+"( id="+id+")";
			ISpaceProcess proc = observerCenter_.getSpace().getSpaceProcess(id);
			if(proc!=null)
				plModel.addElement(proc);
		}
		processList_.setSelectedValue(selection, true);
//		ISpaceProcess proc = observerCenter_.getSpace().getSpaceProcess(selection);
		if(selection != null)
		{
			fillPropertyTable(processPropertyTable_, selection);
		}
		else
		{
			((DefaultTableModel)processPropertyTable_.getModel()).setRowCount(0);
		}
		
		IPerspective p = observerCenter_.getSelectedPerspective();
		if(p==null)
			System.out.println("h");
		Object observedObj = p==null? null: p.getSelectedObject();
		if(observedObj == null)
		{
			objIdLabel_.setText("");
			objTypeLabel_.setText("");
			objPropertyTable_.setModel(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
			TableColumn col = objPropertyTable_.getColumnModel().getColumn(0);
			col.setPreferredWidth(80);
			col.setMaxWidth(300);
		}
		else
		{
			if(!fillPropertyTable(objPropertyTable_, observedObj))
			{
				if(p!=null)
					p.setSelectedObject(null);
			}
			else
			{
				objIdLabel_.setText(String.valueOf(SObjectInspector.getId(observedObj)));
				String type = String.valueOf(SObjectInspector.getType(observedObj));
				objTypeLabel_.setText(type);
				
				if(observedObj instanceof SpaceObject)
				{
					DefaultComboBoxModel tlModel = (DefaultComboBoxModel) taskList_.getModel();
					SpaceObject sObj = (SpaceObject) observedObj;
					Collection tasks = sObj.getTasks();
					selection = taskList_.getSelectedValue();
					tlModel.removeAllElements();
					for(Iterator it = tasks.iterator(); it.hasNext(); )
					{
						IObjectTask task = (IObjectTask)it.next();
		//				String str = SReflect.getUnqualifiedClassName(it.next().getClass())+" "+task;
						tlModel.addElement(task);
					}
					if (tasks.contains(selection))
						taskList_.setSelectedValue(selection, true);
				}
				if(selection != null)
				{
					fillPropertyTable(taskPropertyTable_, selection);
				}
				else
				{
					((DefaultTableModel) taskPropertyTable_.getModel()).setRowCount(0);
				}
			}
		}
			
		fillPropertyTable(perspPropertyTable_, observerCenter_.getSelectedPerspective());
	}
	
	private static boolean fillPropertyTable(JTable table, Object propHolder)
	{
		Set propNames = propHolder==null? null: SObjectInspector.getPropertyNames(propHolder);
//		System.out.println("prop holder: "+propHolder+" "+propNames);
		if(propNames != null)
		{
			Object[][] dataSet = new Object[propNames.size()][2];
			int i = 0;
			for(Iterator it = propNames.iterator(); it.hasNext(); )
			{
				String name = (String)it.next();
				dataSet[i][0] = name;
				dataSet[i][1] = String.valueOf(SObjectInspector.getProperty(propHolder, name));
				++i;
			}
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			model.setRowCount(dataSet.length);
			for (i = 0; i < dataSet.length; ++i)
			{
				model.setValueAt(dataSet[i][0], i, 0);
				model.setValueAt(dataSet[i][1], i, 1);
			}
		}
		return propNames!=null;
	}
	
	/**
	 *  Should plugin be visible.
	 */
	public boolean isVisible()
	{
		return true;
	}
	
	/**
	 *  Should plugin be started on load.
	 */
	public boolean isStartOnLoad()
	{
		return false;
	}
}
