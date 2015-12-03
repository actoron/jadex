package jadex.bpmn.editor.gui.propertypanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.commons.IFilter;
import jadex.commons.SUtil;

public class SubcomponentTab extends JPanel
{
	protected static final String[] SUBCOMPONENT_TYPES_COLUMN_NAMES = new String[] { "Name", "Model" };
	
	protected ModelContainer modelcontainer;
	
	/**
	 *  Creates the tab pane.
	 *  @param container The model container.
	 */
	public SubcomponentTab(ModelContainer container)
	{
		super();
		this.modelcontainer = container;
		setLayout(new GridBagLayout());
		
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		final JTable sctypetable = new JTable(new SubcomponentTypeTableModel());
		JScrollPane tablescrollpane = new JScrollPane(sctypetable);
		add(tablescrollpane, gc);
		
		Action addaction = new AbstractAction("Add Subcomponent Type")
		{
			public void actionPerformed(ActionEvent e)
			{
				BasePropertyPanel.stopEditing(sctypetable);
				
				int row = sctypetable.getRowCount();
				ModelInfo modelinfo = (ModelInfo) modelcontainer.getBpmnModel().getModelInfo();
				String name = BasePropertyPanel.createFreeName("name", new SubcomponentTypesContains(modelinfo.getSubcomponentTypes()));
				SubcomponentTypeInfo scti = new SubcomponentTypeInfo(name, "");
				modelinfo.addSubcomponentType(scti);
				modelcontainer.setDirty(true);
				((SubcomponentTypeTableModel) sctypetable.getModel()).fireTableRowsInserted(row, row);
			}
		};
		
		Action removeaction = new AbstractAction("Remove Subcomponent Type")
		{
			public void actionPerformed(ActionEvent e)
			{
				BasePropertyPanel.stopEditing(sctypetable);
				
				int[] ind = sctypetable.getSelectedRows();
				Arrays.sort(ind);
				ModelInfo modelinfo = (ModelInfo) modelcontainer.getBpmnModel().getModelInfo();
				
				List<SubcomponentTypeInfo> scinfos = SUtil.arrayToList(modelinfo.getSubcomponentTypes());
				for (int i = ind.length - 1; i >= 0; --i)
				{
					String rmname = scinfos.remove(ind[i]).getName();
					modelinfo.setSubcomponentTypes(scinfos.toArray(new SubcomponentTypeInfo[scinfos.size()]));
					ConfigurationInfo[] confinfos = modelinfo.getConfigurations();
					for (ConfigurationInfo cinfo : confinfos)
					{
						ComponentInstanceInfo[] ciinfos = cinfo.getComponentInstances();
						for(ComponentInstanceInfo ciinfo : ciinfos)
						{
							if(!rmname.equals(ciinfo.getTypeName()))
							{
//								cinfo.removeComponentInstance(ciinfo);
							}
						}
					}
					((SubcomponentTypeTableModel) sctypetable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
				}
				modelcontainer.setDirty(true);
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(modelcontainer.getSettings().getImageProvider(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		add(buttonpanel, gc);
	}
	
	/**
	 *  Table model for types.
	 *
	 */
	protected class SubcomponentTypeTableModel extends AbstractTableModel
	{
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			String ret = SUBCOMPONENT_TYPES_COLUMN_NAMES[column];
			return ret;
		}
		
		/**
		 *  Returns the column class.
		 */
		public Class<?> getColumnClass(int columnIndex)
		{
			return super.getColumnClass(columnIndex);
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
			return modelcontainer.getBpmnModel().getModelInfo().getSubcomponentTypes().length;
		}

		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return SUBCOMPONENT_TYPES_COLUMN_NAMES.length;
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
			SubcomponentTypeInfo[] infos = modelcontainer.getBpmnModel().getModelInfo().getSubcomponentTypes();
			switch (columnIndex)
			{
				case 1:
				{
					return infos[rowIndex].getFilename();
				}
				case 0:
				default:
				{
					return infos[rowIndex].getName();
				}
			}
		}
		
		/**
		 *  Sets the value.
		 *  
		 *  @param rowIndex The row.
		 *  @param columnIndex The column.
		 *  @return The value.
		 */
		public void setValueAt(Object value, int rowIndex, int columnIndex)
		{
			SubcomponentTypeInfo[] infos = modelcontainer.getBpmnModel().getModelInfo().getSubcomponentTypes();
			switch (columnIndex)
			{
				case 1:
				{
					infos[rowIndex].setFilename((String) value);
					modelcontainer.setDirty(true);
					break;
				}
				case 0:
				default:
				{
					String newname = (String) value;
					String oldname = infos[rowIndex].getName();
					if (!oldname.equals(newname))
					{
						newname = BasePropertyPanel.createFreeName((String) value, new SubcomponentTypesContains(infos));
						
						infos[rowIndex].setName((String) value);
						ConfigurationInfo[] confinfos = modelcontainer.getBpmnModel().getModelInfo().getConfigurations();
						for (ConfigurationInfo cinfo : confinfos)
						{
							ComponentInstanceInfo[] ciinfos = cinfo.getComponentInstances();
							for (ComponentInstanceInfo ciinfo : ciinfos)
							{
								if (oldname.equals(ciinfo.getTypeName()))
								{
									ciinfo.setTypeName(newname);
								}
							}
						}
						
						modelcontainer.setDirty(true);
					}
					break;
				}
			}
		}
	}
	
	/**
	 *  Tests if the subcomponent types contain a name.
	 *
	 */
	protected class SubcomponentTypesContains implements IFilter<String>
	{
		/** The infos */
		protected SubcomponentTypeInfo[] infos;
		
		/**
		 *  Creates the filter, duh.
		 */
		public SubcomponentTypesContains(SubcomponentTypeInfo[] infos)
		{
			this.infos = infos;
		}
		
		/**
		 *  Test if an object passes the filter.
		 *  @return True, if passes the filter.
		 */
		public boolean filter(String obj)
		{
			for (SubcomponentTypeInfo info : infos)
			{
				if (obj.equals(info.getName()))
				{
					return true;
				}
			}
			return false;
		}
	}
}
