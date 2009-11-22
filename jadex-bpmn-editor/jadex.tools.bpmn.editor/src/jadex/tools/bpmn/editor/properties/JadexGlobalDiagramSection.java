/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;


/**
 * @author Claas Altschaffel
 * 
 */
public class JadexGlobalDiagramSection extends
		JadexAbstract1ColumnTablePropertySection
{

	/**
	 * Default constructor, initializes super class
	 */
	public JadexGlobalDiagramSection()
	{
		super(JadexCommonPropertySection.JADEX_GLOBAL_ANNOTATION, JadexCommonPropertySection.JADEX_IMPORT_LIST_DETAIL,
				Messages.JadexSequenceMappingSection_MappingTable_Label);
	}

}
