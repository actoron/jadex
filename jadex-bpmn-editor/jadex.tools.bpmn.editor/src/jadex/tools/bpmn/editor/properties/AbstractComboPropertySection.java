/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.xml.type.internal.RegEx.RegularExpression;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * Property section Tab to enable Jadex specific properties
 * 
 * @author Claas Altschaffel
 */
public abstract class AbstractComboPropertySection extends AbstractJadexPropertySection
{

	// JadexCommonPropertySection.JADEX_ACTIVITY_ANNOTATION
	// containerEAnnotationName
	
	// ---- constants ----
	
	private static final String[] DEFAULT_COMBO_ITEMS = new String[] {
			"Some", //$NON-NLS-1$
			"Combo", //$NON-NLS-1$
			"Field", //$NON-NLS-1$
			"Defaults" //$NON-NLS-1$
	};
	
	// ---- attributes ----
	
	/** The Combo for implementing class */
	protected CCombo cCombo;
	
	protected String[] cComboItems;

	// ---- constructor ----
	
	/**
	 * @param containerEAnnotationName
	 * @param annotationDetailName
	 */
	protected AbstractComboPropertySection(String containerEAnnotationName,
			String annotationDetailName)
	{
		this(containerEAnnotationName, annotationDetailName, DEFAULT_COMBO_ITEMS);
	}

	/**
	 * @param containerEAnnotationName
	 * @param annotationDetailName
	 * @param cComboItems
	 * @param verifyListener
	 * @param traverseLListener
	 */
	protected AbstractComboPropertySection(String containerEAnnotationName,
			String annotationDetailName, String[] comboItems)
	{
		super(containerEAnnotationName, annotationDetailName);

		assert comboItems != null;
		this.cComboItems = comboItems;
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
		
		createTaskClassComposite(sectionComposite);

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
			
			// update the combo values
			String[] predefinedItems = this.cComboItems;
			cCombo.setItems(predefinedItems);
			
			EAnnotation ea = modelElement.getEAnnotation(containerEAnnotationName);
			if (ea != null)
			{
				String value = (String) ea.getDetails().get(annotationDetailName);
				int valueIndex = -1;
				
				// search value in items
				String[] items = cCombo.getItems();
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
					cCombo.add(value, 0);
					valueIndex = 0;
				}
				cCombo.select(0);
				
			}
			
			cCombo.setEnabled(true);
			return;

		}
		
		// fall through
		modelElement = null;
		cCombo.setText(""); //$NON-NLS-1$
		cCombo.setEnabled(false);
		
	}


	// ---- control creation methods ----
	
	/**
	 * Create a combo for task class selection in parent
	 *  
	 * @param parent
	 */
	protected Composite createTaskClassComposite(Composite parent)
	{
		
		// The layout of the section composite
		GridLayout layout = new GridLayout(1, false);
		sectionComposite.setLayout(layout);
		
		getWidgetFactory().createCLabel(sectionComposite, Messages.ActivityParameterListSection_ImplementationClass_label);

		final CCombo combo = getWidgetFactory().createCCombo(sectionComposite, SWT.NONE);
		
		GridData gridData = new GridData(SWT.FILL);
		gridData.minimumWidth = 500;
		gridData.widthHint = 500;
		combo.setLayoutData(gridData);
		
		
		String[] items = this.cComboItems;
		combo.setItems(items);
		combo.setText(combo.getItem(0));
		combo.addVerifyListener(new VerifyListener()
		{
			public void verifyText(VerifyEvent e)
			{
				String text = combo.getText();
				String newText = text.substring(0, e.start) + e.text
						+ text.substring(e.end);
				
				// don't allow non word characters
				RegularExpression re = new RegularExpression("\\w*"); //$NON-NLS-1$
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
					if (newText.endsWith(".class")) //$NON-NLS-1$
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
				updateJadexEAnnotation(annotationDetailName, combo.getText());
			}
		});
		
		return cCombo = combo;
	}

}


