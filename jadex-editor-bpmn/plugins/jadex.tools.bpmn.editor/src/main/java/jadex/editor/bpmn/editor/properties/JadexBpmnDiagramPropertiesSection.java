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
	private static final String[] textFieldNames = new String[]{
		Messages.JadexGlobalDiagramSection_Description_Label,
		Messages.JadexGlobalDiagramSection_Package_Label
		//"Configuration"
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

}
