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
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * <p>Use this as an simple Example for property tab sections</p>
 * 
 * @author Claas Altschaffel
 */
public class JadexCommonPropertySection extends AbstractPropertySection
{

	// ---- constants ----
	
	/** Key for the common annotations of all shapes. NOT USED? */
	public static final String JADEX_COMMON_ANNOTATION = "common";
	
	/** Key for the annotation from the activity shape. */
	public static final String JADEX_ACTIVITY_ANNOTATION = "activity";
	
	/** Key for the annotation from the flow connector. */
	public static final String JADEX_SEQUENCE_ANNOTATION = "sequence";
	
	
	/** Key for the implementing class of a task. */
	public static final String JADEX_ACTIVITY_CLASS_DETAIL = "class";
	
	/** Key for the parameter map of a task. */
	public static final String JADEX_PARAMETER_LIST_DETAIL = "prameter";

	
	/** Key for the parameter map of a task. */
	public static final String JADEX_FLOW_EXAMPLE_ANNOTATION = "example";
	
	
	
	/** String delimiter for list elements */
	public static final String LIST_ELEMENT_DELIMITER = "<*>";
	
	/** String delimiter for element attributes */
	public static final String LIST_ELEMENT_ATTRIBUTE_DELIMITER = "#|#";

	
	
	
	// ---- attributes ----

	/** The text for the implementing class */
	//private Text implText;

	/** The text for the role */
	//private Text parameterText;

	/** The activity (task) that holds impl and role, may be null. */
	private EModelElement selectedElement;

	
	
	

	// ---- methods ----

	/**
	 * Creates the UI of the section.
	 */
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite sectionPartComposite = getWidgetFactory().createComposite(parent, SWT.BOLD);
		GridLayout layout = new GridLayout();
		sectionPartComposite.setLayout(layout);
		
		GridData gd = new GridData(SWT.FILL);
		//gd.minimumWidth = 500;
		//gd.widthHint = 500;
		
		Label commonLabel = getWidgetFactory().createLabel(sectionPartComposite, Messages.CommonSection_label_text);
		commonLabel.setLayoutData(gd);
		
		//getWidgetFactory().createCLabel(parent, "class");
		//implText = getWidgetFactory().createText(parent, "");
		//implText.setLayoutData(gd);
		//getWidgetFactory().createCLabel(parent, "parameter");
		//parameterText = getWidgetFactory().createText(parent, "");
		//parameterText.setLayoutData(gd);

		//implText.addModifyListener(new ModifyJadexInformation(JadexProptertyConstants.JADEX_ACTIVITY_CLASS, implText));
		//parameterText.addModifyListener(new ModifyJadexInformation(JadexProptertyConstants.JADEX_PARAMETER_LIST, parameterText));

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
			Object unknownInput = ((IStructuredSelection) selection).getFirstElement();
			if (unknownInput instanceof IGraphicalEditPart
					&& (((IGraphicalEditPart) unknownInput).resolveSemanticElement() != null))
			{
				unknownInput = ((IGraphicalEditPart) unknownInput).resolveSemanticElement();
			}
			if (unknownInput instanceof EModelElement)
			{
				EModelElement elt = (EModelElement) unknownInput;
				EAnnotation ea = elt.getEAnnotation(JadexCommonPropertySection.JADEX_COMMON_ANNOTATION);
				if (ea != null)
				{
					//implText.setText((String) ea.getDetails().get(JadexProptertyConstants.JADEX_ACTIVITY_CLASS));
					//parameterText.setText((String) ea.getDetails().get(JadexProptertyConstants.JADEX_PARAMETER_LIST));
				}
				selectedElement = (EModelElement) elt;
				//implText.setEnabled(true);
				//parameterText.setEnabled(true);
				return;
			}
		}
		selectedElement = null;
		//implText.setText("");
		//parameterText.setText("");
		//implText.setEnabled(false);
		//parameterText.setEnabled(false);
	}
	
	// ---- common helper methods ----
	
	/**
	 * Update 
	 * @param key
	 * @param value
	 */
	protected static boolean updateJadexEAnnotation(final EModelElement element, final String key, final String value)
	{
		// we can only update an activity
		if(element == null)
		{
			return false;
		}
		
		
		// create the TransactionalCommand
		ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
				element, Messages.JadexCommonPropertySection_update_eannotation_command_name)
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor arg0, IAdaptable arg1)
					throws ExecutionException
			{
				EAnnotation annotation = element.getEAnnotation(JadexCommonPropertySection.JADEX_ACTIVITY_ANNOTATION);
				if (annotation == null)
				{
					annotation = EcoreFactory.eINSTANCE.createEAnnotation();
					annotation.setSource(JadexCommonPropertySection.JADEX_ACTIVITY_ANNOTATION);
					annotation.setEModelElement(element);
					annotation.getDetails().put(JadexCommonPropertySection.JADEX_ACTIVITY_CLASS_DETAIL, ""); //$NON-NLS-1$
					annotation.getDetails().put(JadexCommonPropertySection.JADEX_PARAMETER_LIST_DETAIL, ""); //$NON-NLS-1$
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
