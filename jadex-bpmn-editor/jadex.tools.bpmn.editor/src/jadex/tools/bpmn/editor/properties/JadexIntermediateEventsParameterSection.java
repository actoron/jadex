/**
 * 
 */
package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.diagram.Messages;

/**
 * @author Claas Altschaffel
 * 
 */
public class JadexIntermediateEventsParameterSection extends
		AbstractMultiColumnTablePropertySection
{

	//public static final String label = "Parameter";
	public static final String[] fields = new String[]{"Name", "Value"};
	public static final int[] columnWeights = new int[]{1 ,6};
	public static final String[] defaultListElementAttributeValues = new String[]{"name", ""};
	public static final int uniqueListElementAttribute = 0;

	/**
	 * Default constructor, initializes super class
	 */
	public JadexIntermediateEventsParameterSection()
	{
		super(JADEX_ACTIVITY_ANNOTATION, JADEX_PARAMETER_LIST_DETAIL,
				Messages.JadexCommonParameterListSection_ParameterTable_Label, fields, columnWeights, defaultListElementAttributeValues, uniqueListElementAttribute);
	}

}
