/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
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
public abstract class AbstractComboPropertySection extends AbstractBpmnPropertySection
{
	
	// ---- attributes ----
	
	/** The CCombo for implementing class */
	protected CCombo cCombo;
	
	/** The CCombo label */
	protected String cComboLabel;

	// ---- constructor ----
	
	/**
	 * @param containerEAnnotationName
	 * @param annotationDetailName
	 */
	protected AbstractComboPropertySection(String containerEAnnotationName,
			String annotationDetailName)
	{
		this(containerEAnnotationName, annotationDetailName, annotationDetailName);
	}
	
	/**
	 */
	protected AbstractComboPropertySection(String containerEAnnotationName,
			String annotationDetailName, String comboLabel)
	{
		super(containerEAnnotationName, annotationDetailName);
		
		if (comboLabel == null)
		{
			comboLabel = annotationDetailName;
		}

		this.cComboLabel = comboLabel;
	}

	// ---- abstract methods ----
	
	
	/** 
	 * Provide predefined items for CCombo 
	 * TODO: create ComboItemsProvider for tooltips?
	 */
	protected abstract String[] getComboItems();

	
	// ---- methods ----
	
	/* (non-Javadoc)
	 * @see jadex.tools.model.common.properties.AbstractCommonPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		// nothing to dispose here, use addDisposable(Object) instead
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
			//String[] predefinedItems = this.cComboItems;
			String[] predefinedItems = getComboItems();
			
			cCombo.setItems(predefinedItems);
			
			EAnnotation ea = modelElement.getEAnnotation(util.containerEAnnotationName);
			if (ea != null && ea.getDetails().get(util.annotationDetailName) != null)
			{
				String value = (String) ea.getDetails().get(util.annotationDetailName);
				
				int valueIndex = -1;
				// search value in items
				String[] items = cCombo.getItems();
				for (int i = 0; i < items.length; i++)
				{
					if (items[i].equals(value))
					{
						valueIndex = i;
					}
				}
				
				// add the value to the items list
				if (valueIndex == -1)
				{
					cCombo.add(value, 0);
					valueIndex = 0;
				}
				cCombo.select(valueIndex);
				
			}
			else
			{
				// add empty value
				cCombo.add("", 0); // //$NON-NLS-1$
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
		GridLayout layout = new GridLayout(2, false);
		sectionComposite.setLayout(layout);

		GridData comboGridData = new GridData(SWT.FILL);
		comboGridData.minimumWidth = 500;
		comboGridData.widthHint = 500;
		
		GridData labelGridData = new GridData();
		labelGridData.minimumWidth = 60;
		labelGridData.widthHint = 60;
		
		CLabel cLabel = getWidgetFactory().createCLabel(sectionComposite, cComboLabel+":"); // //$NON-NLS-1$
		addDisposable(cLabel);
		cLabel.setLayoutData(labelGridData);
		
		final CCombo combo = getWidgetFactory().createCCombo(sectionComposite, SWT.NONE);
		addDisposable(combo);
		combo.setLayoutData(comboGridData);
		
		//String[] items = this.cComboItems;
		String[] items = getComboItems();
		
		combo.setItems(items);
		combo.setText(combo.getItem(0));
		
		combo.addTraverseListener(new TraverseListener()
		{
			public void keyTraversed(TraverseEvent e)
			{
				if (e.detail == SWT.TRAVERSE_RETURN)
				{
					e.doit = false;
					e.detail = SWT.TRAVERSE_NONE;
					String newText = combo.getText();
					
					combo.add(newText);
					combo.setSelection(new Point(0, newText
							.length()));

				}
			}
		});
		
		combo.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent e)
			{
				updateJadexEAnnotation(util.annotationDetailName, combo.getText());
			}
		});
		
		return cCombo = combo;
	}

}


