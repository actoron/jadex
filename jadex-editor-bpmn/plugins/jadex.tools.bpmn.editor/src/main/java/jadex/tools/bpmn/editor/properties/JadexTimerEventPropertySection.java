package jadex.tools.bpmn.editor.properties;

import jadex.tools.bpmn.editor.properties.template.AbstractMultiTextfieldPropertySection;
import jadex.tools.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;


/**
 * 
 */
public class JadexTimerEventPropertySection extends AbstractMultiTextfieldPropertySection
{
	// ---- constants ----
	
	private static final String[] textFieldNames = new String[]{"duration"};
	
	// ---- attributes ----


	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexTimerEventPropertySection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION,
				textFieldNames);
	}

	// ---- methods ----
}
