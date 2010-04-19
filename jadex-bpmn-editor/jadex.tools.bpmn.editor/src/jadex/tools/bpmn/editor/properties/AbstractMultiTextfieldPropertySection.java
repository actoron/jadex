/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


/**
 * @author Claas Altschaffel
 * 
 */
public abstract class AbstractMultiTextfieldPropertySection extends
		AbstractBpmnPropertySection
{
	// ---- constants ----
	
	protected static final String[] DEFAULT_NAMES = new String[] { "Default_1", "Default_2", "Default_3" };
	
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
	protected AbstractMultiTextfieldPropertySection(
			String containerEAnnotationName,
			String[] textFieldNames)
	{
		super(containerEAnnotationName, null);
		this.textFieldNames = textFieldNames != null ? textFieldNames : DEFAULT_NAMES;
	}


	// ---- methods ----

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		if (textFields != null)
		{
			for (int i = 0; i < textFields.length; i++)
			{
				textFields[i].dispose();
			}
		}

		super.dispose();
	}

	
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
		
		GridData textGridData = new GridData();
		textGridData.minimumWidth = 500;
		textGridData.widthHint = 500;
		
		GridData labelGridData = new GridData();
		labelGridData.minimumWidth = 60;
		labelGridData.widthHint = 60;

		textFields = new Text[textFieldNames.length];
		for (int i = 0; i < textFieldNames.length; i++)
		{
			// TODO: use group?
			Composite cComposite = getWidgetFactory().createComposite(sectionComposite);
			cComposite.setLayout(new GridLayout(2, false));
			
			Label cLabel = getWidgetFactory().createLabel(cComposite, textFieldNames[i]+":"); // //$NON-NLS-1$
//			Text cTextfield = getWidgetFactory().createText(cComposite, textFieldNames[i]);
			Text cTextfield = getWidgetFactory().createText(cComposite, "");
			textFields[i] = cTextfield;
			cTextfield.addModifyListener(new ModifyJadexEAnnotation(textFieldNames[i], cTextfield));
			cLabel.setLayoutData(labelGridData);
			cTextfield.setLayoutData(textGridData);
		}

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

	// ---- internal used classes ----
	
	/**
	 * Tracks the change occurring on the text field.
	 */
	private class ModifyJadexEAnnotation implements ModifyListener
	{
		private String key;
		private Text field;

		public ModifyJadexEAnnotation(String k, Text field)
		{
			key = k;
			this.field = field;
		}

		public void modifyText(ModifyEvent e)
		{
			if (modelElement == null)
			{ 
				// the value was just initialized
				return;
			}
			
			updateJadexEAnnotation(key, field.getText());
		}
	}

}
