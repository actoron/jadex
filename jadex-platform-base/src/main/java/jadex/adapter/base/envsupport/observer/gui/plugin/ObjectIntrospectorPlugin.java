package jadex.adapter.base.envsupport.observer.gui.plugin;

import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.gui.ObserverCenter;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;
import jadex.adapter.base.envsupport.observer.perspective.IPerspective;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
	private JPanel mainPanel_;
	
	/** The ID label
	 */
	private JLabel idLabel_;
	
	/** The type label
	 */
	private JLabel typeLabel_;
	
	/** Property table
	 */
	private JTable propertyTable_;
	
	/** The observer center
	 */
	private ObserverCenter observerCenter_;
	
	public ObjectIntrospectorPlugin()
	{
		mainPanel_ = new JPanel(new GridBagLayout());
		mainPanel_.setBorder(new TitledBorder(NAME));
		
		mainPanel_.setMinimumSize(new Dimension(200, 200));
		
		JPanel objectPanel = new JPanel();
		objectPanel.setBorder(new TitledBorder("Object"));
		objectPanel.setLayout(new GridLayout(2, 2));
		JLabel idLabelDesc = new JLabel("Object ID");
		objectPanel.add(idLabelDesc, BorderLayout.WEST);
		idLabel_ = new JLabel("");
		objectPanel.add(idLabel_, BorderLayout.EAST);
		JLabel typeLabelDesc = new JLabel("Type");
		objectPanel.add(typeLabelDesc);
		typeLabel_ = new JLabel("");
		objectPanel.add(typeLabel_);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanel_.add(objectPanel, c);
		propertyTable_ = new JTable(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
		JScrollPane tableScrollPane = new JScrollPane(propertyTable_);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 9.0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.BOTH;
		mainPanel_.add(tableScrollPane, c);
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
		return mainPanel_;
	}
	
	public synchronized void refresh()
	{
		
		IPerspective p = observerCenter_.getSelectedPerspective();
		Object observedId = p.getSelectedObject();
		IDataView dataview = observerCenter_.getSelectedDataView();
		if ((observedId == null) || (dataview == null))
		{
			idLabel_.setText("");
			typeLabel_.setText("");
			propertyTable_.setModel(new DefaultTableModel(new Object[0][2], COLUMM_NAMES));
			return;
		}
		
		Object observedObj = dataview.getObject(observedId);
		Map properties = SObjectInspector.getProperties(observedObj);
		String type = SObjectInspector.getType(observedObj).toString();
		if ((properties == null) || (type == null))
		{
			p.setSelectedObject(null);
			return;
		}
		
		idLabel_.setText(observedId.toString());
		typeLabel_.setText(type);
		Set entries = properties.entrySet();
		Object[][] dataSet = new Object[entries.size()][2];
		int i = 0;
		for (Iterator it = entries.iterator(); it.hasNext(); )
		{
			Map.Entry entry = (Entry) it.next();
			dataSet[i][0] = entry.getKey();
			dataSet[i][1] = String.valueOf(entry.getValue());
			++i;
		}
		DefaultTableModel model = (DefaultTableModel) propertyTable_.getModel();
		model.setDataVector(dataSet, COLUMM_NAMES);
		model.fireTableStructureChanged();
	}
}
