/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.stp.bpmn.Activity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * @author Claas Altschaffel
 */
public class JadexPropertySection extends AbstractPropertySection
{

	// ---- attributes ----

	/** The text for the implementing class */
	private Text implText;

	/** The text for the role */
	private Text roleText;

	/** The activity (task) that holds impl and role, may be null. */
	private Activity activity;

	// ---- methods ----

	/**
	 * Creates the UI of the section.
	 */
	// @Override
	public void createControls(Composite parent,TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		GridData gd = new GridData(SWT.FILL);
		gd.minimumWidth = 500;
		gd.widthHint = 500;
		getWidgetFactory().createCLabel(parent, "Implementing class");
		implText = getWidgetFactory().createText(parent, "");
		implText.setLayoutData(gd);
		getWidgetFactory().createCLabel(parent, "Role");
		roleText = getWidgetFactory().createText(parent, "");
		roleText.setLayoutData(gd);
		
		implText.addModifyListener(new ModifyJadexInformation(JadexPropteryConstants.JADEX_TASK_IMPL, implText));
		roleText.addModifyListener(new ModifyJadexInformation(JadexPropteryConstants.JADEX_TASK_ROLE, roleText));

	}

	/**
	 * Manages the input.
	 */
	// @Override
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
			if (unknownInput instanceof Activity)
			{
				Activity elt = (Activity) unknownInput;
				EAnnotation ea = elt.getEAnnotation(JadexPropteryConstants.JADEX_PROP_ANNOTATION);
				if (ea != null)
				{
					implText.setText((String) ea.getDetails().get(JadexPropteryConstants.JADEX_TASK_IMPL));
					roleText.setText((String) ea.getDetails().get(JadexPropteryConstants.JADEX_TASK_ROLE));
				}
				activity = (Activity) elt;
				implText.setEnabled(true);
				roleText.setEnabled(true);
				return;
			}
		}
		activity = null;
		implText.setText("");
		roleText.setText("");
		implText.setEnabled(false);
		roleText.setEnabled(false);
	}
	
	/**
	 * Utility class that finds the files and the editing domain easily,
	 */
	private abstract class ModifyJadexEAnnotationCommand extends
			AbstractTransactionalCommand
	{

		public ModifyJadexEAnnotationCommand(Activity ann, String label)
		{
			super((TransactionalEditingDomain) AdapterFactoryEditingDomain
					.getEditingDomainFor(ann), label, getWorkspaceFiles(ann));
		}
	}

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
			if (activity == null)
			{ 
				// the value was just initialized
				return;
			}
			ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
					activity, "Modifying participant")
			{

				@Override
				protected CommandResult doExecuteWithResult(
						IProgressMonitor arg0, IAdaptable arg1)
						throws ExecutionException
				{
					EAnnotation annotation = activity.getEAnnotation(JadexPropteryConstants.JADEX_PROP_ANNOTATION);
					if (annotation == null)
					{
						annotation = EcoreFactory.eINSTANCE.createEAnnotation();
						annotation.setSource(JadexPropteryConstants.JADEX_PROP_ANNOTATION);
						annotation.setEModelElement(activity);
						annotation.getDetails().put(JadexPropteryConstants.JADEX_TASK_IMPL, "");
						annotation.getDetails().put(JadexPropteryConstants.JADEX_TASK_ROLE, "");
					}
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
//				STPEclipseConPlugin.getDefault().getLog().log(
//						new Status(IStatus.ERROR,
//								STPEclipseConPlugin.PLUGIN_ID, IStatus.ERROR,
//								exception.getMessage(), exception));
			}
		}
	}

}
