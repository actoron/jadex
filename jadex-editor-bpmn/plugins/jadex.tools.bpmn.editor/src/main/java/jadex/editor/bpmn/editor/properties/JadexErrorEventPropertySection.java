package jadex.editor.bpmn.editor.properties;

import jadex.editor.bpmn.editor.properties.template.AbstractMultiTextfieldPropertySection;
import jadex.editor.bpmn.editor.properties.template.JadexBpmnPropertiesUtil;

/**
 * 
 *
 */
public class JadexErrorEventPropertySection extends AbstractMultiTextfieldPropertySection
{
	// ---- constants ----
	
	private static final String[] textFieldNames = new String[]{"exception"};
	
	// ---- constructor ----
	
	/**
	 * Default constructor, initializes super class
	 */
	public JadexErrorEventPropertySection()
	{
		super(JadexBpmnPropertiesUtil.JADEX_GLOBAL_ANNOTATION, textFieldNames);
	}
}
