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
		Abstract2ColumnTablePropertySection
{

	private static final String[] COLUMN_NAMES = new String[]{"Name", "Value"};
	private static final  int[] COLUMN_WEIGHTS = new int[]{1 ,6};
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexIntermediateEventsParameterSection()
	{
		super(JadexCommonPropertySection.JADEX_ACTIVITY_ANNOTATION, JadexCommonPropertySection.JADEX_PARAMETER_LIST_DETAIL,
				Messages.JadexCommonParameterListSection_ParameterTable_Label, COLUMN_NAMES, COLUMN_WEIGHTS );
	}

}
