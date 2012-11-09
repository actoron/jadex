/**
 * 
 */
package jadex.editor.bpmn.editor.properties.template;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


/**
 * 
 */
public abstract class AbstractMultiTextfieldPropertySection extends AbstractBpmnPropertySection
{
	// ---- constants ----
	
	protected static final String[] DEFAULT_NAMES = new String[] {"Default_1", "Default_2", "Default_3" };
	
	// ---- attributes ----

	private String[] textFieldNames;
	private Text[] textFields;
	
	// ---- constructor ----
	
	/**
	 * Default Constructor
	 * 
	 * @param textFieldNames
	 * @param textFields
	 */
	protected AbstractMultiTextfieldPropertySection(String containerEAnnotationName, String[] textFieldNames)
	{
		super(containerEAnnotationName, null);
		this.textFieldNames = textFieldNames != null ? textFieldNames : DEFAULT_NAMES;
	}


	// ---- methods ----

	/* (non-Javadoc)
	 * @see jadex.tools.model.common.properties.AbstractCommonPropertySection#dispose()
	 */
	public void dispose()
	{
		// nothing to dispose here, use addDisposable(Object) instead
		super.dispose();
	}

	
	/**
	 * Creates the UI of the section.
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		
		GridLayout sectionLayout = new GridLayout(1, true);
		sectionComposite.setLayout(sectionLayout);
		
		GridData textGridData = new GridData();
		textGridData.minimumWidth = 500;
		textGridData.widthHint = 500;
		
		GridData labelGridData = new GridData();
		labelGridData.minimumWidth = 80;
		labelGridData.widthHint = 80;

		textFields = new Text[textFieldNames.length];
		for(int i = 0; i < textFieldNames.length; i++)
		{
			// TO DO: use group?
			Composite cComposite = getWidgetFactory().createComposite(sectionComposite);
			addDisposable(cComposite);
			cComposite.setLayout(new GridLayout(2, false));
			
			Label cLabel = getWidgetFactory().createLabel(cComposite, textFieldNames[i]+":"); // //$NON-NLS-1$
			addDisposable(cLabel);
			Text cTextfield = getWidgetFactory().createText(cComposite, "");
			addDisposable(cTextfield);
			textFields[i] = cTextfield;
			cTextfield.addFocusListener(new ModifyJadexEAnnotation(textFieldNames[i], cTextfield));
			cLabel.setLayoutData(labelGridData);
			cTextfield.setLayoutData(textGridData);
		}
	}
	
	/**
	 * Manages the input.
	 */
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		if(modelElement != null)
		{
			EAnnotation ea = modelElement.getEAnnotation(util.containerEAnnotationName);
			if (ea != null)
			{
				for (int i = 0; i < textFieldNames.length; i++)
				{
					String tmpName = textFieldNames[i];
					Text tmpField = textFields[i];
					String tmpValue = (String)util.getJadexEAnnotationDetail(tmpName);
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
		for(int i = 0; i < textFieldNames.length; i++)
		{
			Text tmpField = textFields[i];
			tmpField.setEnabled(false);
		}
	}

	// ---- internal used classes ----
	
	/**
	 * Tracks the change occurring on the text field.
	 */
	private class ModifyJadexEAnnotation implements FocusListener
	{
		private String key;
		private Text field;

		public ModifyJadexEAnnotation(String k, Text field)
		{
			key = k;
			this.field = field;
		}

		public void focusGained(FocusEvent e)
		{
			// nothing to to
		}

		public void focusLost(FocusEvent e)
		{
			if(modelElement == null)
			{ 
				// the value was just initialized
				return;
			}
			String value = field.getText();
			if(value != null)
			{
				updateJadexEAnnotation(key, value);
			}
		}
	}

}
