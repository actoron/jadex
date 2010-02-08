/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;


/**
 * @author Claas Altschaffel
 * 
 */
public class JadexBpmnDiagramImportsSection extends
		AbstractMultiColumnTablePropertySection
{

	/**
	 * Default constructor, initializes super class
	 */
	public JadexBpmnDiagramImportsSection()
	{
//		super(JADEX_GLOBAL_ANNOTATION, JADEX_IMPORT_LIST_DETAIL,
//				Messages.JadexGlobalDiagramSection_Imports_Label, "import");
		super(JADEX_GLOBAL_ANNOTATION, JADEX_IMPORT_LIST_DETAIL,
				Messages.JadexGlobalDiagramSection_Imports_Label, 
				new String[]{"import"}, 
				new int[]{1}, 
				new String[]{"jadex.*"}, 0);
	}

}
