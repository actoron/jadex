package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;
import jadex.tools.bpmn.editor.JadexBpmnPlugin;
import jadex.tools.model.common.properties.AbstractCommonPropertySection;
import jadex.tools.model.common.properties.ModifyEObjectCommand;

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
		
		
		// create the TransactionalCommand
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
				
				annotation.getDetails().put(annotationDetail, value);
				
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
			return annotation.getDetails().get(annotationDetail);
		}
	
		return null;
		
	}
	
	
}
