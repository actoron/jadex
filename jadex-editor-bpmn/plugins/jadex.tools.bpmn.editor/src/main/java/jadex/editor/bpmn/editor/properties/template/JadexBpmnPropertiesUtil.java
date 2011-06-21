package jadex.editor.bpmn.editor.properties.template;

import jadex.editor.bpmn.diagram.Messages;
import jadex.editor.bpmn.editor.JadexBpmnEditor;
import jadex.editor.bpmn.editor.JadexBpmnEditorActivator;
import jadex.editor.bpmn.editor.properties.JadexBpmnDiagramImportsSection;
import jadex.editor.bpmn.editor.properties.JadexBpmnDiagramParameterSection;
import jadex.editor.bpmn.editor.properties.JadexBpmnDiagramPropertiesTableSection;
import jadex.editor.bpmn.editor.properties.JadexCommonParameterSection;
import jadex.editor.bpmn.editor.properties.JadexIntermediateEventsParameterSection;
import jadex.editor.bpmn.editor.properties.JadexSequenceMappingSection;
import jadex.editor.bpmn.model.MultiColumnTableEx;
import jadex.editor.common.model.properties.AbstractCommonPropertySection;
import jadex.editor.common.model.properties.ModifyEObjectCommand;
import jadex.editor.common.model.properties.table.MultiColumnTable;
import jadex.editor.common.model.properties.table.MultiColumnTable.MultiColumnTableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.stp.bpmn.BpmnDiagram;
import org.eclipse.stp.bpmn.diagram.edit.parts.BpmnDiagramEditPart;

public class JadexBpmnPropertiesUtil
{

	// ---- constants ----
	
	private static List<TableAnnotationIdentifier> toConvert;

	static {
		toConvert = new ArrayList<TableAnnotationIdentifier>();
		
		// ---- parameters ----
		toConvert
				.add(new TableAnnotationIdentifier(
						JadexCommonParameterSection.PARAMETER_ANNOTATION_IDENTIFIER,
						JadexCommonParameterSection.PARAMETER_ANNOTATION_DETAIL_IDENTIFIER,
						AbstractParameterTablePropertySection.UNIQUE_PARAMETER_ROW_ATTRIBUTE));
		
		// ---- diagram imports ----
		toConvert.add(new TableAnnotationIdentifier(
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_IMPORT_LIST_DETAIL,
						JadexBpmnDiagramImportsSection.UNIQUE_COLUMN_INDEX));
		
		// ---- diagram arguments ----
		toConvert
				.add(new TableAnnotationIdentifier(
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_ARGUMENTS_LIST_DETAIL,
						JadexBpmnDiagramParameterSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		
		// ---- sub process ---
		toConvert 
				.add(new TableAnnotationIdentifier(
						// old diagrams uses "subProcess" as source
						JadexBpmnPropertiesUtil.JADEX_SUBPROCESS_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_PROPERTIES_LIST_DETAIL,
						JadexBpmnDiagramPropertiesTableSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		toConvert
				.add(new TableAnnotationIdentifier(
						// use "jadex" in newer and converted diagrams
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_PROPERTIES_LIST_DETAIL,
						JadexBpmnDiagramPropertiesTableSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		
		// ---- activity ----
		toConvert
				.add(new TableAnnotationIdentifier(
						// old diagrams uses "activity" as source
						JadexBpmnPropertiesUtil.JADEX_ACTIVITY_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_PARAMETER_LIST_DETAIL,
						JadexIntermediateEventsParameterSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		toConvert
				.add(new TableAnnotationIdentifier(
						// use "jadex" in newer and converted diagrams
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_PARAMETER_LIST_DETAIL,
						JadexIntermediateEventsParameterSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		
		
		// ---- sequence edges ----
		toConvert
				.add(new TableAnnotationIdentifier(
						// old diagrams uses "sequence" as source
						JadexBpmnPropertiesUtil.JADEX_SEQUENCE_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_MAPPING_LIST_DETAIL,
						JadexSequenceMappingSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
		toConvert
				.add(new TableAnnotationIdentifier(
						// use "jadex" in newer and converted diagrams
						JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
						JadexBpmnPropertiesUtil.JADEX_MAPPING_LIST_DETAIL,
						JadexSequenceMappingSection.UNIQUE_LIST_ELEMENT_ATTRIBUTE_INDEX));
	}
	
	
	/** Key for the global package/import annotations of the BPMN diagram. */
	public static final String JADEX_GLOBAL_ANNOTATION = "jadex";
	
	/** Key for the package of a BPMN diagram. */
	public static final String JADEX_PROPERTIES_VERSION_DETAIL = "editor_version";
	
	/** Key for the common annotations of all shapes. NOT USED? */
	@Deprecated
	public static final String JADEX_COMMON_ANNOTATION = "common";
	
	/** Key for the annotation from the activity shape. */
	@Deprecated
	public static final String JADEX_ACTIVITY_ANNOTATION = "activity";
	
	/** Key for the annotation from the flow connector. */
	@Deprecated
	public static final String JADEX_SEQUENCE_ANNOTATION = "sequence";
	
	/** Key for the annotation from the sub process. */
	@Deprecated
	public static final String JADEX_SUBPROCESS_ANNOTATION = "subProcess";
	
	/** Key for the package of a BPMN diagram. */
	public static final String JADEX_PACKAGE_DETAIL = "package";
	
	/** Key for the imports of a BPMN diagram. */
	public static final String JADEX_IMPORT_LIST_DETAIL = "imports";
	
	/** Key for the configurations map of a BPMN diagram. */
	public static final String JADEX_CONFIGURATIONS_LIST_DETAIL = "configurations";
	
	/** Key for the configurations map of a BPMN diagram. */
	public static final String JADEX_ACTIVE_CONFIGURATION_DETAIL = "configuration";
	
	/** Key for the arguments of a BPMN diagram. */
	public static final String JADEX_ARGUMENTS_LIST_DETAIL = "arguments";
	
	/** Key for the arguments of a BPMN diagram. */
	public static final String JADEX_RESULTS_LIST_DETAIL = "results";
	
	/** Key for the provided services map of a BPMN diagram. */
	public static final String JADEX_PROVIDEDSERVICES_LIST_DETAIL = "providedservices";

	/** Key for the required services map of a BPMN diagram. */
	public static final String JADEX_REQUIREDSERVICES_LIST_DETAIL = "requiredservices";

	/** Key for the bindings map of a BPMN diagram. */
	public static final String JADEX_BINDINGS_LIST_DETAIL = "bindings";
	
	/** Key for the bindings map of a BPMN diagram. */
	public static final String JADEX_SUBCOMPONENTS_LIST_DETAIL = "subcomponents";
	
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
	
	/** Key for the table unique column index. */
	public static final String JADEX_TABLE_COMPLEX_COLUMNS_DETAIL = "complexColumns";
	
	/** Delimiter for the table cell index and dimension */
	public static final String JADEX_TABLE_DIMENSION_DELIMITER = ":";
	
	
	/** Delimiter for combined keys (e.g. "annotationIdentifier + annotaionDetailIdentifier" for e.g. tables) */
	public static final String JADEX_COMBINED_KEY_DELIMITER = "_";

	/** Collection of reserved annotation / detail identifier that must not be changed */
	private static final List<String> RESERVED_BPMN_ANNOTATIONS = new ArrayList<String>();
	
	static {
		RESERVED_BPMN_ANNOTATIONS.add("isThrowing");
	}
	
	
	/** The modelElement, may NOT be null. */
	protected AbstractCommonPropertySection section;
	
	/** The EAnnotations name that contains the detail */
	protected String containerEAnnotationName;
	
	/** The EAnnotations detail that contains the information */
	protected String annotationDetailName;

	/**
	 * 
	 */
	public JadexBpmnPropertiesUtil(String containerEAnnotationName,
		String annotationDetailName, AbstractCommonPropertySection section)
	{
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
	 */
	protected boolean updateJadexEAnnotation(final String detail, final String value)
	{
		EModelElement modelElement = getModelElement();
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
	
	public EAnnotation getJadexEAnnotation()
	{
		return getJadexEAnnotation(getModelElement(), containerEAnnotationName, true);
	}
	

	public String getJadexEAnnotationDetail(String detailID)
	{
		return getJadexEAnnotationDetail(getModelElement(), containerEAnnotationName, detailID);
	}
	
	public String getJadexEAnnotationDetail()
	{
		return getJadexEAnnotationDetail(getModelElement(), containerEAnnotationName, annotationDetailName);
	}
	
	/**
	 * Access the model element
	 * @return the model element from section
	 */
	private EModelElement getModelElement()
	{
		return section.getEModelElement();
	}

	// ---- static methods ----

	/**
	 * Create the annotation identifier from util instance values
	 */
	public static String getTableAnnotationIdentifier(String annotationID, String detailID)
	{
		return annotationID + JADEX_COMBINED_KEY_DELIMITER + detailID 
			+ JADEX_COMBINED_KEY_DELIMITER + JADEX_TABLE_KEY_EXTENSION;
	}
	
	/**
	 * Create the annotation identifier from util instance values
	 */
	public static String getComplexValueAnnotationIdentifier(String annotationID, String detailID, String colname)
	{
		StringBuffer b = new StringBuffer(getTableAnnotationIdentifier(annotationID, detailID));
		b.append(JADEX_COMBINED_KEY_DELIMITER);
		b.append(colname.replace(" ", "-"));
		b.append(JADEX_COMBINED_KEY_DELIMITER);
		b.append(UUID.randomUUID());
		b.append(JADEX_COMBINED_KEY_DELIMITER);
		b.append("complex");
//		b.append(JADEX_TABLE_COMPLEX_COLUMNS_DETAIL);
		return b.toString();
	}
	
	/**
	 *  Get an annotation.
	 */
	public static EAnnotation getJadexEAnnotation(final EModelElement element, final String annotationIdentifier, boolean create)
	{
		if(element == null)
		{
			return null;
		}
	
		final String lcAnnotationIdentifier = annotationIdentifier.toLowerCase();
		EAnnotation annotation = element.getEAnnotation(lcAnnotationIdentifier);
		
		// try the upper case value
		if(annotation == null)
		{
			annotation = element.getEAnnotation(annotationIdentifier);
			
			// change upper case annotation identifier
			if(annotation != null && !RESERVED_BPMN_ANNOTATIONS.contains(annotationIdentifier))
			{
				// XXX: FixME!!! Use command!
				
				if(element.getEAnnotations().remove(annotation))
				{
					annotation.setSource(lcAnnotationIdentifier);
					element.getEAnnotations().add(annotation);
				}
			}
		}
		
		// create annotation if not found yet
		if(create && annotation == null)
		{
			// update or create the annotation detail
			ModifyEObjectCommand command = new ModifyEObjectCommand(
				element, Messages.JadexCommonPropertySection_update_eannotation_command_name)
			{
				protected CommandResult doExecuteWithResult(IProgressMonitor arg0, IAdaptable arg1)
					throws ExecutionException
				{
					EAnnotation annotation = EcoreFactory.eINSTANCE.createEAnnotation();
					annotation.setSource(lcAnnotationIdentifier);
					annotation.setEModelElement(element);
					return CommandResult.newOKCommandResult();
				}
			};
			
			// execute command
			try
			{
				IStatus status = command.execute(new NullProgressMonitor(), null);
				status.isOK();
				annotation = getJadexEAnnotation(element, annotationIdentifier, true);
			}
			catch (ExecutionException exception)
			{
				JadexBpmnEditorActivator.getDefault().getLog().log(new Status(IStatus.ERROR, 
					JadexBpmnEditorActivator.ID, IStatus.ERROR, exception.getMessage(), exception));
			}
		}

		return annotation;
	}
	
	public static boolean removeJadexEAnnotation(final EModelElement element, final String annotationIdentifier)
	{
		final EAnnotation eAnnotation = getJadexEAnnotation(element, annotationIdentifier, false);
		if (eAnnotation != null)
		{
			// update or create the annotation detail
			ModifyEObjectCommand command = new ModifyEObjectCommand(
					element, Messages.JadexCommonPropertySection_update_eannotation_command_name)
			{
				protected CommandResult doExecuteWithResult(IProgressMonitor arg0, IAdaptable arg1)
					throws ExecutionException
				{
					element.getEAnnotations().remove(eAnnotation);
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
				JadexBpmnEditorActivator.getDefault().getLog().log(new Status(IStatus.ERROR, JadexBpmnEditorActivator.ID,
					IStatus.ERROR, exception.getMessage(), exception));
			}
		}
		
		// fall through
		return false;
	}
	
	/**
	 * Update annotation detail
	 */
	public static boolean updateJadexEAnnotationDetail(final EModelElement element, 
		final String annotationIdentifier, final String annotationDetail, final String value)
	{
		if(element == null)
		{
			return false;
		}
		
		// update or create the annotation detail
		ModifyEObjectCommand command = new ModifyEObjectCommand(
			element, Messages.JadexCommonPropertySection_update_eannotation_command_name)
		{
			protected CommandResult doExecuteWithResult(
				IProgressMonitor arg0, IAdaptable arg1) throws ExecutionException
			{
				EAnnotation annotation = getJadexEAnnotation(element, annotationIdentifier, true);
				
				// use only lower case identifier strings!
				String lcAnnotationDetail = annotationDetail.toLowerCase();
				
				// remove upper case annotation detail
				if (!lcAnnotationDetail.equals(annotationDetail))
				{
					if (!RESERVED_BPMN_ANNOTATIONS.contains(annotationDetail))
					{
						annotation.getDetails().removeKey(annotationDetail);
					}
					else
					{
						// use the original annotation id
						lcAnnotationDetail = annotationDetail;
					}
				}

				// add key value pair or remove empty value 
				// strings as well as empty annotations
				if (value != null && !value.isEmpty())
				{
					annotation.getDetails().put(lcAnnotationDetail, value);
				}
				else
				{
					annotation.getDetails().removeKey(lcAnnotationDetail);
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
			JadexBpmnEditorActivator.getDefault().getLog().log(new Status(IStatus.ERROR, JadexBpmnEditorActivator.ID,
				IStatus.ERROR, exception.getMessage(), exception));
			
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
		
		if(annotation == null)
		{
			// try the lower case identifier
			annotation = element.getEAnnotation(annotationIdentifier.toLowerCase());
		}
		
		if(annotation != null)
		{
			Object detail = annotation.getDetails().get(annotationDetail);
			if (detail == null)
			// try the lower case identifier
			{
				
				detail = annotation.getDetails().get(annotationDetail.toLowerCase());
			}
			
			if (detail != null)
			{
				return detail.toString();
			}
			
			// fall through, return empty string as detail
			return "";
		}
	
		return null;
		
	}
	
	/**
	 * Update annotation detail
	 */
	public static boolean updateJadexEAnnotationTable(final EModelElement element, final String annotationIdentifier, final MultiColumnTableEx table)
	{
		if(element == null)
		{
			return false;
		}

		// update or create the annotation / detail
		ModifyEObjectCommand command = new ModifyEObjectCommand(
			element, Messages.JadexCommonPropertySection_update_eannotation_command_name)
		{
			protected CommandResult doExecuteWithResult(IProgressMonitor arg0, IAdaptable arg1)
				throws ExecutionException
			{
				EAnnotation annotation = element.getEAnnotation(annotationIdentifier);
				if(annotation == null && table != null)
				{
					annotation = EcoreFactory.eINSTANCE.createEAnnotation();
					annotation.setSource(annotationIdentifier);
					annotation.setEModelElement(element);
				}
				
				if(table != null && !table.isEmpty())
				{
					String tableDimension =  (new TableCellIndex(table.size(), table.getRowSize())).toString();
					annotation.getDetails().clear();
					annotation.getDetails().put(JADEX_TABLE_DIMESION_DETAIL, tableDimension);
					annotation.getDetails().put(JADEX_TABLE_UNIQUE_COLUMN_DETAIL, String.valueOf(table.getUniqueColumn()));
					annotation.getDetails().put(JADEX_TABLE_COMPLEX_COLUMNS_DETAIL, encodeComplexColumnMarker(table.getComplexColumnsMarker()));
					int rowIndex = 0;
					for(MultiColumnTableRow row : table.getRowList())
					{
						for(int columnIndex = 0; columnIndex < row.getColumnValues().length; columnIndex++)
						{
							annotation.getDetails().put(new TableCellIndex(rowIndex, columnIndex).toString(), row.getColumnValueAt(columnIndex));
							
							// save complex values
							if(table.isComplexColumn(columnIndex))
							{
								String anname = row.getColumnValueAt(columnIndex);
								EAnnotation cvan = element.getEAnnotation(anname);
								if(cvan == null)
								{
									cvan = EcoreFactory.eINSTANCE.createEAnnotation();
									cvan.setSource(anname);
									cvan.setEModelElement(element);
								}
								cvan.getDetails().clear();
								cvan.getDetails().putAll(table.getComplexValue(row.getColumnValueAt(columnIndex)));
							}
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
			JadexBpmnEditorActivator.getDefault().getLog().log(new Status(IStatus.ERROR, JadexBpmnEditorActivator.ID,
				IStatus.ERROR, exception.getMessage(), exception));
			return false;
		}
	}
	
	
	
	/**
	 * Get annotation detail
	 */
	public static MultiColumnTableEx getJadexEAnnotationTable(final EModelElement element, final String annotationIdentifier)
	{
		if(element == null)
		{
			return null;
		}
	
		EAnnotation annotation = element.getEAnnotation(annotationIdentifier);
		if(annotation != null)
		{
			MultiColumnTableEx table;
			
			String dimension = annotation.getDetails().get(JADEX_TABLE_DIMESION_DETAIL);
			int uniqueColumn = Integer.valueOf(annotation.getDetails().get(JADEX_TABLE_UNIQUE_COLUMN_DETAIL));
			boolean[] complexColumnMarker = decodeComplexColumnMarker(annotation.getDetails().get(JADEX_TABLE_COMPLEX_COLUMNS_DETAIL));
			if(dimension != null) 
			{
				TableCellIndex tableDimension = new TableCellIndex(dimension);
				table = new MultiColumnTableEx(tableDimension.getRowCount(), uniqueColumn, complexColumnMarker);
				for(int rowIndex = 0; rowIndex < tableDimension.rowCount; rowIndex++)
				{
					String[] rowdata = new String[tableDimension.columnCount];
					for(int columnIndex = 0; columnIndex < tableDimension.columnCount; columnIndex++)
					{
						rowdata[columnIndex] = annotation.getDetails().get((new TableCellIndex(rowIndex, columnIndex)).toString());
						
						// set complex values
						if(table.isComplexColumn(columnIndex))
						{
							EAnnotation cvan = element.getEAnnotation(rowdata[columnIndex]);
							if(cvan != null)
							{
								Map<String, String> cv = new HashMap<String, String>();
								for(Entry<String, String> e: cvan.getDetails().entrySet())
								{
									cv.put(e.getKey(), e.getValue());
								}
								
								table.setComplexValue(rowdata[columnIndex], cv);
							}
						}
						
					}
					table.add(table.new MultiColumnTableRow(rowdata, table));
				}

				return table;
			}
			
			// fall through
			table = new MultiColumnTableEx(0, uniqueColumn, complexColumnMarker);
			// set parameter
			return table;
		}
	
		return null;
		
	}
	
	/**
	 * 
	 * @param string
	 * @return string as boolean[] marker
	 */
	private static boolean[] decodeComplexColumnMarker(String markerString)
	{
		boolean[] marker = null;
		
		if (markerString != null && !markerString.trim().isEmpty())
		{
			String[] split = markerString
					.split(JADEX_TABLE_DIMENSION_DELIMITER);
			marker = new boolean[split.length];
			for (int i = 0; i < split.length; i++)
			{
				marker[i] = Boolean.parseBoolean(split[i]);
			}
		}
		return marker;
	}
	

	/**
	 * 
	 * @param marker
	 * @return boolean[] marker as String
	 */
	private static String encodeComplexColumnMarker(boolean[] marker)
	{
		
		if (marker != null)
		{
			StringBuffer b = new StringBuffer();
			for (int i = 0; i < marker.length; i++)
			{
				b.append(marker[i]);
				if (i + 1 < marker.length)
				{
					b.append(JADEX_TABLE_DIMENSION_DELIMITER);
				}
			}
			return b.toString();
		}
		
		return "";
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
		if (ea == null && 
				annotationId != JADEX_GLOBAL_ANNOTATION && 
				!RESERVED_BPMN_ANNOTATIONS.contains(annotationId))
		{
			ea = modelElement.getEAnnotation(JADEX_GLOBAL_ANNOTATION);
			
			assert ea != null : "Can't convert annotation: "+annotationId+":"+detailId+" from '"+modelElement+"'";
			annotationId = JADEX_GLOBAL_ANNOTATION;
		}
		
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
						modelElement, getTableAnnotationIdentifier(annotationId, detailId), new MultiColumnTableEx(table));
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
	
	
	public static boolean checkAnnotationConversion(final EModelElement eModelElement)
	{
		if(eModelElement == null)
		{
			return false;
		}

			// update or create the annotation / detail
			ModifyEObjectCommand command = new ModifyEObjectCommand(
				eModelElement, Messages.JadexCommonPropertySection_update_eannotation_command_name)
			{
				protected CommandResult doExecuteWithResult(
						IProgressMonitor arg0, IAdaptable arg1)
						throws ExecutionException
				{
					// move all details into a single "jadex" annotation
					EAnnotation jadexAnnotation = eModelElement.getEAnnotation(JADEX_GLOBAL_ANNOTATION);
					EList<EAnnotation> annos = eModelElement.getEAnnotations();
					
					// if there is no jadex annotation but another annotation
					if(jadexAnnotation == null && annos.size() > 1)
					{
						// create the jadex annotation
						jadexAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
						jadexAnnotation.setSource(JADEX_GLOBAL_ANNOTATION);
						jadexAnnotation.setEModelElement(eModelElement);
					}
					
					// remember: index is off by one
					for (int i = annos.size()-1; i >= 0; i--) 
					{
						EAnnotation eAnnotation = annos.get(i);
						// only convert non-table and none "single jadex" annotations
						String annotationSource = eAnnotation.getSource();
						if (!annotationSource.equals(JADEX_GLOBAL_ANNOTATION) 
								&& !annotationSource.endsWith(JADEX_TABLE_KEY_EXTENSION)
								&& !RESERVED_BPMN_ANNOTATIONS.contains(annotationSource))
						{
							jadexAnnotation.getDetails().addAll(eAnnotation.getDetails());
							annos.remove(i);
						}
					}
					
					// remove annotation from element, if there are no details
					if (jadexAnnotation != null && jadexAnnotation.getDetails().isEmpty())
					{
						annos.remove(jadexAnnotation);
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
			JadexBpmnEditorActivator.getDefault().getLog().log(
					new Status(IStatus.ERROR, JadexBpmnEditorActivator.ID,
							IStatus.ERROR, exception.getMessage(),
							exception));
			
			return false;
		}

	}
	
	/**
	 * The static conversion method to sub sequentially 
	 * convert all known annotation properties
	 * @param editPart to convert properties for  
	 */
	public static void convertDiagramProperties(BpmnDiagramEditPart editPart)
	{
	
		List<EModelElement> elementsToCheck = new ArrayList<EModelElement>();
		EObject diagramEModelElement = ((View) editPart.getModel()).getElement();
		
		// add the diagram to the elements to check
		elementsToCheck.add((EModelElement) diagramEModelElement);
		
		// select all elements from diagram and subsequent elements
		TreeIterator<EObject> contents = diagramEModelElement.eAllContents();
		while (contents.hasNext())
		{
			EObject eObject = (EObject) contents.next();
			if (eObject instanceof EModelElement && !(eObject instanceof EAnnotation))
			{
				elementsToCheck.add((EModelElement) eObject);
			}
		}
		
		// check each element for existing annotations to convert
		for (EModelElement eModelElement : elementsToCheck)
		{
			// check annotations and convert to a single jadex annotation
			JadexBpmnPropertiesUtil.checkAnnotationConversion(eModelElement);
			
			// check table conversion
			for (TableAnnotationIdentifier identifier : toConvert)
			{
				JadexBpmnPropertiesUtil.checkAnnotationConversion(
						eModelElement, identifier.annotationID,
						identifier.detailID, identifier.uniqueTableColumn);
			}
	
		}
		
		updateEditorVersionInfo(editPart);
		
	}
	
	public static void updateEditorVersionInfo(BpmnDiagramEditPart editPart)
	{
		// save diagram version = 2.0
		EObject diagramEModelElement = ((View) editPart.getModel()).getElement();
		JadexBpmnPropertiesUtil.updateJadexEAnnotationDetail(
				(EModelElement) diagramEModelElement, JADEX_GLOBAL_ANNOTATION,
				JADEX_PROPERTIES_VERSION_DETAIL, new Double(
						JadexBpmnEditor.EDITOR_VERSION).toString());
	}


	public static EModelElement retrieveBpmnDiagram(EModelElement element)
	{
		// save a marker 
		EModelElement elm = element;
		
		while(element.eContainer()!= null && !(element instanceof BpmnDiagram))
		{
			element = (EModelElement)element.eContainer();
		}
		
		if(!(element instanceof BpmnDiagram))
		{
			throw new UnsupportedOperationException("The method " + JadexBpmnPropertiesUtil.class.getSimpleName() + "#retrieveBpmnDiagram(EModelElement element) doesnt support the input: " + elm);
		}
		
		return element;
		
	}
}

/**
 * A cell index data type
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
		this(rowCount, columnCount, null);
	}
	
	/**
	 * @param rowCount
	 * @param columnCount
	 */
	protected TableCellIndex(int rowCount, int columnCount, String cellDimension)
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
		return rowCount + JadexBpmnPropertiesUtil.JADEX_TABLE_DIMENSION_DELIMITER + columnCount;
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

class TableAnnotationIdentifier
{
	String annotationID;
	String detailID;
	int uniqueTableColumn;
	
	/**
	 * @param annotationID
	 * @param detailID
	 * @param uniqueTableColumn
	 */
	protected TableAnnotationIdentifier(String annotationID,
			String detailID, int uniqueTableColumn)
	{
		super();
		this.annotationID = annotationID;
		this.detailID = detailID;
		this.uniqueTableColumn = uniqueTableColumn;
	}

	/**
	 * @return the annotationID
	 */
	public String getAnnotationID()
	{
		return annotationID;
	}

	/**
	 * @return the detailID
	 */
	public String getDetailID()
	{
		return detailID;
	}

	/**
	 * @return the uniqueTableColumn
	 */
	public int getUniqueTableColumn()
	{
		return uniqueTableColumn;
	}

}
