package jadex.tools.bpmn.editor.properties;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

/**
 * 
 *
 */
public class JadexMessageEventPropertySection extends AbstractJadexPropertySection
{
	// ---- constants ----
	
	//private static final String[] textFieldNames = new String[]{"msgtype", "message", "expression"};
	
	private static String[] cComboItems = new String[]{"send", "receive"};
	
	// ---- attributes ----

	
	
	private CLabel msgtypeLabel;
	private Text msgtypeText;
	
	private CLabel messageLabel;
	private Text messageText;
	
	private CLabel expressionLabel;
	private Text expressionText;
	
	private CLabel cComboLabel;
	private CCombo combo;
	
	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexMessageEventPropertySection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_EVENT_MESSAGE_DETAIL);
	}

	// ---- methods ----
	
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		
		Group sectionGroup = getWidgetFactory().createGroup(sectionComposite, JADEX_EVENT_MESSAGE_DETAIL);
		controls.add(sectionGroup);
		sectionComposite = sectionGroup;
		
		// The layout of the section composite
		GridLayout layout = new GridLayout(2, false);
		sectionComposite.setLayout(layout);
		
		GridData labelGridData = new GridData();
		labelGridData.minimumWidth = 80;
		labelGridData.widthHint = 80;
		
		GridData textGridData = new GridData();
		textGridData.minimumWidth = 500;
		textGridData.widthHint = 500;
		
		GridData comboGridData = new GridData();
		comboGridData.minimumWidth = 80;
		comboGridData.widthHint = 80;
		
		cComboLabel = getWidgetFactory().createCLabel(sectionComposite, "mode"+":"); // //$NON-NLS-0$
		cComboLabel.setLayoutData(labelGridData);
		controls.add(cComboLabel);
		
		combo = getWidgetFactory().createCCombo(sectionComposite, SWT.BORDER | SWT.READ_ONLY);
		combo.setLayoutData(comboGridData);
		combo.setItems(cComboItems);
		combo.setText(combo.getItem(0));
		combo.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int selectionIndex = ((CCombo) e.getSource()).getSelectionIndex();
				updateControls(selectionIndex);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		combo.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				updateJadexEAnnotation("mode", combo.getText());
			}
		});
		controls.add(combo);
		
		msgtypeLabel = getWidgetFactory().createCLabel(sectionComposite, "msgtype"+":"); // //$NON-NLS-0$
		msgtypeLabel.setLayoutData(labelGridData);
		msgtypeText = getWidgetFactory().createText(sectionComposite, "");
		msgtypeText.setLayoutData(textGridData);
		msgtypeText.addModifyListener(new ModifyJadexEAnnotation("msgtype", msgtypeText));
		controls.add(msgtypeLabel);
		controls.add(msgtypeText);
		
		messageLabel = getWidgetFactory().createCLabel(sectionComposite, "message"+":"); // //$NON-NLS-0$
		messageLabel.setLayoutData(labelGridData);
		messageText = getWidgetFactory().createText(sectionComposite, "");
		messageText.setLayoutData(textGridData);
		messageText.addModifyListener(new ModifyJadexEAnnotation("message", messageText));
		controls.add(messageLabel);
		controls.add(messageText);
		
		expressionLabel = getWidgetFactory().createCLabel(sectionComposite, "expression"+":"); // //$NON-NLS-0$
		expressionLabel.setLayoutData(labelGridData);
		expressionText = getWidgetFactory().createText(sectionComposite, "");
		expressionText.setLayoutData(textGridData);
		expressionText.addModifyListener(new ModifyJadexEAnnotation("expression", expressionText));
		controls.add(expressionLabel);
		controls.add(expressionText);
	}
	

	/**
	 * @see org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose()
	{
		super.dispose();
	}

	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection)
	{
		super.setInput(part, selection);
		if (modelElement != null)
		{
			EAnnotation ea = modelElement.getEAnnotation(containerEAnnotationName);
			if (ea != null)
			{
				String comboValue = (String) ea.getDetails().get("mode");
				comboValue = comboValue != null ? comboValue : cComboItems[0];
				
				int valueIndex = 0;
				// search value in items
				String[] items = combo.getItems();
				for (int i = 0; i < items.length; i++)
				{
					if (items[i].equals(comboValue))
					{
						valueIndex = i;
					}
				}
				combo.select(valueIndex);
				
				String tmpValue;
				
				tmpValue = (String) ea.getDetails().get("msgtype");
				msgtypeText.setText(tmpValue != null ? tmpValue : "");
				
				tmpValue = (String) ea.getDetails().get("message");
				messageText.setText(tmpValue != null ? tmpValue : "");
				
				tmpValue = (String) ea.getDetails().get("expression");
				expressionText.setText(tmpValue != null ? tmpValue : "");
				
				
			}
			else
			{
				combo.select(0);
				msgtypeText.setText("");
				messageText.setText("");
				expressionText.setText("");
			}
			
			updateControls(combo.getSelectionIndex());
			return;
		}
	}

	// ---- methods ----
	
	private void updateControls(int selectionIndex)
	{
		
		//messageLabel.setVisible(selectionIndex == 0);
		messageLabel.setEnabled(selectionIndex == 0);
		//messageText.setVisible(selectionIndex == 0);
		messageText.setEnabled(selectionIndex == 0);
		
		//expressionLabel.setVisible(selectionIndex != 0);
		expressionLabel.setEnabled(selectionIndex != 0);
		//expressionText.setVisible(selectionIndex != 0);
		expressionText.setEnabled(selectionIndex != 0);
		
	}
	
	// ---- internal classes ----
	
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

