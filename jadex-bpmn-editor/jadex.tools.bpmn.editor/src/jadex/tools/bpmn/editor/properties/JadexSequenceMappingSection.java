/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;


/**
 * @author Claas Altschaffel
 * 
 */
public class JadexSequenceMappingSection extends
		JadexAbstractParameterTablePropertySection
{

	/**
	 * Default constructor, initializes super class
	 */
	public JadexSequenceMappingSection()
	{
		super(JadexCommonPropertySection.JADEX_SEQUENCE_ANNOTATION,
				Messages.JadexSequenceMappingSection_MappingTable_Label);
	}

}
