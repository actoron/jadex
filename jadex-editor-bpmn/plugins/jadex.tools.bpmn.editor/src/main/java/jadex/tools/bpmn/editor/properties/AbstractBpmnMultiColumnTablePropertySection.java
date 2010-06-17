
package jadex.tools.bpmn.editor.properties;

import jadex.tools.model.common.properties.ModifyEObjectCommand;
import jadex.tools.model.common.properties.table.AbstractCommonTablePropertySection;
import jadex.tools.model.common.properties.table.MultiColumnTable;
import jadex.tools.model.common.properties.table.MultiColumnTable.MultiColumnTableRow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * @author Claas Altschaffel
 */
public abstract class AbstractBpmnMultiColumnTablePropertySection extends AbstractCommonTablePropertySection
{

	// ---- attributes ----
	
	/** Utility class to hold attribute an element reference */
	protected JadexBpmnPropertiesUtil util;
	
	private int uniqueColumnIndex;
	
	private Map<EModelElement, HashSet<String>> uniqueColumnValuesMap;

	// ---- constructor ----
	
	/**
	 * Protected constructor for subclasses
	 * @param containerEAnnotationName @see {@link AbstractBpmnPropertySection}
	 * @param annotationDetailName @see {@link AbstractBpmnPropertySection}
	 * @param tableLabel the name of table
	 * @param columns the column of table
	 * @param columnsWeight the weight of columns
	 * @param defaultListElementAttributeValues default columnValues for new elements, may be <code>null</code>
	 */
	protected AbstractBpmnMultiColumnTablePropertySection(String containerEAnnotationName, String annotationDetailName, String tableLabel, int uniqueColumnIndex)
	{
		super(tableLabel);
		
		assert containerEAnnotationName != null && !containerEAnnotationName.isEmpty() : this.getClass() + ": containerEAnnotationName not set";
		assert annotationDetailName != null && !annotationDetailName.isEmpty() : this.getClass() + ": annotationDetailName not set";
		
		this.util = new JadexBpmnPropertiesUtil(containerEAnnotationName, annotationDetailName, this);

		//assert (uniqueColumnIndex != -1 && uniqueColumnIndex < columns.length);
		this.uniqueColumnValuesMap = new HashMap<EModelElement, HashSet<String>>();
		
		this.uniqueColumnIndex = uniqueColumnIndex;

	}

	// ---- abstract methods ----
	
	/** Retrieve the default values for a new row */
	protected abstract String[] getDefaultListElementAttributeValues();
	

	// ---- methods ----

	/* (non-Javadoc)
	 * @see jadex.tools.model.common.properties.AbstractCommonPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		// nothing to dispose here, use addDisposable(Object) instead
		super.dispose();
	}
	
	/**
	 * Manages the input.
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);

		if (modelElement != null)
		{
			getUniqueColumnValueCash(modelElement);
			return;
		}
	}
	
	/**
	 * Updates the uniqueColumnValueCash for modelElement
	 * @param element
	 * @return
	 */
	private HashSet<String> getUniqueColumnValueCash(EModelElement element)
	{
		if (uniqueColumnValuesMap.containsKey(modelElement))
		{
			return uniqueColumnValuesMap.get(modelElement);
		}
		else
		{
			HashSet<String> newSet = new HashSet<String>();
			uniqueColumnValuesMap.put(modelElement, newSet);
			return newSet;
		}
	}

	// ---- control creation methods ----
	
	@Override
	protected ModifyEObjectCommand getAddCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				"Add EModelElement parameter element")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				
				MultiColumnTableRow newRow;
				HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
				synchronized (uniqueValueCash)
				{
					MultiColumnTable table = getTableRowList();
					newRow = table.new MultiColumnTableRow(
							getDefaultListElementAttributeValues(), uniqueColumnIndex);
					String uniqueValue = createUniqueRowValue(newRow, table);
					newRow.getColumnValues()[uniqueColumnIndex] = uniqueValue;
					addUniqueRowValue(uniqueValue);
					table.add(newRow);
					updateTableRowList(table);
				}

				return CommandResult.newOKCommandResult(newRow);
			}
		};
		return command;
	}

	@Override
	protected ModifyEObjectCommand getDeleteCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				"Delete EModelElement parameter element")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
				synchronized (uniqueValueCash)
				{
					MultiColumnTableRow rowToRemove = (MultiColumnTableRow) ((IStructuredSelection) tableViewer
							.getSelection()).getFirstElement();
					
					MultiColumnTable tableRowList = getTableRowList();
					//modelElementUniqueColumnValueCash.remove(rowToRemove.columnValues[uniqueColumnIndex]);
					uniqueValueCash.remove(rowToRemove.getColumnValues()[uniqueColumnIndex]);
					tableRowList.remove(rowToRemove);
					updateTableRowList(tableRowList);
				}

				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}

	@Override
	protected ModifyEObjectCommand getUpCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				"Move parameter element up")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				
				HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
				synchronized (uniqueValueCash)
				{
					MultiColumnTableRow rowToMove = (MultiColumnTableRow) ((IStructuredSelection) tableViewer
							.getSelection()).getFirstElement();
				
					MultiColumnTable tableRowList = getTableRowList();
					int index = tableRowList.indexOf(rowToMove);

					if (0 < index && index < tableRowList.size())
					{
						MultiColumnTableRow tableRow = tableRowList.get(index);
						tableRowList.remove(index);
						tableRowList.add(index-1, tableRow);
						updateTableRowList(tableRowList);
					}
				}

				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}

	
	@Override
	protected ModifyEObjectCommand getDownCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				"Move parameter element down")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
				synchronized (uniqueValueCash)
				{
					MultiColumnTableRow rowToMove = (MultiColumnTableRow) ((IStructuredSelection) tableViewer
							.getSelection()).getFirstElement();
					
					MultiColumnTable tableRowList = getTableRowList();
					int index = tableRowList.indexOf(rowToMove);

					if (0 <= index && index < tableRowList.size()-1)
					{
						MultiColumnTableRow tableRow = tableRowList.get(index);
						tableRowList.remove(index);
						tableRowList.add(index+1, tableRow);
						updateTableRowList(tableRowList);
					}
				}

				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}
	
	@Override
	protected ModifyEObjectCommand getClearCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(
				modelElement,
				"Clear parameter")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
				HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
				synchronized (uniqueValueCash)
				{
					updateTableRowList(null);
					uniqueValueCash.clear();
				}

				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}
	
	@Override
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
		for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex++)
		{
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.LEFT);
			column.getColumn().setText(columnNames[columnIndex]);

			column.setEditingSupport(new BpmnMultiColumnTableEditingSupport(viewer, columnIndex));
			column.setLabelProvider(new MultiColumnTableLabelProvider(columnIndex));
		}

	}
	
	private String createUniqueRowValue(MultiColumnTableRow row, MultiColumnTable table)
	{
		assert (row != null && table != null);
		
		HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
		synchronized (uniqueValueCash)
		{
			String uniqueColumnValue = row.getColumnValues()[uniqueColumnIndex];

			int counter = 1;
			String uniqueValueToUse = uniqueColumnValue;
			while (uniqueValueCash.contains(uniqueValueToUse))
			{
				uniqueValueToUse = uniqueColumnValue + counter;
				counter++;
			}

			return uniqueValueToUse;
		}

	}
	
	private boolean addUniqueRowValue(String uniqueValue)
	{
		HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
		synchronized (uniqueValueCash)
		{
			return uniqueValueCash.add(uniqueValue);
		}
	}
	
	private boolean removeUniqueRowValue(String uniqueValue)
	{
		HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
		synchronized (uniqueValueCash)
		{
			return uniqueValueCash.remove(uniqueValue);
		}
	}
	
	/**
	 * Retrieve the EAnnotation from the modelElement and converts it to a {@link MultiColumnTableRow} list
	 * @param act
	 * @return
	 */
	private MultiColumnTable getTableRowList()
	{
		EAnnotation ea = modelElement.getEAnnotation(util.containerEAnnotationName);
		if (ea != null)
		{
			String value = (String) ea.getDetails().get(util.annotationDetailName);
			if (value != null)
			{
				TableColumn[] columns = tableViewer.getTable().getColumns();
				MultiColumnTable table = MultiColumnTable.convertMultiColumnTableString(value, columns.length , uniqueColumnIndex);
				for (MultiColumnTableRow multiColumnTableRow : table.getRowList())
				{
					addUniqueRowValue(multiColumnTableRow.getColumnValueAt(uniqueColumnIndex));
				}
				return table;
			}
		}
		
		// fall through
		return new MultiColumnTable(0);
	}
	
	/**
	 * Updates the EAnnotation for the modelElement with table data
	 * @param table
	 */
	private void updateTableRowList(MultiColumnTable table)
	{
		util.updateJadexEAnnotation(util.annotationDetailName, MultiColumnTable.convertMultiColumnRowList(table));
	}
	
	// ---- internal used classes ----
	
	/**
	 * Simple content provider that reflects the table values  
	 * of the specified annotation detail given as value from containing class.
	 */
	protected class MultiColumnTableContentProvider implements IStructuredContentProvider {

		/**
		 * Generate the content for the table.
		 * 
		 * @return Object[] that contains MultiColumnTableRow objects.
		 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
		 */
		public Object[] getElements(Object inputElement)
		{
			if (inputElement instanceof EModelElement)
			{
				EAnnotation ea = ((EModelElement)inputElement).getEAnnotation(util.containerEAnnotationName);
				inputElement = ea;
			}
			
			if (inputElement instanceof EAnnotation)
			{
				String tableString = ((EAnnotation) inputElement).getDetails().get(util.annotationDetailName);
				if(tableString!=null)
				{
					TableColumn[] columns = tableViewer.getTable().getColumns();
					return MultiColumnTable.convertMultiColumnTableString(tableString, columns.length, uniqueColumnIndex).toArray();
					// add to unique map not need, its only a content provider
				}
					
			}
			
			// fall through
			return new Object[] {};
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
	
	protected class BpmnMultiColumnTableEditingSupport extends MultiColumnTableEditingSupport {
		
		public BpmnMultiColumnTableEditingSupport(TableViewer viewer, int attributeIndex)
		{
			super(viewer, attributeIndex);

		}
		
		public BpmnMultiColumnTableEditingSupport(TableViewer viewer, int attributeIndex, CellEditor editor)
		{
			super(viewer, attributeIndex, editor);
		}

		@Override
		protected ModifyEObjectCommand getSetValueCommand(
				final MultiColumnTableRow tableViewerRow, final Object value)
		{
			final String newValue = value.toString();
			
			// modify the Model
			ModifyEObjectCommand command = new ModifyEObjectCommand(
					modelElement,
					"Update EModelElement parameter list")
			{
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor monitor,
						IAdaptable info)
						throws ExecutionException
				{

					MultiColumnTable tableRowList = getTableRowList();
					MultiColumnTableRow rowToEdit = (MultiColumnTableRow) tableRowList.get(tableRowList.indexOf(tableViewerRow));

					if (attributeIndex == uniqueColumnIndex)
					{
						if (!newValue.equals(rowToEdit.getColumnValues()[attributeIndex]))
						{
							HashSet<String> uniqueValueCash = getUniqueColumnValueCash(modelElement);
							synchronized (uniqueValueCash)
							{
								removeUniqueRowValue(rowToEdit.getColumnValues()[uniqueColumnIndex]);
								
								rowToEdit.getColumnValues()[attributeIndex] = newValue;
								String newUniqueValue = createUniqueRowValue(rowToEdit, tableRowList);
								rowToEdit.getColumnValues()[attributeIndex] = newUniqueValue;
								
								addUniqueRowValue(newUniqueValue);
								
								updateTableRowList(tableRowList);

								// update the corresponding table element
								tableViewerRow.getColumnValues()[attributeIndex] = newUniqueValue;
							}

							return CommandResult.newOKCommandResult();
						}
					}
					
					rowToEdit.getColumnValues()[attributeIndex] = newValue;
					updateTableRowList(tableRowList);
					
					// update the corresponding table element
					tableViewerRow.getColumnValues()[attributeIndex] = newValue;
										
					return CommandResult.newOKCommandResult();
				}
			};
			
			return command;
		}
		
	}

}


