/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.editor.JadexBpmnPlugin;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.xml.type.internal.RegEx.RegularExpression;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.stp.bpmn.Activity;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * @author Claas Altschaffel
 */
public class JadexActivityPropertySection extends AbstractPropertySection
{

	// ---- attributes ----

	/** The text for the implementing class */
	private CCombo classImplCombo;

	/** The activity (task) that holds task implementation class and parameters, may be null. */
	private Activity activity;

	// ---- methods ----

	/**
	 * Creates the UI of the section.
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);

		GridData gd = new GridData(SWT.FILL);
		gd.minimumWidth = 500;
		gd.widthHint = 500;

		classImplCombo = createTaskClassCombo(parent, gd);

		getWidgetFactory().createCLabel(parent, "parameter");

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
			if (unknownInput instanceof Activity)
			{
				Activity elt = (Activity) unknownInput;
				EAnnotation ea = elt.getEAnnotation(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
				if (ea != null)
				{
					String value = (String) ea.getDetails().get(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS);
					int valueIndex = -1;
					
					// search value in items
					String[] items = classImplCombo.getItems();
					for (int i = 0; i < items.length; i++)
					{
						if(items[i].equals(value))
						{
							valueIndex = i;
						}
					}
					
					// add the value to the items list
					if (valueIndex == -1 )
					{
						classImplCombo.add(value, 0);
						valueIndex = 0;
					}
					
					classImplCombo.select(0);
					
				}
				activity = (Activity) elt;
				classImplCombo.setEnabled(true);
				return;
			}
		}
		activity = null;
		classImplCombo.setText("");
		classImplCombo.setEnabled(false);
	}

	

	
	/**
	 * Create a combo for task class selection in parent
	 *  
	 * @param parent
	 */
	protected CCombo createTaskClassCombo(Composite parent, GridData data)
	{
		getWidgetFactory().createCLabel(parent, "class");
		final CCombo combo = getWidgetFactory().createCCombo(parent, SWT.NONE);
		combo.setLayoutData(data);
		combo.setItems(new String[] { 
				"Test.class", 
				"SomeTask.class", 
				"MessageTask.class", 
				"OneMoreTestTask.class" });
		combo.setText(combo.getItem(0));
		combo.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String text = combo.getText();
				String newText = text.substring(0, e.start) + e.text
						+ text.substring(e.end);
				
				// don't allow non word characters
				RegularExpression re = new RegularExpression("\\w*");
				if (!re.matches(newText))
				{
					e.doit = false;
				}
			}
		});
		combo.addTraverseListener(new TraverseListener()
		{
			public void keyTraversed(TraverseEvent e)
			{
				if (e.detail == SWT.TRAVERSE_RETURN)
				{
					e.doit = false;
					e.detail = SWT.TRAVERSE_NONE;
					String newText = combo.getText();

					// check if we have a valid class name
					if (newText.endsWith(".class"))
					{
						combo.add(newText);
						combo.setSelection(new Point(0, newText
								.length()));
					}

				}
			}
		});
		
		combo.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				updateTaskImpl(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS, combo.getText());
			}
		});
		
		return combo;
	}
	
	/**
	 * Update 
	 * @param key
	 * @param value
	 */
	private void updateTaskImpl(final String key, final String value)
	{
		// create the TransactionalCommand
		ModifyJadexEAnnotationCommand command = new ModifyJadexEAnnotationCommand(
				activity, "Modifying " + JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION)
		{
			@Override
			protected CommandResult doExecuteWithResult(
					IProgressMonitor arg0, IAdaptable arg1)
					throws ExecutionException
			{
				EAnnotation annotation = activity.getEAnnotation(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
				if (annotation == null)
				{
					annotation = EcoreFactory.eINSTANCE.createEAnnotation();
					annotation.setSource(JadexProptertyConstants.JADEX_ACTIVITY_ANNOTATION);
					annotation.setEModelElement(activity);
					annotation.getDetails().put(JadexProptertyConstants.JADEX_ACTIVITY_TASK_CLASS, "");
					annotation.getDetails().put(JadexProptertyConstants.JADEX_ACTIVITY_TASK_PARAMETER_LIST, "");
				}
				
				annotation.getDetails().put(key, value);

				return CommandResult.newOKCommandResult();
			}
		};
		// execute command
		try
		{
			command.execute(new NullProgressMonitor(), null);
		}
		catch (ExecutionException exception)
		{
			JadexBpmnPlugin.getDefault().getLog().log(
					new Status(IStatus.ERROR, JadexBpmnPlugin.PLUGIN_ID,
							IStatus.ERROR, exception.getMessage(),
							exception));
		}
	}

}
