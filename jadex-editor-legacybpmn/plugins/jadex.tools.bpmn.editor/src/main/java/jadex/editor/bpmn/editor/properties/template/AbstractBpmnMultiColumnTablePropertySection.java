package jadex.editor.bpmn.editor.properties.template;

import jadex.editor.bpmn.editor.JadexBpmnEditor;
import jadex.editor.bpmn.editor.properties.JadexBpmnDiagramConfigurationsTableSection;
import jadex.editor.bpmn.model.MultiColumnTableEx;
import jadex.editor.common.model.properties.ModifyEObjectCommand;
import jadex.editor.common.model.properties.table.AbstractCommonTablePropertySection;
import jadex.editor.common.model.properties.table.MultiColumnTable;
import jadex.editor.common.model.properties.table.MultiColumnTable.MultiColumnTableRow;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Property section Tab to enable Jadex specific properties
 */
public abstract class AbstractBpmnMultiColumnTablePropertySection extends AbstractCommonTablePropertySection
{
	// ---- constants ----
	
	public static final String TEXT = "text";
	public static final String CHECKBOX = "checkbox";
	public static final String COMBOBOX = "combobox";
	public static final String SPINNER = "spinner";
	
	// ---- attributes ----

	/** Utility class to hold attribute an element reference */
	protected JadexBpmnPropertiesUtil util;

	/** The unique column index for this TablePropertySection */
	private int uniqueColumnIndex;
	
	/** A boolean array to mark a column as reference column */
	private boolean[] complexColumnMarker;
	
	/** The column names. */
	private String[] colnames;

	// ---- constructor ----

	/**
	 * Protected constructor for subclasses
	 * 
	 * @param containerEAnnotationName
	 *   @see {@link AbstractBpmnPropertySection}
	 * @param annotationDetailName
	 *   @see {@link AbstractBpmnPropertySection}
	 * @param tableLabel the name of table
	 * @param uniqueColumnIndex the unique value column of the table
	 */
	protected AbstractBpmnMultiColumnTablePropertySection(
		String containerEAnnotationName, String annotationDetailName,
		String tableLabel, int uniqueColumnIndex, boolean[] complexColumnMarker)
	{
		super(tableLabel);

		assert containerEAnnotationName != null && !containerEAnnotationName.isEmpty() 
			: this.getClass()+ ": containerEAnnotationName not set";
		assert annotationDetailName != null && !annotationDetailName.isEmpty() : this.getClass()+ ": annotationDetailName not set";
		
		this.util = new JadexBpmnPropertiesUtil(containerEAnnotationName, annotationDetailName, this);
		this.uniqueColumnIndex = uniqueColumnIndex;
		this.complexColumnMarker = complexColumnMarker;
		
		if(this.complexColumnMarker == null)
		{
			this.complexColumnMarker = new boolean[getDefaultListElementAttributeValues().length];
		}
	}

	// ---- abstract methods ----

	/** Retrieve the default values for a new row */
	protected abstract String[] getDefaultListElementAttributeValues();
	
	// ---- methods ----

	/**
	 * This method should contain all "update" code for controls
	 * currently used in setInput(). This method is called at the
	 * end of setInput().
	 * @see jadex.editor.common.model.properties.AbstractCommonPropertySection#updateSectionValues()
	 */
	protected void updateSectionValues()
	{
		// empty default method
	}

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#refresh()
	 */
	public void refresh()
	{
		super.refresh();
		
		for(int i=0; i<complexColumnMarker.length; i++)
		{
			if(complexColumnMarker[i])
			{
				TableColumn col = tableViewer.getTable().getColumn(i);
				String config = getConfigurationName();
				String name = colnames[i];
				if(config!=null && config.length()>0)
				{
					name += " [" + config + "]";
				}
				else
				{
					name += " [select config]";
				}
				col.setText(name);
			}
		}
	}
	
	/**
	 *  Get the current configuration.
	 */
	protected String getConfiguration()
	{
		return JadexBpmnPropertiesUtil.getJadexEAnnotationDetail(modelElement, JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, 
			JadexBpmnPropertiesUtil.JADEX_ACTIVE_CONFIGURATION_DETAIL);
	}
	
	/**
	 *  Get the current configuration.
	 */
	protected String getConfigurationName()
	{
		String ret = getConfiguration();
		if(ret!=null && ret.indexOf(":")!=-1)
			ret = ret.substring(ret.indexOf(":")+1);
		return ret;
	}
	
	/**
	 *  Get the current configuration.
	 */
	protected String getConfigurationId()
	{
		String ret = getConfiguration();
		if(ret!=null && ret.indexOf(":")!=-1)
			ret = ret.substring(0, ret.indexOf(":"));
		return ret;
	}
	
	/**
	 * 
	 */
	private String[] extendDefaultListElementAttributeValues(MultiColumnTableEx table)
	{
		String[] values = getDefaultListElementAttributeValues();
		for(int i = 0; i < values.length; i++)
		{
			if(table.isComplexColumn(i))
			{
				values[i] = JadexBpmnPropertiesUtil.getComplexValueAnnotationIdentifier(
					util.containerEAnnotationName, util.annotationDetailName, colnames[i]);
			}
		}
		return values;
	}
	
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * jadex.tools.model.common.properties.AbstractCommonPropertySection#dispose
//	 * ()
//	 */
//	public void dispose()
//	{
//		// nothing to dispose here, use addDisposable(Object) instead
//		super.dispose();
//	}

//	/**
//	 * Manages the input.
//	 */
//	public void setInput(IWorkbenchPart part, ISelection selection)
//	{
//		super.setInput(part, selection);
//	}

	// ---- control creation methods ----

	protected ModifyEObjectCommand getAddCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement,
			"Add EModelElement annotation element")
		{
			protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
			{
				MultiColumnTableRow newRow;
				MultiColumnTableEx table = getTableFromAnnotation();
				newRow = table.new MultiColumnTableRow(extendDefaultListElementAttributeValues(table), table);
				table.add(newRow);
				updateTableAnnotation(table);

				return CommandResult.newOKCommandResult(newRow);
			}
		};
		return command;
	}

	protected ModifyEObjectCommand getDeleteCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement,
				"Delete EModelElement annotation element")
		{
			protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
			{
				MultiColumnTableRow rowToRemove = (MultiColumnTableRow) ((IStructuredSelection)tableViewer
					.getSelection()).getFirstElement();
				MultiColumnTableEx table = getTableFromAnnotation();
				table.remove(rowToRemove);
				updateTableAnnotation(table);
				
				// remove complex values as well
				String[] columnValues = rowToRemove.getColumnValues();
				for (int columnIndex = 0; columnIndex < columnValues.length; columnIndex++)
				{
					if (table.isComplexColumn(columnIndex))
					{
						boolean complexAnnotationRemoved = JadexBpmnPropertiesUtil.removeJadexEAnnotation(modelElement, columnValues[columnIndex]);
						if (!complexAnnotationRemoved)
						{
							JadexBpmnEditor.log("Complex table value annotation not removed!",
								null, IStatus.INFO);
						}
					}
				}
				
				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}

	protected ModifyEObjectCommand getUpCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement, "Move parameter element up")
		{
			protected CommandResult doExecuteWithResult(
				IProgressMonitor monitor, IAdaptable info) throws ExecutionException
			{
				MultiColumnTableRow rowToMove = (MultiColumnTableRow) ((IStructuredSelection)tableViewer
					.getSelection()).getFirstElement();

				MultiColumnTableEx tableRowList = getTableFromAnnotation();
				int index = tableRowList.indexOf(rowToMove);

				if (0 < index && index < tableRowList.size())
				{
					MultiColumnTableRow tableRow = tableRowList.get(index);
					tableRowList.remove(index);
					tableRowList.add(index - 1, tableRow);
					updateTableAnnotation(tableRowList);
				}

				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}

	protected ModifyEObjectCommand getDownCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement, "Move parameter element down")
		{
			protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
			{
				MultiColumnTableRow rowToMove = (MultiColumnTableRow) ((IStructuredSelection)tableViewer
					.getSelection()).getFirstElement();

				MultiColumnTableEx tableRowList = getTableFromAnnotation();
				int index = tableRowList.indexOf(rowToMove);

				if (0 <= index && index < tableRowList.size() - 1)
				{
					MultiColumnTableRow tableRow = tableRowList.get(index);
					tableRowList.remove(index);
					tableRowList.add(index + 1, tableRow);
					updateTableAnnotation(tableRowList);
				}

				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}

	protected ModifyEObjectCommand getClearCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement,
				"Clear parameter")
		{
			protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info)
				throws ExecutionException
			{
				updateTableAnnotation(null);
				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}

	protected IStructuredContentProvider getTableContentProvider()
	{
		MultiColumnTableContentProvider contentProvider = new MultiColumnTableContentProvider();
		addDisposable(contentProvider);
		return contentProvider;
	}

	// ---- converter and help methods ----

	/**
	 * Helper method to ease creation of simple string valued tables
	 */
	protected void createColumns(TableViewer viewer, String[] columnNames)
	{
		createColumns(viewer, columnNames, null, null);
	}
	
	/**
	 * Helper method to ease creation of simple string valued tables
	 */
	protected void createColumns(TableViewer viewer, String[] columnNames, String[] columntypes, Map<String, String[]> values)
	{
		this.colnames = columnNames;
		TableViewerColumn[] ret = new TableViewerColumn[columnNames.length];
		for(int i = 0; i<columnNames.length; i++)
		{
			if(columntypes==null || columntypes[i].equals(TEXT))
			{
				ret[i] = createTextColumn(viewer, columnNames[i], i);
			}
			else if(columntypes[i].equals(CHECKBOX))
			{
				ret[i] = createCheckboxColumn(viewer, columnNames[i], i);
			}
			else if(columntypes[i].equals(COMBOBOX))
			{
				ret[i] = createComboboxColumn(viewer, columnNames[i], i, values==null? null: (String[])values.get(columnNames[i]));
			}
			else if(columntypes[i].equals(SPINNER))
			{
				ret[i] = createSpinnerColumn(viewer, columnNames[i], i);
			}
		}
	}

	/**
	 *  Create a text column.
	 */
	protected TableViewerColumn createTextColumn(TableViewer viewer, String name, int idx)
	{
		TableViewerColumn ret = new TableViewerColumn(viewer, SWT.LEFT);
		ret.getColumn().setText(name);
		ret.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, idx));
		ret.setLabelProvider(new BpmnMultiColumnTableLabelProvider(idx));
		return ret;
	}
	
	/**
	 *  Create a checkbox column.
	 */
	public TableViewerColumn createCheckboxColumn(TableViewer viewer, String name, final int idx)
	{
		TableViewerColumn ret = new TableViewerColumn(viewer, SWT.LEFT);
		ret.getColumn().setText(name);
		
		ret.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, idx, 
			new CheckboxCellEditor(((TableViewer)viewer).getTable(), SWT.ARROW))
		{
			protected Object getValue(Object element)
			{
				return Boolean.valueOf(((MultiColumnTableRow)element).getColumnValueAt(idx));
			}
	
			protected void doSetValue(Object element, Object value)
			{
				super.doSetValue(element, ((Boolean)value).toString());
			}
		});
		
		ret.setLabelProvider(new BpmnMultiColumnTableLabelProvider(idx)
		{
			public Image getColumnImage(Object element, int columnIndex)
			{
				if(Boolean.valueOf(((MultiColumnTableRow)element).getColumnValueAt(idx)))
				{
					return getCheckboxImageProvider().getCheckboxImage(true, true);
				}
				return getCheckboxImageProvider().getCheckboxImage(false, true);
			}
	
			public String getColumnText(Object element, int columnIndex)
			{
				return null;
			}
		});
		
		return ret;
	}
	
	/**
	 *  Create a checkbox column.
	 */
	public TableViewerColumn createComboboxColumn(TableViewer viewer, String name, final int idx, final String[] values)
	{
		TableViewerColumn ret = new TableViewerColumn(viewer, SWT.LEFT);
		ret.getColumn().setText(name);
		
		ComboBoxCellEditor editor = new ComboBoxCellEditor(((TableViewer)viewer).getTable(), values, SWT.READ_ONLY);
		ret.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, idx, editor)
		{
			protected Object getValue(Object element)
			{
				for(int i=0; i<values.length; i++)
				{
					if(values[i].equals(((MultiColumnTableRow)element).getColumnValueAt(i)))
					{
						return Integer.valueOf(i);
					}
				}
				// fall through
				return Integer.valueOf(0);
			}
	
			protected void doSetValue(Object element, Object value)
			{
				super.doSetValue(element, values[((Integer)value).intValue()]);
			}
		});
		ret.setLabelProvider(new BpmnMultiColumnTableLabelProvider(idx));
		
		return ret;
	}
	
	/**
	 *  Create a spinner column.
	 */
	public TableViewerColumn createSpinnerColumn(TableViewer viewer, String name, final int idx)
	{
		TableViewerColumn ret = new TableViewerColumn(viewer, SWT.LEFT);
		ret.getColumn().setText(name);
		
		SpinnerCellEditor editor = new SpinnerCellEditor(((TableViewer)viewer).getTable());
		ret.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, idx, editor)
		{
			protected Object getValue(Object element)
			{
				Object value = ((MultiColumnTableRow)element).getColumnValueAt(idx);
				Integer val = Integer.valueOf(0);
				if(value instanceof String && ((String)value).length()>0)
				{
					try
					{
						val = Integer.valueOf((String)value);
					}
					catch(Exception e)
					{
					}
				}
				return val;
			}
	
			protected void doSetValue(Object element, Object value)
			{
//				System.out.println("value: "+value);
				super.doSetValue(element, value);
			}
		});
		ret.setLabelProvider(new BpmnMultiColumnTableLabelProvider(idx));
		
		return ret;
	}

	/**
	 * Retrieve the EAnnotation from the modelElement and converts it to a
	 * {@link MultiColumnTableRow} list
	 */
	private MultiColumnTableEx getTableFromAnnotation()
	{
		checkAnnotationConversion();

		MultiColumnTableEx table = JadexBpmnPropertiesUtil
			.getJadexEAnnotationTable(modelElement, getTableAnnotationIdentifier());
		if(table != null)
		{
			return table;
		}

		// fall through
		return new MultiColumnTableEx(0, uniqueColumnIndex, complexColumnMarker);
	}

	/**
	 * Updates the EAnnotation for the modelElement with table data
	 */
	private void updateTableAnnotation(MultiColumnTableEx table)
	{
		JadexBpmnPropertiesUtil.updateJadexEAnnotationTable(modelElement,
			getTableAnnotationIdentifier(), table);
	}

	/**
	 * Check if have to convert the annotation to new format
	 * @return true if a conversion is done
	 */
	private boolean checkAnnotationConversion()
	{
		return JadexBpmnPropertiesUtil.checkAnnotationConversion(modelElement,
			util.containerEAnnotationName, util.annotationDetailName, uniqueColumnIndex);
	}

	/**
	 * Create the annotation identifier from util values
	 */
	private String getTableAnnotationIdentifier()
	{
		return JadexBpmnPropertiesUtil.getTableAnnotationIdentifier(util.containerEAnnotationName,
			util.annotationDetailName);
	}
	
	/**
	 * Simple content provider that reflects the table values of the specified
	 * annotation detail given as value from containing class.
	 */
	protected class MultiColumnTableContentProvider implements IStructuredContentProvider
	{
		/**
		 * Generate the content for the table.
		 * 
		 * @return Object[] that contains MultiColumnTableRow objects.
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			checkAnnotationConversion();
			
			if(inputElement instanceof EModelElement)
			{
				MultiColumnTable table = JadexBpmnPropertiesUtil.getJadexEAnnotationTable(
					(EModelElement)inputElement, getTableAnnotationIdentifier());
				if(table != null)
					return table.toArray();
			}

			// fall through
			return new Object[]{};
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */
		public void dispose()
		{
			// nothing to dispose.
		}

		/**
		 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
		 *      java.lang.Object, java.lang.Object)
		 */
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
			// no actions taken.
		}
	}

	protected class BpmnMultiColumnTableLabelProvider extends MultiColumnTableLabelProvider
	{
		/**
		 * empty constructor, sets column index to -1
		 */
		public BpmnMultiColumnTableLabelProvider()
		{
		}
		
		/**
		 * @param columIndex the column index to provide the label for
		 */
		public BpmnMultiColumnTableLabelProvider(int columIndex)
		{
			super(columIndex);
		}
		
		/**
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex)
		{
			String ret = super.getColumnText(element, columnIndex);
			MultiColumnTableEx table = getTableFromAnnotation();
			if(table.isComplexColumn(columnIndex))
			{
				ret = (String)table.getComplexValue(ret).get(getConfigurationId());
			}
			return ret;
		}
	}
	
	protected class BpmnMultiColumnTableEditingSupport extends MultiColumnTableEditingSupport
	{

		public BpmnMultiColumnTableEditingSupport(TableViewer viewer, int attributeIndex)
		{
			super(viewer, attributeIndex);
		}

		public BpmnMultiColumnTableEditingSupport(TableViewer viewer,
				int attributeIndex, CellEditor editor)
		{
			super(viewer, attributeIndex, editor);
		}
		
		/**
		 * Can edit all columns.
		 */
		public boolean canEdit(Object element)
		{
			boolean ret = super.canEdit(element);
			if(ret)
			{
				if(getConfiguration()==null || getConfiguration().length()==0)
				{
					MultiColumnTableRow row = (MultiColumnTableRow)element;
					Object o = row.getTable();
					if(o instanceof MultiColumnTableEx)
					{
						MultiColumnTableEx table = (MultiColumnTableEx)o;
						if(table.isComplexColumn(attributeIndex))
						{
							ret = false;
						}
					}
				}
			}
			return ret;
		}
		
		/**
		 * Respect the complex value types in BPMN tables
		 * @see jadex.editor.common.model.properties.table.AbstractCommonTablePropertySection.MultiColumnTableEditingSupport#getValue(java.lang.Object)
		 */
		protected Object getValue(Object element)
		{
			MultiColumnTableRow row = (MultiColumnTableRow)element;
			Object o = row.getTable();
			if(o instanceof MultiColumnTableEx)
			{
				MultiColumnTableEx table = (MultiColumnTableEx) o;
				if(table.isComplexColumn(attributeIndex))
				{
					// the value should be a redirection
					String complexValueIdentifier = row.getColumnValueAt(attributeIndex);
					Map<String, String> vals = table.getComplexValue(complexValueIdentifier);
//					String selectedConfiguration = JadexBpmnDiagramConfigurationsTableSection.getConfigurationSectionInstanceForModelElement(modelElement).getCurrentConfiguration();
					String selconf = getConfigurationId();
					String value = vals.get(selconf);
//					System.out.println("selconf: "+selconf+" "+value+" "+vals);
					return value != null ? value : "";
				}
			}
				
			return super.getValue(element);
		}

		protected ModifyEObjectCommand getSetValueCommand(final MultiColumnTableRow tableViewerRow, final Object value)
		{
			final String newValue = value.toString();

			// modify the Model
			ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement, "Update EModelElement parameter list")
			{
				protected CommandResult doExecuteWithResult(IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
				{
					// Always use the fresh row from editor. Otherwise complex values will be lost.
					MultiColumnTableEx table = getTableFromAnnotation();
					MultiColumnTableRow rowToEdit = tableViewerRow;
					table.set(table.indexOf(tableViewerRow), tableViewerRow);
//					MultiColumnTableRow rowToEdit = (MultiColumnTableRow)table.get(table.indexOf(tableViewerRow));

					if(attributeIndex == table.getUniqueColumn())
					{
						if(!newValue.equals(rowToEdit.getColumnValues()[attributeIndex]))
						{
							int rowIndex = table.indexOf(rowToEdit);
							table.remove(rowIndex);
							rowToEdit.setColumnValueAt(attributeIndex, newValue);
							table.add(rowIndex, rowToEdit);
						}
					} 
					else if(table.isComplexColumn(attributeIndex))
					{
						// the value should be a redirection
						String id = rowToEdit.getColumnValueAt(attributeIndex);
						Map<String, String> map = table.getComplexValue(id);
						//String selectedConfiguration = JadexBpmnDiagramConfigurationsTableSection.getConfigurationSectionInstanceForModelElement(modelElement).getCurrentConfiguration();
						String selconf = getConfigurationId();
						map.put(selconf, newValue);
//						System.out.println("setting: "+selconf+" "+newValue+" "+map);
					}
					else
					{
						rowToEdit.setColumnValueAt(attributeIndex, newValue);
//						rowToEdit.getColumnValues()[attributeIndex] = newValue;
					}
					
					// update the model annotation
					updateTableAnnotation(table);

					// update the corresponding table element
					tableViewerRow.getColumnValues()[attributeIndex] = rowToEdit.getColumnValueAt(attributeIndex);

					return CommandResult.newOKCommandResult();
				}
			};

			return command;
		}
	}
}
