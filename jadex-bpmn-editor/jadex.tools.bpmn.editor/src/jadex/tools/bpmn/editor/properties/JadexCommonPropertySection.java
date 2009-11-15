/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.JadexBpmnDiagramMessages;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
	public void createControls(Composite parent,TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Composite sectionPartComposite = getWidgetFactory().createComposite(parent, SWT.BOLD);
		GridLayout layout = new GridLayout();
		sectionPartComposite.setLayout(layout);
		
		GridData gd = new GridData(SWT.FILL);
		//gd.minimumWidth = 500;
		//gd.widthHint = 500;
		
		Label commonLabel = getWidgetFactory().createLabel(sectionPartComposite, JadexBpmnDiagramMessages.CommonSection_label_text);
		commonLabel.setLayoutData(gd);
		
		//getWidgetFactory().createCLabel(parent, "class");
		//implText = getWidgetFactory().createText(parent, "");
		//implText.setLayoutData(gd);
		//getWidgetFactory().createCLabel(parent, "parameter");
		//parameterText = getWidgetFactory().createText(parent, "");
		//parameterText.setLayoutData(gd);

		//implText.addModifyListener(new ModifyJadexInformation(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS, implText));
		//parameterText.addModifyListener(new ModifyJadexInformation(JadexProptertyConstants.JADEX_ACTIVITY_TASK_PARAMETER_LIST, parameterText));

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
				EAnnotation ea = elt.getEAnnotation(JadexProptertyConstants.JADEX_COMMON_ANNOTATION);
				if (ea != null)
				{
					//implText.setText((String) ea.getDetails().get(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS));
					//parameterText.setText((String) ea.getDetails().get(JadexProptertyConstants.JADEX_ACTIVITY_TASK_PARAMETER_LIST));
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

	
//	/**
//	 * Utility class that finds the files and the editing domain easily,
//	 *
//	 * Maybe we need this internally only
//	 */
//	private abstract class ModifyJadexEAnnotationCommand extends
//			AbstractTransactionalCommand
//	{
//
//		public ModifyJadexEAnnotationCommand(Activity ann, String label)
//		{
//			super((TransactionalEditingDomain) AdapterFactoryEditingDomain
//					.getEditingDomainFor(ann), label, getWorkspaceFiles(ann));
//		}
//	}

	/**
	 * Tracks the change occurring on the text field.
	 */
	private class ModifyJadexInformation implements ModifyListener
	{
		private String key;
		private Text field;

		public ModifyJadexInformation(String k, Text field)
		{
			key = k;
			this.field = field;
		}

		public void modifyText(ModifyEvent e)
		{
			if (selectedElement == null)
			{ 
				// the value was just initialized
				return;
			}
			ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
					selectedElement, JadexBpmnDiagramMessages.CommonSection_update_command_name)
			{

				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor arg0, IAdaptable arg1)
						throws ExecutionException
				{
					EAnnotation annotation = selectedElement.getEAnnotation(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
					if (annotation == null)
					{
						// create the complete initial EAnnotation here! 
						annotation = EcoreFactory.eINSTANCE.createEAnnotation();
						annotation.setSource(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
						annotation.setEModelElement(selectedElement);
						annotation.getDetails().put(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS, "");
						annotation.getDetails().put(JadexProptertyConstants.JADEX_ACTIVITY_TASK_PARAMETER_LIST, "");
					}
					// add the annotation details
					annotation.getDetails().put(key, field.getText());

					return CommandResult.newOKCommandResult();
				}
			};
			try
			{
				command.execute(new NullProgressMonitor(), null);
			}
			catch (ExecutionException exception)
			{
				JadexBpmnPlugin.getDefault().getLog().log(
						new Status(IStatus.ERROR,
								JadexBpmnPlugin.PLUGIN_ID, IStatus.ERROR,
								exception.getMessage(), exception));
			}
		}
	}

}
