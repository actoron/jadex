package jadex.bpmn.editor.gui.propertypanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.table.AbstractTableModel;

import jadex.bpmn.editor.gui.ModelContainer;
import jadex.bpmn.editor.model.visual.VActivity;
import jadex.bpmn.editor.model.visual.VSequenceEdge;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.Tuple2;

public class SequenceEdgePropertyPanel extends BasePropertyPanel
{
	/** The column names for the mapping table */
	protected String[] MAPPINGS_COLUMN_NAMES = { "Name", "Value" };
	
	/** The sequence edge */
	protected VSequenceEdge seqedge;
	
	/** The mappings table. */
	protected JTable maptable;
	
	/**
	 *  Creates a new property panel.
	 *  @param container The model container.
	 */
	public SequenceEdgePropertyPanel(ModelContainer container, Object selection)
	{
		super("Sequence Edge", container);
		VSequenceEdge edge = (VSequenceEdge) selection;
		this.seqedge = edge;
		
		int y = 0;
		int colnum = 0;
		JPanel column = createColumn(colnum++);
		
		JLabel label = new JLabel("Default Edge");
		JCheckBox defaultbox = new JCheckBox();
		defaultbox.setSelected(getBpmnSequenceEdge().isDefault());
		defaultbox.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				boolean selected = ((JCheckBox) e.getSource()).isSelected();
				if (selected)
				{
					VActivity vsactivity = (VActivity) seqedge.getSource();
					MActivity sactivity = (MActivity) vsactivity.getBpmnElement();
					List<MSequenceEdge> edges = sactivity.getOutgoingSequenceEdges();
					for (MSequenceEdge edge : edges)
					{
						edge.setDefault(false);
					}
					
					modelcontainer.getGraph().getView().invalidate(vsactivity);
					/*modelcontainer.getGraph().getModel().beginUpdate();
					for (int i = 0; i < vsactivity.getEdgeCount(); ++i)
					{
						modelcontainer.getGraph().getModel().setStyle(vsactivity.getEdgeAt(i), VSequenceEdge.class.getSimpleName());
					}
					modelcontainer.getGraph().getModel().endUpdate();*/
					
					
				}
				getBpmnSequenceEdge().setDefault(selected);
				modelcontainer.getGraphComponent().refresh();
			}
		});
		configureAndAddInputLine(column, label, defaultbox, y++);
		
		if (!(edge.getSource() instanceof VActivity && MBpmnModel.GATEWAY_DATABASED_EXCLUSIVE.equals(((MActivity) ((VActivity) edge.getSource()).getBpmnElement()).getActivityType())))
		{
			defaultbox.setEnabled(false);
		}
		
		label = new JLabel("Condition");
		JTextArea condarea = new JTextArea();
		condarea.setText(getBpmnSequenceEdge().getCondition() != null? (String) getBpmnSequenceEdge().getCondition().getValue() : "");
		condarea.getDocument().addDocumentListener(new DocumentAdapter()
		{
			public void update(DocumentEvent e)
			{
				getBpmnSequenceEdge().setCondition(new UnparsedExpression("", "java.lang.Boolean", getText(e.getDocument()), null));
				modelcontainer.setDirty(true);
			}
		});
		configureAndAddInputLine(column, label, condarea, y++);
		
		JPanel mappanel = new JPanel(new GridBagLayout());
		mappanel.setBorder(new TitledBorder("Mappings"));
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridy = 1;
		gc.gridheight = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.fill = GridBagConstraints.BOTH;
		gc.insets = new Insets(0, 5, 5, 0);
		maptable = new JTable(new MappingsTableModel());
		JScrollPane mapscrollpane = new JScrollPane(maptable);
		mappanel.add(mapscrollpane, gc);
		
		Action addaction = new AbstractAction("Add Mapping")
		{
			public void actionPerformed(ActionEvent e)
			{
				stopEditing(maptable);
				
				String name = createFreeName("name", new BasePropertyPanel.IndexMapContains(getBpmnSequenceEdge().getParameterMappings()));
				int row = maptable.getRowCount();
				getBpmnSequenceEdge().addParameterMapping(name, new UnparsedExpression(name, "java.lang.Object", "", null), null);
				((MappingsTableModel) maptable.getModel()).fireTableRowsInserted(row, row);
			}
		};
		Action removeaction = new AbstractAction("Remove Mappings")
		{
			public void actionPerformed(ActionEvent e)
			{
				stopEditing(maptable);
				
				int[] ind = maptable.getSelectedRows();
				Arrays.sort(ind);
				
				for (int i = ind.length - 1; i >= 0; --i)
				{
					getBpmnSequenceEdge().getParameterMappings().remove(ind[i]);
					((MappingsTableModel) maptable.getModel()).fireTableRowsDeleted(ind[i], ind[i]);
				}
			}
		};
		AddRemoveButtonPanel buttonpanel = new AddRemoveButtonPanel(modelcontainer.getSettings().getImageProvider(), addaction, removeaction);
		gc = new GridBagConstraints();
		gc.gridx = 1;
		gc.gridy = 1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets = new Insets(0, 0, 5, 5);
		mappanel.add(buttonpanel, gc);
		configureAndAddInputLine(column, null, mappanel, y++);
		
		gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = y;
		gc.gridwidth = 2;
		gc.weightx = 1.0;
		gc.weighty = 1.0;
		gc.anchor = GridBagConstraints.SOUTH;
		gc.fill = GridBagConstraints.BOTH;
		
		// Parameter mapping disabled.
//		column.add(mappanel, gc);
		addVerticalFiller(column, y);
	}
	
	/**
	 * Get the edge.
	 */
	protected MSequenceEdge getBpmnSequenceEdge()
	{
		return (MSequenceEdge) seqedge.getBpmnElement();
	}
	
	/**
	 *  Terminate.
	 */
	public void terminate()
	{
		if (maptable.isEditing())
		{
			maptable.getCellEditor().stopCellEditing();
		}
	}
	
	/**
	 *  Table model for parameters.
	 */
	protected class MappingsTableModel extends AbstractTableModel
	{
		/**
		 *  Gets the column name.
		 *  
		 *  @return The column name.
		 */
		public String getColumnName(int column)
		{
			return MAPPINGS_COLUMN_NAMES[column];
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
			return getBpmnSequenceEdge().getParameterMappings() != null?
				   getBpmnSequenceEdge().getParameterMappings().size() : 0;
		}
		
		/**
		 *  Returns the column count.
		 *  
		 *  @return The column count.
		 */
		public int getColumnCount()
		{
			return 2;
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
			if(columnIndex == 0)
			{
				ret = getBpmnSequenceEdge().getParameterMappings().getKey(rowIndex);
			}
			else
			{
				ret = ((Tuple2<UnparsedExpression, UnparsedExpression>) getBpmnSequenceEdge().getParameterMappings().get(rowIndex)).getFirstEntity().getValue();
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
			switch (columnIndex)
			{
				case 0:
					Tuple2<UnparsedExpression, UnparsedExpression> rem = getBpmnSequenceEdge().getParameterMappings().remove(rowIndex);
					getBpmnSequenceEdge().getParameterMappings().add(rowIndex,
						createFreeName((String)value, new BasePropertyPanel.IndexMapContains(getBpmnSequenceEdge().getParameterMappings())),
						rem);
					break;
				case 1:
				default:
					String paramname = (String) getBpmnSequenceEdge().getParameterMappings().getKey(rowIndex);
					String paramtext = (String) value;
					UnparsedExpression exp = new UnparsedExpression(paramname, "", paramtext, null);
					
					getBpmnSequenceEdge().getParameterMappings().set(rowIndex, new Tuple2<UnparsedExpression, UnparsedExpression>(exp, null));
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
		
	}
}
