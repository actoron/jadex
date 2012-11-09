package jadex.editor.bpmn.editor.properties.template;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 *  Multi checkbox section.
 */
public class AbstractMultiCheckboxPropertySection extends AbstractBpmnPropertySection
{
	protected static final String[] DEFAULT_NAMES = new String[] {"Default_1", "Default_2", "Default_3" };
	
	private String[] checkboxnames;
	private Button[] checkboxes;
	private boolean[] defaultvalues;
	
	// ---- constructor ----
	
	/**
	 * Default constructor
	 */
	protected AbstractMultiCheckboxPropertySection(String containerEAnnotationName, String[] checkboxnames, boolean[] defaultvalues)
	{
		super(containerEAnnotationName, null);
		this.checkboxnames = checkboxnames!= null? checkboxnames: DEFAULT_NAMES;
		this.defaultvalues = defaultvalues;
	}


	// ---- methods ----

	/**
	 *  Dispose.
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

		checkboxes = new Button[checkboxnames.length];
		for(int i = 0; i < checkboxnames.length; i++)
		{
			// TO DO: use group?
			Composite cComposite = getWidgetFactory().createComposite(sectionComposite);
			addDisposable(cComposite);
			cComposite.setLayout(new GridLayout(2, false));
			
			Label cLabel = getWidgetFactory().createLabel(cComposite, checkboxnames[i]+":"); // //$NON-NLS-1$
			addDisposable(cLabel);
			checkboxes[i] = getWidgetFactory().createButton(cComposite, null, SWT.CHECK);
			addDisposable(checkboxes[i]);
			checkboxes[i].addFocusListener(new ModifyJadexEAnnotation(checkboxnames[i], checkboxes[i]));
			cLabel.setLayoutData(labelGridData);
			checkboxes[i].setLayoutData(textGridData);
		}
	}
	
	/**
	 * Manages the input.
	 */
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		updateSectionValues();
	}


	/**
	 * 
	 */
	protected void updateSectionValues()
	{
		if(modelElement != null)
		{
			EAnnotation ea = modelElement.getEAnnotation(util.containerEAnnotationName);
			if(ea != null)
			{
				for(int i = 0; i < checkboxnames.length; i++)
				{
					String tmpName = checkboxnames[i];
					String tmpValue = (String)util.getJadexEAnnotationDetail(tmpName);
					boolean sel = tmpValue!=null? new Boolean(tmpValue).booleanValue(): defaultvalues!=null? defaultvalues[i]: false; 
					checkboxes[i].setSelection(sel);
					checkboxes[i].setEnabled(true);
				}
			}
			else
			{
				for(int i = 0; i < checkboxnames.length; i++)
				{
					boolean sel = defaultvalues!=null? defaultvalues[i]: false; 
					checkboxes[i].setSelection(sel);
					checkboxes[i].setEnabled(true);
				}
			}
			return;
		}

		// fall through
		for(int i = 0; i < checkboxnames.length; i++)
		{
			checkboxes[i].setEnabled(false);
		}
	}

	// ---- internal used classes ----
	
	/**
	 * Tracks the change occurring on the text field.
	 */
	private class ModifyJadexEAnnotation implements FocusListener
	{
		private String key;
		private Button field;

		public ModifyJadexEAnnotation(String k, Button field)
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
			boolean value = field.getSelection();
			updateJadexEAnnotation(key, ""+value);
		}
	}

}
