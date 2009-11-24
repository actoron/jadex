/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;


/**
 * @author Claas Altschaffel
 * 
 */
public class JadexGlobalDiagramPropertiesSection extends
		AbstractMultiTextfieldPropertySection
{

	// ---- constants ----
	
	private static final String[] textFieldNames = new String[] {Messages.JadexGlobalDiagramSection_Package_Label};
	
	// ---- attributes ----


	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexGlobalDiagramPropertiesSection()
	{
		super(JADEX_GLOBAL_ANNOTATION, JADEX_IMPORT_LIST_DETAIL,
				textFieldNames);
	}

	// ---- methods ----

}
