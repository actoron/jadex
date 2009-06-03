package jadex.adapter.base.envsupport.observer.gui.plugin;

import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.SpaceObject;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;
import jadex.adapter.base.envsupport.observer.perspective.IPerspective;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/** The object introspector
 */
public class ObjectIntrospectorPlugin implements IObserverCenterPlugin
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
	
	public ObjectIntrospectorPlugin()
	{
		mainPane_ = new JTabbedPane();
		mainPane_.setMinimumSize(new Dimension(200, 300));
		
		JPanel spacePanel = new JPanel();
		spacePanel.setBorder(new TitledBorder("Space"));
		spacePanel.setLayout(new GridBagLayout());
		
		spacePropertyTable_ = new JTable(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
		JScrollPane spcPropScrollPane = new JScrollPane(spacePropertyTable_);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		spacePanel.add(spcPropScrollPane, c);
		
		JPanel processPanel = new JPanel();
		processPanel.setBorder(new TitledBorder("Processes"));
		processPanel.setLayout(new GridBagLayout());
		
		processList_ = new JList(new DefaultComboBoxModel());
		processList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		processList_.setBorder(new BevelBorder(BevelBorder.LOWERED));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		processPanel.add(processList_, c);
		
		processPropertyTable_ = new JTable(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
		JScrollPane procPropScrollPane = new JScrollPane(processPropertyTable_);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 2.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		processPanel.add(procPropScrollPane, c);
		
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 2.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		spacePanel.add(processPanel, c);
		
		mainPane_.add("Space", spacePanel);
		
		JPanel objectPanel = new JPanel();
		objectPanel.setBorder(new TitledBorder("Object"));
		objectPanel.setLayout(new GridBagLayout());
		
		JLabel idLabelDesc = new JLabel("Object ID");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.ipadx = 10;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		objectPanel.add(idLabelDesc, c);
		
		objIdLabel_ = new JLabel("");
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		objectPanel.add(objIdLabel_, c);
		
		JLabel typeLabelDesc = new JLabel("Type");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 0.0;
		c.ipadx = 10;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		objectPanel.add(typeLabelDesc, c);
		
		objTypeLabel_ = new JLabel("");
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 0.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.NONE;
		objectPanel.add(objTypeLabel_, c);
		
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
		objectPanel.add(tableScrollPane, c);
		
		/////
		JPanel taskPanel = new JPanel();
		taskPanel.setBorder(new TitledBorder("Tasks"));
		taskPanel.setLayout(new GridBagLayout());
		
		taskList_ = new JList(new DefaultComboBoxModel());
		taskList_.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		taskList_.setBorder(new BevelBorder(BevelBorder.LOWERED));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		taskPanel.add(taskList_, c);
		
		taskPropertyTable_ = new JTable(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
		JScrollPane taskPropScrollPane = new JScrollPane(taskPropertyTable_);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 2.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		taskPanel.add(taskPropScrollPane, c);
		
		
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 2;
		c.weighty = 2.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.BOTH;
		objectPanel.add(taskPanel, c);
		/////
		
		mainPane_.add("Object", objectPanel);
	}

	public synchronized void start(ObserverCenter main)
	{
		observerCenter_ = main;
	}
	
	public synchronized void shutdown()
	{
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
		
		DefaultComboBoxModel plModel = (DefaultComboBoxModel) processList_.getModel();
		Object selection = processList_.getSelectedValue();
		plModel.removeAllElements();
		Set processIds = observerCenter_.getSpace().getSpaceProcessNames();
		for (Iterator it = processIds.iterator(); it.hasNext(); )
		{
			Object id = it.next();
			plModel.addElement(id);
		}
		processList_.setSelectedValue(selection, true);
		ISpaceProcess proc = observerCenter_.getSpace().getSpaceProcess(selection);
		if (proc != null)
			fillPropertyTable(processPropertyTable_, proc);
		else
			((DefaultTableModel) processPropertyTable_.getModel()).setRowCount(0);
		
		IPerspective p = observerCenter_.getSelectedPerspective();
		Object observedObj = p.getSelectedObject();
		if (observedObj == null)
		{
			objIdLabel_.setText("");
			objTypeLabel_.setText("");
			objPropertyTable_.setModel(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
			return;
		}
		
		if (!fillPropertyTable(objPropertyTable_, observedObj))
		{
			p.setSelectedObject(null);
			return;
		}
		
		objIdLabel_.setText(String.valueOf(SObjectInspector.getId(observedObj)));
		String type = String.valueOf(SObjectInspector.getType(observedObj));
		objTypeLabel_.setText(type);
		
		if (observedObj instanceof SpaceObject)
		{
			DefaultComboBoxModel tlModel = (DefaultComboBoxModel) taskList_.getModel();
			SpaceObject sObj = (SpaceObject) observedObj;
			Set tasks = sObj.getTasks();
			selection = taskList_.getSelectedValue();
			tlModel.removeAllElements();
			for (Iterator it = tasks.iterator(); it.hasNext(); )
				tlModel.addElement(it.next());
			if (tasks.contains(selection))
				taskList_.setSelectedValue(selection, true);
		}
		if (selection != null)
			fillPropertyTable(taskPropertyTable_, selection);
		else
			((DefaultTableModel) taskPropertyTable_.getModel()).setRowCount(0);
	}
	
	private static boolean fillPropertyTable(JTable table, Object propHolder)
	{
		Set propNames = SObjectInspector.getPropertyNames(propHolder);
		if (propNames == null)
		{
			return false;
		}
		
		
		Object[][] dataSet = new Object[propNames.size()][2];
		int i = 0;
		for (Iterator it = propNames.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			dataSet[i][0] = name;
			dataSet[i][1] = String.valueOf(SObjectInspector.getProperty(propHolder, name));
			++i;
		}
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(dataSet.length);
		for (i = 0; i < dataSet.length; ++i)
		{
			model.setValueAt(dataSet[i][0], i, 0);
			model.setValueAt(dataSet[i][1], i, 1);
		}
		return true;
	}
}
