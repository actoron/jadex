/**
 * 
 */
package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.diagram.Messages;
import jadex.editor.bpmn.editor.properties.template.AbstractMultiTextfieldPropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


/**
 * 
 */
public class JadexBpmnDiagramPropertiesSection extends AbstractMultiTextfieldPropertySection
{
	// ---- constants ----
	
	protected static final String[] CHECKBOX_NAMES = new String[] {"Suspend", "Master", "Daemon", "Autoshutdown"};
	
	private static final String[] textFieldNames = new String[]{
		Messages.JadexGlobalDiagramSection_Description_Label,
		Messages.JadexGlobalDiagramSection_Package_Label,
		"Configuration"
//		Messages.JadexGlobalDiagramSection_Configuration_Label
	};
	
	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramPropertiesSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, textFieldNames);
	}
	
	/**
	 * Creates the UI of the section.
	 */
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage)
	{
		super.createControls(parent, aTabbedPropertySheetPage);
		
		for(int i=0; i<CHECKBOX_NAMES.length; i++)
		{
			final String myname = CHECKBOX_NAMES[i];
			Label label = getWidgetFactory().createLabel(sectionComposite, CHECKBOX_NAMES[i] + ":");
			addDisposable(label);
			Button button = getWidgetFactory().createButton(sectionComposite, null, SWT.CHECK);
			addDisposable(button);
			button.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					boolean state = ((Button) e.getSource()).getSelection();
					updateJadexEAnnotation(myname, String.valueOf(state));
				}
			});
//			label.setLayoutData(labelGridData);
//			button.setLayoutData(checkboxGridData);
		}
	}

}
