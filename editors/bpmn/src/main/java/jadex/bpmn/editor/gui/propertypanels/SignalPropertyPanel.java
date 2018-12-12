package jadex.bpmn.editor.gui.propertypanels;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MProperty;
import jadex.bridge.ClassInfo;
import jadex.commons.collection.IndexMap;
import jadex.commons.gui.autocombo.AutoComboTableCellEditor;
import jadex.commons.gui.autocombo.AutoComboTableCellRenderer;
import jadex.commons.gui.autocombo.AutoCompleteCombo;
import jadex.commons.gui.autocombo.ClassInfoComboBoxRenderer;
import jadex.commons.gui.autocombo.ComboBoxEditor;
import jadex.commons.gui.autocombo.FixedClassInfoComboModel;

/**
 * 
 */
public class SignalPropertyPanel extends BasePropertyPanel
{
	protected final static IndexMap<String, MProperty> EMPTY_MAP = new IndexMap<String, MProperty>();
	
	/** The column names for the properties table. */
	protected String[] PROPERTIES_COLUMN_NAMES = {"Name", "Type", "Value"};
	
	/** The event. */
	protected VActivity vevent;
	
	/**
	 *  Creates the panel.
	 */
	public SignalPropertyPanel(ModelContainer container, Object selection)
	{
		super("Signal", container);
		setLayout(new BorderLayout());
		
		VActivity event = (VActivity) selection;
		vevent = event;
		
		JPanel tablepanel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		final JTable proptable = new JTable(new PropertyTableModel());
		JScrollPane tablescrollpane = new JScrollPane(proptable);
		tablepanel.add(tablescrollpane, gc);
		
		final AutoCompleteCombo acc = new AutoCompleteCombo(null, null);
		final FixedClassInfoComboModel accm = new FixedClassInfoComboModel(acc, 20, modelcontainer.getAllClasses());
		acc.setModel(accm);
		acc.setEditor(new ComboBoxEditor(accm));
		acc.setRenderer(new ClassInfoComboBoxRenderer());

		TableColumn col = proptable.getColumnModel().getColumn(1);
		col.setCellEditor(new AutoComboTableCellEditor(acc));
		col.setCellRenderer(new AutoComboTableCellRenderer(acc));
		
		Action addaction = new AbstractAction("Add Property")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(proptable.isEditing())
					proptable.getCellEditor().stopCellEditing();
				
				int row = getMEvent().getProperties()!=null? getMEvent().getProperties().size(): 0;
				MProperty prop = new MProperty();
				prop.setName(createFreeName("name", new CollectionContains(getMEvent().getProperties()!=null? getMEvent().getProperties().keySet(): Collections.EMPTY_LIST)));
				prop.setInitialValue("null");
				getMEvent().addProperty(prop);
				((PropertyTableModel)proptable.getModel()).fireTableRowsInserted(row, row);
				modelcontainer.setDirty(true);
			}
		};
		Action removeaction = new AbstractAction("Remove Properties")
		{
			public void actionPerformed(ActionEvent e)
			{
				if(proptable.isEditing())
					proptable.getCellEditor().stopCellEditing();
				
				int[] ind = proptable.getSelectedRows();
				if(ind.length>0)
				{
					Arrays.sort(ind);
					
					IndexMap<String, MProperty> props = getMEvent().getProperties();
					for(int i = ind.length - 1; i >= 0; --i)
					{
						props.remove(ind[i]);
						((PropertyTableModel)proptable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
						modelcontainer.setDirty(true);
					}
				}
			}
		};
		
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(modelcontainer.getSettings().getImageProvider(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		tablepanel.add(buttonpanel, gc);
		add(tablepanel, BorderLayout.CENTER);
	}
	
	/**
	 *  Gets the semantic exception event.
	 * 
	 *  @return The event.
	 */
	protected MActivity getMEvent()
	{
		return (MActivity)vevent.getBpmnElement();
	}
	
//	/**
//	 * 
//	 */
//	protected IndexMap<String, MProperty> getProperties()
//	{
//		return getMEvent().getProperties()!=null? getMEvent().getProperties(): EMPTY_MAP;
//	}
	
	/**
	 *  Table model for model properties.
	 */
	protected class PropertyTableModel extends AbstractTableModel
	{
		/**
		 *  Gets the column name.
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			return PROPERTIES_COLUMN_NAMES[column];
		}
		
		/**
	     *  Returns whether a cell is editable.
	     *
	     *  @param  rowIndex The row being queried.
	     *  @param  columnIndex The column being queried.
	     *  @return If a cell is editable.
	     */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			return true;
		}
		
		/**
		 *  Returns the row count.
		 *  
		 *  @return The row count.
		 */
		public int getRowCount()
		{
			return getMEvent().getProperties()!=null? getMEvent().getProperties().size(): 0;
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return PROPERTIES_COLUMN_NAMES.length;
		}
		
		/**
		 *  Gets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			Object ret = null;
			MProperty prop = getMEvent().getProperties().get(rowIndex);
			switch (columnIndex)
			{
				case 0:
				default:
					ret = prop.getName();
					break;
				case 1:
					ret = prop.getClazz();
					break;
				case 2:
					ret = prop.getInitialValueString();
			}
			return ret;
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param value The value.
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			MProperty prop = getMEvent().getProperties().get(rowIndex);
			switch (columnIndex)
			{
				case 0:
				default:
					if(!value.equals(getValueAt(rowIndex, columnIndex)))
					{
						getMEvent().getProperties().removeKey(prop.getName());
						prop.setName(createFreeName((String)value, new CollectionContains(getMEvent().getProperties().keySet())));
						getMEvent().addProperty(prop);
					}
					break;
				case 1:
					prop.setClazz((ClassInfo)value);
					break;
				case 2:
					prop.setInitialValue((String)value);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
			modelcontainer.setDirty(true);
		}
	}
}
