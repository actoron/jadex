/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;
import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * @author Claas
 *
 */
public abstract class AbstractJadexPropertySection extends AbstractPropertySection
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
	
	
	/** Key for the parameter map of a task. */
	public static final String JADEX_FLOW_EXAMPLE_ANNOTATION = "example";
	
	
	/** 
	 * String delimiter for list elements <p>
	 * <p><code>0x241F</code>	(9247)	SYMBOL FOR UNIT SEPARATOR</p>
	 */
	public static final String LIST_ELEMENT_DELIMITER = "\u241F"; // "<*>";
	
	
	/** 
	 * String delimiter for element attributes  <p>
	 * <p><code>0x240B</code>	(9227)	SYMBOL FOR VERTICAL TABULATION</p>
	 */
	public static final String LIST_ELEMENT_ATTRIBUTE_DELIMITER = "\u240B"; //"#|#";

	
	// ---- attributes ----
	
	/** The composite that holds the section parts */
	protected Composite sectionComposite;
	
	/** The modelElement (task) that holds task implementation class and parameters, may be null. */
	protected EModelElement modelElement;
	
	/** The EAnnotations name that contains the table information as detail */
	protected String containerEAnnotationName;
	
	/** The EAnnotations detail that contains the information */
	protected String annotationDetailName;

	
	
	// ---- constructor ----
	
	/**
	 * @param containerEAnnotationName
	 * @param annotationDetailName
	 */
	protected AbstractJadexPropertySection(String containerEAnnotationName,
			String annotationDetailName)
	{
		super();
		
		assert containerEAnnotationName != null && !containerEAnnotationName.isEmpty();
		assert annotationDetailName != null && !annotationDetailName.isEmpty();
		
		this.containerEAnnotationName = containerEAnnotationName;
		this.annotationDetailName = annotationDetailName;
	}


	// ---- methods ----

	/**
	 * Creates the UI of the section.
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		sectionComposite = getWidgetFactory().createComposite(parent);
		sectionComposite.setLayout(new FillLayout());
	}

	
	/**
	 * Manages the input.
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		if (selection instanceof IStructuredSelection)
		{
			Object unknownInput = ((IStructuredSelection) selection)
					.getFirstElement();
			if (unknownInput instanceof IGraphicalEditPart
					&& (((IGraphicalEditPart) unknownInput)
							.resolveSemanticElement() != null))
			{
				unknownInput = ((IGraphicalEditPart) unknownInput)
						.resolveSemanticElement();
			}
			if (unknownInput instanceof EModelElement)
			{
				EModelElement elm = (EModelElement) unknownInput;
				modelElement = (EModelElement) elm;

				return;
			}
		}
		
		// fall through
		modelElement = null;

	}

	
	
	/**
	 * Update 
	 * @param key
	 * @param value
	 */
	protected boolean updateJadexEAnnotation(final String key, final String value)
	{
		// we can only update an activity
		if(modelElement == null)
		{
			return false;
		}
		
		
		// create the TransactionalCommand
		ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
				modelElement, Messages.JadexCommonPropertySection_update_eannotation_command_name)
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor arg0, IAdaptable arg1)
					throws ExecutionException
			{
				EAnnotation annotation = modelElement.getEAnnotation(containerEAnnotationName);
				if (annotation == null)
				{
					annotation = EcoreFactory.eINSTANCE.createEAnnotation();
					annotation.setSource(containerEAnnotationName);
					annotation.setEModelElement(modelElement);
					annotation.getDetails().put(annotationDetailName, ""); //$NON-NLS-1$
				}
				
				annotation.getDetails().put(key, value);
				
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
	 * Refreshes the graphical selection after a modify operation.
	 * 
	 * @generated NOT
	 */
	protected void refreshSelection()
	{
		if (getSelection() instanceof IStructuredSelection)
		{
			IStructuredSelection sel = (IStructuredSelection) getSelection();
			if (null != sel)
				for (Object selElt : sel.toList())
				{
					if (selElt instanceof EditPart)
					{
						final EditPart part = (EditPart) selElt;
						Display.getCurrent().asyncExec(new Runnable()
						{
							@Override
							public void run()
							{
								part.refresh();
							}
						});
					}
					
//					if (selElt instanceof EditPart)
//					{
//						((EditPart) selElt).refresh();
//					}
					
				}
		}
	}
	
	// ---- static methods ----
	
	/**
	 * Dummy method for empty composites
	 */
	protected static Composite createEmptyComposite(Composite parent, AbstractPropertySection section)
	{
		Composite newComposite = section.getWidgetFactory().createComposite(parent/*, SWT.BORDER*/);
		
		// The layout of the composite
		GridLayout layout = new GridLayout(1, false);
		newComposite.setLayout(layout);
		
		//section.getWidgetFactory().createCLabel(newComposite, "---- empty composite ----");
		
		return newComposite;
	}
}
