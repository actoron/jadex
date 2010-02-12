/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;
import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
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
	
	/** Key for the implementing error. */
	public static final String JADEX_EVENT_ERROR_DETAIL = "error";


	
	
	/** The composite that holds the section parts */
	protected Composite sectionComposite;
	
	/** The modelElement, may be null. */
	protected EModelElement modelElement;
	
	/** The EAnnotations name that contains the detail */
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
		
		assert containerEAnnotationName != null && !containerEAnnotationName.isEmpty() : this.getClass() + ": containerEAnnotationName not set";
		assert annotationDetailName != null && !annotationDetailName.isEmpty() : this.getClass() + ": annotationDetailName not set";
		
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
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		if (sectionComposite != null)
			sectionComposite.dispose();
		
		super.dispose();
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

	protected void changed(Control[] changed)
	{
		sectionComposite.changed(changed);
	}
	
	
	/**
	 * Update 
	 * @param detail
	 * @param value
	 */
	protected boolean updateJadexEAnnotation(final String detail, final String value)
	{
		if(modelElement == null)
		{
			return false;
		}
		
		return updateJadexEAnnotationDetail(modelElement, containerEAnnotationName, detail, value);
		
//		// create the TransactionalCommand
//		ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
//				modelElement, Messages.JadexCommonPropertySection_update_eannotation_command_name)
//		{
//			@Override
//			protected CommandResult doExecuteWithResult(
//					IProgressMonitor arg0, IAdaptable arg1)
//					throws ExecutionException
//			{
//				EAnnotation annotation = modelElement.getEAnnotation(containerEAnnotationName);
//				if (annotation == null)
//				{
//					annotation = EcoreFactory.eINSTANCE.createEAnnotation();
//					annotation.setSource(containerEAnnotationName);
//					annotation.setEModelElement(modelElement);
//					annotation.getDetails().put(key, ""); //$NON-NLS-1$
//				}
//				
//				annotation.getDetails().put(key, value);
//				
//				return CommandResult.newOKCommandResult();
//			}
//		};
//		// execute command
//		try
//		{
//			IStatus status = command.execute(new NullProgressMonitor(), null);
//			return status.isOK();
//		}
//		catch (ExecutionException exception)
//		{
//			JadexBpmnPlugin.getDefault().getLog().log(
//					new Status(IStatus.ERROR, JadexBpmnPlugin.PLUGIN_ID,
//							IStatus.ERROR, exception.getMessage(),
//							exception));
//			
//			return false;
//		}
	}
	
	/**
	 * Refreshes the graphical selection after a modify operation.
	 * 
	 * @generated NOT
	 */
	protected void refreshSelectedEditPart()
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
	
	/**
	 * Create a group with all existing controls in sectionComposite 
	 * and replace the section composite with it
	 * @param groupLabel
	 * @return created Group
	 */
	protected Group groupExistingControls(String groupLabel)
	{
		// The layout of the section composite
		Layout sectionLayout = sectionComposite.getLayout();
		Control[] controls = sectionComposite.getParent().getChildren();
		Group sectionGroup = getWidgetFactory().createGroup(
				sectionComposite.getParent(), groupLabel);
		sectionGroup.setLayout(sectionLayout);
		sectionComposite = sectionGroup;
		for (int i = 0; i < controls.length; i++)
		{
			controls[i].setParent(sectionGroup);
		}
		return sectionGroup;
	}
	
	
//	/**
//	 * Search for Composite containing section root composite as data for key
//	 * JADEX_PROPERTY_SECTION_ROOT
//	 * @param receiver the Composite on which getParent is acalled
//	 * @return the jadex root Composite
//	 */
//	protected static Composite findSectionRootComposite(Composite receiver)
//	{
//		Composite currentSection = receiver;
//		Composite sectionRoot = (Composite) currentSection.getData(JADEX_PROPERTY_SECTION_ROOT);;
//		
//		while (sectionRoot == null)
//		{
//			currentSection = currentSection.getParent();
//			sectionRoot = (Composite) currentSection.getData(JADEX_PROPERTY_SECTION_ROOT);
//		}
//		
//		return sectionRoot;		
//	}
	
//	/**
//	 * Search subsequent the Composites for groups with specified label
//	 * @param receiver the section on which getChildren() is called
//	 * @param groupLabel to search groups with
//	 * @return List<Group> of all found groups with groupLabel
//	 */
//	protected static  List<Group> findSectionGroupComposite(Composite receiver, String groupLabel)
//	{
//		ArrayList<Group> foundGroups = new ArrayList<Group>();
//		Control[] children = receiver.getChildren();
//		for (int i = 0; i < children.length; i++)
//		{
//			if (children[i] instanceof Composite)
//			{
//				Composite child = (Composite) children[i];
//				if (child instanceof Group)
//				{
//					String label = ((Group) child).getText();
//					if (groupLabel.equals(label))
//					{
//						foundGroups.add((Group) child);
//						continue;
//					}
//				}
//				foundGroups.addAll(findSectionGroupComposite(child, groupLabel));
//			}
//		}
//		return foundGroups;		
//	}
	
	// ---- static methods ----
	
	/**
	 * Update annotation detail
	 * @param element
	 * @param annotationIdentifier
	 * @param annotationDetail
	 * @param value
	 * @return
	 */
	protected static boolean updateJadexEAnnotationDetail(final EModelElement element, final String annotationIdentifier, final String annotationDetail, final String value)
	{
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
	protected static String getJadexEAnnotationDetail(final EModelElement element, final String annotationIdentifier, final String annotationDetail)
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
	
//	/**
//	 * Dummy method for empty composites
//	 */
//	protected static Composite createEmptyComposite(Composite parent, AbstractPropertySection section)
//	{
//		Composite newComposite = section.getWidgetFactory().createComposite(parent/*, SWT.BORDER*/);
//		
//		// The layout of the composite
//		GridLayout layout = new GridLayout(1, false);
//		newComposite.setLayout(layout);
//		
//		//section.getWidgetFactory().createCLabel(newComposite, "---- empty composite ----");
//		
//		return newComposite;
//	}
}
