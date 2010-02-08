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
		AbstractMultiColumnTablePropertySection
{

	//public static final String label = "Mappings";
	public static final String[] fields = new String[]{"Name", "Value"};
	public static final int[] columnWeights = new int[]{1 ,6};
	public static final String[] defaultListElementAttributeValues = new String[]{"name", ""};
	public static final int uniqueListElementAttribute = 0;
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexSequenceMappingSection()
	{
		super(JADEX_SEQUENCE_ANNOTATION, JADEX_MAPPING_LIST_DETAIL,
				Messages.JadexSequenceMappingSection_MappingTable_Label, fields, columnWeights, defaultListElementAttributeValues, uniqueListElementAttribute);
		
	}

}
