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
		JadexAbstractParameterTablePropertySection
{

	/**
	 * Default constructor, initializes super class
	 */
	public JadexIntermediateEventsParameterSection()
	{
		super(JadexCommonPropertySection.JADEX_ACTIVITY_ANNOTATION,
				Messages.JadexCommonParameterListSection_ParameterTable_Label);
	}

}
