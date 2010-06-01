/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;


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
