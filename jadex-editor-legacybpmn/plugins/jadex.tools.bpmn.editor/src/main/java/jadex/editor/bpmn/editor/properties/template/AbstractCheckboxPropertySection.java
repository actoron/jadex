package jadex.editor.bpmn.editor.properties.template;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * 
 */
public abstract class AbstractCheckboxPropertySection extends
		AbstractBpmnPropertySection
{
	// ---- constants ----

	protected static final String DEFAULT_NAME = new String("Default_1");

	// ---- attributes ----

	private String checkboxLabel;
	
	private Button checkbox;

	// ---- constructor ----

	/**
	 * Default Constructor
	 */
	protected AbstractCheckboxPropertySection(String containerEAnnotationName,
			String eAnnotationDetailId, String checkboxLabel)
	{
		super(containerEAnnotationName, eAnnotationDetailId != null ? eAnnotationDetailId: checkboxLabel);
		this.checkboxLabel = checkboxLabel != null ? checkboxLabel
				: DEFAULT_NAME;
	}

	// ---- methods ----

	/**
	 * Creates the UI of the section.
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage)
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

		Label cLabel = getWidgetFactory().createLabel(sectionComposite, checkboxLabel + ":");
		addDisposable(cLabel);
		
		Button button = getWidgetFactory().createButton(sectionComposite, null, SWT.CHECK);
		addDisposable(button);

		button.addSelectionListener(new SelectionAdapter()
		{
			/**
			 * Add a ContextElement to the Context and refresh the view
			 * @generated NOT
			 */
			public void widgetSelected(SelectionEvent e)
			{
				boolean state = ((Button) e.getSource()).getSelection();
				updateJadexEAnnotation(util.annotationDetailName, String
						.valueOf(state));
			}
		});

		cLabel.setLayoutData(labelGridData);
		button.setLayoutData(checkboxGridData);
		
		checkbox = button;
	}

	/**
	 * Manages the input.
	 */
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		if (modelElement != null)
		{
			String value = util.getJadexEAnnotationDetail(util.annotationDetailName);
			checkbox.setSelection(value != null && Boolean.valueOf(value));
			checkbox.setEnabled(true);
		}
		else 
		{
			checkbox.setSelection(false);
			checkbox.setEnabled(false);
		}

	}

}
