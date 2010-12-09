package jadex.editor.bpmn.editor.properties.template;

import jadex.editor.model.common.properties.ModifyEObjectCommand;
import jadex.editor.model.common.properties.table.AbstractCommonTablePropertySection;
import jadex.editor.model.common.properties.table.MultiColumnTable;
import jadex.editor.model.common.properties.table.MultiColumnTable.MultiColumnTableRow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.IWorkbenchPart;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * @author Claas Altschaffel
 */
public abstract class AbstractBpmnMultiColumnTablePropertySection extends
		AbstractCommonTablePropertySection
{

	// ---- attributes ----

	/** Utility class to hold attribute an element reference */
	protected JadexBpmnPropertiesUtil util;

	/** The unique column index for this TablePropertySection */
	private int uniqueColumnIndex;

//	/** The table column unique values cache for model elements */
//	private Map<EModelElement, HashSet<String>> uniqueColumnValuesMap;

	// ---- constructor ----

	/**
	 * Protected constructor for subclasses
	 * 
	 * @param containerEAnnotationName
	 *            @see {@link AbstractBpmnPropertySection}
	 * @param annotationDetailName
	 *            @see {@link AbstractBpmnPropertySection}
	 * @param tableLabel
	 *            the name of table
	 * @param uniqueColumnIndex
	 *            the unique value column of the table
	 */
	protected AbstractBpmnMultiColumnTablePropertySection(
			String containerEAnnotationName, String annotationDetailName,
			String tableLabel, int uniqueColumnIndex)
	{
		super(tableLabel);

		assert containerEAnnotationName != null
				&& !containerEAnnotationName.isEmpty() : this.getClass()
				+ ": containerEAnnotationName not set";
		assert annotationDetailName != null && !annotationDetailName.isEmpty() : this
				.getClass()
				+ ": annotationDetailName not set";

		this.util = new JadexBpmnPropertiesUtil(containerEAnnotationName,
				annotationDetailName, this);

		//assert (uniqueColumnIndex != -1 && uniqueColumnIndex < columns.length);
		
//		this.uniqueColumnValuesMap = new HashMap<EModelElement, HashSet<String>>();

//		this.uniqueColumnIndex = uniqueColumnIndex;

	}

	// ---- abstract methods ----

	/** Retrieve the default values for a new row */
	protected abstract String[] getDefaultListElementAttributeValues();

	// ---- methods ----

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jadex.tools.model.common.properties.AbstractCommonPropertySection#dispose
	 * ()
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
//			getUniqueColumnValueCache(modelElement);
			return;
		}
	}

//	/**
//	 * Updates the uniqueColumnValueCache for modelElement
//	 * 
//	 * @param element
//	 * @return
//	 */
//	private HashSet<String> getUniqueColumnValueCache(EModelElement element)
//	{
//		if (uniqueColumnValuesMap.containsKey(modelElement))
//		{
//			return uniqueColumnValuesMap.get(modelElement);
//		}
//		else
//		{
//			HashSet<String> newSet = new HashSet<String>();
//			uniqueColumnValuesMap.put(modelElement, newSet);
//			return newSet;
//		}
//	}

	// ---- control creation methods ----

	@Override
	protected ModifyEObjectCommand getAddCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement,
				"Add EModelElement annotation element")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{

				MultiColumnTableRow newRow;
//				HashSet<String> uniqueValueCache = getUniqueColumnValueCache(modelElement);
//				synchronized (uniqueValueCache)
//				{
					MultiColumnTable table = getTableFromAnnotation();
					newRow = table.new MultiColumnTableRow(
							getDefaultListElementAttributeValues(),
							table);
//					String uniqueValue = createUniqueRowValue(newRow, table);
//					newRow.getColumnValues()[uniqueColumnIndex] = uniqueValue;
//					addUniqueRowValue(uniqueValue);
					table.add(newRow);
					updateTableAnnotation(table);
//				}

				return CommandResult.newOKCommandResult(newRow);
			}
		};
		return command;
	}

	@Override
	protected ModifyEObjectCommand getDeleteCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement,
				"Delete EModelElement annotation element")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
//				HashSet<String> uniqueValueCache = getUniqueColumnValueCache(modelElement);
//				synchronized (uniqueValueCache)
//				{
					MultiColumnTableRow rowToRemove = (MultiColumnTableRow) ((IStructuredSelection) tableViewer
							.getSelection()).getFirstElement();

					MultiColumnTable tableRowList = getTableFromAnnotation();
//					uniqueValueCache
//							.remove(rowToRemove.getColumnValues()[uniqueColumnIndex]);
					tableRowList.remove(rowToRemove);
					updateTableAnnotation(tableRowList);
//				}

				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}

	@Override
	protected ModifyEObjectCommand getUpCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement,
				"Move parameter element up")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{

//				HashSet<String> uniqueValueCache = getUniqueColumnValueCache(modelElement);
//				synchronized (uniqueValueCache)
//				{
					MultiColumnTableRow rowToMove = (MultiColumnTableRow) ((IStructuredSelection) tableViewer
							.getSelection()).getFirstElement();

					MultiColumnTable tableRowList = getTableFromAnnotation();
					int index = tableRowList.indexOf(rowToMove);

					if (0 < index && index < tableRowList.size())
					{
						MultiColumnTableRow tableRow = tableRowList.get(index);
						tableRowList.remove(index);
						tableRowList.add(index - 1, tableRow);
						updateTableAnnotation(tableRowList);
					}
//				}

				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}

	@Override
	protected ModifyEObjectCommand getDownCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement,
				"Move parameter element down")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
//				HashSet<String> uniqueValueCache = getUniqueColumnValueCache(modelElement);
//				synchronized (uniqueValueCache)
//				{
					MultiColumnTableRow rowToMove = (MultiColumnTableRow) ((IStructuredSelection) tableViewer
							.getSelection()).getFirstElement();

					MultiColumnTable tableRowList = getTableFromAnnotation();
					int index = tableRowList.indexOf(rowToMove);

					if (0 <= index && index < tableRowList.size() - 1)
					{
						MultiColumnTableRow tableRow = tableRowList.get(index);
						tableRowList.remove(index);
						tableRowList.add(index + 1, tableRow);
						updateTableAnnotation(tableRowList);
					}
//				}

				return CommandResult.newOKCommandResult(null);
			}
		};
		return command;
	}

	@Override
	protected ModifyEObjectCommand getClearCommand()
	{
		// modify the EModelElement annotation
		ModifyEObjectCommand command = new ModifyEObjectCommand(modelElement,
				"Clear parameter")
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor monitor, IAdaptable info)
					throws ExecutionException
			{
//				HashSet<String> uniqueValueCache = getUniqueColumnValueCache(modelElement);
//				synchronized (uniqueValueCache)
//				{
					updateTableAnnotation(null);
//					uniqueValueCache.clear();
//				}

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

			column.setEditingSupport(new BpmnMultiColumnTableEditingSupport(
					viewer, columnIndex));
			column.setLabelProvider(new MultiColumnTableLabelProvider(
					columnIndex));
		}

	}

//	private String createUniqueRowValue(MultiColumnTableRow row,
//			MultiColumnTable table)
//	{
//		assert (row != null && table != null);
//
//		HashSet<String> uniqueValueCache = getUniqueColumnValueCache(modelElement);
//		synchronized (uniqueValueCache)
//		{
//			String uniqueColumnValue = row.getColumnValues()[uniqueColumnIndex];
//
//			int counter = 1;
//			String uniqueValueToUse = uniqueColumnValue;
//			while (uniqueValueCache.contains(uniqueValueToUse))
//			{
//				uniqueValueToUse = uniqueColumnValue + counter;
//				counter++;
//			}
//
//			return uniqueValueToUse;
//		}
//
//	}

//	private boolean addUniqueRowValue(String uniqueValue)
//	{
//		HashSet<String> uniqueValueCache = getUniqueColumnValueCache(modelElement);
//		synchronized (uniqueValueCache)
//		{
//			return uniqueValueCache.add(uniqueValue);
//		}
//	}

//	private boolean removeUniqueRowValue(String uniqueValue)
//	{
//		HashSet<String> uniqueValueCache = getUniqueColumnValueCache(modelElement);
//		synchronized (uniqueValueCache)
//		{
//			return uniqueValueCache.remove(uniqueValue);
//		}
//	}

	/**
	 * Retrieve the EAnnotation from the modelElement and converts it to a
	 * {@link MultiColumnTableRow} list
	 * 
	 * @param act
	 * @return
	 */
	private MultiColumnTable getTableFromAnnotation()
	{
		checkAnnotationConversion();

		MultiColumnTable table = JadexBpmnPropertiesUtil
				.getJadexEAnnotationTable(modelElement,
						getTableAnnotationIdentifier());
		if (table != null)
		{
//			for (MultiColumnTableRow multiColumnTableRow : table.getRowList())
//			{
//				addUniqueRowValue(multiColumnTableRow
//						.getColumnValueAt(table.getUniqueColumn()));
//			}
			return table;
		}

		// fall through
		return new MultiColumnTable(0, uniqueColumnIndex);
	}

	/**
	 * Updates the EAnnotation for the modelElement with table data
	 * 
	 * @param table
	 */
	private void updateTableAnnotation(MultiColumnTable table)
	{
		JadexBpmnPropertiesUtil.updateJadexEAnnotationTable(modelElement,
				getTableAnnotationIdentifier(), table);
	}

//	/**
//	 * Check if have to convert the annotation to new format
//	 * 
//	 * @return true if a conversion is done
//	 */
//	private boolean checkAnnotationConversion()
//	{
//		EAnnotation ea = modelElement
//				.getEAnnotation(util.containerEAnnotationName);
//		if (ea != null)
//		{
//			String value = (String) ea.getDetails().get(
//					util.annotationDetailName);
//			if (value != null)
//			{
//				// This is the old format, so convert to new format an remove
//				// detail.
//				//TableColumn[] columns = tableViewer.getTable().getColumns();
//				MultiColumnTable table = MultiColumnTable
//						.convertMultiColumnTableString(value, /*columns.length,*/
//								uniqueColumnIndex);
//				// save the new annotation
//				JadexBpmnPropertiesUtil.updateJadexEAnnotationTable(
//						modelElement, getTableAnnotationIdentifier(), table);
//				// remove the old detail, this removes annotation if details are
//				// empty too
//				util.updateJadexEAnnotation(util.annotationDetailName, null);
//				return true;
//			}
//		}
//		return false;
//	}
	
	/**
	 * Check if have to convert the annotation to new format
	 * 
	 * @return true if a conversion is done
	 */
	private boolean checkAnnotationConversion()
	{
		return JadexBpmnPropertiesUtil.checkAnnotationConversion(modelElement,
				util.containerEAnnotationName, util.annotationDetailName, uniqueColumnIndex);
	}

	/**
	 * Create the annotation identifier from util values
	 * 
	 * @return
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
	protected class MultiColumnTableContentProvider implements
			IStructuredContentProvider
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
			
			if (inputElement instanceof EModelElement)
			{
				MultiColumnTable table = JadexBpmnPropertiesUtil.getJadexEAnnotationTable((EModelElement)inputElement, getTableAnnotationIdentifier());
				if (table != null)
					return table.toArray();
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

	protected class BpmnMultiColumnTableEditingSupport extends
			MultiColumnTableEditingSupport
	{

		public BpmnMultiColumnTableEditingSupport(TableViewer viewer,
				int attributeIndex)
		{
			super(viewer, attributeIndex);

		}

		public BpmnMultiColumnTableEditingSupport(TableViewer viewer,
				int attributeIndex, CellEditor editor)
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
					modelElement, "Update EModelElement parameter list")
			{
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor monitor, IAdaptable info)
						throws ExecutionException
				{

					MultiColumnTable modelTable = getTableFromAnnotation();
					MultiColumnTableRow rowToEdit = (MultiColumnTableRow) modelTable
							.get(modelTable.indexOf(tableViewerRow));

					if (attributeIndex == modelTable.getUniqueColumn())
					{
						if (!newValue
								.equals(rowToEdit.getColumnValues()[attributeIndex]))
						{
							int rowIndex = modelTable.indexOf(rowToEdit);
							modelTable.remove(rowIndex);
							rowToEdit.setColumnValueAt(attributeIndex, newValue);
							modelTable.add(rowIndex, rowToEdit);
							
							// update the model annotation
							updateTableAnnotation(modelTable);

							// update the corresponding table element
							tableViewerRow.getColumnValues()[attributeIndex] = rowToEdit.getColumnValueAt(attributeIndex);
							
							
//							HashSet<String> uniqueValueCache = getUniqueColumnValueCache(modelElement);
//							synchronized (uniqueValueCache)
//							{
//								removeUniqueRowValue(rowToEdit
//										.getColumnValues()[uniqueColumnIndex]);
//
//								rowToEdit.getColumnValues()[attributeIndex] = newValue;
//								String newUniqueValue = createUniqueRowValue(
//										rowToEdit, tableRowList);
//								rowToEdit.getColumnValues()[attributeIndex] = newUniqueValue;
//
//								addUniqueRowValue(newUniqueValue);
//
//								updateTableAnnotation(tableRowList);
//
//								// update the corresponding table element
//								tableViewerRow.getColumnValues()[attributeIndex] = newUniqueValue;
//							}

							return CommandResult.newOKCommandResult();
						}
					}

					rowToEdit.getColumnValues()[attributeIndex] = newValue;
					updateTableAnnotation(modelTable);

					// update the corresponding table element
					tableViewerRow.getColumnValues()[attributeIndex] = newValue;

					return CommandResult.newOKCommandResult();
				}
			};

			return command;
		}

	}

}
