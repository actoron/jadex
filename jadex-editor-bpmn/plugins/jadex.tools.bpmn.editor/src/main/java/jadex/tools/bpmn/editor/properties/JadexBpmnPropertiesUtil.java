package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;
import jadex.tools.bpmn.editor.JadexBpmnPlugin;
import jadex.tools.model.common.properties.AbstractCommonPropertySection;
import jadex.tools.model.common.properties.ModifyEObjectCommand;
import jadex.tools.model.common.properties.table.MultiColumnTable;
import jadex.tools.model.common.properties.table.MultiColumnTable.MultiColumnTableRow;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;

public class JadexBpmnPropertiesUtil
{

	// ---- constants ----
	
	/** Key for the global package/import annotations of the BPMN diagram. */
	public static final String JADEX_GLOBAL_ANNOTATION = "jadex";
	
	/** Key for the common annotations of all shapes. NOT USED? */
	public static final String JADEX_COMMON_ANNOTATION = "common";
	
	/** Key for the annotation from the activity shape. */
	public static final String JADEX_ACTIVITY_ANNOTATION = "activity";
	
	/** Key for the annotation from the flow connector. */
	public static final String JADEX_SEQUENCE_ANNOTATION = "sequence";
	
	/** Key for the annotation from the sub process. */
	public static final String JADEX_SUBPROCESS_ANNOTATION = "subProcess";
	
	/** Key for the package of a BPMN diagram. */
	public static final String JADEX_PACKAGE_DETAIL = "package";
	
	/** Key for the imports of a BPMN diagram. */
	public static final String JADEX_IMPORT_LIST_DETAIL = "imports";
	
	/** Key for the arguments of a BPMN diagram. */
	public static final String JADEX_ARGUMENTS_LIST_DETAIL = "arguments";
	
	/** Key for the arguments of a BPMN diagram. */
	public static final String JADEX_RESULTS_LIST_DETAIL = "results";
	
	/** Key for the implementing class of a task. */
	public static final String JADEX_ACTIVITY_CLASS_DETAIL = "class";
	
	/** Key for the parameter map of a activity. */
	public static final String JADEX_PARAMETER_LIST_DETAIL = "parameters";
	
	/** Key for the properties map of a SubProcess. */
	public static final String JADEX_PROPERTIES_LIST_DETAIL = "properties";
	
	/** Key for the mapping map of a sequence edge. */
	public static final String JADEX_MAPPING_LIST_DETAIL = "mappings";
	
	/** Key for the condition of a sequence edge. */
	public static final String JADEX_CONDITION_DETAIL = "condition";
	
	/** Key for the implementing duration of a timer. */
	public static final String JADEX_EVENT_TIMER_DETAIL = "timer";
	
	/** Key for the implementing rule. */
	public static final String JADEX_EVENT_RULE_DETAIL = "rule";
	
	/** Key for the implementing message. */
	public static final String JADEX_EVENT_MESSAGE_DETAIL = "message";
	
	/** Key for the implementing error. */
	public static final String JADEX_EVENT_ERROR_DETAIL = "error";
	
	/** Key for the table unique column index. */
	public static final String JADEX_TABLE_KEY_EXTENSION = "table";
	
	/** Key for the table dimension. */
	public static final String JADEX_TABLE_DIMESION_DETAIL = "dimension";
	
	/** Key for the table unique column index. */
	public static final String JADEX_TABLE_UNIQUE_COLUMN_DETAIL = "uniqueColumnIndex";
	
	/** Delimiter for the table cell index and dimension */
	public static final String JADEX_TABLE_DIMENSION_DELIMITER = ":";
	
	
	/** Delimiter for combined keys (e.g. "annotationIdentifier + annotaionDetailIdentifier" for e.g. tables) */
	public static final String JADEX_COMBINED_KEY_DELIMITER = "_";
	
	
	
	
	/** The modelElement, may NOT be null. */
	protected AbstractCommonPropertySection section;
	
	/** The EAnnotations name that contains the detail */
	protected String containerEAnnotationName;
	
	/** The EAnnotations detail that contains the information */
	protected String annotationDetailName;

	/**
	 * @param containerEAnnotationName
	 * @param annotationDetailName
	 */
	protected JadexBpmnPropertiesUtil(String containerEAnnotationName,
			String annotationDetailName, AbstractCommonPropertySection section)
	{
		super();
		
		assert section != null : "PropertySection may NOT be null";
		assert containerEAnnotationName != null && !containerEAnnotationName.isEmpty() : this.getClass() + ": containerEAnnotationName not set";
		assert annotationDetailName != null && !annotationDetailName.isEmpty() : this.getClass() + ": annotationDetailName not set";
		
		this.section = section;
		this.containerEAnnotationName = containerEAnnotationName;
		this.annotationDetailName = annotationDetailName;
		
	}


	// ---- methods ----

	/**
	 * Update 
	 * @param detail
	 * @param value
	 */
	protected boolean updateJadexEAnnotation(final String detail, final String value)
	{
		EModelElement modelElement = section.getEModelElement();
		if(modelElement == null)
		{
			return false;
		}
		
		boolean success = JadexBpmnPropertiesUtil.updateJadexEAnnotationDetail(modelElement, containerEAnnotationName, detail, value);
		if (success)
		{
			section.refreshSelectedEditPart();
		}
		
		return success;
	}
	
	// ---- static methods ----
	
	/**
	 * Create the annotation identifier from util values
	 * 
	 * @return
	 */
	protected static String getTableAnnotationIdentifier(String annotationID, String detailID)
	{
		return annotationID
				+ JADEX_COMBINED_KEY_DELIMITER
				+ detailID
				+ JADEX_COMBINED_KEY_DELIMITER
				+ JADEX_TABLE_KEY_EXTENSION;
	}


	/**
	 * Update annotation detail
	 * @param element
	 * @param annotationIdentifier
	 * @param annotationDetail
	 * @param value
	 * @return
	 */
	public static boolean updateJadexEAnnotationDetail(final EModelElement element, final String annotationIdentifier, final String annotationDetail, final String value)
	{
		if(element == null)
		{
			return false;
		}

			// update or create the annotation / detail
			ModifyEObjectCommand command = new ModifyEObjectCommand(
					element, Messages.JadexCommonPropertySection_update_eannotation_command_name)
			{
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor arg0, IAdaptable arg1)
						throws ExecutionException
				{
					
					EAnnotation annotation = element.getEAnnotation(annotationIdentifier);
					if (annotation == null)
					{
						annotation = EcoreFactory.eINSTANCE.createEAnnotation();
						annotation.setSource(annotationIdentifier);
						annotation.setEModelElement(element);
						annotation.getDetails().put(annotationDetail, ""); //$NON-NLS-1$
					}
					
					if (value != null)
					{
						annotation.getDetails().put(annotationDetail, value);
					}
					else
					{
						annotation.getDetails().removeKey(annotationDetail);
						if (annotation.getDetails().isEmpty())
						{
							element.getEAnnotations().remove(annotation);
						}
					}
					
					return CommandResult.newOKCommandResult();
				}
			};

		
		// execute command
		try
		{
			IStatus status = command.execute(new NullProgressMonitor(), null);
			return status.isOK();
		}
		catch (ExecutionException exception)
		{
			JadexBpmnPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, JadexBpmnPlugin.PLUGIN_ID,
							IStatus.ERROR, exception.getMessage(),
							exception));
			
			return false;
		}
	}
	/**
	 * Get annotation detail
	 * @param element
	 * @param annotationIdentifier
	 * @param annotationDetail
	 * @return
	 */
	public static String getJadexEAnnotationDetail(final EModelElement element, final String annotationIdentifier, final String annotationDetail)
	{
		if(element == null)
		{
			return null;
		}
	
		EAnnotation annotation = element.getEAnnotation(annotationIdentifier);
		if (annotation != null)
		{
			Object detail = annotation.getDetails().get(annotationDetail);
			if (detail != null)
				return detail.toString();
			
			return "";
		}
	
		return null;
		
	}
	
	
	/**
	 * Update annotation detail
	 * @param element
	 * @param annotationIdentifier
	 * @param annotationDetail
	 * @param value
	 * @return
	 */
	public static boolean updateJadexEAnnotationTable(final EModelElement element, final String annotationIdentifier, final MultiColumnTable table)
	{
		if(element == null)
		{
			return false;
		}

			// update or create the annotation / detail
			ModifyEObjectCommand command = new ModifyEObjectCommand(
					element, Messages.JadexCommonPropertySection_update_eannotation_command_name)
			{
				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor arg0, IAdaptable arg1)
						throws ExecutionException
				{
					
					EAnnotation annotation = element.getEAnnotation(annotationIdentifier);
					if (annotation == null && table != null)
					{
						annotation = EcoreFactory.eINSTANCE.createEAnnotation();
						annotation.setSource(annotationIdentifier);
						annotation.setEModelElement(element);
					}
					
					if (table != null && !table.isEmpty())
					{
						String tableDimension =  (new TableCellIndex(table.size(), table.getRowSize())).toString();
						annotation.getDetails().put(JADEX_TABLE_DIMESION_DETAIL, tableDimension);
						annotation.getDetails().put(JADEX_TABLE_UNIQUE_COLUMN_DETAIL, String.valueOf(table.getUniqueColumn()));
						int rowIndex = 0;
						for (MultiColumnTableRow row : table.getRowList())
						{
							for (int columnIndex = 0; columnIndex < row.getColumnValues().length; columnIndex++)
							{
								annotation.getDetails().put(new TableCellIndex(rowIndex, columnIndex).toString(), row.getColumnValueAt(columnIndex));
							}
							rowIndex++;
						}
					}
					else
					{
						element.getEAnnotations().remove(annotation);
					}
					
					return CommandResult.newOKCommandResult();
				}
			};

		
		// execute command
		try
		{
			IStatus status = command.execute(new NullProgressMonitor(), null);
			return status.isOK();
		}
		catch (ExecutionException exception)
		{
			JadexBpmnPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, JadexBpmnPlugin.PLUGIN_ID,
							IStatus.ERROR, exception.getMessage(),
							exception));
			
			return false;
		}
	}
	/**
	 * Get annotation detail
	 * @param element
	 * @param annotationIdentifier
	 * @param annotationDetail
	 * @return
	 */
	public static MultiColumnTable getJadexEAnnotationTable(final EModelElement element, final String annotationIdentifier)
	{
		if(element == null)
		{
			return null;
		}
	
		EAnnotation annotation = element.getEAnnotation(annotationIdentifier);
		if (annotation != null)
		{
			String dimension = annotation.getDetails().get(JADEX_TABLE_DIMESION_DETAIL);
			int uniqueColumn = Integer.valueOf(annotation.getDetails().get(JADEX_TABLE_UNIQUE_COLUMN_DETAIL));
			if (dimension != null) 
			{
				TableCellIndex tableDimension = new TableCellIndex(dimension);
				MultiColumnTable newTable = new MultiColumnTable(tableDimension.getRowCount(), uniqueColumn);
				for (int rowIndex = 0; rowIndex < tableDimension.rowCount; rowIndex++)
				{
					String[] newRow = new String[tableDimension.columnCount];
					for (int columnIndex = 0; columnIndex < tableDimension.columnCount; columnIndex++)
					{
						newRow[columnIndex] = annotation.getDetails().get((new TableCellIndex(rowIndex, columnIndex)).toString());
					}
					newTable.add(newTable.new MultiColumnTableRow(newRow, uniqueColumn));
				}
				
				return newTable;
			}
			
			// fall through
			MultiColumnTable table = new MultiColumnTable(0,0);
			// set parameter
			return table;
		}
	
		return null;
		
	}
	
	/**
	 * Check if have to convert the annotation to new format
	 * 
	 * @return true if a conversion is done
	 */
	@SuppressWarnings("deprecation")
	public static boolean checkAnnotationConversion(EModelElement modelElement,
			String annotationId, String detailId, int uniqueColumnIndex)
	{
		EAnnotation ea = modelElement.getEAnnotation(annotationId);
		if (ea != null)
		{
			String value = (String) ea.getDetails().get(detailId);
			if (value != null)
			{
				// Old format, convert to new format and remove detail.
				// TableColumn[] columns = tableViewer.getTable().getColumns();
				MultiColumnTable table = MultiColumnTable
						.convertMultiColumnTableString(value, /* columns.length, */
						uniqueColumnIndex);
				// save the new annotation
				JadexBpmnPropertiesUtil.updateJadexEAnnotationTable(
						modelElement, getTableAnnotationIdentifier(annotationId, detailId), table);
				// remove the old detail, this removes annotation if details are
				// empty too
				JadexBpmnPropertiesUtil.updateJadexEAnnotationDetail(
						modelElement, annotationId,
						detailId, null);
				return true;
			}
		}
		return false;
	}
	
}

/**
 * A cell index data type
 * @TODO: replace with "toString" overridden existing java class * Point?
 * @author Claas
 */
class TableCellIndex
{
	/** row dimension */
	int rowCount;
	
	/** column dimension */
	int columnCount;
	
	/**
	 * @param rowCount
	 * @param columnCount
	 */
	protected TableCellIndex(int rowCount, int columnCount)
	{
		super();
		this.rowCount = rowCount;
		this.columnCount = columnCount;
	}

	/**
	 * @param dimension as string generated by toString() method
	 */
	protected TableCellIndex(String dimensionString)
	{
		String[] dimension = dimensionString.split(JadexBpmnPropertiesUtil.JADEX_TABLE_DIMENSION_DELIMITER);
		this.rowCount = Integer.valueOf(dimension[0]);
		this.columnCount = Integer.valueOf(dimension[1]);
	}
	
	// ---- methods ----
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return rowCount+JadexBpmnPropertiesUtil.JADEX_TABLE_DIMENSION_DELIMITER+columnCount;
	}

	/**
	 * @return the rowCount
	 */
	public int getRowCount()
	{
		return rowCount;
	}

	/**
	 * @param rowCount the rowCount to set
	 */
	public void setRowCount(int rowCount)
	{
		this.rowCount = rowCount;
	}

	/**
	 * @return the columnCount
	 */
	public int getColumnCount()
	{
		return columnCount;
	}

	/**
	 * @param columnCount the columnCount to set
	 */
	public void setColumnCount(int columnCount)
	{
		this.columnCount = columnCount;
	}

}
