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
		JadexAbstract2ColumnTablePropertySection
{

	/**
	 * Default constructor, initializes super class
	 */
	public JadexIntermediateEventsParameterSection()
	{
		super(JadexCommonPropertySection.JADEX_ACTIVITY_ANNOTATION, JadexCommonPropertySection.JADEX_PARAMETER_LIST_DETAIL,
				Messages.JadexCommonParameterListSection_ParameterTable_Label);
	}

}
