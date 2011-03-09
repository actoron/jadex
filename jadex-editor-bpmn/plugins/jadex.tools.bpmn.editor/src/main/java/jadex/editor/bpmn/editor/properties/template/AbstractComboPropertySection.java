/**
 * 
 */
package jadex.editor.bpmn.editor.properties.template;

import jadex.editor.bpmn.editor.JadexBpmnEditor;

import org.eclipse.core.runtime.IStatus;
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
import org.eclipse.swt.widgets.Button;
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
	
	/** A Button on the right side of the cCombo */
	protected Button rightButton;

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
	 * TO DO: create Class ComboItemsProvider for Tooltips?
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
		
		createComboComposite(sectionComposite);

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
			
			EAnnotation ea = util.getJadexEAnnotation();
			if (ea != null)
			{
				
				String value = util.getJadexEAnnotationDetail(util.annotationDetailName);
				
				if (value != null)
				{
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
				
			}
			else
			{
				// add empty value
				cCombo.add("", 0); // //$NON-NLS-1$
				cCombo.select(0);
			}
			
			cCombo.setEnabled(true);
			rightButton.setEnabled(true);
			return;

		}
		
		// fall through
		modelElement = null;
		cCombo.setText(""); //$NON-NLS-1$
		cCombo.setEnabled(false);
		rightButton.setEnabled(false);
		
	}


	// ---- control creation methods ----
	
	/**
	 * Create a combo for task class selection in parent
	 *  
	 * @param parent
	 */
	protected Composite createComboComposite(Composite parent)
	{
		
		// The layout of the section composite
		GridLayout layout = new GridLayout(3, false);
		sectionComposite.setLayout(layout);

		GridData labelGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		labelGridData.minimumWidth = 60;
		labelGridData.widthHint = 60;
		
		GridData comboGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		comboGridData.minimumWidth = 500;
		comboGridData.widthHint = 500;
		
		GridData buttonGridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		buttonGridData.minimumWidth = 60;
		buttonGridData.widthHint = 60;
		
		CLabel cLabel = getWidgetFactory().createCLabel(sectionComposite, cComboLabel+":"); // //$NON-NLS-1$
		addDisposable(cLabel);
		cLabel.setLayoutData(labelGridData);
		
		final CCombo combo = getWidgetFactory().createCCombo(sectionComposite, SWT.NONE);
		addDisposable(combo);
		combo.setLayoutData(comboGridData);
		
		Button disabledDefaultButton = getWidgetFactory().createButton(sectionComposite, "", SWT.Hide);
		disabledDefaultButton.setLayoutData(buttonGridData);
		disabledDefaultButton.setVisible(false);
		disabledDefaultButton.setEnabled(false);
		this.rightButton = disabledDefaultButton;
		
		
		String[] items = getComboItems();
		// avoid exception with bad implementations
		if (items == null || items.length == 0)
		{
			items = new String[]{"No items provided"};
			JadexBpmnEditor.log("No items for class combo property", null, IStatus.WARNING);
		}
		
		
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


