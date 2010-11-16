/**
 * 
 */
package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.diagram.Messages;
import jadex.editor.bpmn.editor.properties.template.AbstractMultiTextfieldPropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;


/**
 * @author Claas Altschaffel
 * 
 */
public class JadexBpmnDiagramPropertiesSection extends
		AbstractMultiTextfieldPropertySection
{

	// ---- constants ----
	
	private static final String[] textFieldNames = new String[]{Messages.JadexGlobalDiagramSection_Description_Label,
		Messages.JadexGlobalDiagramSection_Package_Label};
	
	// ---- attributes ----


	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramPropertiesSection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
				textFieldNames);
	}

	// ---- methods ----

}
