/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.stp.bpmn.diagram.part.BpmnDiagramEditorPlugin;
import org.eclipse.stp.bpmn.properties.ModifyBpmnEAnnotationCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


/**
 * @author Claas Altschaffel
 * 
 */
public abstract class AbstractCheckboxPropertySection extends
		AbstractBpmnPropertySection
{
	// ---- constants ----
	
	protected static final String DEFAULT_NAME = new String("Default_1");
	
	// ---- attributes ----

	private String checkboxLabel;
	
	private Text[] textFields;
	
	// ---- constructor ----
	
	/**
	 * Default Constructor
	 * 
	 * @param textFieldNames
	 * @param textFields
	 */
	protected AbstractCheckboxPropertySection(
			String containerEAnnotationName,
			String checkboxLabel)
	{
		super(containerEAnnotationName, checkboxLabel);
		this.checkboxLabel = checkboxLabel != null ? checkboxLabel : DEFAULT_NAME;
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
		
		GridLayout sectionLayout = new GridLayout(2, true);
		sectionComposite.setLayout(sectionLayout);
		
		GridData labelGridData = new GridData();
		labelGridData.minimumWidth = 60;
		labelGridData.widthHint = 60;
		
		GridData checkboxGridData = new GridData();
		checkboxGridData.minimumWidth = 50;
		checkboxGridData.widthHint = 50;

			
			
			Label cLabel = getWidgetFactory().createLabel(sectionComposite, checkboxLabel+":"); // 
			Button button = getWidgetFactory().createButton(sectionComposite, "", SWT.CHECK);
			
			button.addSelectionListener(new SelectionAdapter()
			{
				/** 
				 * Add a ContextElement to the Context and refresh the view
				 * @generated NOT 
				 */
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					((Button) e.getSource()).
					updateJadexEAnnotation(util.annotationDetailName, );
					
				}
			});
			
			cLabel.setLayoutData(labelGridData);
			button.setLayoutData(checkboxGridData);
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
			EAnnotation ea = modelElement.getEAnnotation(util.containerEAnnotationName);
			if (ea != null)
			{
				for (int i = 0; i < textFieldNames.length; i++)
				{
					String tmpName = textFieldNames[i];
					Text tmpField = textFields[i];
					String tmpValue = (String) ea.getDetails().get(tmpName);
					tmpField.setText(tmpValue != null ? tmpValue : "");
					tmpField.setEnabled(true);
				}
			}
			else
			{
				for (int i = 0; i < textFieldNames.length; i++)
				{
					textFields[i].setText("");
					textFields[i].setEnabled(true);
				}
			}

			return;
		}
		
		// fall through
		for (int i = 0; i < textFieldNames.length; i++)
		{
			Text tmpField = textFields[i];
			tmpField.setEnabled(false);
		}
		
		
	}


}
