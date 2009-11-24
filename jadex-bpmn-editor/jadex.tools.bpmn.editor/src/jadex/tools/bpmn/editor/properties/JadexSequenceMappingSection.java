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
		Abstract2ColumnTablePropertySection
{

	private static final String[] COLUMN_NAMES = new String[]{"Name", "Value"};
	private static final  int[] COLUMN_WEIGHTS = new int[]{1 ,6};
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexSequenceMappingSection()
	{
		super(JADEX_SEQUENCE_ANNOTATION, JADEX_MAPPING_LIST_DETAIL,
				Messages.JadexSequenceMappingSection_MappingTable_Label, COLUMN_NAMES, COLUMN_WEIGHTS);
	}

}
