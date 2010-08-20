package jadex.tools.common.componenttree;

import jadex.bridge.IComponentDescription;
import jadex.commons.SUtil;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.EventObject;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
	 *  Panel for showing component properties.
	 */
	public class ComponentProperties	extends	PropertiesPanel
	{
		//-------- constructors --------
		
		/**
		 *  Create new component properties panel.
		 */
		public ComponentProperties()
		{
			super(" Component properties ");

			createTextField("Name");
			
			JTable	list	= new JTable();
			list.setBackground(getBackground());
			list.setShowGrid(false);
			list.setDefaultRenderer(Object.class, new TableCellRenderer()
			{
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column)
				{
					JTextField	ret	= new JTextField(""+value);
					Dimension	dim	= ret.getPreferredSize();
					ret.setBounds(new Rectangle(0, 0, dim.width, dim.height));
					ret.setEditable(false);
					return ret;
				}
			});
			list.setDefaultEditor(Object.class, new TableCellEditor()
			{
				public boolean stopCellEditing()
				{
					return true;
				}
				
				public boolean shouldSelectCell(EventObject anEvent)
				{
					return true;
				}
				
				public void removeCellEditorListener(CellEditorListener l)
				{
				}
				
				public boolean isCellEditable(EventObject anEvent)
				{
					return true;
				}
				
				public Object getCellEditorValue()
				{
					return null;
				}
				
				public void cancelCellEditing()
				{
				}
				
				public void addCellEditorListener(CellEditorListener l)
				{
				}
				
				public Component getTableCellEditorComponent(JTable table, Object value,
						boolean isSelected, int row, int column)
				{
					JTextField	ret	= new JTextField(""+value);
					Dimension	dim	= ret.getPreferredSize();
					ret.setBounds(new Rectangle(0, 0, dim.width, dim.height));
					ret.setEditable(false);
					return ret;
				}
			});
			addFullLineComponent("Addresses_label", new JLabel("Addresses"));
			addFullLineComponent("Addresses", list);
			
			createTextField("Type");
			createTextField("Model name");
			createTextField("Ownership");
			createTextField("State");
			createTextField("Processing state");
			
			createCheckBox("Master");
			createCheckBox("Daemon");
			createCheckBox("Auto shutdown");
		}
		
		//-------- methods --------
		
		/**
		 *  Set the description.
		 */
		public void	setDescription(IComponentDescription desc)
		{
			getTextField("Name").setText(desc.getName().getName());
			getTextField("Type").setText(desc.getType());
			getTextField("Ownership").setText(desc.getOwnership());
			getTextField("State").setText(desc.getState());
			getTextField("Processing state").setText(desc.getProcessingState());
			getCheckBox("Master").setSelected(desc.isMaster());
			getCheckBox("Daemon").setSelected(desc.isDaemon());
			getCheckBox("Auto shutdown").setSelected(desc.isAutoShutdown());
			
			JTable	list	= (JTable)getComponent("Addresses");
			String[]	addresses	= desc.getName().getAddresses();
			DefaultTableModel	dtm	= new DefaultTableModel();
			dtm.addColumn("Addresses", addresses!=null?addresses:SUtil.EMPTY_STRING_ARRAY);
			list.setModel(dtm);
		}
		
		/**
		 *  Set the model name.
		 */
		public void	setModelname(String name)
		{
			getTextField("Model name").setText(name);
		}
	}